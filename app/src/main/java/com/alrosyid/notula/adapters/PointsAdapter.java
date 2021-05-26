package com.alrosyid.notula.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.attendances.EditAttendancesActivity;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Attendances;
import com.alrosyid.notula.models.Points;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.PointsHolder>  {

    private Context context;
    private ArrayList<Points> list;
    private ArrayList<Points> listAll;
    private SharedPreferences preferences;

    public PointsAdapter(Context context,  ArrayList<Points> list) {
        this.context = context;

        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }

//    public PointsAdapter(Context context, ArrayList<Points> arrayList) {
//    }


    @NonNull
    @Override
    public PointsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_points,parent,false);
        return new PointsHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull PointsHolder holder, int position) {
//        int num = position+1;
//        holder.txtNumber.setText(+num + ". " + list[position]);
//        num++;
        Points points = list.get(position);
        holder.txtPoints.setText(points.getPoints());
        holder.txtNumber.setText(String.valueOf(position+1+" .") );
        holder.btnPostOption.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btnPostOption);
            popupMenu.inflate(R.menu.menu_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.item_edit: {
                            Intent i = new Intent(((Activity)context), EditAttendancesActivity.class);
                            i.putExtra("pointsId",points.getId());
                            i.putExtra("position",position);
                            i.putExtra("points",points.getPoints());
                            context.startActivity(i);
                            return true;
                        }
                        case R.id.item_delete: {
                            deleteAttendances(points.getId(),position);
                            return true;
                        }
                    }

                    return false;
                }
            });
            popupMenu.show();
        });

    }

    private void deleteAttendances(int attendancesId,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Hapus dari daftar hadir?");
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_ATTENDANCES, response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")){
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);
                            Toast.makeText(context, "Hapus berhasil", Toast.LENGTH_SHORT).show();
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
                        map.put("id",attendancesId+"");
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

    public Object getItem(int position) {
        return this.list.get(position);
    }
    @Override
    public int getItemCount() { return list.size(); }
//    public int getItemCount() {
//        return list.size();
//    }

    class PointsHolder extends RecyclerView.ViewHolder{
        private TextView txtNumber,txtPoints;
        private ImageButton btnPostOption;
        public PointsHolder(@NonNull View itemView) {
            super(itemView);
//            btnAttendaces= itemView.findViewById(R.id.btnAttendaces);
            txtNumber = itemView.findViewById(R.id.tvNumber);
            txtPoints = itemView.findViewById(R.id.tvPoints);
            btnPostOption = itemView.findViewById(R.id.btnPostOption);
            btnPostOption.setVisibility(View.VISIBLE);
        }
    }

}
