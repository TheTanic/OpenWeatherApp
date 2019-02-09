package de.vhoeher.openweatherapp.util;

import android.content.Context;

import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import java.text.DateFormat;
import java.text.NumberFormat;

public class AxisLabelFormatter extends DateAsXAxisLabelFormatter {

    public AxisLabelFormatter(Context context, DateFormat dateFormat) {
        super(context, dateFormat);
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(0);
        mNumberFormatter[0] = format;
    }
}
