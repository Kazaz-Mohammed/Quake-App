package com.aiquake.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aiquake.R;
import com.aiquake.models.Earthquake;
import com.aiquake.services.EarthquakeService;
import com.aiquake.ui.adapters.EarthquakeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeAlertsActivity extends AppCompatActivity {
    private RecyclerView earthquakeList;
    private SwipeRefreshLayout swipeRefresh;
    private EarthquakeAdapter adapter;
    private EarthquakeService earthquakeService;
    private List<Earthquake> earthquakes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_alerts);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        earthquakeList = findViewById(R.id.earthquakeList);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);

        // Initialize data
        earthquakes = new ArrayList<>();
        adapter = new EarthquakeAdapter(earthquakes);
        earthquakeService = new EarthquakeService(this);

        // Setup RecyclerView
        earthquakeList.setLayoutManager(new LinearLayoutManager(this));
        earthquakeList.setAdapter(adapter);

        // Setup SwipeRefresh
        swipeRefresh.setOnRefreshListener(this::refreshEarthquakeData);

        // Setup FAB
        fabSettings.setOnClickListener(v -> {
            // TODO: Open settings activity
        });

        // Start monitoring
        earthquakeService.startMonitoring();
    }

    private void refreshEarthquakeData() {
        // TODO: Implement refresh logic
        swipeRefresh.setRefreshing(false);
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
        // TODO: Clean up resources
    }
} 