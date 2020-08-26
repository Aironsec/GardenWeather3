package com.example.gardenweather3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gardenweather3.model.WeatherData;

public class ViewModelData extends ViewModel {
    private final MutableLiveData<String> city = new MutableLiveData<>();
    private final MutableLiveData<WeatherData> data = new MutableLiveData<>();

    public void setCity(String city){
        this.city.setValue(city);
    }

    public LiveData<String> getCity(){
        return city;
    }

    public void setData(WeatherData weatherData){
        this.data.setValue(weatherData);
    }

    public LiveData<WeatherData> getData() {
        return data;
    }


}
