package com.example.gardenweather3.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = TableCity.class,
        parentColumns = "city_id",
        childColumns = "city_id", onDelete = CASCADE, onUpdate = CASCADE))
public class TableHistory {

    @PrimaryKey
    public long date;

    @ColumnInfo(name = "city_id")
    public long cityId;

    public int temp;

}
