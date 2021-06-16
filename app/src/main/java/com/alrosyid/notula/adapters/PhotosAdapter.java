package com.alrosyid.notula.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.models.Photos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotosHolder> {


    private Context context;
    private ArrayList<Photos> list;
    private ArrayList<Photos> listAll;
    private SharedPreferences preferences;

    public PhotosAdapter(Context context, ArrayList<Photos> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        preferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public PhotosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_photos, parent, false);
        return new PhotosHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosHolder holder, int position) {
        Photos photos = list.get(position);
        Picasso.get().load(Constant.URL + "storage/photos/" + photos.getPhoto()).into(holder.imgPhotos);
        holder.txtTitle.setText(photos.getTitle());

//        if (post.getUser().getId() == preferences.getInt("id", 0)) {
//            holder.btnPostOption.setVisibility(View.VISIBLE);
//        } else {
//            holder.btnPostOption.setVisibility(View.GONE);
//        }


    }

    // delete post
//    private void deletePost(int postId, int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Confirm");
//        builder.setMessage("Delete post?");
//        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                StringRequest request = new StringRequest(Request.Method.POST, Constant.DELETE_POST, response -> {
//
//                    try {
//                        JSONObject object = new JSONObject(response);
//                        if (object.getBoolean("success")) {
//                            list.remove(position);
//                            notifyItemRemoved(position);
//                            notifyDataSetChanged();
//                            listAll.clear();
//                            listAll.addAll(list);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }, error -> {
//
//                }) {
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        String token = preferences.getString("token", "");
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("Authorization", "Bearer " + token);
//                        return map;
//                    }
//
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError {
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("id", postId + "");
//                        return map;
//                    }
//                };
//
//                RequestQueue queue = Volley.newRequestQueue(context);
//                queue.add(request);
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//
//            }
//        });
//        builder.show();
//    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Photos> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Photos photos : listAll){
                    if(photos.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || photos.getCreated_at().toLowerCase().contains(constraint.toString().toLowerCase())

                    ){
                        filteredList.add(photos);
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
            list.addAll((Collection<? extends Photos>) results.values);
            notifyDataSetChanged();
        }
    };

    public  Filter getFilter() {
        return filter;
    }
    class PhotosHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private ImageView imgPhotos;

        public PhotosHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.tvTitle);
            imgPhotos = itemView.findViewById(R.id.imgPhoto);


        }
    }
}

