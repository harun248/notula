package com.alrosyid.notula.activities.meetings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.alrosyid.notula.R;
import com.alrosyid.notula.adapters.DetailMeetingsAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Meetings;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DetailMeetingsActivity extends AppCompatActivity {
    private TextInputLayout lytTitle, lytAgenda, lytStartTime, lytEndTime, lytDate;

    private TextInputEditText txtTitle, txtAgenda, txtStartTime, txtEndTime, txtDate;

    private RecyclerView recyclerView;
    private ArrayList<Meetings> list;
    private SwipeRefreshLayout refreshLayout;
    private DetailMeetingsAdapter adapter;
    private int meetingsId = 0;
    public  static  int meetingsPosition = 0;
    private SharedPreferences preferences;

    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_meetings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Rapat");


        init();
    }
    private void init(){
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        meetingsPosition = getIntent().getIntExtra("meetingsPosition",-1);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = findViewById(R.id.swipeHome);
        meetingsId = getIntent().getIntExtra("meetingsId",0);
        getMeets();


    }
    private void getMeets() {
        list = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_meetings = getIntent().getIntExtra("meetingsId",0);
        StringRequest request = new StringRequest(Request.Method.GET,Constant.DETAIL_MEETING+(id_meetings),res->{

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("meetings"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject meeting = array.getJSONObject(i);


                        Meetings meetings = new Meetings();
                        meetings.setId(meeting.getInt("id"));
                        meetings.setTitle(meeting.getString("title"));
                        meetings.setAgenda(meeting.getString("agenda"));
                        String source = meeting.getString("date");
                        String[] sourceSplit= source.split("-");
                        int anno= Integer.parseInt(sourceSplit[0]);
                        int mese= Integer.parseInt(sourceSplit[1]);
                        int giorno= Integer.parseInt(sourceSplit[2]);
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.set(anno,mese-1,giorno);
                        Date data1= calendar.getTime();
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");

                        String   dayFormatted= myFormat.format(data1);
                        meetings.setDate(dayFormatted);
                        String startTime = meeting.getString("start_time");

                        DateFormat df = new SimpleDateFormat("HH:mm:ss");
                        //Desired format: 24 hour format: Change the pattern as per the need
                        DateFormat outputformat = new SimpleDateFormat("HH:mm");
                        Date stInput = null;
                        String stOutput = null;
                        try{
                            //Converting the input String to time
                            stInput= df.parse(startTime);
                            //Changing the format of date and storing it in String
                            stOutput = outputformat.format(stInput);
                            //Displaying the time
                            meetings.setStart_time(stOutput);
                        }catch(ParseException pe){
                            pe.printStackTrace();
                        }
                        String endTime = meeting.getString("end_time");
                        DateFormat endDf = new SimpleDateFormat("HH:mm:ss");
                        //Desired format: 24 hour format: Change the pattern as per the need
                        DateFormat endOutputformat = new SimpleDateFormat("HH:mm");
                        Date enInput = null;
                        String enOutput = null;
                        try{
                            //Converting the input String to time
                            enInput= endDf.parse(endTime);
                            //Changing the format of date and storing it in String
                            enOutput = endOutputformat.format(enInput);
                            //Displaying the time
                            meetings.setEnd_time(enOutput);
                        }catch(ParseException pe){
                            pe.printStackTrace();
                        }


                        list.add(meetings);
                    }
                }

                adapter = new DetailMeetingsAdapter(DetailMeetingsActivity.this,list);
                recyclerView.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        },error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String,String> map = new HashMap<>();
//                map.put("id",meetingsId+"");
//                return map;
//            }
        };

        RequestQueue queue = Volley.newRequestQueue(DetailMeetingsActivity.this);
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