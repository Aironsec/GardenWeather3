package com.example.gardenweather3;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class CityWithHistory {
    @Embedded
    public TableCity tableCity;

    @Relation(parentColumn = "city_id", entityColumn = "city_id", entity = TableHistory.class)
    public List<TableHistory> histories;
}
