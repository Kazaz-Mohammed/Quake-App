package com.aiquake.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.aiquake.data.AppDatabase;
import com.aiquake.data.dao.SensorDataDao;
import com.aiquake.data.dao.EarthquakeEventDao;
import com.aiquake.data.dao.DetectionSettingsDao;
import com.aiquake.data.entity.SensorData;
import com.aiquake.data.entity.EarthquakeEvent;
import com.aiquake.data.entity.DetectionSettings;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EarthquakeRepository {
    private final SensorDataDao sensorDataDao;
    private final EarthquakeEventDao earthquakeEventDao;
    private final DetectionSettingsDao detectionSettingsDao;
    private final ExecutorService executorService;

    public EarthquakeRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        sensorDataDao = db.sensorDataDao();
        earthquakeEventDao = db.earthquakeEventDao();
        detectionSettingsDao = db.detectionSettingsDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Sensor Data Operations
    public void insertSensorData(SensorData sensorData) {
        executorService.execute(() -> sensorDataDao.insert(sensorData));
    }

    public LiveData<List<SensorData>> getAllSensorData() {
        return sensorDataDao.getAllSensorData();
    }

    public LiveData<List<SensorData>> getPotentialEarthquakes() {
        return sensorDataDao.getPotentialEarthquakes();
    }

    public LiveData<List<SensorData>> getSensorDataBetweenDates(Date startDate, Date endDate) {
        return sensorDataDao.getSensorDataBetweenDates(startDate, endDate);
    }

    public LiveData<List<SensorData>> getSensorDataAboveMagnitude(double threshold) {
        return sensorDataDao.getSensorDataAboveMagnitude(threshold);
    }

    public LiveData<List<SensorData>> getPotentialEarthquakesBetweenDates(Date startDate, Date endDate) {
        return sensorDataDao.getPotentialEarthquakesBetweenDates(startDate, endDate);
    }

    // Earthquake Event Operations
    public void insertEarthquakeEvent(EarthquakeEvent earthquakeEvent) {
        executorService.execute(() -> earthquakeEventDao.insert(earthquakeEvent));
    }

    public LiveData<List<EarthquakeEvent>> getAllEarthquakeEvents() {
        return earthquakeEventDao.getAllEarthquakeEvents();
    }

    public LiveData<List<EarthquakeEvent>> getEarthquakeEventsInTimeRange(long startTime, long endTime) {
        return earthquakeEventDao.getEarthquakeEventsInTimeRange(startTime, endTime);
    }

    // Settings Operations
    public LiveData<DetectionSettings> getSettings() {
        return detectionSettingsDao.getSettings();
    }

    public void updateSettings(DetectionSettings settings) {
        executorService.execute(() -> detectionSettingsDao.update(settings));
    }

    public DetectionSettings getSettingsSync() {
        return detectionSettingsDao.getSettingsSync();
    }

    // Cleanup Operations
    public void deleteOldSensorData(long timestamp) {
        executorService.execute(() -> sensorDataDao.deleteOlderThan(timestamp));
    }
} 