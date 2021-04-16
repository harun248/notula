package com.alrosyid.notula.fragments.notula;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alrosyid.notula.DetailNotulaActivity;
import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.notula.AddNotulaActivity;
import com.alrosyid.notula.adapters.NotulasAdapter;
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

public class NotulaFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Notula> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private NotulasAdapter notulasAdapter;
    private SharedPreferences sharedPreferences;
    CardView detailNotula;
    FloatingActionButton addNotula;
    public NotulaFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notula,container,false);
        init();
        return view;
    }
    private void init(){
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeHome);

        setHasOptionsMenu(true);

        getNotula();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotula();
            }
        });

        detailNotula=(CardView)view.findViewById(R.id.cvNotula);
        detailNotula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetailNotulaActivity();
            }
            private void getDetailNotulaActivity() {
                Intent intent = new Intent(getActivity(), DetailNotulaActivity.class);
                startActivity(intent);
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

                    notulasAdapter = new NotulasAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(notulasAdapter);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notulasAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}