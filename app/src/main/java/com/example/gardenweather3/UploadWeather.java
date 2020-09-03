package com.example.gardenweather3;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gardenweather3.modelCurrentWeatherData.CurrentWeatherData;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
/*lat=55.117092&lon=36.597041   id=516436    Обнинск*/
public class UploadWeather extends AppCompatActivity {
    private static final String WEATHER_URL_ONE_CALL_API =
            "https://api.openweathermap.org/data/2.5/onecall?lat=55.117092&lon=36.597041&lang=ru&units=metric&exclude=minutely&appid=";
    private  String currentCity;
    private  String WEATHER_URL_CURRENT_DATA;
    private Context mainContext;
    private ViewModelData modelData;

    public UploadWeather(MainActivity mainActivity, String city) {
        mainContext = mainActivity.getBaseContext();
        modelData = mainActivity.modelData;
        currentCity = city;
        WEATHER_URL_CURRENT_DATA = "https://api.openweathermap.org/data/2.5/weather?q=" + currentCity + "&units=metric&appid=";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean upload() throws ExecutionException, InterruptedException {
        Handler handler = new Handler();
        FutureTask<Boolean> future = null;
        try {
//            final URL uriWeatherOneCallApi = new URL(WEATHER_URL_ONE_CALL_API + BuildConfig.WEATHER_API_KEY);
            final URL uriWeatherCurrentData = new URL(WEATHER_URL_CURRENT_DATA + BuildConfig.WEATHER_API_KEY);
//            Callable task = () -> {
//                HttpsURLConnection httpsURLConnection = null;
//                try {
//                    httpsURLConnection = (HttpsURLConnection) uriWeatherOneCallApi.openConnection();
//                    httpsURLConnection.setRequestMethod("GET");
//                    httpsURLConnection.setReadTimeout(10000);
//                    httpsURLConnection.setConnectTimeout(10000);
//                    int code = httpsURLConnection.getResponseCode();
//                    if (code >= 200 && code <= 299) {
//                        BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); // читаем  данные в поток
//                        String result = in.lines().collect(Collectors.joining("\n"));
//
//                        Gson gson = new Gson();
//                        handler.post(() ->
//                                modelData.setDataOneCallApi(gson.fromJson(result, WeatherDataOneCallApi.class)));
//
//                        return true;
//                    }
//                    BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); // читаем  данные в поток
//                    String result = in.lines().collect(Collectors.joining("\n"));
//                    handler.post(() ->
//                            Toast.makeText(mainContext, result, Toast.LENGTH_LONG).show());
//
//
//                } catch (Exception e) {
//                    handler.post(() ->
//                            Toast.makeText(mainContext, "Fail connection", Toast.LENGTH_LONG).show());
//                    e.printStackTrace();
//                } finally {
//                    if (null != httpsURLConnection) {
//                        httpsURLConnection.disconnect();
//                    }
//                }
//                return false;
//            };
//            future = new FutureTask<>(task);
//            new Thread(future).start();

            new Thread(() -> {
                HttpsURLConnection httpsURLConnection = null;
                try {
                    httpsURLConnection = (HttpsURLConnection) uriWeatherCurrentData.openConnection();
                    httpsURLConnection.setRequestMethod("GET");
                    httpsURLConnection.setReadTimeout(10000);
                    httpsURLConnection.setConnectTimeout(10000);
                    int code = httpsURLConnection.getResponseCode();
                    if (code >= 200 && code <= 299) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); // читаем  данные в поток
                        String result = in.lines().collect(Collectors.joining("\n"));

                        Gson gson = new Gson();
                        handler.post(() ->
                                modelData.setDataCurrent(gson.fromJson(result, CurrentWeatherData.class)));

                        return;
                    }
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); // читаем  данные в поток
                    String result = in.lines().collect(Collectors.joining("\n"));
                    handler.post(() ->
                            Toast.makeText(mainContext, result, Toast.LENGTH_LONG).show());


                } catch (Exception e) {
                    handler.post(() ->
                            Toast.makeText(mainContext, "Fail connection", Toast.LENGTH_LONG).show());
                    e.printStackTrace();
                } finally {
                    if (null != httpsURLConnection) {
                        httpsURLConnection.disconnect();
                    }
                }
            }).start();

        } catch (MalformedURLException ex) {
            handler.post(() ->
                    Toast.makeText(mainContext, "Fail URI", Toast.LENGTH_LONG).show());
            ex.printStackTrace();
        }
//        return future.get();
        return true;
    }
}
