package com.aiquake.ui.activities.safety;

public class FirstAidItem {
    private String title;
    private String description;
    private String steps;
    private String videoUrl;

    public FirstAidItem(String title, String description, String steps, String videoUrl) {
        this.title = title;
        this.description = description;
        this.steps = steps;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSteps() {
        return steps;
    }

    public String getVideoUrl() {
        return videoUrl;
    }
} 