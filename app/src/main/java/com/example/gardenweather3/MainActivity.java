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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private CollapsingToolbarLayout colapsCityName;
    protected ViewModelData modelData;
    private final String CELCIA = "\u00B0";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private OpenCurrentWeather openCurrentWeather;
    private final String METRIC = "metric";
    private TextView currentTemp;
    private TextView currentDayWeek;
    private ImageView mainImage;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setObserveForModel();
        setSharePreferences();

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

        initGui();
        initRetrofit();
        requestRetrofit(colapsCityName.getTitle().toString());

        Picasso.get()
                .load("https://live.staticflickr.com/65535/47980222552_abef406e4f_w_d.jpg")
                .into(mainImage);
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

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/") // Базовая часть
                // адреса
                // Конвертер, необходимый для преобразования JSON
                // в объекты
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Создаём объект, при помощи которого будем выполнять запросы
        openCurrentWeather = retrofit.create(OpenCurrentWeather.class);
    }

    private void requestRetrofit(String city) {
        openCurrentWeather.loadWeather(city,METRIC, BuildConfig.WEATHER_API_KEY)
                .enqueue(new Callback<CurrentWeatherData>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<CurrentWeatherData> call, Response<CurrentWeatherData> response) {
                        if (response.body() != null) {
                            Date date = new Date((long) response.body().getDt() * 1000);
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
                            currentTemp.setText(response.body().getMain().getTemp().intValue() + CELCIA);
                            currentDayWeek.setText(dateFormat.format(date));
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherData> call, Throwable t) {
                        colapsCityName.setTitle(getString(R.string.city_not_found));
                    }
                });
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

    private void setSharePreferences() {
        SharedPreferences sharedPref = getSharedPreferences("com.example.gardenweather3_preferences", MODE_PRIVATE);
        if (sharedPref.getBoolean("theme", false)) {
            setTheme(R.style.AppLiteTheme);
        }
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