package com.example.gardenweather3;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TableCity {

    @PrimaryKey
    @ColumnInfo(name = "city_id")
    public int cityId;

    @ColumnInfo(name = "city_name", index = true)
    public String cityName;

    public Double lat;

    public Double lon;
}
