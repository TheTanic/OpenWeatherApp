package de.vhoeher.openweatherapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.vhoeher.openweatherapp.R;
import de.vhoeher.openweatherapp.adapter.HistoryAdapter;
import de.vhoeher.openweatherapp.model.WeatherDataModel;
import de.vhoeher.openweatherapp.util.HistoryUtil;

/**
 * Fragment, which shows history of the made requests for the current weather.
 *
 * @author Victor Höher
 * @version 1.0
 */
public class HistoryFragment extends Fragment implements HistoryAdapter.IHistoryItemClickListener {


    private ArrayList<WeatherDataModel> mHistory;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        //Get the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rv_history);
        recyclerView.setHasFixedSize(true);
        //Set the LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //Get the history
        mHistory = HistoryUtil.getInstance(view.getContext()).getHistory();

        //Create the adapter
        HistoryAdapter adapter = new HistoryAdapter(mHistory, getContext(), this);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void modelClicked(WeatherDataModel model) {
        //Show the SingleWeatherDataFragment with the clicked model
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
}
