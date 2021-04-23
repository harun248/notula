package com.alrosyid.notula.activities.notula;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alrosyid.notula.R;
import com.alrosyid.notula.adapters.NotulasByMeetsAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Notula;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class ListsNotulaActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static ArrayList<Notula> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private NotulasByMeetsAdapter notulasByMeetsAdapter;
    FloatingActionButton addNotula;
//    private int postId = 0;
//    public  static  int position = 0;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notulasbymeets);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List");


        init();
    }
    private void init(){
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.swipeHome);
//
//        setHasOptionsMenu(true);

        getMeets();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMeets();
            }
        });

//
        addNotula =(FloatingActionButton)findViewById(R.id.fab);
        addNotula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddNotulaActivity();

            }

            private void getAddNotulaActivity() {
                Intent intent = new Intent(ListsNotulaActivity.this, AddNotulaActivity.class);
                startActivity(intent);
            }
        });


    }
    private void getMeets() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_meetings = getIntent().getIntExtra("meetingsId",0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.MY_NOTULA_BY_MEETING+(id_meetings), response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("meetings"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject meetObject = array.getJSONObject(i);
                        Notula notula = new Notula();
                        notula.setId(meetObject.getInt("id"));
                        notula.setTitle(meetObject.getString("title"));
                        //covert string to date
                        notula.setMeetings_title(meetObject.getString("meetings_title"));
                        String source = meetObject.getString("date");
                        String[] sourceSplit= source.split("-");
                        int anno= Integer.parseInt(sourceSplit[0]);
                        int mese= Integer.parseInt(sourceSplit[1]);
                        int giorno= Integer.parseInt(sourceSplit[2]);
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.set(anno,mese-1,giorno);
                        Date data1= calendar.getTime();
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");

                        String   dayFormatted= myFormat.format(data1);
                        notula.setDate(dayFormatted);



                        arrayList.add(notula);
                    }

                    notulasByMeetsAdapter = new NotulasByMeetsAdapter(this,arrayList);
                    recyclerView.setAdapter(notulasByMeetsAdapter);
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

        RequestQueue queue = Volley.newRequestQueue(ListsNotulaActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notulasByMeetsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu);
        return true;
    }




}