package com.example.gardenweather3.fragments.setting;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.gardenweather3.activity.App;
import com.example.gardenweather3.R;

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
        boolean statusTheme = sharedPref.getBoolean("theme", false);
        if (statusTheme) {
            requireActivity().setTheme(R.style.AppLiteTheme);
        } else {
            requireActivity().setTheme(R.style.AppDarkTheme);
        }
        if (chTheme != statusTheme) requireActivity().recreate();

        App.getInstance().setUpdateCity(sharedPref.getBoolean("updateCity", false));
    }
}