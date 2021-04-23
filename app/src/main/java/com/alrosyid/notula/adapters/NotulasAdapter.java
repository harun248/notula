package com.alrosyid.notula.adapters;

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
import com.alrosyid.notula.activities.notula.DetailNotulaActivity;
import com.alrosyid.notula.activities.notula.EditNotulaActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Notula;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
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
//        String source = notula.getDate();
//
//        String[] sourceSplit= source.split("-");
//
//        int anno= Integer.parseInt(sourceSplit[0]);
//        int mese= Integer.parseInt(sourceSplit[1]);
//        int giorno= Integer.parseInt(sourceSplit[2]);
//
//        GregorianCalendar calendar = new GregorianCalendar();
//        calendar.set(anno,mese-1,giorno);
//        Date   data1= calendar.getTime();
//        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMM yyyy");
//
//        String   dayFormatted= myFormat.format(data1);

//        holder.txtDate.setText(dayFormatted);
//        SimpleDateFormat formattgl = new SimpleDateFormat("dd/MM/yyyy");
//        String currentDateandTime = formattgl.format(notula.getDate());
//        holder.txtDate.setText(currentDateandTime);
//        if(notula.getUser().getId()==preferences.getInt("id",0)){
//            holder.btnPostOption.setVisibility(View.VISIBLE);
//        } else {
//            holder.btnPostOption.setVisibility(View.GONE);
//        }
        holder.detailNotula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDetailNotulaActivity();
            }
            private void getDetailNotulaActivity() {

                Intent i = new Intent(((MainActivity)context), DetailNotulaActivity.class);
                context.startActivity(i);
            }
        });
        holder.btnPostOption.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btnPostOption);
            popupMenu.inflate(R.menu.menu_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.item_edit: {
                            Intent i = new Intent(((MainActivity)context), EditNotulaActivity.class);
                            i.putExtra("notulaId",notula.getId());
                            i.putExtra("position",position);
                            i.putExtra("title",notula.getTitle());
                            context.startActivity(i);
                            return true;
                        }
                        case R.id.item_delete: {
                            deleteNotula(notula.getId(),position);
                            return true;
                        }
                    }

                    return false;
                }
            });
            popupMenu.show();
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
                            Toast.makeText(context, R.string.delete_notula, Toast.LENGTH_SHORT).show();
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
        private ImageButton btnPostOption;
        private CardView detailNotula;
        public NotulaHolder(@NonNull View itemView) {
            super(itemView);
            detailNotula=itemView.findViewById(R.id.cvNotula);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtMeetTitle = itemView.findViewById(R.id.tvMeetTitle);
            txtDate = itemView.findViewById(R.id.tvNotulatDate);
            btnPostOption = itemView.findViewById(R.id.btnPostOption);
            btnPostOption.setVisibility(View.VISIBLE);
        }
    }

}
