package com.example.gardenweather3;

import com.example.gardenweather3.modelCurrentWeatherData.CurrentWeatherData;

public class TempData {
    private String tempStr;
    private CurrentWeatherData weatherData;

    private TempData(){}

    private static class TempDataHolder {
        private static final TempData instance = new TempData();
    }
    public static TempData getInstance() {
        return TempDataHolder.instance;
    }

    public String getTempStr() {
        return tempStr;
    }

    public void setTempStr(String tempStr) {
        this.tempStr = tempStr;
    }

    public CurrentWeatherData getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(CurrentWeatherData weatherData) {
        this.weatherData = weatherData;
    }
}
