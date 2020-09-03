package com.example.gardenweather3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gardenweather3.modelCurrentWeatherData.CurrentWeatherData;
import com.example.gardenweather3.modelOneCallApi.WeatherDataOneCallApi;


public class ViewModelData extends ViewModel {
    private final MutableLiveData<String> city = new MutableLiveData<>();
    private final MutableLiveData<WeatherDataOneCallApi> dataOneCallApi = new MutableLiveData<>();
    private final MutableLiveData<CurrentWeatherData> dataCurrent = new MutableLiveData<>();

    public void setCity(String city){
        this.city.setValue(city);
    }

    public LiveData<String> getCity(){
        return city;
    }

    public void setDataOneCallApi(WeatherDataOneCallApi weatherData){
        this.dataOneCallApi.setValue(weatherData);
    }

    public LiveData<WeatherDataOneCallApi> getDataOneCallApi() {
        return dataOneCallApi;
    }

    public void setDataCurrent(CurrentWeatherData weatherData){
        this.dataCurrent.setValue(weatherData);
    }

    public LiveData<CurrentWeatherData> getDataCurrent() {
        return dataCurrent;
    }

}
