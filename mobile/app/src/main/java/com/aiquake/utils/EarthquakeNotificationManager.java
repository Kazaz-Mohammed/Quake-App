package com.aiquake.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aiquake.R;
import com.aiquake.MainActivity;
import com.aiquake.models.EarthquakeEvent;

public class EarthquakeNotificationManager {
    private static final String CHANNEL_ID = "earthquake_channel";
    private static final int NOTIFICATION_ID = 1;
    private final Context context;

    public EarthquakeNotificationManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Earthquake Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            
            // Configure the channel
            channel.setDescription("Notifications for detected earthquakes");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            
            // Enable sound
            channel.enableLights(true);
            channel.setShowBadge(true);
            
            // Set default sound
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
            channel.setSound(defaultSoundUri, audioAttributes);

            // Create the channel
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showEarthquakeNotification(EarthquakeEvent event) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Earthquake Detected!")
            .setContentText(String.format("Magnitude %.1f at %s", event.getMagnitude(), event.getLocation()))
            .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(String.format("Magnitude %.1f earthquake detected at %s\nDepth: %.1f km\nConfidence: %.0f%%",
                    event.getMagnitude(),
                    event.getLocation(),
                    event.getDepth(),
                    event.getConfidence() * 100)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        // Set notification sound based on magnitude
        NotificationSoundManager.setNotificationSound(builder, event.getMagnitude());

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
} 