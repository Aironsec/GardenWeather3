package com.example.gardenweather3;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import java.util.Objects;

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
            Objects.requireNonNull(getActivity()).setTheme(R.style.AppLiteTheme);
        } else {
            Objects.requireNonNull(getActivity()).setTheme(R.style.AppDarkTheme);
        }
        if (chTheme != ch) getActivity().recreate();
    }
}