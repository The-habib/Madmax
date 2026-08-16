package com.termux.app.madmax.settings.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.termux.R;
import com.termux.app.madmax.dev.DeveloperDashboardActivity;
import com.termux.shared.activity.ActivityUtils;

public class DeveloperPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.madmax_developer_preferences, rootKey);

        Preference openDevDashboard = findPreference("open_dev_dashboard");
        if (openDevDashboard != null) {
            openDevDashboard.setOnPreferenceClickListener(pref -> {
                Context context = getContext();
                if (context != null) {
                    ActivityUtils.startActivity(context, new Intent(context, DeveloperDashboardActivity.class));
                }
                return true;
            });
        }
    }
}
