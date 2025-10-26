package com.aiquake.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aiquake.data.entity.DetectionSettings;

@Dao
public interface DetectionSettingsDao {
    @Insert
    void insert(DetectionSettings settings);

    @Update
    void update(DetectionSettings settings);

    @Query("SELECT * FROM detection_settings WHERE id = 1")
    LiveData<DetectionSettings> getSettings();

    @Query("SELECT * FROM detection_settings WHERE id = 1")
    DetectionSettings getSettingsSync();
} 