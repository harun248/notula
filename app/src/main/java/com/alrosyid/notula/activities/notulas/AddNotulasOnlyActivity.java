package com.alrosyid.notula.activities.notulas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.notulas.NotulasListFragment;
import com.alrosyid.notula.models.Meetings;
import com.alrosyid.notula.models.Notula;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNotulasOnlyActivity extends AppCompatActivity {

    private Button btnSave;
    private TextInputLayout layoutTitle ,layoutMeetings;
    private TextInputEditText txtTitle , txtMeetings;
    private ProgressDialog dialog;
    private int meetingsId = 0;
    private SharedPreferences sharedPreferences;



    public static ArrayList<Meetings> arrayList;

    //TextViews to display details
    private TextView txtTitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dd_notulas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_notula);



        //Adding an Item Selected Listener to our Spinner
        //As we have implemented the class Spinner.OnItemSelectedListener to this class iteself we are passing this to setOnItemSelectedListener
//        spinner.setOnItemSelectedListener(AddNotulasActivity.this);
//        getData();

        init();
    }
    private void init(){
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);


        //Initializing Spinner
//        spinner = (Spinner) findViewById(R.id.spinner);
        layoutTitle = findViewById(R.id.tilTitle);
        txtTitle = findViewById(R.id.tieTitle);
        layoutMeetings = findViewById(R.id.tilMeetings);
        txtMeetings = findViewById(R.id.tieMeetings);
        meetingsId = getIntent().getIntExtra("meetingsId", 0);


        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                create();
            }
        });

    }

    private boolean validate() {
        if (txtTitle.getText().toString().isEmpty()) {
            layoutTitle.setErrorEnabled(true);
            layoutTitle.setError(getString(R.string.required));
            return false;
        }

        return true;
    }
    private void create() {
        dialog.setMessage(getString(R.string.save_load));
        dialog.show();
        String titleText = txtTitle.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_NOTULA, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject notulaObject = object.getJSONObject("notulas");

                    Notula notula = new Notula();
                    notula.setId(notulaObject.getInt("id"));
                    notula.setTitle(notulaObject.getString("title"));

                    NotulasListFragment.arrayList.add(0, notula);
                    NotulasListFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    NotulasListFragment.recyclerView.getAdapter().notifyDataSetChanged();

                    Toast.makeText(this, R.string.added_successfully, Toast.LENGTH_SHORT).show();
                    finish();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }) {

            // add token to header


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");

                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);

                return map;
            }

            // add params
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("meetings_id", meetingsId + "");
                map.put("title", titleText);
                return map;
            }


        };

        RequestQueue queue = Volley.newRequestQueue(AddNotulasOnlyActivity.this);
        queue.add(request);

    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
