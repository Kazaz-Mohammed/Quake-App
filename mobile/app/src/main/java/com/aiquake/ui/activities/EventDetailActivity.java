package com.aiquake.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.aiquake.R;
import com.aiquake.models.EarthquakeEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private EarthquakeEvent event;
    private static final float[] MAGNITUDE_COLORS = {
        BitmapDescriptorFactory.HUE_GREEN,  // 0-2
        BitmapDescriptorFactory.HUE_YELLOW, // 2-4
        BitmapDescriptorFactory.HUE_ORANGE, // 4-6
        BitmapDescriptorFactory.HUE_RED     // 6+
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Earthquake Details");

        // Get event data from intent
        event = (EarthquakeEvent) getIntent().getSerializableExtra("event");
        if (event == null) {
            finish();
            return;
        }

        // Set up text views
        setupTextViews();

        // Set up map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupTextViews() {
        TextView magnitudeText = findViewById(R.id.magnitude_text);
        TextView timeText = findViewById(R.id.time_text);
        TextView locationText = findViewById(R.id.location_text);
        TextView coordinatesText = findViewById(R.id.coordinates_text);
//        TextView depthText = findViewById(R.id.depth_text);
//        TextView confidenceText = findViewById(R.id.confidence_text);

        magnitudeText.setText(String.format(Locale.getDefault(), "%.1f", event.getMagnitude()));
        timeText.setText(new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
                .format(event.getTimestamp()));
        locationText.setText(event.getLocation());
        
        // Format coordinates with N/S and E/W indicators
        String lat = String.format(Locale.getDefault(), "%.4f°%s", 
            Math.abs(event.getLatitude()), 
            event.getLatitude() >= 0 ? "N" : "S");
        String lon = String.format(Locale.getDefault(), "%.4f°%s", 
            Math.abs(event.getLongitude()), 
            event.getLongitude() >= 0 ? "E" : "W");
        coordinatesText.setText(String.format("%s, %s", lat, lon));
        
//        depthText.setText(String.format(Locale.getDefault(), "%.1f km", event.getDepth()));
//        confidenceText.setText(String.format(Locale.getDefault(), "%.1f%%", event.getConfidence() * 100));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());

        // Enable map controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Add marker with custom color based on magnitude
        float markerColor = getMarkerColor(event.getMagnitude());
        mMap.addMarker(new MarkerOptions()
                .position(eventLocation)
                .title(String.format(Locale.getDefault(), "Magnitude %.1f", event.getMagnitude()))
                .snippet(event.getLocation())
                .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

        // Add circle to show affected area (radius based on magnitude)
        double radius = calculateAffectedRadius(event.getMagnitude());
        mMap.addCircle(new CircleOptions()
                .center(eventLocation)
                .radius(radius)
                .strokeColor(Color.RED)
                .strokeWidth(2)
                .fillColor(Color.argb(30, 255, 0, 0)));

        // Move camera to event location with appropriate zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, getZoomLevel(radius)));
    }

    private float getMarkerColor(double magnitude) {
        if (magnitude < 2) return MAGNITUDE_COLORS[0];
        if (magnitude < 4) return MAGNITUDE_COLORS[1];
        if (magnitude < 6) return MAGNITUDE_COLORS[2];
        return MAGNITUDE_COLORS[3];
    }

    private double calculateAffectedRadius(double magnitude) {
        // Simple formula: radius in meters = 10^(magnitude/2)
        return Math.pow(10, magnitude/2) * 1000; // Convert to meters
    }

    private float getZoomLevel(double radius) {
        // Adjust zoom level based on the affected radius
        if (radius < 10000) return 12;      // Local
        if (radius < 50000) return 10;      // Regional
        if (radius < 200000) return 8;      // National
        return 6;                           // Continental
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 