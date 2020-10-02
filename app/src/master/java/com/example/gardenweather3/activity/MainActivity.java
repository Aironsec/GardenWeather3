package com.example.gardenweather3.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.example.gardenweather3.R;
import com.example.gardenweather3.ViewModelData;
import com.example.gardenweather3.db.RelationCityDaily;
import com.example.gardenweather3.fragments.listCity.CityListFragment;
import com.example.gardenweather3.fragments.main.MainFragment;
import com.example.gardenweather3.fragments.setting.SettingsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private static final String CURRENT_CITY = "currentCity";
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private CollapsingToolbarLayout colapsCityName;
    protected ViewModelData modelData;
    private final String CELCIA = "\u00B0";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TextView currentTemp;
    private TextView dayTemp;
    private TextView nightTemp;
    private TextView currentDayWeek;
    private ImageView conditionImage;
    private static final int PERMISSION_REQUEST_CODE = 10;
    private App allFunc;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSettings();
        initGui();
        try {
            loadPreferences();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setObserveToChangeCity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            menuItemClick(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        Fragment dynamicFragment = getSupportFragmentManager().findFragmentById(R.id.frame_dynamic);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!(dynamicFragment instanceof MainFragment))
            // TODO: 14.09.2020 вызвать функцию фрагмента ((MainFragment) fragment).func;
            showMainFragment();
        else
            super.onBackPressed();
    }

    private void initSettings() {
        allFunc = App.getInstance();
        allFunc.setLang(getString(R.string.lang));
        allFunc.setContext(getBaseContext());
    }

    private void initGui() {
        currentTemp = findViewById(R.id.currentTemp);
        dayTemp = findViewById(R.id.tempDay);
        nightTemp = findViewById(R.id.tempNight);
        currentDayWeek = findViewById(R.id.currentDayWeek);
        colapsCityName = findViewById(R.id.activity_collapsing);
        conditionImage = findViewById(R.id.condition_image);
        allFunc.setMainImage(findViewById(R.id.mainImage));
        mFab = findViewById(R.id.activity_fab);
        setOnClickForFab();
        Toolbar toolbar = findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setOnClickForSideMenuItems();

        AppBarLayout appbar = findViewById(R.id.activity_appbar);
        appbar.addOnOffsetChangedListener(this);
    }

    @SuppressLint("CheckResult")
    public void subscribeLoadWeatherOneCity() {
        allFunc.getDb().getCurrentHoursDay().oneCity(allFunc.getCurrentCityName())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<RelationCityDaily>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void accept(List<RelationCityDaily> relationCityDailies) {
                        if (relationCityDailies.isEmpty()) return;

                        colapsCityName.setTitle(relationCityDailies.get(0).tableCity.cityName);
                        Date date = new Date((long) relationCityDailies.get(0).tableCity.dt * 1000);
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
                        currentDayWeek.setText(dateFormat.format(date));
                        currentTemp.setText(relationCityDailies.get(0).tableCity.tempCurrent + CELCIA);
                        if (relationCityDailies.get(0).dailies.isEmpty()) return;
                        dayTemp.setText(relationCityDailies.get(0).dailies.get(0).tempDay + CELCIA);
                        nightTemp.setText(relationCityDailies.get(0).dailies.get(0).tempNight + CELCIA);
                        conditionImage.setImageResource(allFunc.loadPic(relationCityDailies.get(0).tableCity.icon));

                        allFunc.loadImage(relationCityDailies.get(0).tableCity.icon);

                        allFunc.updateWeather(relationCityDailies.get(0).tableCity);
                        showMainFragment();
                    }
                });
    }

    private void setOnClickForFab() {
        mFab.setOnClickListener(view -> showCityListFragment());
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_dynamic, fragment)
                .commit();
    }

    private void menuItemClick(MenuItem item) throws IOException {
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
            case R.id.action_map_location: {
                requestPermissions();
                break;
            }
        }

    }

    private void loadPreferences() throws IOException {
        SharedPreferences sharedPref = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        if (sharedPref.getBoolean("theme", false)) {
            setTheme(R.style.AppLiteTheme);
        }
        allFunc.setUpdateCity(sharedPref.getBoolean("updateCity", false));
        String sDef = getResources().getString(R.string.default_current_city);
        String cityName = sharedPref.getString(CURRENT_CITY, sDef);
        boolean gpsOn = sharedPref.getBoolean("locate", true);
        assert cityName != null;
        if (cityName.equals(sDef)) {
            if (gpsOn) requestPermissions();
        } else {
            allFunc.setCurrentCityName(cityName);
            subscribeLoadWeatherOneCity();
        }
    }

    private void savePreferences(String city) {
        SharedPreferences sharedPref = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        // Для сохранения настроек надо воспользоваться классом Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        // Теперь устанавливаем значения в Editor...
        editor.putString(CURRENT_CITY, city);
        // ...и сохраняем файл настроек
        editor.apply();

    }

    @SuppressLint("SetTextI18n")
    private void setObserveToChangeCity() {
        modelData = ViewModelProviders.of(this).get(ViewModelData.class);
        modelData.getCityName().observe(this, cityName -> {
            allFunc.setCurrentCityName(cityName);
            savePreferences(cityName);
            subscribeLoadWeatherOneCity();
        });
        modelData.getCityData().observe(this, cityData -> {

            Date date = new Date((long) cityData.dt * 1000);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
            currentDayWeek.setText(dateFormat.format(date));
            dayTemp.setText(cityData.tempDay + CELCIA);
            nightTemp.setText(cityData.tempNight + CELCIA);

        });
    }

    // Запрашиваем Permission’ы
    private void requestPermissions() throws IOException {
        // Проверим, есть ли Permission’ы, и если их нет, запрашиваем их у
        // пользователя
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем координаты
            requestLocation();
        } else {
            // Permission’ов нет, запрашиваем их у пользователя
            requestLocationPermissions();
        }
    }

    // Запрашиваем координаты
    private void requestLocation() throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            final Location loc = locationManager.getLastKnownLocation(provider);

            if (loc != null) {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

                double cityLat = addresses.get(0).getLatitude();
                double cityLon = addresses.get(0).getLongitude();
                String cityName = addresses.get(0).getLocality();
                savePreferences(cityName);
                allFunc.setCurrentCityName(cityName);
                subscribeLoadWeatherOneCity();

                allFunc.requestRetrofitModelOneCallApi(cityLat, cityLon);
            }
        }
    }

    // Запрашиваем Permission’ы для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                try {
                    requestLocation();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}