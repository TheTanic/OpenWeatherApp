package de.vhoeher.openweatherapp.datasource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.activities.SettingsActivity;
import de.vhoeher.openweatherapp.fragments.settings.SourceSettingsFragment;
import de.vhoeher.openweatherapp.model.ForecastModel;
import de.vhoeher.openweatherapp.model.WeatherDataModel;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Class which implements the IDataSource interface and represents a DataSource to OpenWeatherMap.org.
 * This class is a singleton!
 *
 * @author Victor Hoeher
 * @version 1.0
 */
public class OpenWeatherMapDataSource implements IDataSource {

    private final String BASE_URL_CURRENT_WEATHER = "http://api.openweathermap.org/data/2.5/weather?";
    private final String BASE_URL_FORECAST = "http://api.openweathermap.org/data/2.5/forecast?";
    private static OpenWeatherMapDataSource mInstance = null;
    private Context mContext;
    private String mAPIKey;
    private SharedPreferences mSharedPreference;

    /**
     * Private constructor to follow the Singleton Design
     *
     * @param context The context, in which this DataSource is created.
     */
    private OpenWeatherMapDataSource(Context context) {
        mContext = context;
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        mAPIKey = mSharedPreference.getString(context.getString(R.string.pref_source_enter_key_key), "");
    }

    /**
     * Getter for the Singleton instance. Creates a new instance, if there is none present.
     *
     * @param ctx The context, in which the instance should be created.
     * @return The Singleton instance of this class
     */
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

        fetchByCity(BASE_URL_CURRENT_WEATHER, city, callback);
    }

    /**
     * Getter for the current language of the user.
     *
     * @return The string representation fo the language
     */
    private String getLanguage() {
        return mSharedPreference.getString(mContext.getString(R.string.pref_general_language_key), "");
    }


    /**
     * Fetch the resource by the city name
     *
     * @param baseURL  The base URL of the request
     * @param city     The city name
     * @param callback The callback, which should be called afterwards
     */
    private void fetchByCity(String baseURL, String city, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        //Create URL
        String url = String.format("%sq=%s&APPID=%s&lang=%s&units=metric", baseURL, city, mAPIKey, getLanguage());
        //Build request
        Request request = new Request.Builder().
                url(url)
                .build();
        //Perform request
        client.newCall(request).enqueue(callback);
    }

    /**
     * etch the resource by the given coordinates
     *
     * @param baseURL  The base URL of the request
     * @param lat      Latitude value of the coordinate
     * @param lon      Longitude value of the coordinate
     * @param callback The callback, which should be called afterwards
     */
    private void fetchByCoordinates(String baseURL, double lat, double lon, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        //Create URL
        String url = String.format("%slat=%.4f&lon=%.4f&APPID=%s&lang=%s&units=metric", baseURL, lat, lon, mAPIKey, getLanguage());
        //Build Request
        Request request = new Request.Builder().
                url(url)
                .build();
        //Perform request
        client.newCall(request).enqueue(callback);
    }

    @Override
    public void fetchCurrentWeatherByCoordinates(double lat, double lon, Callback callback) {
        fetchByCoordinates(BASE_URL_CURRENT_WEATHER, lat, lon, callback);
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

        //Try to parse the given json.
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
    public ForecastModel convertJSONToForecast(String json) {
        try {
            //Convert the json and collect the values of the different types
            ArrayList<Double> temperature = new ArrayList<>();
            ArrayList<Double> humidity = new ArrayList<>();
            ArrayList<Double> windSpeed = new ArrayList<>();
            ArrayList<Long> timestamp = new ArrayList<>();
            JSONObject rootObj = new JSONObject(json);
            JSONArray data = rootObj.getJSONArray("list");
            JSONObject obj = null;
            JSONObject childObject = null;
            String location = rootObj.getJSONObject("city").getString("name");
            for (int i = 0; i < data.length(); i++) {
                obj = data.getJSONObject(i);
                timestamp.add(obj.getLong("dt"));
                childObject = obj.getJSONObject("main");
                temperature.add(childObject.has("temp") ? childObject.getDouble("temp") : Double.MIN_VALUE);
                humidity.add(childObject.has("humidity") ? childObject.getDouble("humidity") : Double.MIN_VALUE);
                childObject = obj.getJSONObject("wind");
                windSpeed.add(childObject.has("speed") ? childObject.getDouble("speed") : Double.MIN_VALUE);
            }

            return new ForecastModel(timestamp, humidity, temperature, windSpeed, location);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isResponseCodeValid(int code) {
        //Code is valid when it is between 200 and 300
        if (200 <= code && code <= 300)
            return true;
        if (code == 401) {
            //This code shows that the API-Key is not valid
            //Show AlertDialog
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

    /**
     * Converts the icon name of OpenWeatherMap to the locale icon ids.
     *
     * @param icon The icon name of OpenWeatherMap
     * @return The id of the locale icon, or -1 if the icon is not found
     */
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
