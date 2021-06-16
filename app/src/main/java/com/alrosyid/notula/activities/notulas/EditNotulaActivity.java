package com.alrosyid.notula.activities.notulas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.attendances.EditAttendancesActivity;
import com.alrosyid.notula.activities.meetings.EditMeetingsActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.attendances.AttendancesListFragments;
import com.alrosyid.notula.fragments.meetings.MeetingsFragment;
import com.alrosyid.notula.fragments.notulas.NotulasListFragment;
import com.alrosyid.notula.models.Attendances;
import com.alrosyid.notula.models.Meetings;
import com.alrosyid.notula.models.Notula;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class EditNotulaActivity extends AppCompatActivity {
    private Button btnSave;
    private TextInputLayout layoutTitle;
    private TextInputEditText txtTitle;
    private ProgressDialog dialog;
    private int notulasId = 0 ,position =0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notula);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_notula);
        init();
    }
    private void init(){
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);
        layoutTitle = findViewById(R.id.tilTitle);
        txtTitle = findViewById(R.id.tieTitle);
        position = getIntent().getIntExtra("position",0);
        notulasId = getIntent().getIntExtra("notulasId", 0);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                update();
            }
        });

        getNotulas();
    }

    private boolean validate() {
        if (txtTitle.getText().toString().isEmpty()) {
            layoutTitle.setErrorEnabled(true);
            layoutTitle.setError(getString(R.string.required));
            return false;
        }

        return true;
    }
    private void  getNotulas() {
        Integer id_notula = getIntent().getIntExtra("notulasId",0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_NOTULA+ (id_notula), response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("notulas"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject notula = array.getJSONObject(i);

                        txtTitle.setText(notula.getString("title"));
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error -> {
            error.printStackTrace();
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditNotulaActivity.this);
        queue.add(request);
    }
    private void update() {
        dialog.setMessage(getString(R.string.update));
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.UPDATE_NOTULA, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    Notula notula = NotulasListFragment.arrayList.get(position);

                    notula.setTitle(txtTitle.getText().toString());

                    NotulasListFragment.arrayList.set(position,notula);
                    NotulasListFragment.recyclerView.getAdapter().notifyItemChanged(position);
                    NotulasListFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }){

            //add token to header

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id",notulasId+"");
                map.put("title",txtTitle.getText().toString());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(EditNotulaActivity.this);
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