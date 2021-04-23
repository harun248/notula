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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.activities.MainActivity;
import com.alrosyid.notula.activities.notula.EditNotulaActivity;
import com.alrosyid.notula.activities.notula.ListsNotulaActivity;
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

public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MeetsHolder> {



    private Context context;
    private ArrayList<Meetings> list;
    private ArrayList<Meetings> listAll;
    private SharedPreferences preferences;
    String id_meets;

    public MeetingsAdapter(Context context, ArrayList<Meetings> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user",Context.MODE_PRIVATE);
    }




    @NonNull
    @Override
    public MeetsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meeting,parent,false);
        return new MeetsHolder(view);


    }



    @Override
    public void onBindViewHolder(@NonNull MeetsHolder holder, int position) {
        Meetings meetings = list.get(position);
        holder.txtTitle.setText(meetings.getTitle());
        holder.txtDate.setText(meetings.getDate());


//        holder.txtDate.setText(dayFormatted);
//        SimpleDateFormat formattgl = new SimpleDateFormat("dd/MM/yyyy");
//        String currentDateandTime =    formattgl.format(notula.getDate());
//        holder.txtDate.setText(currentDateandTime);
//        if(notula.getUser().getId()==preferences.getInt("id",0)){
//            holder.btnPostOption.setVisibility(View.VISIBLE);
//        } else {
//            holder.btnPostOption.setVisibility(View.GONE);
//        }
        holder.listNotula.setOnClickListener(new View.OnClickListener() {


            @Override
//            public void onClick(View v) {
//                getListNotulaActivity();
//            }
//
//            private void getListNotulaActivity() {
//
//                Intent i = new Intent(((MainActivity) context), ListsNotulaActivity.class);
//                context.startActivity(i);
//            }
            public void onClick(View view) {

//                Bundle bundle = new Bundle();
//                bundle.putInt("idMeets",meetings.getId());
//                NotulasByMeetingsFragment notulasByMeetsFragment = new NotulasByMeetingsFragment();
//                notulasByMeetsFragment.setArguments(bundle);
//                Intent i = new Intent(((MainActivity)context), ListsNotulaActivity.class);
//                i.putExtra("meetId",meetings.getId());
//                i.putExtra("position",position);
//                context.startActivity(i);
//                FragmentManager fragmentManager = ((ListsNotulaActivity)context).getSupportFragmentManager();
//
//                fragmentManager.beginTransaction().replace(R.id.fragment_container, notulasByMeetsFragment).commit();
                Intent i = new Intent(((MainActivity)context), ListsNotulaActivity.class);
                i.putExtra("meetingsId", meetings.getId());
                i.putExtra("position",position);
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
                            i.putExtra("meetingsId", meetings.getId());
                            i.putExtra("position",position);
                            i.putExtra("title", meetings.getTitle());
                            context.startActivity(i);
                            return true;
                        }
                        case R.id.item_delete: {
                            deleteNotula(meetings.getId(),position);
                            return true;
                        }
                    }

                    return false;
                }
            });
            popupMenu.show();
        });

    }

    private void deleteNotula(int meetId,int position){
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
                        map.put("id",meetId+"");
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

        ArrayList<Meetings> filteredList = new ArrayList<>();
        if (constraint.toString().isEmpty()){
            filteredList.addAll(listAll);
        } else {
            for (Meetings meetings : listAll){
                if(meetings.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                        || meetings.getDate().toLowerCase().contains(constraint.toString().toLowerCase())
                ){
                    filteredList.add(meetings);
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
        list.addAll((Collection<? extends Meetings>) results.values);
        notifyDataSetChanged();
    }
};

    public  Filter getFilter() {
        return filter;
    }

    class MeetsHolder extends RecyclerView.ViewHolder{
        private TextView txtTitle,txtDate;
        private ImageButton btnPostOption;
        private CardView listNotula;
        public MeetsHolder(@NonNull View itemView) {
            super(itemView);
            listNotula=itemView.findViewById(R.id.cvNotula);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            txtDate = itemView.findViewById(R.id.tvDate);
            btnPostOption = itemView.findViewById(R.id.btnPostOption);
            btnPostOption.setVisibility(View.VISIBLE);
        }
    }

}
