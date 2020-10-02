package com.example.gardenweather3.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class WeatherCurrentHoursDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCity(TableCity tableCity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertHours(List<TableHours> tableHours);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertDaily(List<TableDaily> tableDaily);

    @Transaction
    public void insertCurrentHoursDaily(TableCity tableCity, List<TableHours> tableHours, List<TableDaily> tableDaily) {
        insertCity(tableCity);
        insertHours(tableHours);
        insertDaily(tableDaily);
    }

    @Transaction
    @Query("SELECT * FROM TableCity ORDER BY city_name")
    public abstract Flowable<List<TableCity>> loadOnlyAllCity();

    @Transaction
    @Query("SELECT * FROM TableCity WHERE city_name = :cityName")
    public abstract Flowable<List<RelationCityHoursDaily>> oneCityHoursDaily(String cityName);

    @Transaction
    @Query("SELECT * FROM TableCity WHERE city_name = :cityName")
    public abstract Flowable<List<RelationCityDaily>> oneCity(String cityName);

    @Delete
    public abstract void deleteCity(TableCity tableCity);

}
