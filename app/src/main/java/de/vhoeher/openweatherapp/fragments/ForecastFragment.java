package de.vhoeher.openweatherapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
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

/**
 * Fragment to show a forecast. The forecast is presented by a GraphView.
 *
 * @author Victor Höher
 * @version 1.0
 */
public class ForecastFragment extends Fragment {

    public static final String DATA_ARGUMENT = "data";
    private GraphView mGraph;
    private ArrayList<Date> mDates = new ArrayList<>();
    private DateFormat mDateFormat;
    private Calendar mCalendar = Calendar.getInstance();

    /**
     * Add a new series to the GraphView. Removes all other series.
     *
     * @param data       The data which should be presented
     * @param titleYAxis The label for the Y-Axis
     * @param unit       The unit of the values
     */
    private void addNewSeries(ArrayList<Double> data, String titleYAxis, String unit) {
        //Remove series
        mGraph.removeAllSeries();
        //Check if the size are equal
        if (data.size() != mDates.size())
            return;
        //Create DataPoints
        DataPoint[] points = new DataPoint[data.size()];
        for (int i = 0; i < mDates.size(); i++) {
            points[i] = new DataPoint(mDates.get(i), data.get(i));
        }

        //Create series
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        //Show the DataPoints
        series.setDrawDataPoints(true);
        final String unitForTap = unit;
        //Set Listener for taps on the DataPoints
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, final DataPointInterface dataPoint) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Show toasts with the data of the DataPoint
                        mCalendar.setTimeInMillis((long) dataPoint.getX());
                        Toast.makeText(getContext(), "[" + mDateFormat.format(mCalendar.getTime()) + " | " + dataPoint.getY() + " " + unitForTap + "]", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //Add series to the GraphView
        mGraph.addSeries(series);
        //Add the label for the Y-Axis
        mGraph.getGridLabelRenderer().setVerticalAxisTitle(titleYAxis + " (" + unit + ")");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate View
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        //Get DataFormat for DataPoint toasts
        String dateFormat = PreferenceManager.getDefaultSharedPreferences(getContext()).
                getString(getString(R.string.pref_general_dateformat_key), getString(R.string.pref_general_dateformat_dd_mm_yyyy));
        mDateFormat = new SimpleDateFormat(dateFormat);
        mDateFormat.setTimeZone(TimeZone.getDefault());
        //Create DataFormat for the labels on the X-Axis
        DateFormat format = new SimpleDateFormat(dateFormat.replace(" HH:mm:ss", "").replace(".yyyy", ""));
        format.setTimeZone(TimeZone.getDefault());

        //Get the model
        final ForecastModel model = (ForecastModel) getArguments().getSerializable(DATA_ARGUMENT);

        //Convert all the timestamps to dates
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
        //Get GraphView
        mGraph = view.findViewById(R.id.graph_forecast);

        // set date label formatter
        mGraph.getGridLabelRenderer().setLabelFormatter(new AxisLabelFormatter(getActivity(), format));
        mGraph.getGridLabelRenderer().setNumHorizontalLabels(5);
        mGraph.getGridLabelRenderer().setNumVerticalLabels(5);

        // set manual x bounds to have nice steps
        mGraph.getViewport().setMinX(first.getTime());
        mGraph.getViewport().setMaxX(last.getTime());
        mGraph.getViewport().setXAxisBoundsManual(true);

        //Set humand rounding to false, to have the dates formatted well
        mGraph.getGridLabelRenderer().setHumanRounding(false);

        //Set the title for the X-Axis
        mGraph.getGridLabelRenderer().setHorizontalAxisTitle(getString(R.string.forecast_xaxis));

        //Set padding, so that the labels are not cut off
        mGraph.getGridLabelRenderer().setPadding(50);

        // enables horizontal scrolling
        mGraph.getViewport().setScrollable(true);

        //Add default series (Temperature)
        addNewSeries(model.getTemperature(), getString(R.string.weather_data_temperature), "°C");

        //Get the spinner, to change the series
        Spinner spinner = view.findViewById(R.id.spinner_forecast);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        addNewSeries(model.getTemperature(), getString(R.string.weather_data_temperature), "°C");
                        break;
                    case 1:
                        addNewSeries(model.getWindSpeed(), getString(R.string.weather_data_wind_speed), "m/s");
                        break;
                    case 2:
                        addNewSeries(model.getHumidity(), getString(R.string.weather_data_humidity), "%");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //DO NOTHING
            }
        });

        //Set the title of the Fragment
        ((TextView) view.findViewById(R.id.forecast_title)).setText(model.getLocation());

        return view;
    }
}
