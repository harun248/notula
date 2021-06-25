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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditPointsActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout lytPoints;
    private TextInputEditText txtPoints;
    private ProgressDialog dialog;
    private int pointsId = 0, notulasId = 0;
    private int position = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_points);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Point"
        );
        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        lytPoints = findViewById(R.id.tilPoints);
        txtPoints = findViewById(R.id.tiePoints);
        pointsId = getIntent().getIntExtra("pointsId", 0);
        notulasId = getIntent().getIntExtra("notulasId", 0);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                update();
            }
        });


        getPoints();


    }

    //validasi
    private boolean validate() {
        if (txtPoints.getText().toString().isEmpty()) {
            lytPoints.setErrorEnabled(true);
            lytPoints.setError(getString(R.string.required));
            return false;
        }
        if (txtPoints.getText().toString().trim().length() > 200) {
            lytPoints.setErrorEnabled(true);
            lytPoints.setError(getString(R.string.maximum_character));
            return false;
        }
        return true;
    }

    private void getPoints() {

        Integer id_points = getIntent().getIntExtra("pointsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_POINTS + (id_points), res -> {

            try {
                JSONObject object = new JSONObject(res);

                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("points"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attendance = array.getJSONObject(i);

                        txtPoints.setText(attendance.getString("points"));


                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditPointsActivity.this);
        queue.add(request);
    }

    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_POINTS, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    Points points = PointFragment.arrayList.get(position);

                    points.setPoints(txtPoints.getText().toString());
                    PointFragment.arrayList.set(position, points);
                    PointFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    PointFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {

            //add token to header

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", pointsId + "");
//                map.put("notulas_id",notulasId+"");
                map.put("points", txtPoints.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditPointsActivity.this);
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