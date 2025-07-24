package com.aiquake.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "detection_settings")
public class DetectionSettings {
    @PrimaryKey
    private int id = 1; // We'll only have one settings record

    private float sensitivityLevel;
    private int samplingRate;
    private boolean notificationEnabled;
    private long lastUpdated;

    public DetectionSettings(float sensitivityLevel, int samplingRate, boolean notificationEnabled) {
        this.sensitivityLevel = sensitivityLevel;
        this.samplingRate = samplingRate;
        this.notificationEnabled = notificationEnabled;
        this.lastUpdated = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSensitivityLevel() {
        return sensitivityLevel;
    }

    public void setSensitivityLevel(float sensitivityLevel) {
        this.sensitivityLevel = sensitivityLevel;
        this.lastUpdated = System.currentTimeMillis();
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
        this.lastUpdated = System.currentTimeMillis();
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
        this.lastUpdated = System.currentTimeMillis();
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
} 