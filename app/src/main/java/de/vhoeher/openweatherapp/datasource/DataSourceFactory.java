package de.vhoeher.openweatherapp.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import de.vhoeher.openweatherapp.R;

public class DataSourceFactory {

    private static IDataSource mInstance = null;

    public static void updateDataSource(Preference pref, String newValue) {
        if (mInstance == null)
            return;
        else {
            Context ctx = pref.getContext();
            if(pref.getKey().equals(ctx.getString(R.string.pref_source_key))){
                DataSourceType type = DataSourceType.valueOf(newValue);
                if (type != mInstance.getDataSourceType()) {
                    mInstance = null;
                    getDataSourceInstance(ctx);
                    return;
                }
            } else if(pref.getKey().equals(ctx.getString(R.string.pref_source_enter_key_key))){
                mInstance.setAPIKey(newValue);
            }
        }
    }

    public static IDataSource getDataSourceInstance(Context ctx) {

        if (mInstance == null) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            DataSourceType type = DataSourceType.valueOf(pref.getString(ctx.getString(R.string.pref_source_key), ""));
            switch (type) {
                case OPENWEATHERMAP:
                    mInstance = OpenWeatherMapDataSource.getInstance(ctx);
            }
        }
        return mInstance;
    }
}
