package com.aiquake.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationSoundManager {
    private static final double MAGNITUDE_LOW = 3.0;
    private static final double MAGNITUDE_MEDIUM = 5.0;
    private static final double MAGNITUDE_HIGH = 7.0;

    public static void setNotificationSound(NotificationCompat.Builder builder, double magnitude) {
        Uri soundUri;
        
        // Select sound based on magnitude
        if (magnitude >= MAGNITUDE_HIGH) {
            // High magnitude - use alarm sound
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            builder.setVibrate(new long[]{0, 1000, 500, 1000}); // Strong vibration for high magnitude
        } else if (magnitude >= MAGNITUDE_MEDIUM) {
            // Medium magnitude - use notification sound
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setVibrate(new long[]{0, 500, 200, 500}); // Medium vibration for medium magnitude
        } else {
            // Low magnitude - use a gentler sound and shorter vibration
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            builder.setVibrate(new long[]{0, 200}); // Short vibration for low magnitude
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT); // Lower priority for low magnitude
        }

        // Set the sound
        builder.setSound(soundUri);
    }
} 