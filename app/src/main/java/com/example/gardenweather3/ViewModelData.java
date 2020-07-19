package com.example.gardenweather3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelData extends ViewModel {
    private final MutableLiveData<String> city = new MutableLiveData<>();

    public void setCity(String city){
        this.city.setValue(city);
    }

    public LiveData<String> getCity(){
        return city;
    }
}
