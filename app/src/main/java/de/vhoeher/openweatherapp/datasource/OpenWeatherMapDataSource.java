package de.vhoeher.openweatherapp.datasource;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.vhoeher.openweatherapp.R;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OpenWeatherMapDataSource implements IDataSource {

    private final String BASE_URL_CURRENTWEATHER = "http://api.openweathermap.org/data/2.5/weather?";
    private final String BASE_URL_FORECAST = "http://api.openweathermap.org/data/2.5/forecast?";
    private static OpenWeatherMapDataSource mInstance = null;
    private Context mContext;
    private String mAPIKey;
    private SharedPreferences mSharedPreference;

    private OpenWeatherMapDataSource(Context context) {
        mContext = context;
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        mAPIKey = mSharedPreference.getString(context.getString(R.string.pref_source_enter_key_key), "");
    }

    static OpenWeatherMapDataSource getInstance(Context ctx) {
        if (mInstance == null)
            mInstance = new OpenWeatherMapDataSource(ctx);
        return mInstance;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.OPENWEATHERMAP;
    }

    @Override
    public void setAPIKey(String key) {
        mAPIKey = key;
    }

    @Override
    public void fetchCurrentWeatherByCity(String city, Callback callback) {

        fetchByCity(BASE_URL_CURRENTWEATHER, city, callback);
    }

    private String getLanguage() {
        return mSharedPreference.getString(mContext.getString(R.string.pref_general_language_key), "");
    }


    private void fetchByCity(String baseURL, String city, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("%sq=%s&APPID=%s&lang=%s&units=metric", baseURL, city, mAPIKey, getLanguage());
        Request request = new Request.Builder().
                url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    private void fetchByCoordinates(String baseURL, double lat, double lon, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String url = String.format("%slat=%.4f&lon=%.4f&APPID=%s&lang=%s&units=metric", baseURL, lat, lon, mAPIKey, getLanguage());
        Request request = new Request.Builder().
                url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    @Override
    public void fetchCurrentWeatherByCoordinates(double lat, double lon, Callback callback) {
        fetchByCoordinates(BASE_URL_CURRENTWEATHER, lat, lon, callback);
    }

    @Override
    public void fetchForecastByCity(String city, Callback callback) {
        fetchByCity(BASE_URL_FORECAST, city, callback);
    }

    @Override
    public void fetchForecastByCoordinates(double lat, double lon, Callback callback) {
        fetchByCoordinates(BASE_URL_FORECAST, lat, lon, callback);
    }
}
