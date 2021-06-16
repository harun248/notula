package com.alrosyid.notula.fragments.photos;

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
import com.alrosyid.notula.activities.photos.AddPhotosActivity;
import com.alrosyid.notula.adapters.PhotosAdapter;
import com.alrosyid.notula.adapters.PhotosListAdapter;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Photos;
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

public class PhotosFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Photos> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private PhotosAdapter photosAdapter;
    private ImageButton addPhotos;
    private SharedPreferences sharedPreferences;
    public PhotosFragment() {
        // Required empty public constructor
    }
    public static PhotosFragment newInstance() {

        Bundle args = new Bundle();

        PhotosFragment fragment = new PhotosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_photos,container,false);
        init();
        return view;

    }

    private void init() {

        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        recyclerView = view.findViewById(R.id.recyclerPhotos);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipePhotos);
        setHasOptionsMenu(true);

        getPhotos();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPhotos();
            }
        });

        addPhotos =(ImageButton)view.findViewById(R.id.btnAdd);
        addPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddPhotosActivity();

            }

            private void getAddPhotosActivity() {

                Integer id_notulas = getActivity().getIntent().getIntExtra("notulasId", 0);
                Intent i = new Intent(getActivity(), AddPhotosActivity.class);
                i.putExtra("notulasId", (id_notulas));
                startActivity(i);
            }
        });
    }

    private void getPhotos()  {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.PHOTOS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("photos"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject photosObject = object.getJSONObject("photos");

                        Photos photos = new Photos();
                        photos.setId(photosObject.getInt("id"));
                        photos.setPhoto(photosObject.getString("photo"));
                        photos.setTitle(photosObject.getString("title"));


                        arrayList.add(photos);
                    }

                    photosAdapter = new PhotosAdapter(getContext(),arrayList);
                    recyclerView.setAdapter(photosAdapter);
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

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                photosAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}