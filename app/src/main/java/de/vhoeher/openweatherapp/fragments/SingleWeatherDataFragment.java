package de.vhoeher.openweatherapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.model.WeatherDataModel;

public class SingleWeatherDataFragment extends Fragment {

    public SingleWeatherDataFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_weather_data, container, false);
        ImageView iv = view.findViewById(R.id.iv_weather);
        WeatherDataModel model = (WeatherDataModel) getArguments().getSerializable("data");
        iv.setImageResource(R.drawable.ic_clear_d);

        ((ImageView) view.findViewById(R.id.iv_weather)).setImageResource(model.getIconID());
        ((TextView) view.findViewById(R.id.tv_city)).setText(model.getLocationName());
        ((TextView) view.findViewById(R.id.tv_temperature)).setText(model.getTemperature());
        ((TextView) view.findViewById(R.id.tv_condition)).setText(model.getWeatherDescription());
        ((TextView) view.findViewById(R.id.tv_humidity)).setText(model.getHumidity());
        ((TextView) view.findViewById(R.id.tv_wind_speed)).setText(model.getWindSpeed());
        ((TextView) view.findViewById(R.id.tv_wind_direction)).setText(model.getWindDirection());
        ((TextView) view.findViewById(R.id.tv_pressure)).setText(model.getPressure());
        ((TextView) view.findViewById(R.id.tv_precipation)).setText(model.getmPrecipitation());

        String dateFormat = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(getString(R.string.pref_general_dateformat_key), getString(R.string.pref_general_dateformat_dd_mm_yyyy));
        if(dateFormat == null)
            return null;

        Date date = new Date(model.getDataTimestamp() * 1000);
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        TimeZone timeZone = TimeZone.getDefault();
        formatter.setTimeZone(TimeZone.getDefault());
        ((TextView) view.findViewById(R.id.tv_data_timestamp)).setText(formatter.format(date));
        date = new Date(model.getRequestTimestamp() * 1000);
        ((TextView) view.findViewById(R.id.tv_request_timestamp)).setText(formatter.format(date));
        return view;
    }
}
