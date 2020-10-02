package com.example.gardenweather3.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RelationCityDaily {
    @Embedded
    public TableCity tableCity;

    @Relation(parentColumn = "city_name", entityColumn = "city_name_fk", entity = TableDaily.class)
    public List<TableDaily> dailies;
}
