/*
 * Copyright (C) 2014-2016 AnAurelian. All rights reserved.
 * https://anaurelian.com
 */
package com.tecdrop.milliontaps;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

/**
 * The settings activity.
 */
@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    /**
     * Called when the activity is starting. Load the preferences screen, update the elapsed time
     * preference, and customize the action bar.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        updateElapsedTimeText();

        // Set the preference change listener for the cheat mode edit text preference
        EditTextPreference cheatModePreference = (EditTextPreference) findPreference(getString(R.string.pref_cheat_mode_key));
        cheatModePreference.setOnPreferenceChangeListener(this);
    }

    /**
     * Update the elapsed time info preference.
     */
    private void updateElapsedTimeText() {
        final Preference elapsedTimeTextPref = findPreference(getString(R.string.pref_elapsed_time_text_key));
        final long totalElapsedTime = getPreferenceScreen().getSharedPreferences().getLong(
                getString(R.string.pref_elapsed_time_key), 0L);
        elapsedTimeTextPref.setSummary(FormatUtils.formatDuration(this, totalElapsedTime));
    }

    /**
     * Validate the new counter value in the cheat mode edit text preference.
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        try {
            int intValue = Integer.parseInt(newValue.toString());
            if ((intValue >= TapsActivity.MIN_COUNTER) && (intValue <= TapsActivity.MAX_COUNTER)) {
                finish();
                return true;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, getString(R.string.toast_cheat_bad_number), Toast.LENGTH_LONG).show();

        return false;
    }
}
