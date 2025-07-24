package com.aiquake.models;

import java.util.Date;

public class Earthquake {
    private String id;
    private double magnitude;
    private double latitude;
    private double longitude;
    private String location;
    private Date timestamp;
    private double depth;
    private String source;

    public Earthquake(String id, double magnitude, double latitude, double longitude, 
                     String location, Date timestamp, double depth, String source) {
        this.id = id;
        this.magnitude = magnitude;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.timestamp = timestamp;
        this.depth = depth;
        this.source = source;
    }

    // Getters
    public String getId() { return id; }
    public double getMagnitude() { return magnitude; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getLocation() { return location; }
    public Date getTimestamp() { return timestamp; }
    public double getDepth() { return depth; }
    public String getSource() { return source; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setMagnitude(double magnitude) { this.magnitude = magnitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setLocation(String location) { this.location = location; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public void setDepth(double depth) { this.depth = depth; }
    public void setSource(String source) { this.source = source; }
} 