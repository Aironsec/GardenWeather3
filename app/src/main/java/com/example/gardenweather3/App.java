package com.example.gardenweather3;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.example.gardenweather3.model.WeatherData;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class App extends Application {
    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/onecall?lat=55.117092&lon=36.597041&lang=ru&units=metric&exclude=minutely&appid=";


    @Override
    public void onCreate() {
        super.onCreate();
        Handler handler = new Handler();
        try {
            final URL uri = new URL(WEATHER_URL + BuildConfig.WEATHER_API_KEY);
            new Thread(() -> {
                HttpsURLConnection httpsURLConnection = null;
                try {
                    httpsURLConnection = (HttpsURLConnection) uri.openConnection();
                    httpsURLConnection.setRequestMethod("GET");
                    httpsURLConnection.setReadTimeout(10000);
                    httpsURLConnection.setConnectTimeout(10000);
                    int code = httpsURLConnection.getResponseCode();
                    if (code >= 200 && code <= 299) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); // читаем  данные в поток
                        String result = in.lines().collect(Collectors.joining("\n"));
                        ViewModelData modelData = new ViewModelData();

                        Gson gson = new Gson();
                        handler.post(() ->
                                modelData.setData(gson.fromJson(result, WeatherData.class)));
                        return;
                    }
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); // читаем  данные в поток
                    String result = in.lines().collect(Collectors.joining("\n"));
                    handler.post(() ->
                            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show());


                } catch (Exception e) {
                    handler.post(() ->
                            Toast.makeText(getBaseContext(), "Fail connection", Toast.LENGTH_LONG).show());
                    e.printStackTrace();
                } finally {
                    if (null != httpsURLConnection) {
                        httpsURLConnection.disconnect();
                    }
                }
            }).start();
        } catch (MalformedURLException ex) {
            handler.post(() ->
                    Toast.makeText(getBaseContext(), "Fail URI", Toast.LENGTH_LONG).show());
            ex.printStackTrace();
        }
    }
}
