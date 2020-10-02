package com.example.gardenweather3.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.gardenweather3.BuildConfig;
import com.example.gardenweather3.R;
import com.example.gardenweather3.db.TableCity;
import com.example.gardenweather3.db.TableDaily;
import com.example.gardenweather3.db.TableHours;
import com.example.gardenweather3.db.WeatherDB;
import com.example.gardenweather3.modelOneCallApi.Daily;
import com.example.gardenweather3.modelOneCallApi.Hourly;
import com.example.gardenweather3.modelOneCallApi.WeatherDataOneCallApi;
import com.example.gardenweather3.networkService.NetworkService;
import com.google.android.libraries.places.api.Places;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static App instance;
    private WeatherDB db;
    private String currentCityName = "";
    private String lang = "ru";
    private Context context;
    private int timeSetting = 1;
    private boolean updateCity;
    private ImageView mainImage;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(
                getApplicationContext(),
                WeatherDB.class, "Weather_data_base.db")
                .build();
//        Инициализация Places
        Places.initialize(getApplicationContext(), BuildConfig.PLACES_API_KEY);
    }

    public void requestRetrofitModelOneCallApi(String cityName, double lat, double lon) {
        NetworkService.getInstance()
                .getWeatherCoordinates()
                .loadWeather(lat, lon, "metric", lang, BuildConfig.WEATHER_API_KEY)
                .enqueue(new Callback<WeatherDataOneCallApi>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherDataOneCallApi> call, @NonNull Response<WeatherDataOneCallApi> response) {
                        WeatherDataOneCallApi result = response.body();
                        if (result != null) {
                            resultToDataBase(cityName, result);
                        } else {
// TODO: 28.09.2020 диалог для повторной загрузки данных?
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherDataOneCallApi> call, Throwable t) {
                        Toast.makeText(context, "Ошибка загрузки погоды", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void requestRetrofitModelOneCallApi(double lat, double lon) {
        NetworkService.getInstance()
                .getWeatherCoordinates()
                .loadWeather(lat, lon, "metric", lang, BuildConfig.WEATHER_API_KEY)
                .enqueue(new Callback<WeatherDataOneCallApi>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherDataOneCallApi> call, @NonNull Response<WeatherDataOneCallApi> response) {
                        WeatherDataOneCallApi result = response.body();
                        if (result != null) {
                            resultToDataBase(currentCityName, result);
                        } else {
// TODO: 28.09.2020 диалог для повторной загрузки данных?
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherDataOneCallApi> call, Throwable t) {
                        Toast.makeText(context, "Ошибка загрузки погоды", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void resultToDataBase(String currentCityName, @NonNull WeatherDataOneCallApi result) {

        TableCity tableCity = new TableCity();
        tableCity.cityName = currentCityName;
        tableCity.dt = result.getCurrent().getDt();
        tableCity.lat = result.getLat();
        tableCity.lon = result.getLon();
        tableCity.tempCurrent = result.getCurrent().getTemp().intValue();
        tableCity.idCondition = result.getCurrent().getWeather().get(0).getId();
        tableCity.icon = result.getCurrent().getWeather().get(0).getIcon();
        tableCity.timeUpdate = new Date().getTime();

        List<TableDaily> tableDailyList = new ArrayList<>();
        for (Daily daily : result.getDaily()) {
            TableDaily tableDaily = new TableDaily();
            tableDaily.dt = daily.getDt();
            tableDaily.tempDay = daily.getTemp().getDay().intValue();
            tableDaily.tempNight = daily.getTemp().getNight().intValue();
            tableDaily.idCondition = daily.getWeather().get(0).getId();
            tableDaily.icon = daily.getWeather().get(0).getIcon();
            tableDaily.cityNameFk = currentCityName;
            tableDailyList.add(tableDaily);
        }

        List<TableHours> tableHoursList = new ArrayList<>();
        for (Hourly hourly : result.getHourly()) {
            TableHours tableHours = new TableHours();
            tableHours.dt = hourly.getDt();
            tableHours.tempHour = hourly.getTemp().intValue();
            tableHours.idCondition = hourly.getWeather().get(0).getId();
            tableHours.icon = hourly.getWeather().get(0).getIcon();
            tableHours.cityNameFk = currentCityName;
            tableHoursList.add(tableHours);
        }

        new Thread(() -> db.getCurrentHoursDay()
                .insertCurrentHoursDaily(tableCity, tableHoursList, tableDailyList))
                .start();
    }

    public WeatherDB getDb() {
        return db;
    }

    public void setMainImage(ImageView mainImage) {
        this.mainImage = mainImage;
    }

    public String getCurrentCityName() {
        return currentCityName;
    }

    public void setCurrentCityName(String currentCityName) {
        this.currentCityName = currentCityName;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int loadPic(@NonNull String icon) {
        int pic = 0;
        switch (icon) {
            case ("01d"):
                pic = R.drawable.ic_01d;
                break;
            case ("01n"):
                pic = R.drawable.ic_01n;
                break;
            case ("02d"):
                pic = R.drawable.ic_02d;
                break;
            case ("02n"):
                pic = R.drawable.ic_02n;
                break;
            case ("03d"):
                pic = R.drawable.ic_03d;
                break;
            case ("03n"):
                pic = R.drawable.ic_03n;
                break;
            case ("04d"):
                pic = R.drawable.ic_04d;
                break;
            case ("04n"):
                pic = R.drawable.ic_04n;
                break;
            case ("09d"):
                pic = R.drawable.ic_09d;
                break;
            case ("09n"):
                pic = R.drawable.ic_09n;
                break;
            case ("10d"):
                pic = R.drawable.ic_10d;
                break;
            case ("10n"):
                pic = R.drawable.ic_10n;
                break;
            case ("11d"):
                pic = R.drawable.ic_11d;
                break;
            case ("11n"):
                pic = R.drawable.ic_11n;
                break;
            case ("13d"):
                pic = R.drawable.ic_13d;
                break;
            case ("13n"):
                pic = R.drawable.ic_13n;
                break;
            case ("50d"):
                pic = R.drawable.ic_50d;
                break;
            case ("50n"):
                pic = R.drawable.ic_50n;
                break;
        }
        return pic;
    }

    public void loadImage(@NonNull String icon) {
        String url = "";
        switch (icon) {
            case ("01d"):
                url = "https://live.staticflickr.com/1840/29918199748_0519575774_w_d.jpg";
                break;
            case ("01n"):
                url = "https://live.staticflickr.com/679/21872400499_6e5245e14b_w_d.jpg";
                break;
            case ("02d"):
                url = "https://live.staticflickr.com/7302/9022056501_cc03401405_w_d.jpg";
                break;
            case ("02n"):
                url = "https://live.staticflickr.com/3949/15713356025_d022f59287_w_d.jpg";
                break;
            case ("03d"):
                url = "https://live.staticflickr.com/2556/3843085770_6a735791a4_w_d.jpg";
                break;
            case ("03n"):
                url = "https://live.staticflickr.com/2556/3843085770_6a735791a4_w_d.jpg";
                break;
            case ("04d"):
                url = "https://live.staticflickr.com/4908/44813078345_0c2ba943dd_w_d.jpg";
                break;
            case ("04n"):
                url = "https://live.staticflickr.com/4908/44813078345_0c2ba943dd_w_d.jpg";
                break;
            case ("09d"):
                url = "https://live.staticflickr.com/5645/21272512843_9a84c99b35_w_d.jpg";
                break;
            case ("09n"):
                url = "https://live.staticflickr.com/5645/21272512843_9a84c99b35_w_d.jpg";
                break;
            case ("10d"):
                url = "https://live.staticflickr.com/5645/21272512843_9a84c99b35_w_d.jpg";
                break;
            case ("10n"):
                url = "https://live.staticflickr.com/5645/21272512843_9a84c99b35_w_d.jpg";
                break;
            case ("11d"):
                url = "https://live.staticflickr.com/7299/27843289346_31ef9c3767_w_d.jpg";
                break;
            case ("11n"):
                url = "https://live.staticflickr.com/7299/27843289346_31ef9c3767_w_d.jpg";
                break;
            case ("13d"):
                url = "https://live.staticflickr.com/4594/39519868951_c21200c23b_w_d.jpg";
                break;
            case ("13n"):
                url = "https://live.staticflickr.com/4594/39519868951_c21200c23b_w_d.jpg";
                break;
            case ("50d"):
                url = "https://live.staticflickr.com/1499/26175568494_faa9556274_w_d.jpg";
                break;
            case ("50n"):
                url = "https://live.staticflickr.com/1499/26175568494_faa9556274_w_d.jpg";
                break;
        }
        Picasso.get()
                .load(url)
                .into(mainImage);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public long getTimeSetting (){
        return timeSetting * 1000 * 60 * 60;
    }

    public void setTimeSetting (int timeSetting) {
        this.timeSetting = timeSetting;
    }

    public boolean isUpdateCity() {
        return updateCity;
    }

    public void setUpdateCity(boolean updateCity) {
        this.updateCity = updateCity;
    }

    public void updateWeather(TableCity tableCity) {
        if (tableCity != null && isUpdateCity()) {
            long time = new Date().getTime();
            if (time - tableCity.timeUpdate > getTimeSetting())
            requestRetrofitModelOneCallApi(tableCity.cityName, tableCity.lat, tableCity.lon);
        }
    }
}
