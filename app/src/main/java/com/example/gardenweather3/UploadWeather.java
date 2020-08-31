package com.example.gardenweather3;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gardenweather3.model.WeatherData;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class UploadWeather extends AppCompatActivity {
    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/onecall?lat=36.597041&lon=55.117092&lang=ru&units=metric&exclude=minutely&appid=";
    private Context mainContext;
    private ViewModelData modelData;

    public UploadWeather(MainActivity mainActivity) {
        mainContext = mainActivity.getBaseContext();
        modelData = mainActivity.modelData;
    }

    public boolean upload() throws ExecutionException, InterruptedException {
        Handler handler = new Handler();
        FutureTask<Boolean> future = null;
        try {
            final URL uri = new URL(WEATHER_URL + BuildConfig.WEATHER_API_KEY);
            Callable task = () -> {
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

                        Gson gson = new Gson();
                        handler.post(() ->
                                modelData.setData(gson.fromJson(result, WeatherData.class)));
                        return true;
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
                return false;
            };
            future = new FutureTask<>(task);
            new Thread(future).start();
        } catch (MalformedURLException ex) {
            handler.post(() ->
                    Toast.makeText(mainContext, "Fail URI", Toast.LENGTH_LONG).show());
            ex.printStackTrace();
        }
        return future.get();
    }
}
