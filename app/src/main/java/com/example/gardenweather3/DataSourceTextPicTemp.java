package com.example.gardenweather3;

import android.content.res.Resources;
import android.content.res.TypedArray;

import java.util.ArrayList;
import java.util.List;

public class DataSourceTextPicTemp {
    private final int CLOCK = 24;
    private List<ItemTextPicTemp> DataSource;
    private Resources resources;

    public DataSourceTextPicTemp(Resources resources) {
        DataSource = new ArrayList<>();
        this.resources = resources;
    }

    public DataSourceTextPicTemp buildLineByClock() {
        int[] pic = getPicArr();
        for (int i = 0; i < CLOCK; i++) {
            DataSource.add(new ItemTextPicTemp(i + "", pic[i], rnd(25) + "\u00B0"));
        }
        return this;
    }

    public DataSourceTextPicTemp buildCityList() {
        int[] pic = getPicArr();
        String[] city = resources.getStringArray(R.array.city);
        for (int i = 0; i < city.length; i++) {
            DataSource.add(new ItemTextPicTemp(city[i], pic[i], rnd(30) + "\u00B0"));
        }
        return this;
    }

    public ItemTextPicTemp getItemTextPicTemp(int pos) {
        return DataSource.get(pos);
    }

    public int size() {
        return DataSource.size();
    }

    public int rnd(int max) {
        return (int) (Math.random() * max);
    }

    private int[] getPicArr() {
        TypedArray pic = resources.obtainTypedArray(R.array.pictures);
        int length = pic.length();
        int[] ans = new int[CLOCK];
        for (int i = 0; i < CLOCK; i++) {
            ans[i] = pic.getResourceId(rnd(length), 0);
        }
        return ans;
    }
}
