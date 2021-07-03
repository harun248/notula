package com.alrosyid.notula.activities.points;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.photos.EditPhotosActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.photos.PhotosListFragment;
import com.alrosyid.notula.fragments.points.PointFragment;
import com.alrosyid.notula.models.Photos;
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
    private SharedPreferences sharedPreferences;
    private TextInputLayout lytPoints;
    private TextInputEditText txtPoints;
    private ProgressDialog dialog;
    private int pointsId = 0, position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_points);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_point
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
        position = getIntent().getIntExtra("position", 0);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                update();
            }
        });

        getPoints();
    }

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
//    private void getPhotos() {
//        Integer id_photo = getIntent().getIntExtra("photosId", 0);
//        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_PHOTOS + (id_photo), response -> {
//
//            try {
//                JSONObject object = new JSONObject(response);
//
//                if (object.getBoolean("success")) {
//                    JSONArray array = new JSONArray(object.getString("photos"));
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject photos = array.getJSONObject(i);
//
//                        txtTitle.setText(photos.getString("title"));
//                    }
//                }
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }, error -> {
//            error.printStackTrace();
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = sharedPreferences.getString("token", "");
//                HashMap<String, String> map = new HashMap<>();
//                map.put("Authorization", "Bearer " + token);
//                return map;
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(EditPhotosActivity.this);
//        queue.add(request);
//    }

//    private void update() {
//        dialog.setMessage(getString(R.string.update));
//        dialog.show();
//        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_PHOTOS, response -> {
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getBoolean("success")) {
//                    Photos photo = PhotosListFragment.arrayList.get(position);
//
//                    photo.setTitle(txtTitle.getText().toString());
//
//                    PhotosListFragment.arrayList.set(position, photo);
//                    PhotosListFragment.recyclerView.getAdapter().notifyItemChanged(position);
//                    PhotosListFragment.recyclerView.getAdapter().notifyDataSetChanged();
//                    Toast.makeText(this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
//                    finish();
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }, error -> {
//            error.printStackTrace();
//        }) {
//
//            //add token to header
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = sharedPreferences.getString("token", "");
//                HashMap<String, String> map = new HashMap<>();
//                map.put("Authorization", "Bearer " + token);
//                return map;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> map = new HashMap<>();
//                map.put("id", photosId + "");
//                map.put("title", txtTitle.getText().toString());
//                return map;
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(EditPhotosActivity.this);
//        queue.add(request);
//    }




    //validasi


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