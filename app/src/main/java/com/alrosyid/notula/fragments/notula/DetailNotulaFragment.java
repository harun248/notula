package com.alrosyid.notula.fragments.notula;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alrosyid.notula.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailNotulaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailNotulaFragment extends Fragment {

    public DetailNotulaFragment() {
        // Required empty public constructor
    }

    public static DetailNotulaFragment newInstance() {

        Bundle args = new Bundle();

        DetailNotulaFragment fragment = new DetailNotulaFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_notula, container, false);
    }
}