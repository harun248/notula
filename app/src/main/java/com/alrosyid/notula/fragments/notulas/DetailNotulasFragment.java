package com.alrosyid.notula.fragments.notulas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DetailNotulasFragment extends Fragment {
    private TextInputLayout layoutTitle, layoutMeetingsTitle, layoutDate;
    private TextInputEditText txtTitle, txtMeetingsTitle, txtDate;
    private int notulasId = 0, position =0;
    private  Button btnExport;
    private View view;
    private SharedPreferences sharedPreferences;
    public static DetailNotulasFragment newInstance() {

        Bundle args = new Bundle();

        DetailNotulasFragment fragment = new DetailNotulasFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public DetailNotulasFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_notula,container,false);
        init();
        return view;

    }
    private void init(){
        sharedPreferences = getContext().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        layoutTitle = view.findViewById(R.id.tilTitle);
        layoutMeetingsTitle =  view.findViewById(R.id.tilMeetingsTitle);
        layoutDate =  view.findViewById(R.id.tilDate);
        txtTitle =  view.findViewById(R.id.tieTitle);
//        btnExport= view.findViewById(R.id.btnExport);
        txtMeetingsTitle =  view.findViewById(R.id.tieMeetingsTitle);
        txtDate =  view.findViewById(R.id.tieDate);
//        notulaId = getIntent().getIntExtra("notulaId",0);

        setHasOptionsMenu(true);

        getDetailNotulas();




//        btnExport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
////                Intent intent = new Intent();
////                intent.setAction(Intent.ACTION_VIEW);
////                intent.addCategory(Intent.CATEGORY_BROWSABLE);
////                intent.setData(Uri.parse(Constant.EXPORT));
////                startActivity(intent);
////                Uri uri = Uri.parse(Constant.EXPORT); // missing 'http://' will cause crashed
////                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
////                startActivity(intent);
//
////                getAddNotulasActivity();
//
//            }
//
////            private void getAddNotulasActivity() {
////
////                Integer id_meetings = getActivity().getIntent().getIntExtra("meetingsId", 0);
////                Intent i = new Intent(getActivity(), AddNotulasActivity.class);
////                i.putExtra("meetingsId", (id_meetings));
////                startActivity(i);
////            }
//        });





    }



    private void  getDetailNotulas() {
        Integer id_notula = getActivity().getIntent().getIntExtra("notulasId",0);
        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_NOTULA+ (id_notula), response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")){
                    JSONArray array = new JSONArray(object.getString("notulas"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject notula = array.getJSONObject(i);

                        txtTitle.setText(notula.getString("title"));
                        txtMeetingsTitle.setText(notula.getString("meetings_title"));
                        txtDate.setText(notula.getString("date"));
//                        String source = notula.getString("date");
//                        String[] sourceSplit= source.split("-");
//                        int anno= Integer.parseInt(sourceSplit[0]);
//                        int mese= Integer.parseInt(sourceSplit[1]);
//                        int giorno= Integer.parseInt(sourceSplit[2]);
//                        GregorianCalendar calendar = new GregorianCalendar();
//                        calendar.set(anno,mese-1,giorno);
//                        Date data1= calendar.getTime();
//                        SimpleDateFormat myFormat = new SimpleDateFormat("dd MMMM yyyy");
//                        String   dayFormatted= myFormat.format(data1);
//                        txtDate.setText(dayFormatted);


                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        },error -> {
            error.printStackTrace();
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token","");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
//    public void DownloadFiles() {
////        dialog.setMessage("Downloading..");
////        dialog.show();
//
//        try {
//            URL u = new URL(Constant.EXPORT);
//            InputStream is = u.openStream();
//
//            DataInputStream dis = new DataInputStream(is);
//
//            byte[] buffer = new byte[1024];
//            int length;
//
//            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "Download/" + "tes.pdf"));
//
//
//            while ((length = dis.read(buffer)) > 0) {
//                fos.write(buffer, 0, length);
//
//            }
//
//
//        } catch (MalformedURLException mue) {
//            Log.e("SYNC getUpdate", "malformed url error", mue);
//        } catch (IOException ioe) {
//            Log.e("SYNC getUpdate", "io error", ioe);
//        } catch (SecurityException se) {
//            Log.e("SYNC getUpdate", "security error", se);
//        }
//
//
//    }




}