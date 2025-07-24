package com.aiquake.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.aiquake.models.EarthquakeEvent;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EarthquakeRepository {
    private final EarthquakeDao earthquakeDao;
    private final LiveData<List<EarthquakeEvent>> allEarthquakeEvents;
    private final ExecutorService executorService;

    public EarthquakeRepository(Application application) {
        EarthquakeDatabase database = EarthquakeDatabase.getInstance(application);
        earthquakeDao = database.earthquakeDao();
        allEarthquakeEvents = earthquakeDao.getAllEarthquakeEvents();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<EarthquakeEvent>> getAllEarthquakeEvents() {
        return allEarthquakeEvents;
    }

    public void insert(EarthquakeEvent earthquakeEvent) {
        executorService.execute(() -> earthquakeDao.insert(earthquakeEvent));
    }

    public void update(EarthquakeEvent earthquakeEvent) {
        executorService.execute(() -> earthquakeDao.update(earthquakeEvent));
    }

    public void delete(EarthquakeEvent earthquakeEvent) {
        executorService.execute(() -> earthquakeDao.delete(earthquakeEvent));
    }
} 