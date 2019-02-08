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
import de.vhoeher.openweatherapp.model.WeatherDataModel;
import de.vhoeher.openweatherapp.util.HistoryUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
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

    public HomeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mLocationET = view.findViewById(R.id.et_location);
        mUseGPSLocationSwitch = view.findViewById(R.id.use_gps_location);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mHistoryUtil = HistoryUtil.getInstance(getContext().getApplicationContext());

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

        mCurrentWeatherCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                IDataSource source = DataSourceFactory.getDataSourceInstance(getContext());
                if (!source.isResponseCodeValid(response.code()))
                    return;

                WeatherDataModel model = DataSourceFactory.getDataSourceInstance(getContext()).convertJSONToSingleData(response.body().string());
                if (model == null)
                    return;

                mHistoryUtil.addDataToHistory(model);

                Fragment fragment = new SingleWeatherDataFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", model);
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        };

        mForecastCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("Test", response.body().string());
            }
        };

        return view;
    }

    private void fetchData(boolean currentWeather) {
        if (mSharedPreferences.getBoolean(getString(R.string.pref_connectivity_wlan_key), false)) {
            if (!checkWiFi())
                return;
        }

        if (!mUseGPSLocationSwitch.isChecked())
            if (currentWeather)
                DataSourceFactory.getDataSourceInstance(getContext()).fetchCurrentWeatherByCity(mLocationET.getText().toString(), mCurrentWeatherCallback);
            else
                DataSourceFactory.getDataSourceInstance(getContext()).fetchCurrentWeatherByCity(mLocationET.getText().toString(), mForecastCallback);
        else {
            if (ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            try {
                Location gpsLocation = getLastKnownLocation();
                if(gpsLocation == null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), R.string.no_gps_location, Toast.LENGTH_SHORT);
                        }
                    });
                if (currentWeather)
                    DataSourceFactory.getDataSourceInstance(getContext()).fetchCurrentWeatherByCoordinates(gpsLocation.getLatitude(), gpsLocation.getLongitude(), mCurrentWeatherCallback);
                else
                    DataSourceFactory.getDataSourceInstance(getContext()).fetchForecastByCoordinates(gpsLocation.getLatitude(), gpsLocation.getLongitude(), mForecastCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Location getLastKnownLocation() {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
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

    private boolean checkGPS() {
        if (mLocationManager == null)
            mLocationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean checkWiFi() {

        if (mWifiManager == null)
            mWifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(getContext().WIFI_SERVICE);

        boolean flag = mWifiManager.isWifiEnabled();
        if (!flag)
            showNoWifiDialog();
        return mWifiManager.isWifiEnabled();
    }

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
