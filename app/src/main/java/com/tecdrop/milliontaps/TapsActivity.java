/*
 * Copyright (C) 2014-2016 AnAurelian. All rights reserved.
 * https://anaurelian.com
 */
package com.tecdrop.milliontaps;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main Taps activity.
 */
public class TapsActivity extends Activity implements View.OnClickListener {

    /**
     * The default start value for the counter.
     */
    static final int MIN_COUNTER = 0;
    /**
     * The maximum value for the counter.
     */
    static final int MAX_COUNTER = 0xFFFFFF;
    /**
     * The current counter value.
     */
    private int mCounter = MIN_COUNTER;
    /**
     * The current numeral system of the counter.
     */
    private String mNumeralSystem;

    /**
     * The full brightness boolean flag.
     */
    private boolean mFullBrightness;

    /**
     * The elapsed time in the Taps activity.
     */
    private long mElapsedTime;

    /**
     * The counter text view.
     */
    private TextView mColorTextView;

//region Activity Lifecycle

    /**
     * Called when the activity is starting. Hook up event handlers, init menu and preferences.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taps);

        // Get the color text view and set its onclick listener
        mColorTextView = (TextView) findViewById(R.id.counter_text_view);
        mColorTextView.setOnClickListener(this);

        // Set the default preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    /**
     * Called when the activity starts interacting with the user. Load preferences, update
     * the color, start measuring elapsed time, etc.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Load current preferences, as they may have been changed by the Settings activity
        loadPreferences();

        // Update the current color
        updateCounter(mCounter);

        // Update the screen brightness
        Utils.setFullBrightness(getWindow(), mFullBrightness);

        // Start measuring elapsed time
        mElapsedTime = SystemClock.elapsedRealtime();

        // Show the first run advice (when the color is 0)
        if (mCounter == 0) {
            Toast.makeText(this, R.string.toast_first_advice, Toast.LENGTH_LONG).show();
        }

        // Always show the overflow menu even if the phone has a menu button
        Utils.setOverflowMenuAlwaysOn(this);
    }

    /**
     * Called when an activity is going into the background. Stop measuring elapsed time and
     * save preferences.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mElapsedTime = SystemClock.elapsedRealtime() - mElapsedTime;
        savePreferences();
    }

//endregion

//region Main Functionality

    /**
     * Set and show the new counter, and fill the screen with its corresponding int color.
     * (This may be the most important method.)
     *
     * @param counter The new counter to set.
     */
    private void updateCounter(int counter) {
        mCounter = counter;

        // Fill the decor view with the new counter color (including the action bar)
        int counterColor = Utils.fullAlpha(mCounter);
        getWindow().getDecorView().setBackgroundColor(counterColor);

        // Update the counter
        mColorTextView.setText(FormatUtils.formatCounter(this, mCounter, mNumeralSystem));

        // Fill the navigation bar with the new counter color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(counterColor);
        }
    }

    /**
     * Increment the counter value and update.
     */
    private void nextCounter() {
        if (mCounter < MAX_COUNTER) {
            updateCounter(mCounter + 1);
        } else {
            // This is the end (?)
            Toast.makeText(this, R.string.toast_the_end, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Decrement the counter value and update.
     */
    private void previousCounter() {
        if (mCounter > MIN_COUNTER) {
            updateCounter(mCounter - 1);
        } else {
            // Can't go back from 0: show a message
            Toast.makeText(this, R.string.toast_no_cheating, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Increment the counter when the user taps the screen, or open the popup menu when the user
     * taps on the menu button.
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.counter_text_view:
                nextCounter();
                break;
        }
    }

//endregion

//region Popup Menu Actions

    /**
     * Inflate the menu items for use in the action bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_taps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle action bar item clicks.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_one_step_back:
                // Oops, go back to the previous counter
                previousCounter();
                return true;

            // Copy the color code to the clipboard and informs the user with a toast
            case R.id.action_copy:
                String colorHex = Utils.colorToHex(mCounter);
                Utils.copyText(this, getString(R.string.action_copy_label), colorHex);
                Toast.makeText(this, getString(R.string.toast_copied, colorHex), Toast.LENGTH_LONG).show();
                return true;

            // Start the Settings activity to allow the user to change app preferences
            case R.id.action_settings:
                final Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            // Open the Google Play app page to allow the user to rate the app
            case R.id.action_rate:
                Utils.viewUrl(this, getString(R.string.action_rate_url));
                return true;

            // Open the app home page for online help
            case R.id.action_help:
                Utils.viewUrl(this, getString(R.string.action_help_url));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//endregion

//region Load and Save Preferences

    /**
     * Load the required preferences from the Shared Preferences.
     */
    private void loadPreferences() {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final Resources resources = getResources();
        mCounter = sharedPref.getInt(getString(R.string.pref_counter_key),
                resources.getInteger(R.integer.pref_counter_default));
        mNumeralSystem = sharedPref.getString(getString(R.string.pref_numeral_system_key),
                resources.getString(R.string.pref_numeral_system_default));
        mFullBrightness = sharedPref.getBoolean(getString(R.string.pref_full_bright_key),
                resources.getBoolean(R.bool.pref_full_bright_default));

        String cheatValue = sharedPref.getString(getString(R.string.pref_cheat_mode_key), "");
        try {
            mCounter = Integer.parseInt(cheatValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            final int fontSize = Integer.parseInt(sharedPref.getString(getString(R.string.pref_font_size_key), ""));
            mColorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        } catch (NumberFormatException e) {
            // Ignore any invalid font sizes
            e.printStackTrace();
        }
    }

    /**
     * Save the required preferences to the Shared Preferences.
     */
    private void savePreferences() {
        // Get the Shared Preferences and load the previous total elapsed time in Tapping activity
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final long mTotalElapsedTime = sharedPref.getLong(getString(R.string.pref_elapsed_time_key), 0L);

        // Open the Shared Preferences editor and save the current color number
        final SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.pref_counter_key), mCounter);
        editor.putString(getString(R.string.pref_cheat_mode_key), String.valueOf(mCounter));

        // Update the total elapsed time in Tapping activity
        editor.putLong(getString(R.string.pref_elapsed_time_key), mTotalElapsedTime + mElapsedTime);

        // Save the changes
        editor.apply();
    }

//endregion
}