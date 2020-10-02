package com.example.gardenweather3.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TableCity.class, TableHours.class, TableDaily.class}, version = 1)
public abstract class WeatherDB extends RoomDatabase {
    public abstract WeatherCurrentHoursDayDao getCurrentHoursDay();

}
