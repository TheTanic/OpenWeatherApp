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
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements HistoryAdapter.IHistoryItemClickListener {


    ArrayList<WeatherDataModel> mHistory;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rv_history);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        mHistory = HistoryUtil.getInstance(view.getContext()).getHistory();

        HistoryAdapter adapter = new HistoryAdapter(mHistory, getContext(), this);
        recyclerView.setAdapter(adapter);
        return view;
        }

    @Override
    public void modelClicked(WeatherDataModel model) {
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
