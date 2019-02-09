package de.vhoeher.openweatherapp.util;

import android.content.Context;

import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;

import java.text.DateFormat;
import java.text.NumberFormat;

/**
 * Util class to change the format of the axis labels of a GraphView.
 * Extends the DateAsXAxisLabelFormatter and adds a NumberFormat with zero fraction digits for the Y-Axis.
 *
 * @author Victor Höher
 * @version 1.0
 */
public class AxisLabelFormatter extends DateAsXAxisLabelFormatter {

    public AxisLabelFormatter(Context context, DateFormat dateFormat) {
        super(context, dateFormat);
        //Set format for Y-Axis
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(0);
        mNumberFormatter[0] = format;
    }
}
