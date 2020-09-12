package com.example.gardenweather3;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(TableCity city);

    @Update
    void updateStudent(TableCity city);

    @Delete
    void deleteStudent(TableCity city);

    @Query("SELECT * FROM TableCity")
    List<TableCity> getCity();


}
