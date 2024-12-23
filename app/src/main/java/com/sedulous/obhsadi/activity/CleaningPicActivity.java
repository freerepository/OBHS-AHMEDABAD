package com.sedulous.obhsadi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.sedulous.obhsadi.activity.GroupPicActivity.GET_LAST_STATUS;
import static com.sedulous.obhsadi.activity.GroupPicActivity.TRAIN_VALID_API;

public class CleaningPicActivity extends AppCompatActivity {

    String PIC_API="http://obhsadi.projectrailway.in/api/Linen/savecleningfiles";
    String SUBMIT_API="http://obhsadi.projectrailway.in/api/Linen/submitppPicture";
    String fileUri="";
    String[] file_path={"","","",""};
    String[] img_response={"","","",""};
    ImageView[] imageViews=new ImageView[4];
    int[] CAMERA_CAPTURE_IMAGE_REQUEST_CODE={32,33,34,35};
    GPSTracker gps;
    EditText et_trainno;
    ProgressDialog mProgressDialog;
    Button btSubmit;
    String cleaning_type="", task_id;
    RadioGroup radioGroup;
    boolean isTrainValid=false;
    String PRE="Pre Cleaning", POST="Post Cleaning",lastStatus="", orguniqId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaning_pic);
        imageViews[0]=findViewById(R.id.iv1);
        imageViews[1]=findViewById(R.id.iv2);
        imageViews[2]=findViewById(R.id.iv3);
        imageViews[3]=findViewById(R.id.iv4);
        et_trainno=findViewById(R.id.et_trainno);
        radioGroup=findViewById(R.id.radioGroup);
        task_id=getIntent().getStringExtra("task_id");

        for(int a=0; a<imageViews.length; a++) {
            int finalA = a;
            imageViews[a].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE[finalA]);
                }
            });
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb=group.findViewById(checkedId);
                cleaning_type=rb.getText().toString();
            }
        });
        btSubmit=findViewById(R.id.btn_submit);
        btSubmit.setVisibility(View.GONE);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(O.checkNetwork(CleaningPicActivity.this)){
                    if(TextUtils.isEmpty(cleaning_type)) {
                        Toast.makeText(CleaningPicActivity.this,
                                "Select Cleaning Type",Toast.LENGTH_SHORT).show();
                    } else if(!isTrainValid) {
                        trainVerifyPopUp();
                    } else if(TextUtils.isEmpty(file_path[0])||TextUtils.isEmpty(file_path[1])||
                            TextUtils.isEmpty(file_path[2])||TextUtils.isEmpty(file_path[3])) {
                        Toast.makeText(CleaningPicActivity.this,
                                "Take all images",Toast.LENGTH_SHORT).show();
                    }else {
                        uploadPic(4, 0);
                    }
                }else{
                    Toast.makeText(CleaningPicActivity.this,
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

    public void uploadFinal(){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", PreferenceUtil.getUserIdSelected(this));
            jsonObject.put("task_id", task_id);
            jsonObject.put("train_no", et_trainno.getText().toString());
            jsonObject.put("image_type", cleaning_type);
            jsonObject.put("orguniqId", orguniqId);
            jsonObject.put("pp_image1", img_response[0]);
            jsonObject.put("pp_image2", img_response[1]);
            jsonObject.put("pp_image3", img_response[2]);
            jsonObject.put("pp_image4", img_response[3]);

        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("json",jsonObject.toString());
        showLoading("Uploading...");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, SUBMIT_API
                , new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("akm response",""+response);
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
        RequestQueue requestQueue = Volley.newRequestQueue(CleaningPicActivity.this);
        requestQueue.add(stringRequest);
    }
    private void uploadPic(int max, int current) {
        btSubmit.setEnabled(false);
        showLoading("Uploading...");

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, PIC_API,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        String resultResponse = new String(response.data);
                        hideLoading();
                        try {
                            Log.e("response", resultResponse);
                            if(resultResponse.contains("/"))
                            img_response[current] = resultResponse.substring(resultResponse.lastIndexOf("/")+1);
                            else
                                img_response[current] = resultResponse;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(current<max-1) {
                            uploadPic(max, current+1);
                        }else{
                            uploadFinal();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //pb.setVisibility(View.INVISIBLE);
                btSubmit.setEnabled(true);
                Toast.makeText(CleaningPicActivity.this,"Error: File not uploaded",Toast.LENGTH_SHORT).show();
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
                if (!TextUtils.isEmpty(file_path[current])) {
                    try {
                        params.put("image", new DataPart(cleaning_type.replace(" ","")+"_"+
                                new Date().getTime()+".jpg", O.getBytes(file_path[current])));
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
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            fileUri= O.cameraProcess(CleaningPicActivity.this, REQUEST_CODE);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, },100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data);
        Log.e("IntentData",""+data);
        gps = new GPSTracker(CleaningPicActivity.this);
        for(int n=0; n<CAMERA_CAPTURE_IMAGE_REQUEST_CODE.length; n++){
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE[n] && resultCode == RESULT_OK) {

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
                        file_path[n]=O.savefile( CleaningPicActivity.this,O.FOLDER_CAMIMG, bitmap, 80);
                        imageViews[n].setImageBitmap(BitmapFactory.decodeFile(file_path[n]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(CleaningPicActivity.this);
            }

        }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults); -> line add kari hai maine
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 2: {
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
                btSubmit.setVisibility(View.VISIBLE);

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
        RequestQueue requestQueue = Volley.newRequestQueue(CleaningPicActivity.this);
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
                        Toast.makeText(CleaningPicActivity.this,
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
        RequestQueue requestQueue = Volley.newRequestQueue(CleaningPicActivity.this);
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
