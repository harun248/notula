package com.alrosyid.notula.activities.points;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.points.PointFragment;
import com.alrosyid.notula.models.Points;
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

public class AddPointsActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout lytPoints;
    private TextInputEditText txtPoints;
    private ProgressDialog dialog;
    private int notulasId = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_points);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_point);

        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        lytPoints = findViewById(R.id.tilPoints);
        txtPoints = findViewById(R.id.tiePoints);
        notulasId = getIntent().getIntExtra("notulasId", 0);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                create();
            }
        });


    }
//validasi
    private boolean validate() {
        if (txtPoints.getText().toString().isEmpty()) {
            lytPoints.setErrorEnabled(true);
            lytPoints.setError(getString(R.string.required));
            return false;
        }
        if (txtPoints.getText().toString().trim().length() >200) {
            lytPoints.setErrorEnabled(true);
            lytPoints.setError(getString(R.string.maximum_character));
            return false;
        }
        return true;
    }

    private void create() {
        dialog.setMessage(getString(R.string.save));
        dialog.show();
        String pointsText = txtPoints.getText().toString();
        Integer id_notulas = getIntent().getIntExtra("notulasId",0);
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_POINTS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {


                    JSONObject pointsObject = object.getJSONObject("points");
                    Points points = new Points();
                    points.setId(pointsObject.getInt("id"));
                    points.setPoints(pointsObject.getString("points"));
                    PointFragment.arrayList.add(0, points);
                    PointFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    PointFragment.recyclerView.getAdapter().notifyDataSetChanged();

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
                map.put("notulas_id",id_notulas+ "");
                map.put("points", pointsText);
                return map;
            }


        };

        RequestQueue queue = Volley.newRequestQueue(AddPointsActivity.this);
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