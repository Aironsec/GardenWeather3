package com.example.gardenweather3;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static final String BASE_URL = "https://api.openweathermap.org/";
    private Retrofit retrofit;

    private NetworkService() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) // Базовая часть
                // адреса
                // Конвертер, необходимый для преобразования JSON
                // в объекты
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static class NetworkServiceHolder {
        private static final NetworkService instance = new NetworkService();
    }

    public static NetworkService getInstance() {
        return NetworkServiceHolder.instance;
    }

    // Создаём объект, при помощи которого будем выполнять запросы
    public OpenNameCityWeather getWeatherCity() {
        return retrofit.create(OpenNameCityWeather.class);
    }

    public OpenCoordinatesWeather getWeatherCoordinates() {
        return retrofit.create(OpenCoordinatesWeather.class);
    }
}
