package com.aiquake.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.aiquake.data.entity.SensorData;
import java.util.Date;
import java.util.List;

@Dao
public interface SensorDataDao {
    @Insert
    void insert(SensorData sensorData);

    @Insert
    void insertAll(List<SensorData> sensorDataList);

    @Update
    void update(SensorData sensorData);

    @Delete
    void delete(SensorData sensorData);

    @Query("SELECT * FROM sensor_data ORDER BY timestamp DESC")
    LiveData<List<SensorData>> getAllSensorData();

    @Query("SELECT * FROM sensor_data WHERE isPotentialEarthquake = 1 ORDER BY timestamp DESC")
    LiveData<List<SensorData>> getPotentialEarthquakes();

    @Query("SELECT * FROM sensor_data WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    LiveData<List<SensorData>> getSensorDataBetweenDates(Date startDate, Date endDate);

    @Query("SELECT * FROM sensor_data WHERE magnitude >= :threshold ORDER BY timestamp DESC")
    LiveData<List<SensorData>> getSensorDataAboveMagnitude(double threshold);

    @Query("SELECT * FROM sensor_data WHERE isPotentialEarthquake = 1 AND timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    LiveData<List<SensorData>> getPotentialEarthquakesBetweenDates(Date startDate, Date endDate);

    @Query("DELETE FROM sensor_data WHERE timestamp < :timestamp")
    void deleteOlderThan(long timestamp);
} 