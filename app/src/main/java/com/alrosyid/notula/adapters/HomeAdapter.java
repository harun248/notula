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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.TestActivity;
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

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    private Context context;
    private ArrayList<Notula> list;
    private ArrayList<Notula> listAll;
    private SharedPreferences preferences;

    public HomeAdapter(Context context, ArrayList<Notula> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notula,parent,false);
        return new HomeHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        Notula notula = list.get(position);
        holder.txtTitle.setText(notula.getTitle());
        holder.txtSubject.setText(notula.getSubject());
        holder.txtDate.setText(notula.getDate());

//        if(notula.getUser().getId()==preferences.getInt("id",0)){
//            holder.btnPostOption.setVisibility(View.VISIBLE);
//        } else {
//            holder.btnPostOption.setVisibility(View.GONE);
//        }
        holder.btnPostOption.setOnClickListener(v->{
            PopupMenu popupMenu = new PopupMenu(context,holder.btnPostOption);
            popupMenu.inflate(R.menu.menu_options);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()){
                        case R.id.item_edit: {
                            Intent i = new Intent(((MainActivity)context), TestActivity.class);
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
    private final int limit = 3;
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
//    public int getItemCount() {
//        return list.size();
//    }

    class HomeHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle,txtDate,txtSubject;
        private ImageButton btnPostOption;
        public HomeHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtSubject = itemView.findViewById(R.id.txtSubject);
            txtDate = itemView.findViewById(R.id.txtPostDate);
            btnPostOption = itemView.findViewById(R.id.btnPostOption);
            btnPostOption.setVisibility(View.VISIBLE);
        }
    }

}
