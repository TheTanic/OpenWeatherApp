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

interface IHistoryClickListener {
    void onHistoryClick(int position);
}

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> implements IHistoryClickListener{

    private ArrayList<WeatherDataModel> mHistory;
    private DateFormat mDateFormat;
    private IHistoryItemClickListener mListener;

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
        if(mListener == null)
            return;
        mListener.modelClicked(mHistory.get(position));
    }

    public interface IHistoryItemClickListener{

        void modelClicked(WeatherDataModel model);
    }


    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        protected TextView mLocationTV, mTemperatureTV, mTimestampTV;
        protected View mDivider;
        protected LinearLayout mDetailsPanel;
        protected ImageView mIconView;

        public HistoryViewHolder(@NonNull View itemView, final IHistoryClickListener listener) {
            super(itemView);
            mLocationTV = itemView.findViewById(R.id.tv_city);
            mTemperatureTV = itemView.findViewById(R.id.tv_temperature);
            mTimestampTV = itemView.findViewById(R.id.tv_condition);
            mDivider = itemView.findViewById(R.id.divider2);
            mDetailsPanel = itemView.findViewById(R.id.detail_panel);
            mIconView = itemView.findViewById(R.id.iv_weather);

            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(itemView.getLayoutParams());
            marginParams.setMargins(5, 5, 5, 5);
            itemView.setLayoutParams(marginParams);
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
