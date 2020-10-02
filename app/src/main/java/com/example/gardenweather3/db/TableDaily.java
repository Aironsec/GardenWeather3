package com.example.gardenweather3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = TableCity.class, parentColumns = "city_name",
                childColumns = "city_name_fk", onDelete = CASCADE, onUpdate = CASCADE)},
        indices = {
                @Index("city_name_fk"),
        })
public class TableDaily {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public int dt;

    @ColumnInfo(name = "temp_day")
    public int tempDay;

    @ColumnInfo(name = "temp_night")
    public int tempNight;

    @ColumnInfo(name = "id_condition")
    public int idCondition;

    public String icon;

    @ColumnInfo(name = "city_name_fk")
    public String cityNameFk;
}
