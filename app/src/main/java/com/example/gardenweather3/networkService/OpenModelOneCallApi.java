package com.example.gardenweather3.networkService;

import com.example.gardenweather3.modelOneCallApi.WeatherDataOneCallApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenModelOneCallApi {
    @GET("data/2.5/onecall")
    Call<WeatherDataOneCallApi> loadWeather(@Query("lat") double lat,
                                            @Query("lon") double lon,
                                            @Query("units") String metric,
                                            @Query("lang") String lang,
                                            @Query("appid") String keyApi);
}
