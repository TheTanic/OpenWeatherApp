package de.vhoeher.openweatherapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.model.ForecastModel;
import de.vhoeher.openweatherapp.util.AxisLabelFormatter;

public class ForecastFragment extends Fragment {

    public static final String DATA_ARGUMENT = "data";
    private GraphView mGraph;
    private ArrayList<Date> mDates = new ArrayList<>();
    private DateFormat mDateFormat;
    private Calendar mCalendar = Calendar.getInstance();

    public ForecastFragment() {
    }

    private void addNewSeries(ArrayList<Double> data, String titleYAxis, String unit){
        mGraph.removeAllSeries();
        if(data.size() != mDates.size())
            return;
        DataPoint[] points = new DataPoint[data.size()];
        for(int i = 0; i < mDates.size(); i++){
            points[i] = new DataPoint(mDates.get(i), data.get(i));
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        series.setDrawDataPoints(true);
        final String unitForTap = unit;
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, final DataPointInterface dataPoint) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCalendar.setTimeInMillis((long) dataPoint.getX());
                        Toast.makeText(getContext(), "[" + mDateFormat.format(mCalendar.getTime())+ " | " + dataPoint.getY() + " " + unitForTap + "]", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        mGraph.addSeries(series);
        mGraph.getGridLabelRenderer().setVerticalAxisTitle(titleYAxis + " (" + unit + ")");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        String dateFormat = PreferenceManager.getDefaultSharedPreferences(getContext()).
                getString(getString(R.string.pref_general_dateformat_key), getString(R.string.pref_general_dateformat_dd_mm_yyyy));
        mDateFormat = new SimpleDateFormat(dateFormat);
        mDateFormat.setTimeZone(TimeZone.getDefault());
        DateFormat format = new SimpleDateFormat(dateFormat.replace(" HH:mm:ss", "").replace(".YYYY", ""));
        format.setTimeZone(TimeZone.getDefault());
        final ForecastModel model = (ForecastModel) getArguments().getSerializable(DATA_ARGUMENT);

        ArrayList<Long> timestamps = model.getTimestamp();
        Date first = null, last = null;
        for (int i = 0; i < timestamps.size(); i++) {
            Date date = new Date(timestamps.get(i) * 1000);
            mDates.add(date);
            if (i == 0)
                first = date;
            else if (i == timestamps.size() - 1)
                last = date;
        }
        mGraph = view.findViewById(R.id.graph_forecast);

        // set date label formatter
        mGraph.getGridLabelRenderer().setLabelFormatter(new AxisLabelFormatter(getActivity(), format));
        mGraph.getGridLabelRenderer().setNumHorizontalLabels(5);
        mGraph.getGridLabelRenderer().setNumVerticalLabels(5);

        // set manual x bounds to have nice steps
        mGraph.getViewport().setMinX(first.getTime());
        mGraph.getViewport().setMaxX(last.getTime());
        mGraph.getViewport().setXAxisBoundsManual(true);

        mGraph.getGridLabelRenderer().setHumanRounding(false);
        mGraph.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.forecast_xaxis));

        mGraph.getGridLabelRenderer().setPadding(50);

        mGraph.getViewport().setScrollable(true); // enables horizontal scrolling

        addNewSeries(model.getTemperature(), getString(R.string.weather_data_temperature), "°C");

        Spinner spinner = view.findViewById(R.id.spinner_forecast);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long  id) {
                switch (position){
                    case 0:
                        addNewSeries(model.getTemperature(), getString(R.string.weather_data_temperature), "°C");
                        break;
                    case 1:
                        addNewSeries(model.getWindSpeed(), getString(R.string.weather_data_wind_speed),"m/s");
                        break;
                    case 2:
                        addNewSeries(model.getHumidity(), getString(R.string.weather_data_humidity),"%");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //DO NOTHING
            }
        });

        ((TextView) view.findViewById(R.id.forecast_title)).setText(model.getLocation());

        return view;
    }
}
