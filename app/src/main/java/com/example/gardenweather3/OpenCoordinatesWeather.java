package com.example.gardenweather3;

import com.example.gardenweather3.modelCurrentWeatherData.CurrentWeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenCoordinatesWeather {
    @GET("data/2.5/weather")
    Call<CurrentWeatherData> loadWeather(@Query("lat") double lat,
                                         @Query("lon") double lon,
                                         @Query("units") String metric,
                                         @Query("lang") String lang,
                                         @Query("appid") String keyApi);
}
