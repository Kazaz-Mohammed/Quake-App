package com.aiquake.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.aiquake.models.EarthquakeEvent;

@Database(entities = {EarthquakeEvent.class}, version = 1, exportSchema = false)
public abstract class EarthquakeDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "earthquake_database";
    private static EarthquakeDatabase instance;

    public abstract EarthquakeDao earthquakeDao();

    public static synchronized EarthquakeDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                EarthquakeDatabase.class,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
} 