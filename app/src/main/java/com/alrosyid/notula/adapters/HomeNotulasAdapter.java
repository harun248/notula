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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
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
import java.util.HashMap;
import java.util.Map;

public class HomeNotulasAdapter extends RecyclerView.Adapter<HomeNotulasAdapter.HomeHolder> {

    private Context context;
    private ArrayList<Notula> list;
    private ArrayList<Notula> listAll;
    private SharedPreferences preferences;

    public HomeNotulasAdapter(Context context, ArrayList<Notula> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notula, parent, false);
        return new HomeHolder(view);


    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        public MenuViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
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

                Intent i = new Intent(((Activity) context), DetailNotulaActivity.class);
                i.putExtra("notulasId", notula.getId());
                i.putExtra("position", position);
                i.putExtra("title", notula.getTitle());
                context.startActivity(i);
            }
        });



    }

    private void deleteNotula(int notulaId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Hapus dari daftar hadir?");
        builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_NOTULA, response -> {

                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getBoolean("success")) {
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyDataSetChanged();
                            listAll.clear();
                            listAll.addAll(list);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        String token = preferences.getString("token", "");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("Authorization", "Bearer " + token);
                        return map;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", notulaId + "");
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(request);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        builder.show();
    }

    private final int limit = 3;

    @Override
    public int getItemCount() {
        if (list.size() > limit) {
            return limit;
        } else {
            return list.size();
        }

    }


    class HomeHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtMeetTitle, txtDate;
        private ImageButton btnPostOption, btnEdit, btnDelete;
        private CardView detailNotula;

        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            detailNotula = itemView.findViewById(R.id.cvNotula);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtMeetTitle = itemView.findViewById(R.id.tvMeetTitle);
            txtDate = itemView.findViewById(R.id.tvNotulatDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

}
