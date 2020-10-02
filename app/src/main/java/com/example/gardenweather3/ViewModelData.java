package com.example.gardenweather3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gardenweather3.db.TableDaily;


public class ViewModelData extends ViewModel {
    private final MutableLiveData<String> city = new MutableLiveData<>();
    private final MutableLiveData<TableDaily> cityData = new MutableLiveData<>();

    public void setCityName(String city){
        this.city.setValue(city);
    }

    public LiveData<String> getCityName(){
        return city;
    }

    public void setCityData(TableDaily cityData){
        this.cityData.setValue(cityData);
    }

    public LiveData<TableDaily> getCityData(){
        return cityData;
    }

}
