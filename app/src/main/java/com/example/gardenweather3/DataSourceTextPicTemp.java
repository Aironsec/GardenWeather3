package com.example.gardenweather3;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.example.gardenweather3.db.TableCity;

import java.util.ArrayList;
import java.util.List;

public class DataSourceTextPicTemp {
    private final int CLOCK = 24;
    private List<ItemTextPicTemp> dataSource;
    private List<TableCity> dataSourceObject;
    private Resources resources;

    public DataSourceTextPicTemp(Resources resources) {
        dataSource = new ArrayList<>();
        dataSourceObject = new ArrayList<>();
        this.resources = resources;
    }

    public void setDataSource(List<ItemTextPicTemp> dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSourceObject(List<TableCity> tableCities) {
        this.dataSourceObject = tableCities;
    }

    public List<ItemTextPicTemp> getDataSource() {
        return dataSource;
    }

    public void setDefaultCurrentWeather() {
        if (dataSource.size() > 0) return;
            List<ItemTextPicTemp> temps = new ArrayList<>();
            temps.add(new ItemTextPicTemp(resources.getString(R.string.default_current_city)
                    , R.drawable.ic_baseline_add_location_24
                    , ""));
            dataSource = temps;
    }

    public DataSourceTextPicTemp buildLineByClock() {
        setDefaultCurrentWeather();
        return this;
    }

    public DataSourceTextPicTemp buildCityList() {
        setDefaultCurrentWeather();
        return this;
    }

    public ItemTextPicTemp getItemTextPicTemp(int pos) {
        return dataSource.get(pos);
    }

    public TableCity getItemCityObject (int pos) {
        return dataSourceObject.get(pos);
    }

    public int size() {
        return dataSource.size();
    }

    public int rnd(int max) {
        return (int) (Math.random() * max);
    }

    private int[] getPicArr() {
        @SuppressLint("Recycle")
        TypedArray pic = resources.obtainTypedArray(R.array.pictures);
        int length = pic.length();
        int[] ans = new int[CLOCK];
        for (int i = 0; i < CLOCK; i++) {
            ans[i] = pic.getResourceId(rnd(length), 0);
        }
        return ans;
    }
}
