package com.example.gardenweather3;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TableCity.class, TableHistory.class}, version = 1)
public abstract class WeatherDB extends RoomDatabase {
    public abstract CityWithHistoryDao getCityWithHistoryDao();

}
