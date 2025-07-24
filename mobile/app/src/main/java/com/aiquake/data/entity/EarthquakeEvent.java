package com.aiquake.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "earthquake_events")
public class EarthquakeEvent implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private long timestamp;
    private float magnitude;
    private double latitude;
    private double longitude;
    private float depth;
    private String locationName;
    private float confidenceLevel;

    public EarthquakeEvent(long timestamp, float magnitude, double latitude, double longitude, 
                          float depth, String locationName, float confidenceLevel) {
        this.timestamp = timestamp;
        this.magnitude = magnitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
        this.locationName = locationName;
        this.confidenceLevel = confidenceLevel;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public float getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(float confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    // --- Parcelable Implementation ---

    protected EarthquakeEvent(Parcel in) {
        id = in.readLong();
        timestamp = in.readLong();
        magnitude = in.readFloat();
        latitude = in.readDouble();
        longitude = in.readDouble();
        depth = in.readFloat();
        locationName = in.readString();
        confidenceLevel = in.readFloat();
    }

    public static final Creator<EarthquakeEvent> CREATOR = new Creator<EarthquakeEvent>() {
        @Override
        public EarthquakeEvent createFromParcel(Parcel in) {
            return new EarthquakeEvent(in);
        }

        @Override
        public EarthquakeEvent[] newArray(int size) {
            return new EarthquakeEvent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(timestamp);
        dest.writeFloat(magnitude);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(depth);
        dest.writeString(locationName);
        dest.writeFloat(confidenceLevel);
    }
    // --- End Parcelable Implementation ---
} 