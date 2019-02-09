package de.vhoeher.openweatherapp.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import de.vhoeher.openweatherapp.R;

/**
 * This class produces instances of IDataSource elements. There is no other way, to receive a IDataSource.
 * In addition it updates the IDataSource if needed. There is always only a maximum of one IDataSource instance.
 *
 * @author Victor Hoeher
 * @version 1.0
 */
public class DataSourceFactory {

    //Current Instance
    private static IDataSource mInstance = null;

    /**
     * Update the IDataSource Instance with the given Preference
     *
     * @param pref     The preference that changed
     * @param newValue The new value
     */
    public static void updateDataSource(Preference pref, String newValue) {
        //Check if there is an instance present
        if (mInstance == null)
            return;
        else {
            //Get the context
            Context ctx = pref.getContext();
            //If the source changed, create a new instance
            if (pref.getKey().equals(ctx.getString(R.string.pref_source_key))) {
                DataSourceType type = DataSourceType.valueOf(newValue);
                if (type != mInstance.getDataSourceType()) {
                    mInstance = null;
                    getDataSourceInstance(ctx);
                    return;
                }
            } else if (pref.getKey().equals(ctx.getString(R.string.pref_source_enter_key_key))) {
                //Set new API-Key
                mInstance.setAPIKey(newValue);
            }
        }
    }

    /**
     * Getter for the IDataSource instance.
     * The Type of the instance depends on the preference made in the SharedPreferences.
     *
     * @param ctx The context to get the SharedPreference
     * @return The current IDataSource instance
     */
    public static IDataSource getDataSourceInstance(Context ctx) {

        if (mInstance == null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            DataSourceType type = DataSourceType.valueOf(pref.getString(ctx.getString(R.string.pref_source_key), ""));
            switch (type) {
                case OPENWEATHERMAP:
                    mInstance = OpenWeatherMapDataSource.getInstance(ctx);
                    break;
            }
        }
        return mInstance;
    }
}
