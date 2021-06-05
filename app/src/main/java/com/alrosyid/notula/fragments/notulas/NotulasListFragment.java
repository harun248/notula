package com.alrosyid.notula.fragments.notulas;

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
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.notulas.AddNotulasActivity;
import com.alrosyid.notula.adapters.NotulasListAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Notula;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class NotulasListFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Notula> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private NotulasListAdapter notulasListAdapter;
    private SharedPreferences sharedPreferences;
    private ImageButton addNotulas;

    public NotulasListFragment(){}
    public static NotulasListFragment newInstance() {

        Bundle args = new Bundle();

        NotulasListFragment fragment = new NotulasListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notulas,container,false);
        init();
        return view;
    }
    private void init(){
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerNotulas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeNotulas);
        addNotulas =(ImageButton)view.findViewById(R.id.btnAdd);
        addNotulas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddNotulasActivity();

            }

            private void getAddNotulasActivity() {

                Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
                Intent i = new Intent(getActivity(), AddNotulasActivity.class);
                i.putExtra("meetingsId", (id_meetings));
                startActivity(i);
            }
        });

        setHasOptionsMenu(true);

        getNotula();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotula();
            }
        });




    }


    private void getNotula() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LIST_NOTULA + (id_meetings), response -> {


            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("notulas"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject notulaObject = array.getJSONObject(i);
                        Notula notula = new Notula();
                        notula.setId(notulaObject.getInt("id"));
                        notula.setTitle(notulaObject.getString("title"));
                        //covert string to date



                        arrayList.add(notula);
                    }

                    notulasListAdapter = new NotulasListAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(notulasListAdapter);
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
                notulasListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}