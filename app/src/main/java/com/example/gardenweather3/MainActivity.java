package com.example.gardenweather3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
//    private boolean isSetting;
//    private boolean isListCity;
    private Toolbar toolbar;
    CollapsingToolbarLayout colaps;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelData modelData = ViewModelProviders.of(this).get(ViewModelData.class);
        modelData.getCity().observe(this, s -> {
            colaps = findViewById(R.id.activity_collapsing);
            colaps.setTitle(s);
        });
        modelData.getData().observe(this, wd -> {
            TextView textView = findViewById(R.id.tempDay);
            textView.setText("" + wd.getCurrent().getTemp());
        });
        SharedPreferences sharedPref = getSharedPreferences("com.example.gardenweather3_preferences", MODE_PRIVATE);
        if (sharedPref.getBoolean("theme", false)) {
            setTheme(R.style.AppLiteTheme);
        }

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.activity_toolbar);
        mFab = findViewById(R.id.activity_fab);

        mFab.setOnClickListener(view -> {

            colaps = findViewById(R.id.activity_collapsing);
            TempData td = TempData.getInstance();
            td.setTempStr(Objects.requireNonNull(colaps.getTitle()).toString());

            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.frame_dynamic, new CityListFragment())
                    .commit();
        });

        toolbar.setNavigationOnClickListener(v -> {

                toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.frame_dynamic, new SettingsFragment())
                        .commit();
        });

        AppBarLayout appbar = findViewById(R.id.activity_appbar);
        appbar.addOnOffsetChangedListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.frame_dynamic, new MainFragment())
                .commit();
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