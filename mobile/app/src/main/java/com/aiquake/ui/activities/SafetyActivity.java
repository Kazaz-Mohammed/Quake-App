package com.aiquake.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import android.view.View;

import com.aiquake.R;
import com.aiquake.ui.activities.safety.EmergencyContactsActivity;
import com.aiquake.ui.activities.safety.SafetyCheckInActivity;
import com.aiquake.ui.activities.safety.EvacuationRoutesActivity;
import com.aiquake.ui.activities.safety.FirstAidActivity;

public class SafetyActivity extends AppCompatActivity {
    private CardView emergencyContactsCard;
    private CardView safetyCheckInCard;
    private CardView evacuationRoutesCard;
    private CardView firstAidCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Safety Features");

        // Initialize views
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emergencyContactsCard = findViewById(R.id.emergency_contacts_card);
        safetyCheckInCard = findViewById(R.id.safety_check_in_card);
        evacuationRoutesCard = findViewById(R.id.evacuation_routes_card);
        firstAidCard = findViewById(R.id.first_aid_card);
    }

    private void setupClickListeners() {
        emergencyContactsCard.setOnClickListener(v -> 
            startActivity(new Intent(SafetyActivity.this, EmergencyContactsActivity.class)));

        safetyCheckInCard.setOnClickListener(v -> 
            startActivity(new Intent(SafetyActivity.this, SafetyCheckInActivity.class)));

        evacuationRoutesCard.setOnClickListener(v -> 
            startActivity(new Intent(SafetyActivity.this, EvacuationRoutesActivity.class)));

        firstAidCard.setOnClickListener(v -> 
            startActivity(new Intent(SafetyActivity.this, FirstAidActivity.class)));
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