package com.aiquake.ui.activities.safety;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import com.aiquake.R;

public class SafetyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Safety Features");

        // Initialize card views
        CardView emergencyContactsCard = findViewById(R.id.emergency_contacts_card);
        CardView safetyCheckInCard = findViewById(R.id.safety_check_in_card);
        CardView evacuationRoutesCard = findViewById(R.id.evacuation_routes_card);
        CardView firstAidCard = findViewById(R.id.first_aid_card);

        // Set click listeners
        emergencyContactsCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmergencyContactsActivity.class);
            startActivity(intent);
        });

        safetyCheckInCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, SafetyCheckInActivity.class);
            startActivity(intent);
        });

        evacuationRoutesCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, EvacuationRoutesActivity.class);
            startActivity(intent);
        });

        firstAidCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, FirstAidActivity.class);
            startActivity(intent);
        });
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