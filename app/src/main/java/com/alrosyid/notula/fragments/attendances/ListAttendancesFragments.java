package com.alrosyid.notula.fragments.attendances;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.attendances.AddAttendancesActivity;
import com.alrosyid.notula.activities.attendances.AttendacesActivity;
import com.alrosyid.notula.activities.meetings.AddMeetingsActivity;
import com.alrosyid.notula.adapters.AttendancesAdapter;
import com.alrosyid.notula.adapters.PointsAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.notulas.PointFragment;
import com.alrosyid.notula.models.Attendances;
import com.alrosyid.notula.models.Points;
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

public class ListAttendancesFragments extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Attendances> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private Button addAttendances;
    private AttendancesAdapter attendancesAdapter;
    private SharedPreferences sharedPreferences;

    public ListAttendancesFragments() {
        // Required empty public constructor
    }

    public static ListAttendancesFragments newInstance() {

        Bundle args = new Bundle();

        ListAttendancesFragments fragment = new ListAttendancesFragments();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_attendances_fragments, container, false);
        init();
        return view;

    }

    private void init() {
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerAttandances);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeAttandances);
        setHasOptionsMenu(true);

        getAttendances();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAttendances();
            }
        });

        addAttendances = view.findViewById(R.id.btnAdd);
        addAttendances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddAttendancesActivity();

            }

            private void getAddAttendancesActivity() {
                String title_meetings = getActivity().getIntent().getStringExtra("meetingsTitle");
                Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
                Intent i = new Intent(getActivity(), AddAttendancesActivity.class);
                i.putExtra("meetingsId", (id_meetings));
                i.putExtra("meetingsTitle", (title_meetings));

                startActivity(i);
            }
        });
    }

    private void getAttendances() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_ATTENDANCES + (id_meetings), response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONArray array = new JSONArray(object.getString("attendances"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject attendancesObject = array.getJSONObject(i);
                        Attendances attendances = new Attendances();
                        attendances.setId(attendancesObject.getInt("id"));
                        attendances.setName(attendancesObject.getString("name"));
                        attendances.setPosition(attendancesObject.getString("position"));
                        arrayList.add(attendances);

                    }

                    attendancesAdapter = new AttendancesAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(attendancesAdapter);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
        }) {

            // provide token in header

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}