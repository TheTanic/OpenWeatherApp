package de.vhoeher.openweatherapp.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.datasource.DataSourceFactory;
import de.vhoeher.openweatherapp.datasource.IDataSource;
import de.vhoeher.openweatherapp.model.ForecastModel;
import de.vhoeher.openweatherapp.model.WeatherDataModel;
import de.vhoeher.openweatherapp.util.HistoryUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * The HomeFragment. This Fragment gives the user the opportunity to perform requests to get weather data.
 *
 * @author Victor Höher
 * @version 1.0
 */
public class HomeFragment extends Fragment {

    TextInputEditText mLocationET = null;
    SwitchCompat mUseGPSLocationSwitch = null;
    LocationManager mLocationManager = null;
    AlertDialog mAlertDialog = null;
    SharedPreferences mSharedPreferences = null;
    WifiManager mWifiManager = null;
    Callback mCurrentWeatherCallback = null;
    Callback mForecastCallback = null;
    HistoryUtil mHistoryUtil;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //Get the child views
        mLocationET = view.findViewById(R.id.et_location);
        mUseGPSLocationSwitch = view.findViewById(R.id.use_gps_location);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mHistoryUtil = HistoryUtil.getInstance(getContext().getApplicationContext());

        //Switch for the GPS usage
        mUseGPSLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mLocationET.setEnabled(!isChecked);
                if (isChecked) {
                    if (!checkGPS())
                        showNoGPSDialog();
                }
            }
        });

        //Get the Buttons and set the listeners
        Button currentWeatherBtn = view.findViewById(R.id.btn_current);
        currentWeatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData(true);
            }
        });

        Button forecastBtn = view.findViewById(R.id.btn_forecast);
        forecastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData(false);
            }
        });

        // Create the callback for the currentWeather requests.
        mCurrentWeatherCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showNotAbleToFetch();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                //Get the DataSource
                IDataSource source = DataSourceFactory.getDataSourceInstance(getContext());
                //Check if the code is valid
                if (!source.isResponseCodeValid(response.code()))
                    return;

                //Parse the response
                WeatherDataModel model = DataSourceFactory.getDataSourceInstance(getContext()).convertJSONToSingleData(response.body().string());
                if (model == null) {
                    showNotAbleToFetch();
                    return;
                }

                //Add the new data to the history
                mHistoryUtil.addDataToHistory(model);

                //Show the SingleWeatherDataFragment with the new data
                Fragment fragment = new SingleWeatherDataFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(SingleWeatherDataFragment.DATA_ARGUMENT, model);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        };

        // Create the callback for the weather forecast requests.
        mForecastCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showNotAbleToFetch();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Get the datasource
                IDataSource source = DataSourceFactory.getDataSourceInstance(getContext());
                //Check if the code is valid
                if (!source.isResponseCodeValid(response.code()))
                    return;

                //Parse the response
                ForecastModel model = source.convertJSONToForecast(response.body().string());
                if (model == null) {
                    showNotAbleToFetch();
                    return;
                }

                //Show the ForecastFragment
                Fragment fragment = new ForecastFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ForecastFragment.DATA_ARGUMENT, model);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        };

        return view;
    }

    /**
     * Shows a toast, which notifies the user, that the request was not successful.
     */
    private void showNotAbleToFetch() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), getString(R.string.not_able_to_fetch), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method to fetch the data.
     *
     * @param currentWeather True if the current weather should be fetched, False if the weather forecast should be fetched.
     */
    private void fetchData(boolean currentWeather) {

        //Check WLAN-Mode
        if (mSharedPreferences.getBoolean(getString(R.string.pref_connectivity_wlan_key), false)) {
            if (!checkWiFi())
                return;
        }

        //Check if GPS should be used
        if (!mUseGPSLocationSwitch.isChecked()) {
            //If the user did not enter a location, notify him that he should enter one
            if (mLocationET.getText().toString().isEmpty()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), getString(R.string.no_location_entered), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            //Perform request
            if (currentWeather)
                DataSourceFactory.getDataSourceInstance(getContext()).fetchCurrentWeatherByCity(mLocationET.getText().toString(), mCurrentWeatherCallback);
            else
                DataSourceFactory.getDataSourceInstance(getContext()).fetchForecastByCity(mLocationET.getText().toString(), mForecastCallback);
        } else {

            //Check if App is allowed to use fine location
            if (ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            try {
                //Get location
                Location gpsLocation = getLastKnownLocation();
                if (gpsLocation == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Show that the app was not able to get the current location
                            Toast.makeText(getContext(), R.string.no_gps_location, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                //Perform request
                if (currentWeather)
                    DataSourceFactory.getDataSourceInstance(getContext()).fetchCurrentWeatherByCoordinates(gpsLocation.getLatitude(), gpsLocation.getLongitude(), mCurrentWeatherCallback);
                else
                    DataSourceFactory.getDataSourceInstance(getContext()).fetchForecastByCoordinates(gpsLocation.getLatitude(), gpsLocation.getLongitude(), mForecastCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Getter for the last known location
     *
     * @return The last known location, or null if there is no location
     */
    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        //Check if the app is allowed to get the fine location
        if (ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        //Check all providers
        for (String provider : providers) {

            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    /**
     * Check if GPS is enabled
     *
     * @return True if GPS is enabled, False if not
     */
    private boolean checkGPS() {
        if (mLocationManager == null)
            mLocationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Check if WiFi is active.
     *
     * @return True if WiFi is active, False if not
     */
    private boolean checkWiFi() {

        if (mWifiManager == null)
            mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);

        boolean flag = mWifiManager.isWifiEnabled();
        //If WiFi is not active. Show the dialog
        if (!flag)
            showNoWifiDialog();
        return flag;
    }

    /**
     * Show the dialog, which informs the user, that the WiFi is off.
     */
    private void showNoWifiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.no_wifi_message)
                .setTitle(R.string.no_wifi_title)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUseGPSLocationSwitch.setChecked(false);
                        dialog.cancel();
                    }
                });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

    /**
     * Show the dialog, which informs the user, that GPS is not activated
     */
    private void showNoGPSDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.no_gps_message)
                .setTitle(R.string.no_gps_title)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUseGPSLocationSwitch.setChecked(false);
                        dialog.cancel();
                    }
                });
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }
}
