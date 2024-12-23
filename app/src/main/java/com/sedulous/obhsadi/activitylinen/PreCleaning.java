package com.sedulous.obhsadi.activitylinen;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.GPSTracker;
import com.sedulous.obhsadi.service.MyLocation;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;
import com.sedulous.obhsadi.service.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PreCleaning extends AppCompatActivity {
    public static String TRAIN_NO_API = "http://obhsadi.projectrailway.in/api/atd/getTrain";
    public static String COACH_API = "http://obhsadi.projectrailway.in/api/atd/getCoach";
    public static String GET_CLEANING = "http://obhsadi.projectrailway.in/api/atd/get_cleanngType";
    String PIC_API="http://obhsadi.projectrailway.in/api/user/savecleningfiles";
    String SUBMIT_API="http://obhsadi.projectrailway.in/api/user/submitppPicture";
    String fileUri="";
    String[] file_path={""};
    String[] img_response={""};
    ImageView[] imageViews=new ImageView[1];
    int[] CAMERA_CAPTURE_IMAGE_REQUEST_CODE={32};
    GPSTracker gps;

    ImageView v_back;
    Spinner spTrainNo,sp_shift,spcleaning;
    ProgressDialog mProgressDialog;
    Button btSubmit;
    String cleaning_type="", task_id,id;

    public String selectedTrain,selectedIdTrain, selectedShift,selectedCleaning;
    ArrayList<String> trainNoList = new ArrayList<>();
    ArrayList<String> trainNoIdList = new ArrayList<>();

    ArrayList<String> coachList = new ArrayList<>();
    ArrayList<String> coachIdList = new ArrayList<>();

    ArrayList<String> cleaningList = new ArrayList<>();
    ArrayList<String> cleaningIdList = new ArrayList<>();
    ArrayAdapter<String> trainNoAdapter;
    ArrayAdapter<String> coachAdapter;
    ArrayAdapter<String> cleaningAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_cleaning_pic);
        imageViews[0]=findViewById(R.id.iv1);
//        imageViews[1]=findViewById(R.id.iv2);
//        imageViews[2]=findViewById(R.id.iv3);
//        imageViews[3]=findViewById(R.id.iv4);
        spcleaning=findViewById(R.id.sp_cleaning1);
        spTrainNo=findViewById(R.id.sp_train_no);
        sp_shift=findViewById(R.id.sp_cleaning);

        v_back=findViewById(R.id.v_back);
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        task_id=getIntent().getStringExtra("task_id");
        id=getIntent().getStringExtra("id");
        GetTrain();
        trainNoList.add(0, "Select Train.");
        trainNoAdapter = new ArrayAdapter<String>(PreCleaning.this, android.R.layout.simple_spinner_dropdown_item, trainNoList);
        spTrainNo.setAdapter(trainNoAdapter);
        spTrainNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedTrain = "";
                    selectedIdTrain="";

                } else {
                    selectedTrain = trainNoList.get(i);
                    selectedIdTrain=trainNoIdList.get(i);

                    GetCoach(selectedIdTrain);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        coachList.add(0, "Select Coach");
        coachAdapter = new ArrayAdapter<String>(PreCleaning.this, android.R.layout.simple_spinner_dropdown_item, coachList);
        sp_shift.setAdapter(coachAdapter);
        sp_shift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedShift = "";

                } else {
                    selectedShift = coachList.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        GetCleaning();
        cleaningList.add(0, "select Pre and Post Cleaning");
        cleaningAdapter = new ArrayAdapter<String>(PreCleaning.this, android.R.layout.simple_spinner_dropdown_item, cleaningList);
        spcleaning.setAdapter(cleaningAdapter);
        spcleaning.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedCleaning = "";

                } else {
                    selectedCleaning = cleaningList.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        for(int a=0; a<imageViews.length; a++) {
            int finalA = a;
            imageViews[a].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE[finalA]);
                }
            });
        }

        btSubmit=findViewById(R.id.btn_submit);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spTrainNo.getSelectedItemPosition() ==0) {
                    Toast.makeText(PreCleaning.this, "select Train",
                            Toast.LENGTH_SHORT).show();
                } else if (sp_shift.getSelectedItemPosition() ==0) {
                    Toast.makeText(PreCleaning.this, "select Coach",
                            Toast.LENGTH_SHORT).show();
                } else if (spcleaning.getSelectedItemPosition() ==0) {
                    Toast.makeText(PreCleaning.this, "select Pre and Post Cleaning",
                            Toast.LENGTH_SHORT).show();

                } else if(TextUtils.isEmpty(file_path[0])) {
                        Toast.makeText(PreCleaning.this,
                                "Take  images",Toast.LENGTH_SHORT).show();
                    }else {
                        uploadPic(1, 0);
                    }
