package com.example.gardenweather3;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class LoadWeatherService extends IntentService {
    private static final String EXTRA_CURRENT_CITY =
            "input_current_city";
    static final String EXTRA_RESULT =
            "output_result_weather";

    static final String FAIL_CONNECTION = "fail";

    public LoadWeatherService() {
        super("LoadWeatherService");
    }

    public static void startLoadWeatherService(Context context, String city) {
        Intent intent = new Intent(context, LoadWeatherService.class);
        intent.putExtra(EXTRA_CURRENT_CITY, city);
        context.startService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        String city = intent.getStringExtra(EXTRA_CURRENT_CITY);
        String weatherUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=";
        String result = weatherRequest(weatherUrl);
        sendMyBroadcast(result);
    }

    private void sendMyBroadcast(String result) {
        Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ACTION_WEATHER_LOAD);
        broadcastIntent.putExtra(EXTRA_RESULT, result);
        sendBroadcast(broadcastIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String weatherRequest(String weatherUrl) {
        try {
            URL uriWeatherCurrentData = new URL(weatherUrl + BuildConfig.WEATHER_API_KEY);
            HttpsURLConnection httpsURLConnection = null;
            try {
                httpsURLConnection = (HttpsURLConnection) uriWeatherCurrentData.openConnection();
                httpsURLConnection.setRequestMethod("GET");
                httpsURLConnection.setReadTimeout(10000);
                httpsURLConnection.setConnectTimeout(10000);
                int code = httpsURLConnection.getResponseCode();
                if (code >= 200 && code <= 299) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); // читаем  данные в поток
                    return in.lines().collect(Collectors.joining("\n"));
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream())); // читаем  данные в поток
                return in.lines().collect(Collectors.joining("\n"));

            } catch (Exception e) {
                e.printStackTrace();
                return FAIL_CONNECTION;
            } finally {
                if (null != httpsURLConnection) {
                    httpsURLConnection.disconnect();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

}