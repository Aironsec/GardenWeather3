package com.example.gardenweather3;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private CollapsingToolbarLayout colaps;
    protected ViewModelData modelData;
    private final String CELCIA = "\u00B0";
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setObserveForModel();
        setSharePreferences();

        Toolbar toolbar = findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);


        mFab = findViewById(R.id.activity_fab);

        mFab.setOnClickListener(view -> {

            colaps = findViewById(R.id.activity_collapsing);
            TempData td = TempData.getInstance();
            td.setTempStr(Objects.requireNonNull(colaps.getTitle()).toString());

            showCityListFragment();
        });

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_list_city, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.frame_dynamic);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        showMainFragment();
//        setOnClickForSideMenuItems();
//        toolbar.setNavigationOnClickListener(v -> {
//
//            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
//            showSettingFragment();
//        });
//        AppBarLayout appbar = findViewById(R.id.activity_appbar);
//        appbar.addOnOffsetChangedListener(this);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .replace(R.id.frame_dynamic, new MainFragment())
//                .commit();
        new UploadWeather(this);
    }

//    private void setOnClickForSideMenuItems() {
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_home: {
//                        showMainFragment();
//                        drawer.closeDrawers();
//                        break;
//                    }
//                    case R.id.nav_list_city: {
//                        showCityListFragment();
//                        drawer.closeDrawers();
//                        break;
//                    }
//                    case R.id.nav_settings: {
//                        showSettingFragment();
//                        drawer.closeDrawers();
//                        break;
//                    }
//                }
//                return true;
//            }
//        });
//    }

//    private void showMainFragment() {
//        setFragment(new MainFragment());
//    }

    private void showCityListFragment() {
        setFragment(new CityListFragment());
    }

    private void showSettingFragment() {
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.frame_dynamic);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
            colaps = findViewById(R.id.activity_collapsing);
            colaps.setTitle(s);
        });
        modelData.getData().observe(this, wd -> {
            TextView currentTemp = findViewById(R.id.currentTemp);
            TextView tempDay = findViewById(R.id.tempDay);
            TextView tempNight = findViewById(R.id.tempNight);
            TextView currentDayWeek = findViewById(R.id.currentDayWeek);

            currentTemp.setText(wd.getCurrent().getTemp().intValue() + CELCIA);
            // TODO: 26.08.2020 отталкиваться от current.dt с Calendar
            tempDay.setText(wd.getDaily().get(0).getTemp().getDay().intValue() + CELCIA);
            tempNight.setText(wd.getDaily().get(0).getTemp().getNight().intValue() + CELCIA);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
            currentDayWeek.setText(dateFormat.format(wd.getCurrent().getDt()));

        });
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

}