package com.example.gardenweather3;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {
    private static App instance;
    private WeatherDB db;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(
                getApplicationContext(),
                WeatherDB.class, "Weather_data_base.db")
                .build();

    }

    public WeatherDB getDb() {
        return db;
    }
}
