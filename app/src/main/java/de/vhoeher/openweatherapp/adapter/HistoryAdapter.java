package de.vhoeher.openweatherapp.adapter;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.model.WeatherDataModel;

/**
 * Interface to handle a click on an item of the history view.
 *
 * @author Victor Hoeher
 * @version 1.0
 */
interface IHistoryClickListener {

    /**
     * Handle click on an item of the RecyclerView
     *
     * @param position Position of the clicked item
     */
    void onHistoryClick(int position);
}

/**
 * Class which extends the RecyclerView.Adapter for the History View.
 * It handles the clicks on the item and calls a given listener if a model is clicked.
 *
 * @author Victor Hoeher
 * @version 1.0
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> implements IHistoryClickListener {

    private ArrayList<WeatherDataModel> mHistory;
    private DateFormat mDateFormat;
    private IHistoryItemClickListener mListener;

    /**
     * Constructor for the HistoryAdapter
     *
     * @param history  An List of historic WeatherDataModels, which should be presented
     * @param context  The context, in which the Adapter should be used.
     * @param listener A listener, which is called, when an item is clicked.
     */
    public HistoryAdapter(ArrayList<WeatherDataModel> history, Context context, IHistoryItemClickListener listener) {
        mListener = listener;
        mHistory = history;
        String dateFormat = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_general_dateformat_key),
                context.getString(R.string.pref_general_dateformat_dd_mm_yyyy));
        mDateFormat = new SimpleDateFormat(dateFormat);
        mDateFormat.setTimeZone(TimeZone.getDefault());
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_weather_card_view, viewGroup, false);
        return new HistoryViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder historyViewHolder, int i) {

        //Set the values for the ViewHolder
        WeatherDataModel model = mHistory.get(i);
        historyViewHolder.mIconView.setImageResource(model.getIconID());
        historyViewHolder.mLocationTV.setText(model.getLocationName());
        historyViewHolder.mTemperatureTV.setText(model.getTemperature());
        Date date = new Date(model.getRequestTimestamp() * 1000);
        historyViewHolder.mTimestampTV.setText(mDateFormat.format(date));

        historyViewHolder.mDetailsPanel.setVisibility(View.GONE);
        historyViewHolder.mDivider.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }

    @Override
    public void onHistoryClick(int position) {
        //Call the listener
        if (mListener == null)
            return;
        mListener.modelClicked(mHistory.get(position));
    }

    /**
     * Interface to handle a click on an item of the history view.
     *
     * @author Victor Hoeher
     * @version 1.0
     */
    public interface IHistoryItemClickListener {

        /**
         * Handler for a click on an item.
         *
         * @param model The model, on which the clicked item bases.
         */
        void modelClicked(WeatherDataModel model);
    }


    /**
     * ViewHolder for the HistoryAdapter.
     *
     * @author Victor Hoeher
     * @version 1.0
     */
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        protected TextView mLocationTV, mTemperatureTV, mTimestampTV;
        protected View mDivider;
        protected LinearLayout mDetailsPanel;
        protected ImageView mIconView;

        /**
         * Constructor of the ViewHolder
         *
         * @param itemView The view of a single item
         * @param listener Listener, which should be called, when an item is clicked.
         */
        public HistoryViewHolder(@NonNull View itemView, final IHistoryClickListener listener) {
            super(itemView);

            //Find views
            mLocationTV = itemView.findViewById(R.id.tv_city);
            mTemperatureTV = itemView.findViewById(R.id.tv_temperature);
            mTimestampTV = itemView.findViewById(R.id.tv_condition);
            mDivider = itemView.findViewById(R.id.divider2);
            mDetailsPanel = itemView.findViewById(R.id.detail_panel);
            mIconView = itemView.findViewById(R.id.iv_weather);

            //Set Margins
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(itemView.getLayoutParams());
            marginParams.setMargins(5, 5, 5, 5);
            itemView.setLayoutParams(marginParams);

            //Set ClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION)
                            listener.onHistoryClick(position);
                    }
                }
            });
        }
    }
}
