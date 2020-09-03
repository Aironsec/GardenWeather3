package com.example.gardenweather3;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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
    static final String BROADCAST_ACTION_WEATHER_LOAD =
            "broadcast_action_weather_load";
    static final String BROADCAST_ACTION_WEATHER_PARSING =
            "broadcast_action_weather_parsing";

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

        colapsCityName = findViewById(R.id.activity_collapsing);
        LoadWeatherService.startLoadWeatherService(MainActivity.this, colapsCityName.getTitle().toString());

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(LoadWeatherFinishedReceiver, new IntentFilter(BROADCAST_ACTION_WEATHER_LOAD));
        registerReceiver(ParsingFinishedReceiver, new IntentFilter(BROADCAST_ACTION_WEATHER_PARSING));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(LoadWeatherFinishedReceiver);
        unregisterReceiver(ParsingFinishedReceiver);
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

    private BroadcastReceiver LoadWeatherFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(LoadWeatherService.EXTRA_RESULT);

            if (LoadWeatherService.FAIL_CONNECTION.equals(result)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.error_head)
                        .setMessage(R.string.error_msg)
                        .setCancelable(false)
                        .setNegativeButton("Нет", null)
                        .setPositiveButton("Да", (dialogInterface, i) -> {
                            LoadWeatherService.startLoadWeatherService(MainActivity.this, colapsCityName.getTitle().toString());
                        });

                AlertDialog alert = builder.create();
                alert.show();
            } else {
                ParsingWeatherService.startParsingWeatherService(MainActivity.this, result);
            }
        }
    };

    private BroadcastReceiver ParsingFinishedReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            CurrentWeatherData sourceData = TempData.getInstance().getWeatherData();
            Date date = new Date((long) sourceData.getDt() * 1000);
            TextView currentTemp = findViewById(R.id.currentTemp);
            TextView tempDay = findViewById(R.id.tempDay);
            TextView tempNight = findViewById(R.id.tempNight);
            TextView currentDayWeek = findViewById(R.id.currentDayWeek);

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
            // Потокобезопасный вывод данных
            runOnUiThread(() -> {
                currentTemp.setText(sourceData.getMain().getTemp().intValue() + CELCIA);
                tempDay.setText(sourceData.getMain().getTemp().intValue() + CELCIA);
                currentDayWeek.setText(dateFormat.format(date));
            });
        }
    };

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
            LoadWeatherService.startLoadWeatherService(MainActivity.this, s);
        });
    }
}