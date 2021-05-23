package com.alrosyid.notula.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.attendances.AttendacesActivity;
import com.alrosyid.notula.activities.attendances.EditAttendancesActivity;
import com.alrosyid.notula.activities.meetings.EditMeetingsActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Meetings;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailMeetingsAdapter extends RecyclerView.Adapter<DetailMeetingsAdapter.DetailMeetingsHolder> {

    private Context context;
    private ArrayList<Meetings> list;
    private ArrayList<Meetings> listAll;
    private SharedPreferences preferences;
    private ProgressDialog dialog;
    public DetailMeetingsAdapter(Context context, ArrayList<Meetings> list) {
        this.context = context;
        this.list = list;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public DetailMeetingsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_detail_meetings,parent,false);
        return new DetailMeetingsHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull DetailMeetingsHolder holder, int position) {
        Meetings meetings = list.get(position);
        holder.txtTitle.setText(meetings.getTitle());
        holder.txtAgenda.setText(meetings.getAgenda());
        holder.txtDate.setText(meetings.getDate());
        holder.txtStartTime.setText(meetings.getStart_time());
        holder.txtEndTime.setText(meetings.getEnd_time());



        holder.btnAttendaces.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                Intent i = new Intent(((Activity)context), AttendacesActivity.class);
                i.putExtra("meetingsId", meetings.getId());
                i.putExtra("meetingsTitle", meetings.getTitle());
                i.putExtra("meetingsPosition",position);
                context.startActivity(i);
            }



        });



    }

    private void deleteNotula(int notulaId,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm");
        builder.setMessage("Delete post?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_NOTULA, response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")){
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },error -> {

                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String token = preferences.getString("token","");
                        HashMap<String,String> map = new HashMap<>();
                        map.put("Authorization","Bearer "+token);
                        return map;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> map = new HashMap<>();
                        map.put("id",notulaId+"");
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.show();
    }
    private final int limit = 1;
    @Override
    public int getItemCount() {
        if(list.size() > limit){
            return limit;
        }
        else
        {
            return list.size();
        }

    }


    class DetailMeetingsHolder extends RecyclerView.ViewHolder{
        private TextInputEditText txtTitle,txtAgenda,txtDate,txtStartTime, txtEndTime;
        private Button btnAttendaces;
        public DetailMeetingsHolder(@NonNull View itemView) {
            super(itemView);
            btnAttendaces= itemView.findViewById(R.id.btnAttendaces);
            txtTitle = itemView.findViewById(R.id.tieTitle);
            txtAgenda = itemView.findViewById(R.id.tieAgenda);
            txtDate = itemView.findViewById(R.id.tieDate);
            txtStartTime = itemView.findViewById(R.id.tieStartTime);
            txtEndTime = itemView.findViewById(R.id.tieEndTime);


        }
    }

}
