package com.alrosyid.notula.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class AttendancesAdapter extends RecyclerView.Adapter<AttendancesAdapter.AttendancesHolder> {

    private Context context;
    private ArrayList<Attendances> list;
    private ArrayList<Attendances> listAll;
    private SharedPreferences preferences;
    private ProgressDialog dialog;
    public AttendancesAdapter(Context context, ArrayList<Attendances> list) {
        this.context = context;
        this.list = list;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public AttendancesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_attendances,parent,false);
        return new AttendancesHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull AttendancesHolder holder, int position) {
        Attendances attendances = list.get(position);
//        Meetings meets = meetsList.get(position);
        holder.txtName.setText(attendances.getName());
        holder.txtPosition.setText(attendances.getPosition());

        holder.btnPostOption.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btnPostOption);
            popupMenu.inflate(R.menu.menu_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.item_edit: {
                            Intent i = new Intent(((Activity)context), EditAttendancesActivity.class);
                            i.putExtra("attendancesId",attendances.getId());
                            i.putExtra("position",position);
                            i.putExtra("positions",attendances.getPosition());
                            i.putExtra("name",attendances.getName());
                            context.startActivity(i);
                            return true;
                        }
                        case R.id.item_delete: {
                            deleteAttendances(attendances.getId(),position);
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

    @Override
    public int getItemCount() { return list.size(); }
//    public int getItemCount() {
//        return list.size();
//    }

    class AttendancesHolder extends RecyclerView.ViewHolder{
        private TextView txtName,txtPosition;
        private ImageButton btnPostOption;
        public AttendancesHolder(@NonNull View itemView) {
            super(itemView);
//            btnAttendaces= itemView.findViewById(R.id.btnAttendaces);
            txtName = itemView.findViewById(R.id.tvName);
            txtPosition = itemView.findViewById(R.id.tvPosition);
            btnPostOption = itemView.findViewById(R.id.btnPostOption);
            btnPostOption.setVisibility(View.VISIBLE);
        }
    }

}
