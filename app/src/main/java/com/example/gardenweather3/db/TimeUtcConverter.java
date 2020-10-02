package com.example.gardenweather3.db;

import androidx.room.TypeConverter;

public class TimeUtcConverter {
    @TypeConverter
    public long timeUtcToLong (int time) {
        return time == 0 ? 0 : (long) time * 1000;
    }
}
