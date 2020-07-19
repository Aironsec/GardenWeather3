package com.example.gardenweather3;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragmentCompat {
    private boolean chTheme;
    private  SharedPreferences sharedPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        sharedPref = getPreferenceManager().getSharedPreferences();
        chTheme = sharedPref.getBoolean("theme", false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        boolean ch = sharedPref.getBoolean("theme", false);
        if (ch) {
            getActivity().setTheme(R.style.AppLiteTheme);
        } else {
            getActivity().setTheme(R.style.AppDarkTheme);
        }
        if (chTheme != ch) getActivity().recreate();
    }
}