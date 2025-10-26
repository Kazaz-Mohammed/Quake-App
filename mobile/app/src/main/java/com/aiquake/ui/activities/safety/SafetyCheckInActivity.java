package com.aiquake.ui.activities.safety;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.aiquake.R;
import com.aiquake.models.EmergencyContact;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SafetyCheckInActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private static final String PREFS_NAME = "EmergencyContacts";
    private static final String CONTACTS_KEY = "contacts";
    private static final String WHATSAPP_PACKAGE = "com.whatsapp";

    private FusedLocationProviderClient fusedLocationClient;
    private MaterialButton checkInButton;
    private MaterialButton shareLocationButton;
    private TextInputEditText statusInput;
    private List<EmergencyContact> emergencyContacts;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_check_in);

        // Initialize SharedPreferences and Gson
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Safety Check-in");

        // Initialize views
        checkInButton = findViewById(R.id.check_in_button);
        shareLocationButton = findViewById(R.id.share_location_button);
        statusInput = findViewById(R.id.status_input);

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Load saved contacts
        loadContacts();

        // Setup click listeners
        setupClickListeners();

        // Request necessary permissions
        requestPermissions();
    }

    private void loadContacts() {
        String contactsJson = sharedPreferences.getString(CONTACTS_KEY, null);
        if (contactsJson != null) {
            Type type = new TypeToken<ArrayList<EmergencyContact>>(){}.getType();
            emergencyContacts = gson.fromJson(contactsJson, type);
        } else {
            emergencyContacts = new ArrayList<>();
        }
    }

    private void setupClickListeners() {
        checkInButton.setOnClickListener(v -> {
            String status = statusInput.getText().toString().trim();
            if (status.isEmpty()) {
                statusInput.setError("Please enter your status");
                return;
            }
            performCheckIn(status);
        });

        shareLocationButton.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                shareLocation();
            } else {
                requestLocationPermission();
            }
        });
    }

    private void requestPermissions() {
        // Request location permission
        if (!checkLocationPermission()) {
            requestLocationPermission();
        }
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

    private void performCheckIn(String status) {
        if (emergencyContacts == null || emergencyContacts.isEmpty()) {
            Toast.makeText(this, "Please add emergency contacts first", Toast.LENGTH_LONG).show();
            return;
        }

        // Send status update to all emergency contacts
        for (EmergencyContact contact : emergencyContacts) {
            sendWhatsAppMessage(contact.getPhoneNumber(), 
                "Safety Check-in: " + status + "\nI am safe and at my current location.");
        }

        Toast.makeText(this, "Check-in successful", Toast.LENGTH_SHORT).show();
    }

    private void shareLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            String locationMessage = String.format(
                                "My current location: https://maps.google.com/?q=%f,%f",
                                location.getLatitude(),
                                location.getLongitude()
                            );
                            
                            // Send location to all emergency contacts
                            for (EmergencyContact contact : emergencyContacts) {
                                sendWhatsAppMessage(contact.getPhoneNumber(), locationMessage);
                            }
                            
                            Toast.makeText(this, "Location shared", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendWhatsAppMessage(String phoneNumber, String message) {
        try {
            // Format phone number (remove any spaces, dashes, or special characters)
            String formattedNumber = phoneNumber.replaceAll("[^0-9+]", "");
            
            // Remove the '+' if it exists at the start
            if (formattedNumber.startsWith("+")) {
                formattedNumber = formattedNumber.substring(1);
            }

            // First try to open WhatsApp directly
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage(WHATSAPP_PACKAGE);
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, message);
            whatsappIntent.putExtra("jid", formattedNumber + "@s.whatsapp.net");
            
            try {
                startActivity(whatsappIntent);
            } catch (Exception e) {
                // If WhatsApp app fails, try the web version
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                String url = "https://wa.me/" + formattedNumber + "?text=" + Uri.encode(message);
                browserIntent.setData(Uri.parse(url));
                startActivity(browserIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error sending message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission is required to share location",
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
} 