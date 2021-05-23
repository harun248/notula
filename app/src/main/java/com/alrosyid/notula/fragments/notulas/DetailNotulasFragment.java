package com.alrosyid.notula.fragments.notulas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.meetings.AddMeetingsActivity;
import com.alrosyid.notula.adapters.MeetingsAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class DetailNotulasFragment extends Fragment {
    private TextInputLayout layoutTitle, layoutMeetingsTitle, layoutDate;
    private TextInputEditText txtTitle, txtMeetingsTitle, txtDate;
    private int notulaId = 0, position =0;
    private View view;
    private SharedPreferences sharedPreferences;
    public static DetailNotulasFragment newInstance() {

        Bundle args = new Bundle();

        DetailNotulasFragment fragment = new DetailNotulasFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public DetailNotulasFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_notula,container,false);
        init();
        return view;

    }
    private void init(){
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutTitle = view.findViewById(R.id.tilTitle);
        layoutMeetingsTitle =  view.findViewById(R.id.tilMeetingsTitle);
        layoutDate =  view.findViewById(R.id.tilDate);
        txtTitle =  view.findViewById(R.id.tieTitle);
        txtMeetingsTitle =  view.findViewById(R.id.tieMeetingsTitle);
        txtDate =  view.findViewById(R.id.tieDate);
//        notulaId = getIntent().getIntExtra("notulaId",0);

        setHasOptionsMenu(true);

        getDetailNotulas();




    }


    private void  getDetailNotulas() {
        Integer id_notula = getActivity().getIntent().getIntExtra("notulaId",0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.MY_NOTULA+ (id_notula), response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("notulas"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attendance = array.getJSONObject(i);

                        txtTitle.setText(attendance.getString("title"));
                        txtMeetingsTitle.setText(attendance.getString("meetings_title"));
                        String source = attendance.getString("date");
                        String[] sourceSplit= source.split("-");
                        int anno= Integer.parseInt(sourceSplit[0]);
                        int mese= Integer.parseInt(sourceSplit[1]);
                        int giorno= Integer.parseInt(sourceSplit[2]);
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.set(anno,mese-1,giorno);
                        Date data1= calendar.getTime();
                        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");
                        String   dayFormatted= myFormat.format(data1);
                        txtDate.setText(dayFormatted);


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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }



}