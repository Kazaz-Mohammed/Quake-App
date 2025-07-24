package com.aiquake.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationCompat;

import com.aiquake.R;
import com.aiquake.MainActivity;
import com.aiquake.data.AppDatabase;
import com.aiquake.data.entity.SensorData;
import com.aiquake.data.entity.EarthquakeEvent;
import com.aiquake.data.repository.EarthquakeRepository;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

public class EarthquakeDetectionService extends Service implements SensorEventListener {
    private static final String CHANNEL_ID = "EarthquakeDetectionChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final float EARTHQUAKE_THRESHOLD = 15.0f; // Adjusted threshold
    private static final long COOLDOWN_MILLIS = 10000; // 10 seconds cooldown period
    private static final String TAG = "EarthquakeService";
    private static final int MAGNITUDE_BUFFER_SIZE = 10; // Number of readings for moving average

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private PowerManager.WakeLock wakeLock;
    private ExecutorService executorService;
    private AppDatabase database;
    private EarthquakeRepository earthquakeRepository;
    private long lastDetectionTime = 0;
    private Queue<Float> magnitudeBuffer = new LinkedList<>();

    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        database = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();
        earthquakeRepository = new EarthquakeRepository(getApplication());

        // Initialize FusedLocationProviderClient and Geocoder
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Earthquake Detection Service",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Acquire wake lock
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "QuakeApp::EarthquakeDetectionWakeLock"
        );
        wakeLock.acquire();
        Log.d(TAG, "Wake lock acquired");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        startForeground(NOTIFICATION_ID, createNotification());
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Sensor listener registered");

        
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Log raw sensor values
            // Log.d(TAG, String.format("Sensor data: X=%.2f, Y=%.2f, Z=%.2f", x, y, z));

            // Calculate instantaneous magnitude
            float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

            // Add magnitude to the buffer and calculate average
            magnitudeBuffer.add(magnitude);
            if (magnitudeBuffer.size() > MAGNITUDE_BUFFER_SIZE) {
                magnitudeBuffer.remove();
            }

            float averageMagnitude = 0;
            for (float value : magnitudeBuffer) {
                averageMagnitude += value;
            }
            if (!magnitudeBuffer.isEmpty()) { // Avoid division by zero
                 averageMagnitude /= magnitudeBuffer.size();
            }

            // Log both instantaneous and averaged magnitude
            Log.d(TAG, String.format("Instantaneous Magnitude: %.2f, Averaged Magnitude: %.2f", magnitude, averageMagnitude));

            // --- Earthquake Detection Logic ---
            long currentTime = System.currentTimeMillis();
            if (averageMagnitude > EARTHQUAKE_THRESHOLD && (currentTime - lastDetectionTime > COOLDOWN_MILLIS)) {
                Log.d(TAG, "Threshold exceeded and cooldown passed! Averaged Magnitude: " + averageMagnitude);
                // A potential earthquake is detected!
                // Update last detection time
                lastDetectionTime = currentTime;

                // Get last known location and create/save EarthquakeEvent
                getLastLocationAndSaveEvent(currentTime, averageMagnitude);

                // Show notification for the detected event
                showEarthquakeAlert(averageMagnitude);

                
            }
            
        }
    }

    private void getLastLocationAndSaveEvent(long timestamp, float magnitude) {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissions are not granted. Log an error and save event with default location.
            Log.e(TAG, "Location permissions not granted. Cannot get last known location.");
            createAndSaveEarthquakeEvent(timestamp, magnitude, 0.0, 0.0, 0.0f, "Location Unavailable");
            return;
        }

        // Get last known location
        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(location -> {
                double latitude = 0.0;
                double longitude = 0.0;
                String locationName = "Unknown Location";

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    // Perform reverse geocoding to get a location name
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            StringBuilder sb = new StringBuilder();
                            //  format the location name
                            if (address.getLocality() != null) {
                                sb.append(address.getLocality()).append(", ");
                            }
                            if (address.getAdminArea() != null) {
                                sb.append(address.getAdminArea());
                            }
                            if (sb.length() > 0) {
                                locationName = sb.toString();
                                // Remove trailing comma 
                                if (locationName.endsWith(", ")) {
                                    locationName = locationName.substring(0, locationName.length() - 2);
                                }
                            } else {
                                // Fallback to coordinates if no readable name is found
                                locationName = "Lat: " + String.format("%.4f", latitude) + ", Lon: " + String.format("%.4f", longitude);
                            }
                            Log.d(TAG, "Reverse geocoding successful: " + locationName);
                        } else {
                             locationName = "Lat: " + String.format("%.4f", latitude) + ", Lon: " + String.format("%.4f", longitude); // Fallback to coordinates
                             Log.w(TAG, "No addresses found for location.");
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Reverse geocoding failed", e);
                        locationName = "Geocoding Error"; // Indicate geocoding failed
                    }

                } else {
                    Log.w(TAG, "Last known location is null.");
                    locationName = "Location Data Null";
                }

                // Create and save EarthquakeEvent with obtained location
                createAndSaveEarthquakeEvent(timestamp, magnitude, latitude, longitude, 0.0f, locationName);
            })
            .addOnFailureListener(e -> {
                // Handle failure to get location
                Log.e(TAG, "Failed to get last known location", e);
                createAndSaveEarthquakeEvent(timestamp, magnitude, 0.0, 0.0, 0.0f, "Location Error");
            });
    }

     private void createAndSaveEarthquakeEvent(long timestamp, float magnitude, double latitude, double longitude, float depth, String locationName) {
         EarthquakeEvent earthquakeEvent = new EarthquakeEvent(
            timestamp, // Timestamp as long
            magnitude,
            latitude, // Real Latitude
            longitude, // Real Longitude
            depth, // Placeholder Depth
            locationName, // Location Name (placeholder or derived)
            1.0f // Placeholder Confidence Level
        );

        // Save EarthquakeEvent to database in background
        executorService.execute(() -> {
            earthquakeRepository.insertEarthquakeEvent(earthquakeEvent);
            Log.d(TAG, "EarthquakeEvent saved to database with location: " + locationName);
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Earthquake Detection Active")
            .setContentText("Monitoring for seismic activity...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build();
    }

    private void showEarthquakeAlert(float magnitude) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Potential Earthquake Detected!")
            .setContentText(String.format("Magnitude: %.1f", magnitude))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = 
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        executorService.shutdown();
        Log.d(TAG, "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
} 