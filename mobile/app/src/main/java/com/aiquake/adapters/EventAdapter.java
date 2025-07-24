package com.aiquake.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aiquake.R;
import com.aiquake.models.EarthquakeEvent;
import com.aiquake.ui.activities.EventDetailActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<EarthquakeEvent> events;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EarthquakeEvent event);
    }

    public EventAdapter(Context context, List<EarthquakeEvent> events) {
        this.context = context;
        this.events = events != null ? events : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public List<EarthquakeEvent> getEvents() {
        return events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_earthquake_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EarthquakeEvent event = events.get(position);
        
        // Format the time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
        String timeStr = sdf.format(event.getTimestamp());

        // Set the data
        holder.magnitudeText.setText(String.format(Locale.getDefault(), "%.1f", event.getMagnitude()));
        holder.timeText.setText(timeStr);
        holder.locationText.setText(event.getLocation());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public void updateEvents(List<EarthquakeEvent> newEvents) {
        this.events = newEvents != null ? new ArrayList<>(newEvents) : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView magnitudeText;
        TextView timeText;
        TextView locationText;

        EventViewHolder(View itemView) {
            super(itemView);
            magnitudeText = itemView.findViewById(R.id.magnitude_text);
            timeText = itemView.findViewById(R.id.time_text);
            locationText = itemView.findViewById(R.id.location_text);
        }
    }
} 