package com.sedulous.obhsadi.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.GPSTracker;
import com.sedulous.obhsadi.service.MyLocation;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;
import com.sedulous.obhsadi.service.VolleyMultipartRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupPicActivity extends AppCompatActivity {

    String GROUP_PIC_SUBMIT_API="http://obhsranchi.projectrailway.in/api/Linen/submitgroupPicture";
    String GROUP_PIC_API="http://obhsranchi.projectrailway.in/api/Linen/saveallfiles";
    public static String TRAIN_VALID_API="http://obhsranchi.projectrailway.in/api/Linen/getvalidatetraion";
    public static String GET_LAST_STATUS="http://obhsranchi.projectrailway.in/api/Linen/getgpictureList";
    String fileUri="", file_path="", task_id;
    ImageView imageView;
    TextView tv;
    Button bt_cam;
    EditText et_trainno;
    int CAMERA_CAPTURE_IMAGE_REQUEST_CODE=32;
    GPSTracker gps;
    ProgressDialog mProgressDialog;
    Button btSubmit;
    String orguniqId="";
    String lastStatus="";
    boolean isTrainValid=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_pic);

        tv=findViewById(R.id.tv);
        et_trainno=findViewById(R.id.et_trainno);
        imageView=findViewById(R.id.iv);
        bt_cam=findViewById(R.id.bt_cam);
        task_id=getIntent().getStringExtra("task_id");
        bt_cam.setVisibility(View.GONE);
        btSubmit=findViewById(R.id.btn_submit);
        btSubmit.setVisibility(View.GONE);

        bt_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(O.checkNetwork(GroupPicActivity.this)){
                    if(!isTrainValid) {
                        trainVerifyPopUp();
                    } else if(TextUtils.isEmpty(file_path)) {
                        Toast.makeText(GroupPicActivity.this,
                                "Take all images",Toast.LENGTH_SHORT).show();
                    } else{
                        uploadPic();
                    }
                }else{
                    Toast.makeText(GroupPicActivity.this,
                            "Internet connection not available",Toast.LENGTH_SHORT).show();
                }
            }
        });

        et_trainno.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() ==5){
                    checkTrain();
                }else{
                    isTrainValid=false;
                }
            }
        });

        getLastStatus();
    }
    public void uploadFinal(final String pics){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", PreferenceUtil.getUserIdSelected(this));
            jsonObject.put("task_id", task_id);
            jsonObject.put("train_no", et_trainno.getText().toString());
            if(!TextUtils.isEmpty(orguniqId)){
                jsonObject.put("orguniqId", orguniqId);
            }
            if(lastStatus.equalsIgnoreCase("Originating"))
            jsonObject.put("image_type", "Destination");
            else{
                jsonObject.put("image_type", "Originating");
            }
            jsonObject.put("upload_image", pics);

        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("json",jsonObject.toString());
        showLoading("Uploading...");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,GROUP_PIC_SUBMIT_API
                , new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    if(status==1){
                        showUploaded();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                hideLoading();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                try {
                    return jsonObject == null ? null : jsonObject.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "multipart/form-data");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(GroupPicActivity.this);
        requestQueue.add(stringRequest);
    }
    private void uploadPic() {
        btSubmit.setEnabled(false);
        showLoading("Uploading...");

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, GROUP_PIC_API,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        String resultResponse = new String(response.data);
                        hideLoading();
                        try {
                            JSONObject result = new JSONObject(resultResponse);
                            Log.e("response",resultResponse);
                            int status = result.getInt("status");
                            if(status==1){
                                String images=result.getString("images");
                                uploadFinal(images);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //pb.setVisibility(View.INVISIBLE);
                btSubmit.setEnabled(true);
                Toast.makeText(GroupPicActivity.this,"Error: File not uploaded",Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                Log.e("akm","imgupload");
                if (!TextUtils.isEmpty(file_path)) {
                    try {
                        String status="";
                        if(lastStatus.equalsIgnoreCase("Originating"))
                            status="Destination";
                        else status="Originating";
                        params.put("upload_image", new DataPart(status+"_"+new Date().getTime()+".jpg", O.getBytes(file_path)));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(multipartRequest);
        //VolleySingleton.getInstance(GroupPicActivity.this).addToRequestQueue(multipartRequest);
    }

    private void checkPermission(final int REQUEST_CODE){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fileUri= O.cameraProcess(GroupPicActivity.this, REQUEST_CODE);
        }else{
            Log.e("akm","in req");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,  Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data);
        Log.e("IntentData",""+data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            gps = new GPSTracker(GroupPicActivity.this);
            if (gps.canGetLocation()) {
                Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                try {
                    List<Address> addresses; String cityName="", stateName="", countryName="";
                    try {
                        double latitude = gps.getLocation().getLatitude();
                        double longitude = gps.getLocation().getLongitude();
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        cityName = addresses.get(0).getAddressLine(0);
                        stateName = addresses.get(0).getAddressLine(1);
                        countryName = addresses.get(0).getAddressLine(2);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //cityname.setText(cityName);
                    Canvas cs = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setTextSize(35);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a",Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName,10, bitmap.getHeight()-5, paint);
                    cs.drawText(datetime,10, bitmap.getHeight()-35, paint);
                    try {
                        file_path=O.savefile( GroupPicActivity.this,O.FOLDER_CAMIMG, bitmap, 80);
                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(GroupPicActivity.this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 2 : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                return;
            }
        }
    }
    public void getLastStatus(){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", PreferenceUtil.getUserIdSelected(this));
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("json",jsonObject.toString());
        showLoading("Getting last status...");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, GET_LAST_STATUS
                , new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("akm response",""+response);
                hideLoading();
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    if(status==1){
                        JSONObject jo=jsonResponse.getJSONArray("data").getJSONObject(0);
                        lastStatus=jo.getString("image_type");
                        if(lastStatus.equalsIgnoreCase("Originating")){

                            orguniqId=jo.getString("orguniqId");
                            et_trainno.setText(jo.getString("train_no"));
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bt_cam.setVisibility(View.VISIBLE);
                btSubmit.setVisibility(View.VISIBLE);
                if(lastStatus.equalsIgnoreCase("Originating"))
                    tv.setText("Destination Point");
                else tv.setText("Originating Point");
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                hideLoading();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                try {
                    return jsonObject == null ? null : jsonObject.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "multipart/form-data");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(GroupPicActivity.this);
        requestQueue.add(stringRequest);
    }
    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message0);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
    public void showUploaded(){
        new AlertDialog.Builder(this)
                .setMessage("Your Picture Uploaded")
                .setCancelable(true)
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                }).show();
    }
    public void checkTrain(){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("train_no", et_trainno.getText().toString());
            if(!TextUtils.isEmpty(orguniqId)){
                jsonObject.put("orguniqId", orguniqId);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("json",jsonObject.toString());
        showLoading("Checking Train...");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,TRAIN_VALID_API
                , new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                try {
                    JSONObject jsonResponse=new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    if(status==1){
                        isTrainValid=true;
                        Toast.makeText(GroupPicActivity.this,
                                "Train Valid",Toast.LENGTH_SHORT).show();
                    }else{
                        isTrainValid=false;
                       trainVerifyPopUp();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError error) {
                hideLoading();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                try {
                    return jsonObject == null ? null : jsonObject.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders() throws com.android.volley.AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "multipart/form-data");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(GroupPicActivity.this);
        requestQueue.add(stringRequest);
    }
    public void trainVerifyPopUp(){
        new AlertDialog.Builder(this)
                .setMessage("Train No. not Valid")
                .setCancelable(true)
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }
}



