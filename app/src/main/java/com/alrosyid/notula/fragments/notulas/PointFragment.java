package com.alrosyid.notula.fragments.notulas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alrosyid.notula.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PointFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PointFragment extends Fragment {

    public PointFragment() {
        // Required empty public constructor
    }

    public static PointFragment newInstance() {

        Bundle args = new Bundle();

        PointFragment fragment = new PointFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_point, container, false);
    }
}