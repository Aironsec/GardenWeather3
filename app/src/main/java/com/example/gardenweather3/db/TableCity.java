package com.example.gardenweather3.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class TableCity {

    @PrimaryKey
    @ColumnInfo(name = "city_name", index = true)
    @NonNull
    public String cityName;

    @ColumnInfo(index = true)
    public int dt;

    @ColumnInfo(name = "temp_current")
    public int tempCurrent;

    @ColumnInfo(name = "id_condition")
    public int idCondition;

    public String icon;

    public double lat;

    public double lon;

    @ColumnInfo(name = "time_update")
    public long timeUpdate;

}
