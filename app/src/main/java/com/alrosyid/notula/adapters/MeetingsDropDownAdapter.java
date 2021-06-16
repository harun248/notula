package com.alrosyid.notula.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.meetings.DetailMeetingsActivity;
import com.alrosyid.notula.activities.meetings.EditMeetingsActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Meetings;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MeetingsDropDownAdapter extends RecyclerView.Adapter<MeetingsDropDownAdapter.MeetsHolder> {



    private Context context;
    private ArrayList<Meetings> list;
    private ArrayList<Meetings> listAll;
    private SharedPreferences preferences;




    public MeetingsDropDownAdapter(Context context, ArrayList<Meetings> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        this.notifyDataSetChanged();
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }




    @NonNull
    @Override
    public MeetsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dd_meetings,parent,false);
        return new MeetsHolder(view);


    }



    @Override
    public void onBindViewHolder(@NonNull MeetsHolder holder, int position) {
        Meetings meetings = list.get(position);
        holder.txtTitle.setText(meetings.getTitle());
        holder.txtId.setText(meetings.getId());

    }



    @Override
    public int getItemCount() {
        return list.size();
    }



    class MeetsHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle,txtId;
        private LinearLayout LlDropDown;
        public MeetsHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtId = itemView.findViewById(R.id.tvId);
            LlDropDown = itemView.findViewById(R.id.itemDropdown);




        }
    }

}
