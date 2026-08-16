package com.termux.app.madmax.settings.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.termux.R;

public class PluginsPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.madmax_plugins_preferences, rootKey);
    }
}
