package com.alrosyid.notula.fragments.meetings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
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


public class DetailMeetingsFragment extends Fragment {

    private TextInputLayout layoutTitle,  layoutDate ,layoutStartTime ,layoutEndTime ,layoutAgenda,layoutLocation;
    private TextInputEditText txtTitle, txtDate, txtStartTime, txtEndTime, txtAgenda,txtLocation;
    private int notulaId = 0, position =0;
    private View view;
    private SharedPreferences sharedPreferences;
    public static DetailMeetingsFragment newInstance() {

        Bundle args = new Bundle();

        DetailMeetingsFragment fragment = new DetailMeetingsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public DetailMeetingsFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_meetings,container,false);
        init();
        return view;

    }
    private void init(){
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutTitle = view.findViewById(R.id.tilTitle);
        layoutDate =  view.findViewById(R.id.tilDate);
        layoutStartTime =  view.findViewById(R.id.tilStartTime);
        layoutEndTime =  view.findViewById(R.id.tilEndTime);
        layoutAgenda =  view.findViewById(R.id.tilAgenda);
        layoutLocation =  view.findViewById(R.id.tilLocation);
        txtTitle =  view.findViewById(R.id.tieTitle);
        txtDate =  view.findViewById(R.id.tieDate);
        txtStartTime =  view.findViewById(R.id.tieStartTime);
        txtEndTime =  view.findViewById(R.id.tieEndTime);
        txtAgenda =  view.findViewById(R.id.tieAgenda);
        txtLocation =  view.findViewById(R.id.tieLocation);


        setHasOptionsMenu(true);

        getDetailMeetings();




    }


    private void  getDetailMeetings() {
        Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId",0);
        StringRequest request = new StringRequest(Request.Method.GET,Constant.DETAIL_MEETING+(id_meetings),response->{

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("meetings"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject meetings = array.getJSONObject(i);

                        txtTitle.setText(meetings.getString("title"));
                        txtDate.setText(meetings.getString("date"));
//                        String source = meetings.getString("date");
//                        String[] sourceSplit= source.split("-");
//                        int anno= Integer.parseInt(sourceSplit[0]);
//                        int mese= Integer.parseInt(sourceSplit[1]);
//                        int giorno= Integer.parseInt(sourceSplit[2]);
//                        GregorianCalendar calendar = new GregorianCalendar();
//                        calendar.set(anno,mese-1,giorno);
//                        Date data1= calendar.getTime();
//                        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");
//                        String   dayFormatted= myFormat.format(data1);
//                        txtDate.setText(dayFormatted);
                        txtTitle.setText(meetings.getString("title"));

                        String startTime = meetings.getString("start_time");

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
                            txtStartTime.setText(stOutput);
                        }catch(ParseException pe){
                            pe.printStackTrace();
                        }

                        String endTime = meetings.getString("start_time");

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
                            txtEndTime.setText(enOutput);
                        }catch(ParseException pe){
                            pe.printStackTrace();
                        }
                        txtAgenda.setText(meetings.getString("agenda"));
                        txtLocation.setText(meetings.getString("location"));


                    }
                }


            }
            catch (JSONException e) {
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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }


}