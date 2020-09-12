package com.example.gardenweather3;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.gardenweather3.modelCurrentWeatherData.CurrentWeatherData;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private static final String SETTINGS_PREFERENCES = "com.example.gardenweather3_preferences";
    private static final String CURRENT_CITY = "currentCity";
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private CollapsingToolbarLayout colapsCityName;
    protected ViewModelData modelData;
    private final String CELCIA = "\u00B0";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private final String METRIC = "metric";
    private TextView currentTemp;
    private TextView currentDayWeek;
    private ImageView mainImage;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGui();

        SharedPreferences sharedPref = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE);
        loadPreferences(sharedPref);

        setObserveForModel();

        Toolbar toolbar = findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);

        mFab = findViewById(R.id.activity_fab);
        setOnClickForFab();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        showMainFragment();

        setOnClickForSideMenuItems();

        AppBarLayout appbar = findViewById(R.id.activity_appbar);
        appbar.addOnOffsetChangedListener(this);

        requestRetrofit(colapsCityName.getTitle().toString());


        Picasso.get()
                .load("https://live.staticflickr.com/65535/47980222552_abef406e4f_w_d.jpg")
                .into(mainImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = getSharedPreferences(SETTINGS_PREFERENCES, MODE_PRIVATE);
        savePreferences(sharedPref);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        menuItemClick(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initGui(){
        currentTemp = findViewById(R.id.currentTemp);
        currentDayWeek = findViewById(R.id.currentDayWeek);
        colapsCityName = findViewById(R.id.activity_collapsing);
        mainImage = findViewById(R.id.main_image);
    }

    private void requestRetrofit(String city) {
        NetworkService.getInstance()
                .getWeatherApi()
                .loadWeather(city,METRIC, BuildConfig.WEATHER_API_KEY)
                .enqueue(new Callback<CurrentWeatherData>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherData> call, Response<CurrentWeatherData> response) {
                        CurrentWeatherData result = response.body();
                        if (result != null) {
                            resultToDataBase(result);
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherData> call, Throwable t) {
                        colapsCityName.setTitle(getString(R.string.city_not_found));
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void resultToDataBase(CurrentWeatherData result) {
        Date date = new Date((long) result.getDt() * 1000);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        currentTemp.setText(result.getMain().getTemp().intValue() + CELCIA);
        currentDayWeek.setText(dateFormat.format(date));
        double lon = result.getCoord().getLon();
        double lat = result.getCoord().getLat();
        int cityId = result.getId();
        String cityName = result.getName();
        WeatherDB db = App.getInstance().getDb();
        CityWithHistoryDao dao = db.getCityWithHistoryDao();
        TableCity tableCity = new TableCity();
        TableHistory tableHistory = new TableHistory();
        tableCity.cityId = cityId;
        tableCity.cityName = cityName;
        tableCity.lat = lat;
        tableCity.lon = lon;
        tableHistory.date = date.getTime();
        tableHistory.temp = result.getMain().getTemp().intValue();
        tableHistory.cityId = cityId;
        new Thread(() -> dao.insertCityWithHistory(tableCity, tableHistory)).start();
    }

    private void setOnClickForFab() {
        mFab.setOnClickListener(view -> {
            colapsCityName = findViewById(R.id.activity_collapsing);
            TempData td = TempData.getInstance();
            td.setTempStr(Objects.requireNonNull(colapsCityName.getTitle()).toString());
            showCityListFragment();
        });
    }

    private void setOnClickForSideMenuItems() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home: {
                        showMainFragment();
                        drawer.closeDrawers();
                        break;
                    }
                    case R.id.nav_list_city: {
                        showCityListFragment();
                        drawer.closeDrawers();
                        break;
                    }
                    case R.id.nav_settings: {
                        showSettingFragment();
                        drawer.closeDrawers();
                        break;
                    }
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void showMainFragment() {
        navigationView.setCheckedItem(R.id.nav_home);
        setFragment(new MainFragment());
    }

    private void showCityListFragment() {
        navigationView.setCheckedItem(R.id.nav_list_city);
        setFragment(new CityListFragment());
    }

    private void showSettingFragment() {
        navigationView.setCheckedItem(R.id.nav_settings);
        setFragment(new SettingsFragment());
    }

    private void setFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_dynamic, fragment)
                .commit();
    }

    private void menuItemClick(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings: {
                showSettingFragment();
                break;
            }
            case R.id.action_list_city: {
                showCityListFragment();
                break;
            }
        }

    }

    private void loadPreferences(SharedPreferences sharedPref) {
        if (sharedPref.getBoolean("theme", false)) {
            setTheme(R.style.AppLiteTheme);
        }
        colapsCityName.setTitle(sharedPref.getString(CURRENT_CITY, "Obninsk"));
    }

    private void savePreferences(SharedPreferences sharedPref) {
        // Для сохранения настроек надо воспользоваться классом Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        // Теперь устанавливаем значения в Editor...
        editor.putString(CURRENT_CITY, colapsCityName.getTitle().toString());
        // ...и сохраняем файл настроек
        editor.apply();

    }

    @SuppressLint("SetTextI18n")
    private void setObserveForModel() {
        modelData = ViewModelProviders.of(this).get(ViewModelData.class);
        modelData.getCity().observe(this, s -> {
            colapsCityName = findViewById(R.id.activity_collapsing);
            colapsCityName.setTitle(s);

            requestRetrofit(s);
        });
    }
}