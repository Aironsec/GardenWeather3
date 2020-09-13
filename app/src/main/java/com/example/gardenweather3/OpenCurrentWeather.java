package com.example.gardenweather3;

import com.example.gardenweather3.modelCurrentWeatherData.CurrentWeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenCurrentWeather {
    @GET("data/2.5/weather")
    Call<CurrentWeatherData> loadWeather(@Query("q") String city,
                                         @Query("units") String metric,
                                         @Query("lang") String lang,
                                         @Query("appid") String keyApi);
}
