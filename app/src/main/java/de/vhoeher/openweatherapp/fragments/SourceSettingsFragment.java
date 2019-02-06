package de.vhoeher.openweatherapp.fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.activities.GetAPIKeyActivity;
import de.vhoeher.openweatherapp.activities.MainActivity;
import de.vhoeher.openweatherapp.activities.SettingsActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SourceSettingsFragment extends PreferenceFragment {

    private static final String TAG = SourceSettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_source);
        Preference btn = findPreference(getString(R.string.pref_source_get_key_key));
        btn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent settingsIntent = new Intent(getActivity().getApplicationContext(), GetAPIKeyActivity.class);
                startActivity(settingsIntent);
                return true;
            }
        });

        SettingsActivity.bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_source_enter_key_key)));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_source_key)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
