package com.aiquake.utils;

import android.hardware.SensorEvent;
import java.util.LinkedList;
import java.util.Queue;

public class SignalProcessor {
    private static final int WINDOW_SIZE = 50; // Number of samples to analyze
    private static final float EARTHQUAKE_THRESHOLD = 1.5f; // Minimum magnitude to consider as potential earthquake
    private static final float NOISE_THRESHOLD = 0.1f; // Minimum magnitude to consider as significant movement
    
    private final Queue<Float> magnitudeHistory;
    private float lastMagnitude;
    private long lastUpdateTime;
    private boolean isPotentialEarthquake;

    public SignalProcessor() {
        magnitudeHistory = new LinkedList<>();
        lastMagnitude = 0f;
        lastUpdateTime = System.currentTimeMillis();
        isPotentialEarthquake = false;
    }

    /**
     * Process accelerometer data and determine if it might be an earthquake
     * @param event The sensor event containing accelerometer data
     * @return true if the movement pattern suggests an earthquake
     */
    public boolean processSensorData(SensorEvent event) {
        if (event.sensor.getType() != android.hardware.Sensor.TYPE_ACCELEROMETER) {
            return false;
        }

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Calculate magnitude of acceleration
        float magnitude = calculateMagnitude(x, y, z);
        
        // Apply low-pass filter to reduce noise
        magnitude = applyLowPassFilter(magnitude);
        
        // Update magnitude history
        updateMagnitudeHistory(magnitude);
        
        // Check for earthquake patterns
        return detectEarthquakePattern();
    }

    /**
     * Calculate the magnitude of acceleration from x, y, z components
     */
    private float calculateMagnitude(float x, float y, float z) {
        // Remove gravity component (approximately 9.8 m/s²)
        float gravity = 9.8f;
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z) - gravity;
        return Math.abs(magnitude); // We only care about the absolute value
    }

    /**
     * Apply a simple low-pass filter to reduce noise
     */
    private float applyLowPassFilter(float magnitude) {
        // Simple exponential moving average
        float alpha = 0.1f; // Smoothing factor
        lastMagnitude = alpha * magnitude + (1 - alpha) * lastMagnitude;
        return lastMagnitude;
    }

    /**
     * Update the magnitude history queue
     */
    private void updateMagnitudeHistory(float magnitude) {
        magnitudeHistory.offer(magnitude);
        if (magnitudeHistory.size() > WINDOW_SIZE) {
            magnitudeHistory.poll();
        }
    }

    /**
     * Detect earthquake patterns in the magnitude history
     */
    private boolean detectEarthquakePattern() {
        if (magnitudeHistory.size() < WINDOW_SIZE) {
            return false;
        }

        // Calculate average magnitude in the window
        float sum = 0;
        float maxMagnitude = 0;
        int significantMovements = 0;

        for (float mag : magnitudeHistory) {
            sum += mag;
            maxMagnitude = Math.max(maxMagnitude, mag);
            if (mag > NOISE_THRESHOLD) {
                significantMovements++;
            }
        }

        float averageMagnitude = sum / WINDOW_SIZE;

        // Check for earthquake characteristics:
        // 1. Sustained movement above threshold
        // 2. Significant number of movements in the window
        // 3. Maximum magnitude above earthquake threshold
        boolean isEarthquake = averageMagnitude > EARTHQUAKE_THRESHOLD &&
                             significantMovements > WINDOW_SIZE * 0.3 &&
                             maxMagnitude > EARTHQUAKE_THRESHOLD * 1.5;

        // Update earthquake state
        if (isEarthquake && !isPotentialEarthquake) {
            isPotentialEarthquake = true;
            lastUpdateTime = System.currentTimeMillis();
        } else if (!isEarthquake && isPotentialEarthquake) {
            // Reset after 5 seconds of no earthquake activity
            if (System.currentTimeMillis() - lastUpdateTime > 5000) {
                isPotentialEarthquake = false;
            }
        }

        return isPotentialEarthquake;
    }

    /**
     * Get the current magnitude value
     */
    public float getCurrentMagnitude() {
        return lastMagnitude;
    }

    /**
     * Reset the processor state
     */
    public void reset() {
        magnitudeHistory.clear();
        lastMagnitude = 0f;
        isPotentialEarthquake = false;
        lastUpdateTime = System.currentTimeMillis();
    }
} 