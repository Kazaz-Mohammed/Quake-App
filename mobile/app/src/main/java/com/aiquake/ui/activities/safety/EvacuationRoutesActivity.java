package com.aiquake.ui.activities.safety;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.aiquake.R;
import java.util.ArrayList;
import java.util.List;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EvacuationRoutesActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private FloatingActionButton myLocationFab;
    private List<LatLng> evacuationPoints;
    private LatLng currentLocation;
    private GeoApiContext geoApiContext;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evacuation_routes);

        // Initialize GeoApiContext with your API key
        geoApiContext = new GeoApiContext.Builder()
            .apiKey(getString(R.string.google_maps_key))
            .build();

        // Initialize executor service for background tasks
        executorService = Executors.newSingleThreadExecutor();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Evacuation Routes");

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize FAB
        myLocationFab = findViewById(R.id.my_location_fab);
        myLocationFab.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                getCurrentLocation();
            } else {
                requestLocationPermission();
            }
        });

        // Initialize evacuation points
        initializeEvacuationPoints();

        // Request location permission
        if (!checkLocationPermission()) {
            requestLocationPermission();
        }
    }

    private void initializeEvacuationPoints() {
        evacuationPoints = new ArrayList<>();
        // Marrakech evacuation points (major safe zones)
        evacuationPoints.add(new LatLng(31.6295, -7.9811)); // Jardin Majorelle
        evacuationPoints.add(new LatLng(31.6245, -7.9891)); // Place Jemaa el-Fna
        evacuationPoints.add(new LatLng(31.6335, -7.9911)); // Menara Gardens
        evacuationPoints.add(new LatLng(31.6275, -7.9831)); // Koutoubia Mosque
        evacuationPoints.add(new LatLng(31.6315, -7.9851)); // El Badi Palace
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Enable location button if permission is granted
        if (checkLocationPermission()) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }

        // Add evacuation points to the map
        addEvacuationPoints();

        // Enable traffic layer
        mMap.setTrafficEnabled(true);

        // Set map type to normal with detailed streets
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        // Enable detailed street view
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);

        // Set initial camera position to Marrakech
        LatLng marrakech = new LatLng(31.6295, -7.9811);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marrakech, 14));

        // Add click listener for evacuation points
        mMap.setOnMarkerClickListener(marker -> {
            if (currentLocation != null) {
                drawEvacuationRoute(currentLocation, marker.getPosition());
            }
            return true;
        });
    }

    private void addEvacuationPoints() {
        for (LatLng point : evacuationPoints) {
            mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("Safe Zone")
                .snippet("Click to get directions")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }

    private void drawEvacuationRoute(LatLng start, LatLng end) {
        executorService.execute(() -> {
            try {
                // Request directions
                DirectionsResult result = DirectionsApi.newRequest(geoApiContext)
                    .origin(new com.google.maps.model.LatLng(start.latitude, start.longitude))
                    .destination(new com.google.maps.model.LatLng(end.latitude, end.longitude))
                    .mode(TravelMode.DRIVING)
                    .await();

                if (result.routes.length > 0) {
                    // Get the first route
                    com.google.maps.model.DirectionsRoute route = result.routes[0];
                    
                    // Create a list of points for the polyline
                    List<LatLng> path = new ArrayList<>();
                    for (com.google.maps.model.LatLng point : route.overviewPolyline.decodePath()) {
                        path.add(new LatLng(point.lat, point.lng));
                    }

                    // Draw the route on the map
                    runOnUiThread(() -> {
                        // Create polyline options
                        PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(path)
                            .color(Color.RED)
                            .width(8);

                        // Add the polyline to the map
                        mMap.addPolyline(polylineOptions);

                        // Create bounds that include the entire route
                        com.google.android.gms.maps.model.LatLngBounds.Builder boundsBuilder = 
                            new com.google.android.gms.maps.model.LatLngBounds.Builder();
                        for (LatLng point : path) {
                            boundsBuilder.include(point);
                        }

                        // Move camera to show the entire route with padding
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                            boundsBuilder.build(), 100));
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error getting directions: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_LOCATION);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null && mMap != null) {
                            currentLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                            );
                            // Zoom in closer to show more street details
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
                            mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("Your Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        } else {
                            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            } else {
                Toast.makeText(this, "Location permission is required to show your location",
                    Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
} 