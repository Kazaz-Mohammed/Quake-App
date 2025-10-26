package com.aiquake.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "sensor_data")
public class SensorData {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private double xAcceleration;
    private double yAcceleration;
    private double zAcceleration;
    private double magnitude;
    private Date timestamp;
    private boolean isPotentialEarthquake;

    public SensorData(double xAcceleration, double yAcceleration, double zAcceleration, 
                     double magnitude, Date timestamp, boolean isPotentialEarthquake) {
        this.xAcceleration = xAcceleration;
        this.yAcceleration = yAcceleration;
        this.zAcceleration = zAcceleration;
        this.magnitude = magnitude;
        this.timestamp = timestamp;
        this.isPotentialEarthquake = isPotentialEarthquake;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getXAcceleration() {
        return xAcceleration;
    }

    public void setXAcceleration(double xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public double getYAcceleration() {
        return yAcceleration;
    }

    public void setYAcceleration(double yAcceleration) {
        this.yAcceleration = yAcceleration;
    }

    public double getZAcceleration() {
        return zAcceleration;
    }

    public void setZAcceleration(double zAcceleration) {
        this.zAcceleration = zAcceleration;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPotentialEarthquake() {
        return isPotentialEarthquake;
    }

    public void setPotentialEarthquake(boolean potentialEarthquake) {
        isPotentialEarthquake = potentialEarthquake;
    }
} 