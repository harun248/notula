package com.alrosyid.notula.fragments.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alrosyid.notula.DetailNotulaActivity;
import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.notula.AddNotulaActivity;
import com.alrosyid.notula.adapters.HomeAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Notula> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private HomeAdapter homeAdapter;
    private TextView txtName,txtEmail;
    private SharedPreferences sharedPreferences;

    FloatingActionButton addNotula;

    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);
        init();
        return view;
    }
    private void init(){
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeHome);
        txtName = view.findViewById(R.id.tvName);
        txtEmail = view.findViewById(R.id.tvEmail);
        setHasOptionsMenu(true);

        getNotula();
        getUser();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotula();
            }
        });


        addNotula =(FloatingActionButton)view.findViewById(R.id.fab);
        addNotula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddNotulaActivity();

            }

            private void getAddNotulaActivity() {
                Intent intent = new Intent(getActivity(), AddNotulaActivity.class);
                startActivity(intent);

            }
        });
    }

    private void getUser() {
        arrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET,Constant.ACCOUNT,res->{

            try {
                JSONObject object = new JSONObject(res);
                if (object.getBoolean("success")){

                    JSONObject user = object.getJSONObject("user");
                    txtName.setText(user.getString("name"));
                    txtEmail.setText(user.getString("email"));


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

    private void getNotula() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.MY_NOTULA, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("notulas"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject notulaObject = array.getJSONObject(i);
                        Notula notula = new Notula();
                        notula.setId(notulaObject.getInt("id"));
                        notula.setSubject(notulaObject.getString("subject"));
                        notula.setTitle(notulaObject.getString("title"));
                        notula.setDate(notulaObject.getString("date"));


                        arrayList.add(notula);
                    }

                    homeAdapter = new HomeAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(homeAdapter);
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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}