package com.alrosyid.notula.activities.notulas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.notulas.NotulasListFragment;
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

import java.util.HashMap;
import java.util.Map;

public class AddNotulasActivity extends AppCompatActivity {

    private Button btnSave;
    private TextInputLayout layoutTitle;
    private TextInputEditText txtTitle;
    private ProgressDialog dialog;
    private int meetingsId = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notulas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_notula);

        init();
    }
    private void init(){
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        layoutTitle = findViewById(R.id.tilTitle);
        txtTitle = findViewById(R.id.tieTitle);
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

        RequestQueue queue = Volley.newRequestQueue(AddNotulasActivity.this);
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