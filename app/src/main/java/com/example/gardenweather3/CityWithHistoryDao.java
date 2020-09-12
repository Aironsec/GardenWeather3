package com.example.gardenweather3;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class CityWithHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCity(TableCity tableCity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertHistory(TableHistory tableHistory);

    @Transaction
    public void insertCityWithHistory(TableCity tableCity, TableHistory tableHistory) {
        insertCity(tableCity);
        insertHistory(tableHistory);
    }

    @Transaction
    @Query("SELECT * FROM TableCity")
    public abstract Flowable<List<CityWithHistory>> loadCityWithHistory();
}
