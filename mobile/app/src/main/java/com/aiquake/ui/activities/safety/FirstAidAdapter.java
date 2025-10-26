package com.aiquake.ui.activities.safety;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aiquake.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import java.util.List;

public class FirstAidAdapter extends RecyclerView.Adapter<FirstAidAdapter.ViewHolder> {
    private List<FirstAidItem> items;

    public FirstAidAdapter(List<FirstAidItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_first_aid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirstAidItem item = items.get(position);
        holder.titleText.setText(item.getTitle());
        holder.descriptionText.setText(item.getDescription());
        
        holder.readMoreButton.setOnClickListener(v -> showDetailedInfo(v, item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<FirstAidItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    private void showDetailedInfo(View view, FirstAidItem item) {
        Context context = view.getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_first_aid_detail, null);

        TextView titleText = dialogView.findViewById(R.id.dialog_title);
        TextView stepsText = dialogView.findViewById(R.id.dialog_steps);
        YouTubePlayerView youTubePlayerView = dialogView.findViewById(R.id.youtube_player);

        titleText.setText(item.getTitle());
        stepsText.setText(item.getSteps());

        // Initialize YouTube Player
        if (item.getVideoUrl() != null && !item.getVideoUrl().isEmpty()) {
            youTubePlayerView.setVisibility(View.VISIBLE);
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    // Extract video ID from URL
                    String videoId = extractVideoId(item.getVideoUrl());
                    if (videoId != null) {
                        youTubePlayer.loadVideo(videoId, 0);
                    }
                }
            });
        } else {
            youTubePlayerView.setVisibility(View.GONE);
        }

        AlertDialog dialog = builder.setView(dialogView)
                .setPositiveButton("OK", null)
                .create();

        dialog.setOnDismissListener(dialogInterface -> {
            youTubePlayerView.release();
        });

        dialog.show();
    }

    private String extractVideoId(String url) {
        if (url == null || url.isEmpty()) return null;
        
        // Handle different YouTube URL formats
        if (url.contains("youtu.be/")) {
            return url.substring(url.lastIndexOf("/") + 1);
        } else if (url.contains("youtube.com/watch?v=")) {
            return url.substring(url.indexOf("v=") + 2);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView descriptionText;
        TextView readMoreButton;

        ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.title_text);
            descriptionText = view.findViewById(R.id.description_text);
            readMoreButton = view.findViewById(R.id.read_more_button);
        }
    }
} 