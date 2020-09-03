package com.example.gardenweather3;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.gardenweather3.modelCurrentWeatherData.CurrentWeatherData;
import com.google.gson.Gson;


public class ParsingWeatherService extends IntentService {
    private static final String EXTRA_RESPONSE = "weather_current_data";
    static final String EXTRA_PARSING = "parsing_to_object";
//    CurrentWeatherData data = CurrentWeatherData.getInstance();

    public ParsingWeatherService() {
        super("ParsingWeatherService");
    }

    public static void startParsingWeatherService(Context context, String response) {
        Intent intent = new Intent(context, ParsingWeatherService.class);
        intent.putExtra(EXTRA_RESPONSE, response);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        String response = intent.getStringExtra(EXTRA_RESPONSE);
        Gson gson = new Gson();
        TempData.getInstance().setWeatherData(gson.fromJson(response, CurrentWeatherData.class));
        sendMyBroadcast();
    }

    private void sendMyBroadcast() {
        Intent broadcastIntent = new Intent(MainActivity.BROADCAST_ACTION_WEATHER_PARSING);
        broadcastIntent.putExtra(EXTRA_PARSING, true);
        sendBroadcast(broadcastIntent);
    }
}
