package com.alrosyid.notula.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.alrosyid.notula.R;
import com.alrosyid.notula.fragments.auth.SignInFragment;


public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new SignInFragment()).commit();
    }
}