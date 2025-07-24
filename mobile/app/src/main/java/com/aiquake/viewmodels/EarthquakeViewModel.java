package com.aiquake.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.aiquake.models.EarthquakeEvent;
import com.aiquake.data.EarthquakeRepository;
import java.util.List;

public class EarthquakeViewModel extends AndroidViewModel {
    private final EarthquakeRepository repository;
    private final LiveData<List<EarthquakeEvent>> allEarthquakeEvents;

    public EarthquakeViewModel(Application application) {
        super(application);
        repository = new EarthquakeRepository(application);
        allEarthquakeEvents = repository.getAllEarthquakeEvents();
    }

    public LiveData<List<EarthquakeEvent>> getAllEarthquakeEvents() {
        return allEarthquakeEvents;
    }

    public void insert(EarthquakeEvent earthquakeEvent) {
        repository.insert(earthquakeEvent);
    }

    public void update(EarthquakeEvent earthquakeEvent) {
        repository.update(earthquakeEvent);
    }

    public void delete(EarthquakeEvent earthquakeEvent) {
        repository.delete(earthquakeEvent);
    }
} 