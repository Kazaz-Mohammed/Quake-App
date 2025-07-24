package com.aiquake.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aiquake.R;
import com.aiquake.models.Earthquake;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder> {
    private List<Earthquake> earthquakes;
    private SimpleDateFormat dateFormat;

    public EarthquakeAdapter(List<Earthquake> earthquakes) {
        this.earthquakes = earthquakes;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public EarthquakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_earthquake, parent, false);
        return new EarthquakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarthquakeViewHolder holder, int position) {
        Earthquake earthquake = earthquakes.get(position);
        holder.bind(earthquake);
    }

    @Override
    public int getItemCount() {
        return earthquakes.size();
    }

    public void updateEarthquakes(List<Earthquake> newEarthquakes) {
        this.earthquakes = newEarthquakes;
        notifyDataSetChanged();
    }

    class EarthquakeViewHolder extends RecyclerView.ViewHolder {
        private TextView magnitudeText;
        private TextView locationText;
        private TextView timeText;
        private TextView depthText;

        public EarthquakeViewHolder(@NonNull View itemView) {
            super(itemView);
            magnitudeText = itemView.findViewById(R.id.magnitudeText);
            locationText = itemView.findViewById(R.id.locationText);
            timeText = itemView.findViewById(R.id.timeText);
            depthText = itemView.findViewById(R.id.depthText);
        }

        public void bind(Earthquake earthquake) {
            magnitudeText.setText(String.format(Locale.getDefault(), "%.1f", earthquake.getMagnitude()));
            locationText.setText(earthquake.getLocation());
            timeText.setText(dateFormat.format(earthquake.getTimestamp()));
            depthText.setText(String.format(Locale.getDefault(), "Depth: %.1f km", earthquake.getDepth()));
        }
    }
} 