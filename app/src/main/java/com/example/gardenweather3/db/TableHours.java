package com.example.gardenweather3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = TableCity.class, parentColumns = "city_name",
                childColumns = "city_name_fk", onDelete = CASCADE, onUpdate = CASCADE)},
        indices = {
                @Index("city_name_fk"),
        })

public class TableHours {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public int dt;

    @ColumnInfo(name = "temp_hour")
    public int tempHour;

    @ColumnInfo(name = "id_condition")
    public int idCondition;

    public String icon;

    @ColumnInfo(name = "city_name_fk")
    public String cityNameFk;
}
