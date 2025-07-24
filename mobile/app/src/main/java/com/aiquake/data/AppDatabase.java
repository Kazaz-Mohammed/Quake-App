package com.aiquake.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.aiquake.data.dao.SensorDataDao;
import com.aiquake.data.dao.EarthquakeEventDao;
import com.aiquake.data.dao.DetectionSettingsDao;
import com.aiquake.data.entity.SensorData;
import com.aiquake.data.entity.EarthquakeEvent;
import com.aiquake.data.entity.DetectionSettings;
import com.aiquake.data.util.Converters;

@Database(entities = {
    SensorData.class,
    EarthquakeEvent.class,
    DetectionSettings.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "quake_database";
    private static volatile AppDatabase INSTANCE;

    public abstract SensorDataDao sensorDataDao();
    public abstract EarthquakeEventDao earthquakeEventDao();
    public abstract DetectionSettingsDao detectionSettingsDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        DATABASE_NAME
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
} 