package com.aiquake.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.aiquake.MainActivity;
import com.aiquake.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class QuakeAppFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Send token to your server
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getTitle(),
                           remoteMessage.getNotification().getBody(),
                           remoteMessage.getData());
        }
    }

    private void sendRegistrationToServer(String token) {
        // Get current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                // Send token and location to server
                new Thread(() -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        JSONObject json = new JSONObject();
                        json.put("deviceToken", token);
                        json.put("latitude", lastLocation.getLatitude());
                        json.put("longitude", lastLocation.getLongitude());

                        RequestBody body = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            json.toString()
                        );

                        Request request = new Request.Builder()
                            .url("http://192.168.1.7:3001/api/devices")
                            .post(body)
                            .build();

                        Response response = client.newCall(request).execute();
                        Log.d(TAG, "Server response: " + response.body().string());
                    } catch (Exception e) {
                        Log.e(TAG, "Error sending token to server", e);
                    }
                }).start();
            }
        }
    }

    private void sendNotification(String title, String messageBody, Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Add earthquake data to intent
        if (data != null) {
            intent.putExtra("magnitude", data.get("magnitude"));
            intent.putExtra("latitude", data.get("latitude"));
            intent.putExtra("longitude", data.get("longitude"));
            intent.putExtra("location", data.get("location"));
            intent.putExtra("timestamp", data.get("timestamp"));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "earthquake_alerts";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.earthquake)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                "Earthquake Alerts",
                NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
} 