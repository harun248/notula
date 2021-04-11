package com.alrosyid.notula.fragments.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alrosyid.notula.R;

public class SignUpSucessFragment extends Fragment {

    private View view;

    private Button btnSignIn;
    private ProgressDialog dialog;

    public SignUpSucessFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_up_success,container,false);
        init();
        return view;
    }

    private void init() {
        btnSignIn = view.findViewById(R.id.btnSignIn);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);


        btnSignIn.setOnClickListener(v -> {
            //validate fields first
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new SignInFragment()).commit();

        });


    }
}