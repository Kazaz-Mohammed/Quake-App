package com.aiquake.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.aiquake.R;
import com.aiquake.MainActivity;
import com.aiquake.models.Earthquake;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EarthquakeService {
    private static final String TAG = "EarthquakeService";
    private static final String USGS_API_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    private static final String CHANNEL_ID = "earthquake_alerts";
    private static final int NOTIFICATION_ID = 1;

    private Context context;
    private RequestQueue requestQueue;
    private NotificationManager notificationManager;
    private double lastMagnitude = 0.0;
    private String lastLocation = "";

    public EarthquakeService(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Earthquake Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for earthquake alerts");
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startMonitoring() {
        fetchEarthquakeData();
    }

    private void fetchEarthquakeData() {
        JsonObjectRequest request = new JsonObjectRequest(
            Request.Method.GET,
            USGS_API_URL,
            null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray features = response.getJSONArray("features");
                        if (features.length() > 0) {
                            JSONObject latestQuake = features.getJSONObject(0);
                            JSONObject properties = latestQuake.getJSONObject("properties");
                            
                            double magnitude = properties.getDouble("mag");
                            String location = properties.getString("place");
                            long time = properties.getLong("time");
                            
                            // Check if this is a new significant earthquake
                            if (magnitude >= 4.0 && 
                                (magnitude > lastMagnitude || !location.equals(lastLocation))) {
                                showEarthquakeAlert(magnitude, location, new Date(time));
                                lastMagnitude = magnitude;
                                lastLocation = location;
                            }
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing earthquake data", e);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error fetching earthquake data", error);
                }
            }
        );

        requestQueue.add(request);
    }

    private void showEarthquakeAlert(double magnitude, String location, Date timestamp) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Earthquake Alert!")
            .setContentText(String.format(Locale.getDefault(), 
                "Magnitude %.1f earthquake detected near %s", magnitude, location))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public List<Earthquake> parseEarthquakeData(JSONObject response) throws JSONException {
        List<Earthquake> earthquakes = new ArrayList<>();
        JSONArray features = response.getJSONArray("features");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = features.getJSONObject(i);
            JSONObject properties = feature.getJSONObject("properties");
            JSONObject geometry = feature.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");

            try {
                Date timestamp = dateFormat.parse(properties.getString("time"));
                Earthquake earthquake = new Earthquake(
                    properties.getString("id"),
                    properties.getDouble("mag"),
                    coordinates.getDouble(1), // latitude
                    coordinates.getDouble(0), // longitude
                    properties.getString("place"),
                    timestamp,
                    coordinates.getDouble(2), // depth
                    properties.getString("source")
                );
                earthquakes.add(earthquake);
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date", e);
            }
        }

        return earthquakes;
    }
} 