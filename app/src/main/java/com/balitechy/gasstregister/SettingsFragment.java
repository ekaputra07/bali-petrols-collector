package com.balitechy.gasstregister;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

        // Preset preferences screen
        Preference usernamePref = findPreference(getString(R.string.preference_username_key));
        String username = sp.getString(getString(R.string.preference_username_key), "");
        usernamePref.setSummary(username.equals("") ? getString(R.string.preference_username_summary) : username);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.preference_username_key))) {
            Preference usernamePref = findPreference(key);
            String username = sharedPreferences.getString(key, "");
            usernamePref.setSummary(username.equals("") ? getString(R.string.preference_username_summary) : username);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
