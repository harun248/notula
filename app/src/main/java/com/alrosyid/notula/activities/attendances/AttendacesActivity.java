package com.alrosyid.notula.activities.attendances;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.adapters.AttendancesAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Attendances;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendacesActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static ArrayList<Attendances> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private TextView txtMeetingsTitle,txtDataEmpty;
    private AttendancesAdapter attendancesAdapter;
    private Button addAttendances;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendaces);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Daftar Kehadiran");
        init();

    }

    private void init(){
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerAttendances);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.swipeHome);
        txtMeetingsTitle = findViewById(R.id.tvMeetingsTitle);
        String title_meetings = getIntent().getStringExtra("meetingsTitle");
        txtMeetingsTitle.setText(title_meetings);


        getAttendances();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAttendances();
            }
        });

        addAttendances = findViewById(R.id.btnAdd);
        addAttendances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title_meetings = getIntent().getStringExtra("meetingsTitle");
                Integer id_meetings = getIntent().getIntExtra("meetingsId",0);
                Intent i = new Intent(AttendacesActivity.this, AddAttendancesActivity.class);
                i.putExtra("meetingsId", (id_meetings));
                i.putExtra("meetingsTitle", (title_meetings));
                startActivity(i);

            }

        });
    }

    private void getAttendances() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_meetings = getIntent().getIntExtra("meetingsId",0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_ATTENDANCES+(id_meetings), response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("attendances"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attendancesObject = array.getJSONObject(i);
                        Attendances attendances = new Attendances();
                        attendances.setId(attendancesObject.getInt("id"));
                        attendances.setName(attendancesObject.getString("name"));
                        attendances.setPosition(attendancesObject.getString("position"));
                        arrayList.add(attendances);
                    }

                    attendancesAdapter = new AttendancesAdapter(this,arrayList);
                    recyclerView.setAdapter(attendancesAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        },error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }){

            // provide token in header

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AttendacesActivity.this);
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

    public void addComment(View view) {
    }
}