package com.alrosyid.notula.activities.notulas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.meetings.AddMeetingsHomeActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.notulas.NotulaFragment;
import com.alrosyid.notula.fragments.notulas.NotulasListFragment;
import com.alrosyid.notula.models.Meetings;
import com.alrosyid.notula.models.Notula;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNotulasOnlyActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    private Button btnSave;
    private TextInputLayout lytTitle, lytSummary, lytId;
    private TextInputEditText txtTitle, txtSummary,txtItemId;
    private ProgressDialog dialog;
    private int meetingsId = 0;
    private SharedPreferences sharedPreferences;
    private TextView txtItemTitle, txtItemdNull;


    Spinner spinner;
    String url = "https://app.jeevva.my.id/api/meetings/spinner";
    ArrayList<String> meeting_list = new ArrayList<String>();
    private JSONArray meetings;

//    public static ArrayList<Meetings> arrayList;
//    public static ArrayList<String> arrayList;

    //TextViews to display details
//    private TextView txtTitles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notulas_only);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_notula);

        spinner = (Spinner) findViewById(R.id.spinner);
        txtItemTitle = (TextView) findViewById(R.id.tvTitle);
        txtItemId = (TextInputEditText) findViewById(R.id.tvId);


        meeting_list = new ArrayList<String>();


        spinner.setOnItemSelectedListener(this);


        init();
    }

    private void init() {
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnSave = findViewById(R.id.btnSave);


        //Initializing Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
        lytTitle = findViewById(R.id.tilTitle);
        txtTitle = findViewById(R.id.tieTitle);
        lytSummary = findViewById(R.id.tilSummary);
        txtSummary = findViewById(R.id.tieSummary);
        lytId = findViewById(R.id.tilId);
        txtItemId = findViewById(R.id.tvId);
        meetingsId = getIntent().getIntExtra("meetingsId", 0);

        btnSave.setOnClickListener(v -> {
            //validate fields first
            if (validate()) {
                create();
//                NotulaFragment.recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        getData();
    }

    private void getData() {
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.SPINNER_MEETING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            meetings = j.getJSONArray(Constant.JSON_ARRAY);


                            //Calling method getStudents to get the students from the JSON Array
                            getMeetings(meetings);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//
//                });
                error -> {
                    error.printStackTrace();
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getMeetings(JSONArray j) {
        //Traversing through all the items in the json array

        for (int i = 0; i < j.length(); i++) {

            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);
//                if(json.length() >0){
                //Adding the name of the student to array list

                meeting_list.add(json.getString(Constant.MT_TITLE));
//                }else if(json.length() !=1){
//                    txtItemdNull.setText("No Meetings");
////                    txtItemdNull.setText("No Meetings");
//                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Setting adapter to show the items in the spinner
        spinner.setAdapter(new ArrayAdapter<String>(AddNotulasOnlyActivity.this, android.R.layout.simple_spinner_dropdown_item, meeting_list));
    }

    //    private String getTitle(int position){
//        String title="";
//        try {
//            //Getting object of given index
//            JSONObject json = meetings.getJSONObject(position);
//
//            //Fetching name from that object
//            title = json.getString(Constant.MT_TITLE);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        //Returning the name
//        return title;
//    }
    private String getId(int position) {
        String id = "";
        try {
            //Getting object of given index
            JSONObject json = meetings.getJSONObject(position);

            //Fetching name from that object
            id = json.getString(Constant.MT_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the name
        return id;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Setting the values to textviews for a selected item
//        txtItemTitle.setText(getTitle(position));
        txtItemId.setText(getId(position));
    }

    //When no item is selected this method would execute
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
//        txtItemTitle.setText("");
        txtItemId.setText("");
    }


    private boolean validate() {
        if (txtTitle.getText().toString().isEmpty()) {
            lytTitle.setErrorEnabled(true);
            lytTitle.setError(getString(R.string.required));
            return false;
        }
        if (txtItemId.getText().toString().isEmpty()) {
            lytId.setErrorEnabled(true);
            lytId.setError(getString(R.string.required_meeting));
            return false;
        }
        if (txtSummary.getText().toString().isEmpty()) {
            lytSummary.setErrorEnabled(true);
            lytSummary.setError(getString(R.string.required));
            return false;
        }
        if (txtSummary.getText().toString().trim().length() > 1500) {
            lytSummary.setErrorEnabled(true);
            lytSummary.setError(getString(R.string.max_1500));
            return false;
        }
        return true;
    }

    private void create() {
        dialog.setMessage(getString(R.string.save_load));
        dialog.show();
        String idText = txtItemId.getText().toString();
        String titleText = txtTitle.getText().toString();
        String summaryText = txtSummary.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_NOTULA, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject notulaObject = object.getJSONObject("notulas");

                    Notula notula = new Notula();
                    notula.setId(notulaObject.getInt("id"));
                    notula.setMeetings_id(notulaObject.getInt("meetings_id"));
                    notula.setTitle(notulaObject.getString("title"));
                    notula.setSummary(notulaObject.getString("summary"));
//
                    NotulaFragment.arrayList.add(0, notula);
                    NotulaFragment.recyclerView.getAdapter().notifyItemInserted(0);

                    NotulaFragment.recyclerView.getAdapter().notifyDataSetChanged();
//                    Intent restart = new Intent(AddNotulasOnlyActivity.this, DetailNotulaActivity.class);
//                    restart.putExtra("notulasId", notula.getId());
//                    startActivity(restart);

                    Toast.makeText(this, R.string.added_successfully, Toast.LENGTH_SHORT).show();
                    finish();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }) {

            // add token to header


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");

                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);

                return map;
            }

            // add params
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("meetings_id", idText);
                map.put("summary", summaryText);
                map.put("title", titleText);
                return map;
            }


        };

        RequestQueue queue = Volley.newRequestQueue(AddNotulasOnlyActivity.this);
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
