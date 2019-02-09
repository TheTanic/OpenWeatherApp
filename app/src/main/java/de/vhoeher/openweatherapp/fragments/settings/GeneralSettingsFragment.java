package de.vhoeher.openweatherapp.fragments.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.MenuItem;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.util.LocaleHelper;
import de.vhoeher.openweatherapp.activities.SettingsActivity;

/**
 * PreferenceFragment for the General settings
 *
 * @author Victor Höher
 * @version 1.0
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GeneralSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_general_dateformat_key)));
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_general_language_key)));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //If the version if below 28 do not change language on the fly
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;
        switch (key) {
            case "pref_language":
                LocaleHelper.setLocale(getContext(), PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(key, ""));
                getActivity().recreate(); // necessary here because this Activity is currently running and thus a recreate() in onResume() would be too late
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // documentation requires that a reference to the listener is kept as long as it may be called, which is the case as it can only be called from this Fragment
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
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

