package com.aiquake.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.aiquake.MainActivity;
import com.aiquake.R;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class WebSocketService extends Service {
    private static final String TAG = "WebSocketService";
    private static final String WS_URL = "ws://192.168.1.7:3001";



    private static final String CHANNEL_ID = "earthquake_alerts";
    private static final int SERVER_CHECK_TIMEOUT = 5000; // 5 seconds
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final int RECONNECT_DELAY_SECONDS = 5;
    
    private WebSocket webSocket;
    private Location lastLocation;
    private final IBinder binder = new LocalBinder();
    private OkHttpClient client;
    private int reconnectAttempts = 0;
    private boolean isConnecting = false;

    public class LocalBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        // Move network operations to a background thread
        new Thread(() -> {
            initializeWebSocket();
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Earthquake Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for nearby earthquakes");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            channel.enableLights(true);
            channel.setLightColor(android.graphics.Color.RED);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isServerAvailable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("24.20.0.244", 3001), SERVER_CHECK_TIMEOUT);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Server is not available: " + e.getMessage());
            return false;
        }
    }

    private void initializeWebSocket() {
        if (isConnecting) {
            Log.d(TAG, "Connection attempt already in progress");
            return;
        }

        Log.d(TAG, "Initializing WebSocket connection to " + WS_URL);
        
        if (!isServerAvailable()) {
            Log.e(TAG, "Server is not available, will retry later");
            scheduleReconnect();
            return;
        }

        Log.d(TAG, "Server is available, proceeding with WebSocket connection");
        isConnecting = true;
        
        // Create OkHttpClient on the background thread
        client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

        Request request = new Request.Builder()
            .url(WS_URL)
            .build();

        Log.d(TAG, "Creating WebSocket connection...");
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket connection opened successfully");
                isConnecting = false;
                reconnectAttempts = 0;
                // Send initial location if available
                if (lastLocation != null) {
                    Log.d(TAG, "Sending initial location update");
                    sendLocationUpdate();
                } else {
                    Log.d(TAG, "No initial location available");
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Message received: " + text);
                try {
                    JSONObject json = new JSONObject(text);
                    if (json.getString("type").equals("earthquake")) {
                        Log.d(TAG, "Processing earthquake notification");
                        handleEarthquakeNotification(json.getJSONObject("data"));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing message: " + e.getMessage());
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d(TAG, "Binary message received (ignored)");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket closing: " + reason + " (code: " + code + ")");
                webSocket.close(1000, null);
                isConnecting = false;
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket error: " + t.getMessage());
                if (response != null) {
                    Log.e(TAG, "Response code: " + response.code());
                    Log.e(TAG, "Response message: " + response.message());
                }
                isConnecting = false;
                scheduleReconnect();
            }
        });
    }

    private void scheduleReconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Max reconnection attempts reached");
            return;
        }

        reconnectAttempts++;
        Log.d(TAG, "Scheduling reconnect attempt " + reconnectAttempts + " of " + MAX_RECONNECT_ATTEMPTS);
        
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(RECONNECT_DELAY_SECONDS);
                initializeWebSocket();
            } catch (InterruptedException e) {
                Log.e(TAG, "Reconnection interrupted", e);
            }
        }).start();
    }

    public void updateLocation(Location location) {
        this.lastLocation = location;
        // Move location update to background thread
        new Thread(() -> {
            sendLocationUpdate();
        }).start();
    }

    private void sendLocationUpdate() {
        if (webSocket == null) {
            Log.e(TAG, "Cannot send location update: WebSocket is null");
            return;
        }
        if (lastLocation == null) {
            Log.e(TAG, "Cannot send location update: Location is null");
            return;
        }

        try {
            JSONObject locationData = new JSONObject();
            locationData.put("type", "location");
            locationData.put("latitude", lastLocation.getLatitude());
            locationData.put("longitude", lastLocation.getLongitude());
            String message = locationData.toString();
            Log.d(TAG, "Sending location update: " + message);
            webSocket.send(message);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating location update JSON: " + e.getMessage());
        }
    }

    private void handleEarthquakeNotification(JSONObject data) throws JSONException {
        Log.d(TAG, "Handling earthquake notification with data: " + data.toString());
        
        String title = "Earthquake Alert!";
        String message = String.format("Magnitude %.1f earthquake detected %dkm from your location",
            data.getDouble("magnitude"),
            data.getInt("distance"));

        // Create intent for notification
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("magnitude", data.getDouble("magnitude"));
        intent.putExtra("latitude", data.getDouble("latitude"));
        intent.putExtra("longitude", data.getDouble("longitude"));
        intent.putExtra("location", data.getString("location"));
        intent.putExtra("timestamp", data.getString("timestamp"));

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.earthquake)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setVibrate(new long[]{0, 500, 200, 500})
            .setLights(android.graphics.Color.RED, 3000, 3000)
            .setContentIntent(pendingIntent);

        // Show notification
        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        try {
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, builder.build());
            Log.d(TAG, "Notification sent with ID: " + notificationId);
        } catch (SecurityException e) {
            Log.e(TAG, "Failed to show notification: " + e.getMessage());
            // Try to show a toast message as fallback
            try {
                android.widget.Toast.makeText(this, 
                    "Earthquake Alert! Magnitude " + data.getDouble("magnitude"), 
                    android.widget.Toast.LENGTH_LONG).show();
            } catch (Exception toastError) {
                Log.e(TAG, "Failed to show toast: " + toastError.getMessage());
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Service destroyed");
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }
} 