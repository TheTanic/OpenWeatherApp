package de.vhoeher.openweatherapp.datasource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.activities.SettingsActivity;
import de.vhoeher.openweatherapp.fragments.settings.SourceSettingsFragment;
import de.vhoeher.openweatherapp.model.WeatherDataModel;
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

    @Override
    public WeatherDataModel convertJSONToSingleData(String json) {

        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONObject obj = jsonObj.getJSONObject("main");
            String temperature = obj.has("temp") ? obj.getString("temp") + " °C" : " - ";
            String pressure = obj.has("pressure") ? obj.get("pressure") + " hPa" : " - ";
            String humidity = obj.has("humidity") ? obj.get("humidity") + " %" : " - ";
            obj = jsonObj.getJSONObject("wind");
            String windSpeed = obj.has("speed") ? obj.getString("speed") + " m/s" : " - ";
            String windDirection = obj.has("deg") ? obj.get("deg") + " °" : " - ";
            Long timestamp = jsonObj.getLong("dt");
            String location = jsonObj.getString("name");
            JSONArray arr = jsonObj.getJSONArray("weather");
            obj = arr.getJSONObject(0);
            String weatherDescription = obj.getString("description");
            int iconID = convertToIconID(obj.getString("icon"));

            String precipation = " - ";
            obj = null;
            if (jsonObj.has("snow")) {
                obj = jsonObj.getJSONObject("snow");
                precipation = "snow";
            } else if (jsonObj.has("rain")) {
                obj = jsonObj.getJSONObject("rain");
                precipation = "rain";
            }

            if (obj != null) {
                if (obj.has("1h")) {
                    precipation = obj.getString("1h") + " mm/h (" + precipation + ")";
                } else if (obj.has("3h")) {
                    precipation = obj.getString("3h") + " mm/3h (" + precipation + ")";
                } else {
                    precipation = " - ";
                }
            }

            WeatherDataModel model = new WeatherDataModel(location, humidity, windSpeed, windDirection, pressure, temperature,
                    precipation, weatherDescription, timestamp, System.currentTimeMillis() / 1000, iconID);
            return model;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isResponseCodeValid(int code) {
        if (200 <= code && code <= 300)
            return true;
        if (code == 401) {
            final Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage(R.string.no_api_key_message)
                            .setTitle(R.string.no_api_key_title)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(activity, SettingsActivity.class);
                                    intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SourceSettingsFragment.class.getName());
                                    activity.startActivity(intent);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        return false;
    }

    private int convertToIconID(String icon) {

        switch (icon) {
            case "01d":
                return R.drawable.ic_clear_d;
            case "01n":
                return R.drawable.ic_clear_n;
            case "02d":
                return R.drawable.ic_few_clouds_d;
            case "02n":
                return R.drawable.ic_few_clouds_n;
            case "03d":
            case "03n":
                return R.drawable.ic_scattered_clouds;
            case "04d":
            case "04n":
                return R.drawable.ic_broken_clouds;
            case "09d":
            case "09n":
                return R.drawable.ic_scattered_clouds;
            case "10d":
                return R.drawable.ic_rain_d;
            case "10n":
                return R.drawable.ic_rain_n;
            case "11d":
            case "11n":
                return R.drawable.ic_storm;
            case "13d":
            case "13n":
                return R.drawable.ic_snow;
            case "50d":
            case "50n":
                return R.drawable.ic_fog;
        }

        return -1;
    }
}
