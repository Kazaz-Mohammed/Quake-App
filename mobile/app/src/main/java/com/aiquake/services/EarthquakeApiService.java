package com.aiquake.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.aiquake.models.EarthquakeEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EarthquakeApiService {
    private static final String TAG = "EarthquakeApiService";
    // Update this URL to match your backend server's address
    //private static final String API_BASE_URL = "http://10.0.2.2:3000/api"; // For Android Emulator
    // Use this URL for physical devices (replace with your computer's IP address)
    private static final String API_BASE_URL = "http://192.168.1.7:3001/api"; // Updated port to 3001
    


    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private final OkHttpClient client;
    private final Gson gson;
    private final Context context;

    public EarthquakeApiService(Context context) {
        Log.d(TAG, "Initializing EarthquakeApiService...");
        this.context = context;
        
        client = new OkHttpClient.Builder()
            .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
            .build();
            
        gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .excludeFieldsWithoutExposeAnnotation()
            .create();
        Log.d(TAG, "EarthquakeApiService initialized with URL: " + API_BASE_URL);
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                Log.e(TAG, "ConnectivityManager is null");
                return false;
            }
            
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                Log.e(TAG, "No active network found");
                return false;
            }
            
            boolean isConnected = activeNetworkInfo.isConnected();
            Log.d(TAG, "Network available: " + isConnected);
            return isConnected;
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability", e);
            return false;
        }
    }

    public boolean sendEarthquakeEvent(EarthquakeEvent event) {
        Log.d(TAG, "Starting to send earthquake event...");
        
        if (!isNetworkAvailable()) {
            Log.e(TAG, "No network connection available");
            return false;
        }
        
        try {
            String json = gson.toJson(event);
            Log.d(TAG, "Converted earthquake data to JSON: " + json);
            
            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                .url(API_BASE_URL + "/earthquakes")
                .post(body)
                .build();

            Log.d(TAG, "Sending request to: " + request.url());
            
            try (Response response = client.newCall(request).execute()) {
                if (response.body() == null) {
                    Log.e(TAG, "Response body is null");
                    return false;
                }
                
                String responseBody = response.body().string();
                Log.d(TAG, "Response code: " + response.code() + ", Body: " + responseBody);
                
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to send earthquake event. Response code: " + response.code());
                    return false;
                }
                
                Log.d(TAG, "Successfully sent earthquake event to server");
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Network error while sending earthquake event", e);
                Log.e(TAG, "Error details: " + e.getMessage());
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error while sending earthquake event", e);
            Log.e(TAG, "Error details: " + e.getMessage());
            return false;
        }
    }
} 