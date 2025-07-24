package com.aiquake.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.aiquake.data.DateConverter;
import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "earthquake_events")
@TypeConverters(DateConverter.class)
public class EarthquakeEvent implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @com.google.gson.annotations.SerializedName("id")
    @com.google.gson.annotations.Expose(serialize = false)
    private long id;

    @com.google.gson.annotations.Expose
    private double magnitude;

    @com.google.gson.annotations.Expose
    private double latitude;

    @com.google.gson.annotations.Expose
    private double longitude;

    @com.google.gson.annotations.Expose
    private double depth;

    @com.google.gson.annotations.Expose
    private String location;

    @com.google.gson.annotations.Expose
    private Date timestamp;

    @com.google.gson.annotations.Expose
    private double confidence;

    public EarthquakeEvent(double magnitude, double latitude, double longitude, 
                         double depth, String location, Date timestamp, double confidence) {
        this.magnitude = magnitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
        this.location = location;
        this.timestamp = timestamp;
        this.confidence = confidence;
    }

    // Getters
    public long getId() {
        return id;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDepth() {
        return depth;
    }

    public String getLocation() {
        return location;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public double getConfidence() {
        return confidence;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
} 