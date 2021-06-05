package com.alrosyid.notula.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.alrosyid.notula.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.notulas.DetailNotulaActivity;
import com.alrosyid.notula.activities.notulas.EditNotulaActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Notula;
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

public class NotulasAdapter extends RecyclerView.Adapter<NotulasAdapter.NotulaHolder> {

    private Context context;
    private ArrayList<Notula> list;
    private ArrayList<Notula> listAll;
    private SharedPreferences preferences;

    public NotulasAdapter(Context context, ArrayList<Notula> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }




    @NonNull
    @Override
    public NotulaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notula,parent,false);
        return new NotulaHolder(view);


    }



    @Override
    public void onBindViewHolder(@NonNull NotulaHolder holder, int position) {
        Notula notula = list.get(position);
        holder.txtTitle.setText(notula.getTitle());
        holder.txtMeetTitle.setText(notula.getMeetings_title());
        holder.txtDate.setText(notula.getDate());

        holder.detailNotula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetailNotulaActivity();
            }
            private void getDetailNotulaActivity() {

                Intent i = new Intent(((Activity)context), DetailNotulaActivity.class);
                i.putExtra("notulasId", notula.getId());
                i.putExtra("position", position);
                i.putExtra("title", notula.getTitle());
                context.startActivity(i);
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


Filter filter = new Filter() {
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        ArrayList<Notula> filteredList = new ArrayList<>();
        if (constraint.toString().isEmpty()){
            filteredList.addAll(listAll);
        } else {
            for (Notula notula : listAll){
                if(notula.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                        || notula.getMeetings_title().toLowerCase().contains(constraint.toString().toLowerCase())
                        || notula.getDate().toLowerCase().contains(constraint.toString().toLowerCase())
                ){
                    filteredList.add(notula);
                }
            }

        }

        FilterResults results = new FilterResults();
        results.values = filteredList;
        return  results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        list.clear();
        list.addAll((Collection<? extends Notula>) results.values);
        notifyDataSetChanged();
    }
};

    public  Filter getFilter() {
        return filter;
    }

    class NotulaHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle,txtMeetTitle,txtDate;
        private CardView detailNotula;

        public NotulaHolder(@NonNull View itemView) {
            super(itemView);
            detailNotula = itemView.findViewById(R.id.cvNotula);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtMeetTitle = itemView.findViewById(R.id.tvMeetTitle);
            txtDate = itemView.findViewById(R.id.tvNotulatDate);

        }
    }

}