//
//                }else{
//                    Toast.makeText(PreCleaning.this,
//                            "Internet connection not available",Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void uploadFinal(){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", PreferenceUtil.getUserIdSelected(this));
            jsonObject.put("train_no", trainNoList.get(spTrainNo.getSelectedItemPosition()));
            jsonObject.put("coach", coachList.get(sp_shift.getSelectedItemPosition()));
            jsonObject.put("image_type", cleaningList.get(spcleaning.getSelectedItemPosition()));
            jsonObject.put("pp_image1", img_response[0]);
            jsonObject.put("depot_code",PreferenceUtil.getDepot(this));

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
        RequestQueue requestQueue = Volley.newRequestQueue(PreCleaning.this);
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
                Toast.makeText(PreCleaning.this,"Error: File not uploaded",Toast.LENGTH_SHORT).show();
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
            fileUri= O.cameraProcess(PreCleaning.this, REQUEST_CODE);
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, },100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data);
        Log.e("IntentData",""+data);
        gps = new GPSTracker(PreCleaning.this);
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
                            file_path[n]=O.savefile( PreCleaning.this,O.FOLDER_CAMIMG, bitmap, 80);
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
                    MyLocation.displayPromptForEnablingGPS(PreCleaning.this);
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

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
    private void GetTrain() {

        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("station_code", PreferenceUtil.getDepot(this));
//            showLoading("Loading TrainNo...");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, TRAIN_NO_API, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("TrainList");
                            trainNoList.clear();
                            trainNoList.add(0,"Select Train");
                            trainNoIdList.clear();
                            trainNoIdList.add(0,"Select Train");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                trainNoList.add(obj.getString("train_no"));
                                trainNoIdList.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spTrainNo.setAdapter(new ArrayAdapter<String>(PreCleaning.this, android.R.layout.simple_spinner_dropdown_item, trainNoList));
                        spTrainNo.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(PreCleaning.this);
        requestQueue.add(objectRequest);

    }
    private void GetCoach(String selectedIdTrain) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("station_code", PreferenceUtil.getDepot(this));
            jsonObject.put("train_id",selectedIdTrain);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, COACH_API,jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("coachList");
                            coachList.clear();
                            coachList.add(0,"Select Coach Type");
                            coachIdList.clear();
                            coachIdList.add(0,"Select Coach Type");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                coachList.add(obj.getString("coach"));
                                coachIdList.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_shift.setAdapter(new ArrayAdapter<String>(PreCleaning.this, android.R.layout.simple_spinner_dropdown_item, coachList));
                        sp_shift.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(PreCleaning.this);
        requestQueue.add(objectRequest);

    }

    private void GetCleaning() {

        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, GET_CLEANING,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("CleaningType");
                            cleaningList.clear();
                            cleaningList.add(0,"Select Pre and Post Cleaning");
                            cleaningIdList.clear();
                            cleaningIdList.add(0,"Select Pre and Post Cleaning");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                cleaningList.add(obj.getString("cleaning_type"));
                                cleaningIdList.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spcleaning.setAdapter(new ArrayAdapter<String>(PreCleaning.this, android.R.layout.simple_spinner_dropdown_item, cleaningList));
                        spcleaning.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(PreCleaning.this);
        requestQueue.add(objectRequest);

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



}
