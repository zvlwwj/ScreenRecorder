package com.zou.screenrecorder.activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.zou.screenrecorder.R;

/**
 * Created by zou on 2018/1/10.
 */

public class SettingActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }else {
                // For all other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        addPreferencesFromResource(R.xml.preferences_settings);
        bindPreferenceSummaryToValue(findPreference("sync_frequency_frame"));
        bindPreferenceSummaryToValue(findPreference("sync_frequency_bit_rate"));
        bindPreferenceSummaryToValue(findPreference("sync_frequency_resolution"));
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is changed,
     * its summary (line of text below the preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is dependent on the type of preference.
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                finish();
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }}
