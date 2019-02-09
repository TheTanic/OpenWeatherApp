package de.vhoeher.openweatherapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.vhoeher.openweatherapp.R;

/**
 * Fragment, which shows the about informations(License etc.)
 *
 * @author Victor Höher
 * @version 1.0
 */
public class AboutFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}
