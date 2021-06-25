package com.alrosyid.notula.activities.photos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.photos.PhotosListFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DetailPhotosActivity extends AppCompatActivity {

    ImageView photoview2;
    float[] lastEvent = null;
    float d = 0f;
    float newRot = 0f;
    private boolean isZoomAndRotate;
    private boolean isOutSide;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float oldDist = 1f;
    private float xCoOrdinate, yCoOrdinate;
    private ProgressDialog dialog;

    private int photosId = 0, position = 0;

//    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle((title));

        setContentView(R.layout.activity_detail_photos);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        photoview2 = findViewById(R.id.imageview_trash);

        photoview2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                view.bringToFront();
                viewTransformation(view, event);
                return true;
            }
        });
//        getPhotos();
        String photo = getIntent().getStringExtra("photo");
        Picasso.get().load(Constant.URL + "storage/photos/" + (photo)).into(photoview2);


    }

//    private void  getPhotos() {
//        Integer id_photo = getIntent().getIntExtra("photosId",0);
//        StringRequest request = new StringRequest(Request.Method.GET, Constant.DETAIL_PHOTOS+ (id_photo), response -> {
//
//            try {
//                JSONObject object = new JSONObject(response);
//
//                if (object.getBoolean("success")){
//                    JSONArray array = new JSONArray(object.getString("photos"));
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject photos = array.getJSONObject(i);
//
//                        photoview2.getText(photos.getString("title"));
//                    }
//                }
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        },error -> {
//            error.printStackTrace();
//        }){
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = sharedPreferences.getString("token","");
//                HashMap<String,String> map = new HashMap<>();
//                map.put("Authorization","Bearer "+token);
//                return map;
//            }
//        };
//
//        RequestQueue queue = Volley.newRequestQueue(DetailPhotosActivity.this);
//        queue.add(request);
//    }

    private void viewTransformation(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xCoOrdinate = view.getX() - event.getRawX();
                yCoOrdinate = view.getY() - event.getRawY();

                start.set(event.getX(), event.getY());
                isOutSide = false;
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }

                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
                isZoomAndRotate = false;
                if (mode == DRAG) {
                    float x = event.getX();
                    float y = event.getY();
                }
            case MotionEvent.ACTION_OUTSIDE:
                isOutSide = true;
                mode = NONE;
                lastEvent = null;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isOutSide) {
                    if (mode == DRAG) {
                        isZoomAndRotate = false;
                        view.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                    }
                    if (mode == ZOOM && event.getPointerCount() == 2) {
                        float newDist1 = spacing(event);
                        if (newDist1 > 10f) {
                            float scale = newDist1 / oldDist * view.getScaleX();
                            view.setScaleX(scale);
                            view.setScaleY(scale);
                        }
                        if (lastEvent != null) {
                            newRot = rotation(event);
                            view.setRotation((float) (view.getRotation() + (newRot - d)));
                        }
                    }
                }
                break;
        }
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (int) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_photo, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_share: {
                Intent share = new Intent(Intent.ACTION_SEND);
                String photo = getIntent().getStringExtra("photo");
//                Picasso.get().load(Constant.URL + "storage/photos/" + (photo)).into(photoview2);
                share.putExtra(Intent.EXTRA_TEXT, (Constant.URL + "storage/photos/" + (photo)));
                share.setType("text/plain");


                startActivity(share);

                return true;
            }

            case R.id.item_download: {
                new Thread(new Runnable() {
                    public void run() {
                        DownloadFiles();

                    }

                }).start();

                Toast.makeText(this, "Download successfully, Check your download directory", Toast.LENGTH_LONG).show();
//                Toast.makeText(this, "Download successfully, Check your download directory", Toast.LENGTH_LONG).show();
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void DownloadFiles() {
//        dialog.setMessage("Downloading..");
//        dialog.show();

        try {
            String photo = getIntent().getStringExtra("photo");
            URL u = new URL(Constant.URL + "storage/photos/" + (photo));
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "Download/" + (photo)));


            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);

            }


        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }


    }
//    public void DownloadAndShare(){
//
//        try {
//            String photo = getIntent().getStringExtra("photo");
//            URL u = new URL(Constant.URL + "storage/photos/" + (photo));
//            InputStream is = u.openStream();
//
//            DataInputStream dis = new DataInputStream(is);
//
//            byte[] buffer = new byte[1024];
//            int length;
//
//            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/" + "Download/"+(photo)));
//
//
//            while ((length = dis.read(buffer))>0) {
//                fos.write(buffer, 0, length);
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
//    }
//
//    public Uri getLocalBitmapUri(ImageView imageView) {
//        // Extract Bitmap from ImageView drawable
//        Drawable drawable = imageView.getDrawable();
//        Bitmap bmp = null;
//        if (drawable instanceof BitmapDrawable){
//            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        } else {
//            return null;
//        }
//        // Store image to default external storage directory
//        Uri bmpUri = null;
//        try {
//            File file =  new File(Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
//            file.getParentFile().mkdirs();
//            FileOutputStream out = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
//            out.close();
//            bmpUri = Uri.fromFile(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bmpUri;
//    }
}
