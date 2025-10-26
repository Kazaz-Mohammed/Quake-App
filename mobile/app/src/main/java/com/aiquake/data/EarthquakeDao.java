package com.aiquake.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.aiquake.models.EarthquakeEvent;
import java.util.List;

@Dao
public interface EarthquakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EarthquakeEvent earthquakeEvent);

    @Update
    void update(EarthquakeEvent earthquakeEvent);

    @Delete
    void delete(EarthquakeEvent earthquakeEvent);

    @Query("SELECT * FROM earthquake_events ORDER BY timestamp DESC")
    LiveData<List<EarthquakeEvent>> getAllEarthquakeEvents();
} 