package com.aiquake.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aiquake.data.entity.EarthquakeEvent;
import java.util.List;

@Dao
public interface EarthquakeEventDao {
    @Insert
    void insert(EarthquakeEvent earthquakeEvent);

    @Insert
    void insertAll(List<EarthquakeEvent> earthquakeEvents);

    @Update
    void update(EarthquakeEvent earthquakeEvent);

    @Delete
    void delete(EarthquakeEvent earthquakeEvent);

    @Query("SELECT * FROM earthquake_events ORDER BY timestamp DESC")
    LiveData<List<EarthquakeEvent>> getAllEarthquakeEvents();

    @Query("SELECT * FROM earthquake_events WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    LiveData<List<EarthquakeEvent>> getEarthquakeEventsInTimeRange(long startTime, long endTime);

    @Query("SELECT * FROM earthquake_events WHERE magnitude >= :minMagnitude ORDER BY timestamp DESC")
    LiveData<List<EarthquakeEvent>> getEarthquakeEventsByMagnitude(float minMagnitude);

    @Query("SELECT * FROM earthquake_events WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLon AND :maxLon ORDER BY timestamp DESC")
    LiveData<List<EarthquakeEvent>> getEarthquakeEventsInRegion(double minLat, double maxLat, double minLon, double maxLon);
} 