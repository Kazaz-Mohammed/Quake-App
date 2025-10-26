package com.aiquake;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aiquake.adapters.EventAdapter;
import com.aiquake.models.EarthquakeEvent;
import com.aiquake.service.EarthquakeDetectionService;
import com.aiquake.ui.dialogs.UsgsEventsDialogFragment;
import com.aiquake.viewmodels.EarthquakeViewModel;
import com.aiquake.utils.QueryUtils;
import com.aiquake.ui.activities.EventDetailActivity;
import com.aiquake.ui.activities.safety.SafetyActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.util.Log;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import android.location.Geocoder;
import java.io.IOException;
import java.util.Locale;
import com.aiquake.utils.EarthquakeNotificationManager;
import com.aiquake.services.EarthquakeApiService;
import com.aiquake.services.WebSocketService;
import android.app.NotificationManager;
import android.app.AlertDialog;

public class MainActivity extends AppCompatActivity implements SensorEventListener, EventAdapter.OnItemClickListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 102;
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson";
    private static final String TAG = "MainActivity";
    private static final double EARTHQUAKE_THRESHOLD = 15.0; // Increased base threshold
    private static final int MIN_ACCELERATION_COUNT = 5; // Increased required consecutive readings
    private static final double MIN_MAGNITUDE = 3.0; // Minimum magnitude to consider
    private static final int WINDOW_SIZE = 10; // Number of readings to analyze
    private static final double VARIANCE_THRESHOLD = 5.0; // Minimum variance to consider as earthquake

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView xAxisTextView;
    private TextView yAxisTextView;
    private TextView zAxisTextView;
    private Button startMonitoringButton;
    private TextView statusTextView;
    private boolean isMonitoring = false;

    private LineChart sensorDataChart;
    private List<Entry> chartEntries = new ArrayList<>();
    private int chartXValue = 0;

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<EarthquakeEvent> events;
    private EarthquakeViewModel earthquakeViewModel;

    private FloatingActionButton refreshFab;
    private FloatingActionButton viewUsgsEventsFab;
    private FloatingActionButton fabSafety;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LocationCallback locationCallback;
    private WebSocketService webSocketService;
    private boolean isWebSocketServiceBound = false;

    private Geocoder geocoder;
    private int accelerationCount = 0;
    private List<Double> accelerationWindow = new ArrayList<>();
    private double lastMagnitude = 0.0;
    private long lastDetectionTime = 0;
    private static final long MIN_DETECTION_INTERVAL = 30000; // 30 seconds between detections

    private EarthquakeNotificationManager notificationManager;
    private EarthquakeApiService apiService;

    private ServiceConnection webSocketServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "WebSocket service connected");
            WebSocketService.LocalBinder binder = (WebSocketService.LocalBinder) service;
            webSocketService = binder.getService();
            isWebSocketServiceBound = true;
            // Update location if available
            if (currentLocation != null) {
                Log.d(TAG, "Updating WebSocket service with new location");
                webSocketService.updateLocation(currentLocation);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "WebSocket service disconnected");
            webSocketService = null;
            isWebSocketServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request notification permission for Android 13 and above
        if (Build.VERSION.SDK_INT >= 33) { // Android 13 (API level 33)
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }

        // Start WebSocket service
        startWebSocketService();

        // Initialize views
        xAxisTextView = findViewById(R.id.xAxisTextView);
        yAxisTextView = findViewById(R.id.yAxisTextView);
        zAxisTextView = findViewById(R.id.zAxisTextView);
        startMonitoringButton = findViewById(R.id.startMonitoringButton);
        sensorDataChart = findViewById(R.id.sensorDataChart);
        recyclerView = findViewById(R.id.recycler_view);
        statusTextView = findViewById(R.id.statusTextView);
        refreshFab = findViewById(R.id.refreshFab);
        viewUsgsEventsFab = findViewById(R.id.viewUsgsEventsFab);
        fabSafety = findViewById(R.id.fab_safety);

        // Initialize sensor manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Set up button click listener
        startMonitoringButton.setOnClickListener(v -> toggleMonitoring());

        // Set up click listener for the new USGS events FAB
        viewUsgsEventsFab.setOnClickListener(v -> fetchUsgsDataAndShowPopup());

        // Set up click listener for the safety FAB
        fabSafety.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SafetyActivity.class);
            startActivity(intent);
        });

        // Configure the chart
        setupChart();

        // Setup RecyclerView for recent events
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Initialize events list
        events = new ArrayList<>();
        
        // Initialize adapter with context
        eventAdapter = new EventAdapter(this, events);
        recyclerView.setAdapter(eventAdapter);

        // Set the item click listener for the adapter
        eventAdapter.setOnItemClickListener(this);

        // Initialize ViewModel and observe data
        earthquakeViewModel = new ViewModelProvider(this).get(EarthquakeViewModel.class);
        earthquakeViewModel.getAllEarthquakeEvents().observe(this, this::updateUI);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setupLocationCallback();

        // Initialize Geocoder
        geocoder = new Geocoder(this, Locale.getDefault());

        // Initialize notification manager
        notificationManager = new EarthquakeNotificationManager(this);

        // Initialize API service
        apiService = new EarthquakeApiService(this);

        // Check and request notification permission for Android 11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (!notificationManager.areNotificationsEnabled()) {
                // Show a dialog explaining why notifications are needed
                new AlertDialog.Builder(this)
                    .setTitle("Notification Permission Required")
                    .setMessage("This app needs notification permission to alert you about earthquakes. Please enable notifications in your system settings.")
                    .setPositiveButton("Open Settings", (dialog, which) -> {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        }
    }

    @Override
    public void onItemClick(EarthquakeEvent event) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    private void setupChart() {
        // Customize chart appearance if needed
        sensorDataChart.getDescription().setEnabled(false);
        sensorDataChart.setTouchEnabled(false);
        sensorDataChart.setDrawGridBackground(false);
        sensorDataChart.getXAxis().setDrawLabels(false);
        sensorDataChart.getAxisRight().setEnabled(false);
        sensorDataChart.getLegend().setEnabled(false);
    }

    private void toggleMonitoring() {
        if (!isMonitoring) {
            // Check for all required permissions
            boolean hasLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            boolean hasForegroundPermission = true; // Foreground service permission is not required for Android 11
            
            if (!hasLocationPermission) {
                // Request location permission
                ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
                );
                return;
            }

            // If we have all permissions, start monitoring
            startMonitoring();
        } else {
            stopMonitoring();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startMonitoring();
            } else {
                // Show a dialog explaining why permissions are needed
                new AlertDialog.Builder(this)
                    .setTitle("Permissions Required")
                    .setMessage("Location permission is required to detect earthquakes and provide accurate alerts. Please grant the permission to continue.")
                    .setPositiveButton("Grant Permission", (dialog, which) -> {
                        // Open app settings
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
                statusTextView.setText("Permission Denied");
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
            } else {
                Log.e(TAG, "Notification permission denied");
                Toast.makeText(this, "Notification permission is required for earthquake alerts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    currentLocation = location;
                    Log.d(TAG, "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
                    
                    // Update WebSocket service with new location
                    if (isWebSocketServiceBound && webSocketService != null) {
                        webSocketService.updateLocation(location);
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void startMonitoring() {
        if (accelerometer != null) {
            // Start location updates
            startLocationUpdates();

            // Start the background service
            Intent serviceIntent = new Intent(this, EarthquakeDetectionService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

            // Register listener in Activity ONLY for chart visualization and TextView updates
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            isMonitoring = true;
            startMonitoringButton.setText("Stop Monitoring");
            statusTextView.setText("Monitoring Active");
            // Clear previous data when starting
            chartEntries.clear();
            chartXValue = 0;
        } else {
            Toast.makeText(this, "Accelerometer sensor not available on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopMonitoring() {
        // Stop location updates
        stopLocationUpdates();

        // Stop the background service
        Intent serviceIntent = new Intent(this, EarthquakeDetectionService.class);
        stopService(serviceIntent);

        // Unregister listener in Activity
        sensorManager.unregisterListener(this);

        isMonitoring = false;
        startMonitoringButton.setText("Start Monitoring");
        statusTextView.setText("Idle");
        // Reset text views
        xAxisTextView.setText("X-axis: 0.0");
        yAxisTextView.setText("Y-axis: 0.0");
        zAxisTextView.setText("Z-axis: 0.0");

        // Optional: Clear chart or show a message when stopped
        // sensorDataChart.clear();
        // sensorDataChart.invalidate();
    }

    private void fetchUsgsDataAndShowPopup() {
        new UsgsEarthquakeAsyncTask().execute(USGS_REQUEST_URL);
    }

    private class UsgsEarthquakeAsyncTask extends AsyncTask<String, Void, List<EarthquakeEvent>> {

        @Override
        protected List<EarthquakeEvent> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<EarthquakeEvent> earthquakes = QueryUtils.fetchEarthquakeData(urls[0]);
            return earthquakes;
        }

        @Override
        protected void onPostExecute(List<EarthquakeEvent> earthquakes) {
            if (earthquakes != null && !earthquakes.isEmpty()) {
                UsgsEventsDialogFragment dialogFragment = UsgsEventsDialogFragment.newInstance(earthquakes);
                dialogFragment.show(getSupportFragmentManager(), "usgs_events_dialog");
            } else {
                Toast.makeText(MainActivity.this, "No USGS earthquake data available.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(List<EarthquakeEvent> events) {
        Log.d(TAG, "Updating UI with " + (events != null ? events.size() : 0) + " events");
        if (events != null && !events.isEmpty()) {
            eventAdapter.updateEvents(events);
            Log.d(TAG, "First event magnitude: " + events.get(0).getMagnitude());
        } else {
            Log.d(TAG, "No events to display");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Update axis text views
            xAxisTextView.setText(String.format("X-axis: %.2f", x));
            yAxisTextView.setText(String.format("Y-axis: %.2f", y));
            zAxisTextView.setText(String.format("Z-axis: %.2f", z));

            // Calculate acceleration magnitude
            double acceleration = Math.sqrt(x * x + y * y + z * z);
            
            // Log the acceleration for debugging
            Log.d(TAG, "Acceleration: " + acceleration);

            // Add new entry to the chart data
            chartEntries.add(new Entry(chartXValue++, (float)acceleration));

            // Keep only the last 100 points for a rolling window effect
            if (chartEntries.size() > 100) {
                chartEntries.remove(0);
            }

            // Update the chart
            LineDataSet dataSet = new LineDataSet(chartEntries, "Magnitude");
            dataSet.setDrawCircles(false);
            dataSet.setColor(getResources().getColor(R.color.purple_700, null));
            dataSet.setLineWidth(1.5f);

            LineData lineData = new LineData(dataSet);
            sensorDataChart.setData(lineData);
            sensorDataChart.notifyDataSetChanged();
            sensorDataChart.invalidate();

            // Update acceleration window
            accelerationWindow.add(acceleration);
            if (accelerationWindow.size() > WINDOW_SIZE) {
                accelerationWindow.remove(0);
            }

            // Improved earthquake detection logic
            if (acceleration > EARTHQUAKE_THRESHOLD && accelerationWindow.size() >= WINDOW_SIZE) {
                // Calculate variance in the window
                double mean = calculateMean(accelerationWindow);
                double variance = calculateVariance(accelerationWindow, mean);
                
                Log.d(TAG, String.format("Acceleration: %.2f, Variance: %.2f", acceleration, variance));

                if (variance > VARIANCE_THRESHOLD) {
                    accelerationCount++;
                    Log.d(TAG, "High variance detected: " + variance + " (count: " + accelerationCount + ")");
                    
                    if (accelerationCount >= MIN_ACCELERATION_COUNT) {
                        // Calculate magnitude
                        double magnitude = calculateMagnitude(acceleration, variance);
                        
                        // Check if magnitude is significant and enough time has passed since last detection
                        long currentTime = System.currentTimeMillis();
                        if (magnitude >= MIN_MAGNITUDE && 
                            (currentTime - lastDetectionTime) > MIN_DETECTION_INTERVAL) {
                            
                            Log.d(TAG, "Earthquake detected! Magnitude: " + magnitude);
                            
                            // Create a new earthquake event with current location
                            double latitude = currentLocation != null ? currentLocation.getLatitude() : 0.0;
                            double longitude = currentLocation != null ? currentLocation.getLongitude() : 0.0;
                            
                            // Get location name using reverse geocoding
                            String locationName = getLocationName(latitude, longitude);
                            
                            handleEarthquakeDetection(magnitude, latitude, longitude, locationName);
                            
                            // Update UI immediately
                            List<EarthquakeEvent> currentEvents = new ArrayList<>();
                            if (eventAdapter != null && eventAdapter.getEvents() != null) {
                                currentEvents.addAll(eventAdapter.getEvents());
                            }
                            currentEvents.add(0, new EarthquakeEvent(
                                magnitude,
                                latitude,
                                longitude,
                                0.0,  // Placeholder depth
                                locationName,
                                new Date(),
                                calculateConfidence(magnitude, variance)
                            ));
                            updateUI(currentEvents);
                            
                            // Show toast with location info
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, 
                                    "Earthquake detected! Magnitude: " + String.format("%.1f", magnitude) + 
                                    " at " + locationName,
                                    Toast.LENGTH_SHORT).show();
                            });
                            
                            // Update last detection time
                            lastDetectionTime = currentTime;
                            lastMagnitude = magnitude;
                        }
                        
                        // Reset acceleration count
                        accelerationCount = 0;
                    }
                } else {
                    // Reset acceleration count if variance is too low
                    accelerationCount = 0;
                }
            } else {
                // Reset acceleration count if below threshold
                accelerationCount = 0;
            }
        }
    }

    private String getLocationName(double latitude, double longitude) {
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                StringBuilder locationName = new StringBuilder();
                
                // Add city or locality
                if (address.getLocality() != null) {
                    locationName.append(address.getLocality());
                }
                // Add state/province
                if (address.getAdminArea() != null) {
                    if (locationName.length() > 0) locationName.append(", ");
                    locationName.append(address.getAdminArea());
                }
                // Add country
                if (address.getCountryName() != null) {
                    if (locationName.length() > 0) locationName.append(", ");
                    locationName.append(address.getCountryName());
                }
                
                return locationName.length() > 0 ? locationName.toString() : "Unknown Location";
            }
        } catch (IOException e) {
            Log.e(TAG, "Error getting location name", e);
        }
        return "Unknown Location";
    }

    private double calculateMean(List<Double> values) {
        double sum = 0.0;
        for (Double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    private double calculateVariance(List<Double> values, double mean) {
        double sumSquaredDiff = 0.0;
        for (Double value : values) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        return sumSquaredDiff / values.size();
    }

    private double calculateMagnitude(double acceleration, double variance) {
        // Combine acceleration and variance for more accurate magnitude
        double combinedValue = (acceleration * 0.7) + (variance * 0.3);
        // Scale down to reasonable magnitude range (0-10)
        return Math.min(10.0, Math.max(0.0, Math.log10(combinedValue) * 1.5));
    }

    private double calculateConfidence(double acceleration, double variance) {
        // Calculate confidence based on both acceleration and variance
        double accelerationConfidence = (acceleration - EARTHQUAKE_THRESHOLD) / EARTHQUAKE_THRESHOLD;
        double varianceConfidence = (variance - VARIANCE_THRESHOLD) / VARIANCE_THRESHOLD;
        double combinedConfidence = (accelerationConfidence * 0.7) + (varianceConfidence * 0.3);
        return Math.min(1.0, Math.max(0.0, combinedConfidence));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isMonitoring) {
            stopLocationUpdates();
        }
        // Unregister listener in Activity when activity is paused
        if (sensorManager != null && isMonitoring) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMonitoring) {
            startLocationUpdates();
        }
        // Register listener in Activity when activity is resumed
        if (sensorManager != null && isMonitoring) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure listener is unregistered when activity is destroyed
        if (sensorManager != null && isMonitoring) {
            sensorManager.unregisterListener(this);
        }
        if (isWebSocketServiceBound) {
            unbindService(webSocketServiceConnection);
            isWebSocketServiceBound = false;
        }
    }

    private void handleEarthquakeDetection(double magnitude, double latitude, double longitude, String location) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDetectionTime < MIN_DETECTION_INTERVAL) {
            Log.d(TAG, "Skipping detection - too soon since last detection");
            return;
        }
        
        lastDetectionTime = currentTime;
        
        // Create earthquake event
        EarthquakeEvent event = new EarthquakeEvent(
            magnitude,
            latitude,
            longitude,
            0.0, // depth
            location,
            new Date(),
            calculateConfidence(magnitude, calculateVariance(accelerationWindow, calculateMean(accelerationWindow)))
        );
        
        Log.d(TAG, "Earthquake detected! Magnitude: " + magnitude + ", Location: " + location);
        
        // Save to local database
        earthquakeViewModel.insert(event);
        Log.d(TAG, "Saved earthquake to local database");
        
        // Send to backend
        new Thread(() -> {
            Log.d(TAG, "Attempting to send earthquake data to backend...");
            boolean success = apiService.sendEarthquakeEvent(event);
            if (!success) {
                Log.e(TAG, "Failed to send earthquake data to server");
                runOnUiThread(() -> Toast.makeText(MainActivity.this, 
                    "Failed to send earthquake data to server", Toast.LENGTH_SHORT).show());
            } else {
                Log.d(TAG, "Successfully sent earthquake data to server");
            }
        }).start();
        
        // Show notification
        notificationManager.showEarthquakeNotification(event);
        Log.d(TAG, "Earthquake notification shown");
    }

    private void startWebSocketService() {
        Log.d(TAG, "Starting WebSocket service");
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        startService(serviceIntent);
        bindService(serviceIntent, webSocketServiceConnection, Context.BIND_AUTO_CREATE);
    }
} 