package com.alrosyid.notula.activities.photos;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alrosyid.notula.R;
import com.alrosyid.notula.api.Constant;
import com.alrosyid.notula.fragments.photos.PhotosListFragment;
import com.alrosyid.notula.models.Photos;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddPhotosActivity extends AppCompatActivity {

    private Button btnUpload;
    private ImageView imgPhotos;
    private TextInputLayout lytTitle;
    private TextInputEditText txtTitle;
    private Bitmap bitmap = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private int meetingsId = 0;
    private ProgressDialog dialog;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_photo);
        init();
    }

    private void init() {
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnUpload = findViewById(R.id.btnUpload);
        imgPhotos = findViewById(R.id.imgAddPhotos);
        lytTitle = findViewById(R.id.tilTitle);
        txtTitle = findViewById(R.id.tieTitle);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        meetingsId = getIntent().getIntExtra("meetingsId", 0);


//        imgPhotos.setImageURI(getIntent().getData());
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),getIntent().getData());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        btnUpload.setOnClickListener(v -> {
            if (validate()) {
                create();
            }
//            if(!txtTitle.getText().toString().isEmpty() && !imgPhotos.toString().isEmpty()){
//                post();
//            }else {
//                Toast.makeText(this, "Title description is required"
//                        , Toast.LENGTH_SHORT).show();
//            }
        });

    }

    private boolean validate() {
        if (txtTitle.getText().toString().isEmpty()) {
            lytTitle.setErrorEnabled(true);
            lytTitle.setError(getString(R.string.required));
            return false;
        }
//        if (imgPhotos.toString().isEmpty()) {
//            Toast.makeText(this, "photo required"
//                        , Toast.LENGTH_SHORT).show();
//
//        }
        return true;

    }

    private void create() {
        dialog.setMessage("Uploading..");
        dialog.show();
        String titleText = txtTitle.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CREATE_PHOTOS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject postObject = object.getJSONObject("photos");

                    Photos photos = new Photos();
                    photos.setId(postObject.getInt("id"));
                    photos.setPhoto(postObject.getString("photo"));
                    photos.setTitle(postObject.getString("title"));


                    PhotosListFragment.arrayList.add(0, photos);
                    PhotosListFragment.recyclerView.getAdapter().notifyItemInserted(0);
                    PhotosListFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    finish();


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();


        }, error -> {
            error.printStackTrace();
            dialog.dismiss();

        }) {

            // add token to header


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            // add params

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("meetings_id", meetingsId + "");
                map.put("title", titleText);
                map.put("photo", bitmapToString(bitmap));
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(AddPhotosActivity.this);
        queue.add(request);

    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }

        return "";
    }


    public void cancelPost(View view) {
        super.onBackPressed();
    }

    public void changePhoto(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            imgPhotos.setImageURI(imgUri);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//
//        if (imageReturnedIntent == null
//                || imageReturnedIntent.getData() == null) {
//            return;
//        }
//
//        // aiming for ~500kb max. assumes typical device image size is around 2megs
//        int scaleDivider = 4;
//        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK){
////            Uri imgUri = imageReturnedIntent.getData();
////            imgPhotos.setImageURI(imgUri);
//            Uri imageUri = imageReturnedIntent.getData();
//            imgPhotos.setImageURI(imageUri);
//        try {
//
//            // 1. Convert uri to bitmap
//
//            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
////            Bitmap fullBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
//
//            // 2. Get the downsized image content as a byte[]
//            int scaleWidth = bitmap.getWidth() / scaleDivider;
//            int scaleHeight = bitmap.getHeight() / scaleDivider;
//            byte[] downsizedImageBytes =
//                    getDownsizedImageBytes(bitmap, scaleWidth, scaleHeight);
//
//
//        } catch (IOException ioEx) {
//            ioEx.printStackTrace();
//        }
//    }
//    }
//    public byte[] getDownsizedImageBytes(Bitmap bitmap, int scaleWidth, int scaleHeight) throws IOException {
//
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
//
//        // 2. Instantiate the downsized image content as a byte[]
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
//        byte[] downsizedImageBytes = baos.toByteArray();
//
//        return downsizedImageBytes;
//    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
