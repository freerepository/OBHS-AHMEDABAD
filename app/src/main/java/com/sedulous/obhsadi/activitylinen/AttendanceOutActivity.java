package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.sedulous.obhsadi.ModelLinen.GetAttById;
import com.sedulous.obhsadi.ModelLinen.UserDataModel;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.GPSTracker;
import com.sedulous.obhsadi.service.MyLocation;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;
import com.sedulous.obhsadi.service.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceOutActivity extends AppCompatActivity {
//    public static String Upload_Out_Data = "http://obhsadi.projectrailway.in/api/atd/save_attendanceoutdata";
    public static String Upload_Out_Data = "http://obhsadi.projectrailway.in/api/atd/save_attendanceoutdata_manual";
    public static String STORING_Image = "http://obhsadi.projectrailway.in/api/atd/upload_userimage";
    public static String AttendanceId = "http://obhsadi.projectrailway.in/api/atd/get_attendance_by_id";
    public static String Get_Station = "http://obhsadi.projectrailway.in/api/atd/get_tstations";
    Button btn_submit;
    ImageView v_back, iv;
    TextView tv_coach, tv_trainNo, tv_name, tv_userId;
    String file_path1 = "", imageresponse2 = "", id = "", punchDataTime="";
    MyLocation myLocation;
    GPSTracker gps;
    RelativeLayout addImage1;
    int layout1_width, layout1_height;
    RequestQueue rQueue;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    boolean mLocationPermissionGranted = false;
    private boolean doubleBacktoExitpresone = false;
    int PERMISSIONS_REQUEST_LOCATION = 99;
    private String fileUri;
    boolean hasBitmap;
    GetAttById getAttById;
    AlertDialog dialog;

    Spinner spStation;
    EditText et_dateSelect, et_timeSelect;
    String latitude;
    String longitude;
    String location;

    public String selectedStation, selectedIdstation;
    HashMap<String, JSONObject> stationDetailsMap = new HashMap<>();
    ArrayList<String> stationDetailList = new ArrayList<>();
    ArrayList<String> stationDetailIdList = new ArrayList<>();
    ArrayAdapter<String> stationAdapter;

    SwipeRefreshLayout srl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_out);
        try {
            id = getIntent().getStringExtra("data");
            punchDataTime = getIntent().getStringExtra("punch_data_time");
            UserDataModel userDataModel = new Gson().fromJson(O.getPreference(this, O.USER_DATA), UserDataModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        v_back = findViewById(R.id.v_back);
        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        srl = findViewById(R.id.srl);
        tv_trainNo = findViewById(R.id.tv_train_no);
        tv_coach = findViewById(R.id.tv_coach);
        tv_name = findViewById(R.id.tv_name);
        tv_userId = findViewById(R.id.tv_userId);
        iv = findViewById(R.id.img_train1);
        btn_submit = findViewById(R.id.btn_submit);
        getAttendanceList();
        gps = new GPSTracker(AttendanceOutActivity.this);
        getLocationPermission();
        iv = (ImageView) findViewById(R.id.img_train1);
        addImage1 = (RelativeLayout) findViewById(R.id.file_upload_layout1);

        et_dateSelect = findViewById(R.id.et_dateSelect);
        et_timeSelect = findViewById(R.id.et_timeSelect);
        et_dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDate();
            }
        });
        et_timeSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SelectTime();
                SelectTimeWithSeconds();
            }
        });


        ViewTreeObserver viewTreeObserver = addImage1.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                addImage1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                layout1_width = addImage1.getMeasuredWidth();
                layout1_height = addImage1.getMeasuredHeight();
            }
        });
        addImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });

        spStation = findViewById(R.id.sp_train_no2);

        if (tv_trainNo.getText().toString().isEmpty()) {

        }

        srl.setOnRefreshListener(() -> {
            srl.setRefreshing(true);
            if (O.checkNetwork(AttendanceOutActivity.this)) {
                GetStation(tv_trainNo.getText().toString());
            } else if (tv_trainNo.getText().toString().isEmpty()) {
                GetStation(tv_trainNo.getText().toString());
            } else {
                srl.setRefreshing(false);
            }
        });

        stationDetailList.add(0, "Select Station");
        stationAdapter = new ArrayAdapter<String>(AttendanceOutActivity.this, android.R.layout.simple_spinner_dropdown_item, stationDetailList);
        spStation.setAdapter(stationAdapter);
        spStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    selectedStation = "";
                    selectedIdstation = "";
                } else {
                    selectedStation = stationDetailList.get(position);
                    selectedIdstation = stationDetailIdList.get(position);

                    // Fetch latitude, longitude, and location from the HashMap
                    JSONObject stationDetails = stationDetailsMap.get(selectedIdstation);
                    if (stationDetails != null) {
                        try {
                            latitude = stationDetails.getString("latitude");
                            longitude = stationDetails.getString("longitude");
                            location = stationDetails.getString("location");

//                            Toast.makeText(getApplicationContext(), "Location -> " + location, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (et_dateSelect.getText().toString().isEmpty()) {
                    Toast.makeText(AttendanceOutActivity.this, "select Date",
                            Toast.LENGTH_SHORT).show();
                } else if (et_timeSelect.getText().toString().isEmpty()) {
                    Toast.makeText(AttendanceOutActivity.this, "select Time",
                            Toast.LENGTH_SHORT).show();
                } else if (spStation.getSelectedItemPosition() == 0) {
                    Toast.makeText(AttendanceOutActivity.this, "select Station",
                            Toast.LENGTH_SHORT).show();
                } else{
                    tv_trainNo.getText().toString().trim();
                    tv_coach.getText().toString().trim();
                    tv_name.getText().toString().trim();
                    tv_userId.getText().toString().trim();
                    String dateTime = et_dateSelect.getText().toString() + " " + et_timeSelect.getText().toString();
                    GetUploadOutData(latitude, longitude, location, dateTime, stationDetailList.get(spStation.getSelectedItemPosition()));
                }
            }
        });
    }

    private void SelectDate() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                R.style.CustomDatePickerTheme,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    et_dateSelect.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void SelectTimeWithSeconds() {
        // Current time for default values
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // Custom TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                R.style.CustomDatePickerTheme,
                (view, selectedHour, selectedMinute) -> {
                    final Dialog secondsDialog = new Dialog(this);
                    secondsDialog.setContentView(R.layout.dialog_seconds_picker);
                    secondsDialog.setTitle("Select Seconds");

                    // NumberPicker for Seconds
                    NumberPicker secondPicker = secondsDialog.findViewById(R.id.secondPicker);
                    Button btnSetSeconds = secondsDialog.findViewById(R.id.btnSetSeconds);

                    secondPicker.setMinValue(0);
                    secondPicker.setMaxValue(59);
                    secondPicker.setValue(second); // Default seconds value

                    btnSetSeconds.setOnClickListener(v -> {
                        int selectedSecond = secondPicker.getValue();

                        // Format time with seconds
                        String selectedTime = String.format(Locale.US, "%02d:%02d:%02d", selectedHour, selectedMinute, selectedSecond);

                        // Set time to EditText
                        et_timeSelect.setText(selectedTime);

                        // Dismiss seconds dialog
                        secondsDialog.dismiss();
                    });

                    secondsDialog.show();
                },
                hour, minute, true); // 24-hour format

        timePickerDialog.show();
    }

    private void GetStation(String trainNumber) {
        JSONObject jsonObject = new JSONObject();
        try {
//            jsonObject.put("train_no", tv_trainNo.getText().toString());
            jsonObject.put("train_no", trainNumber);
        } catch (JSONException e) {
            Log.d("station", "GetStation: " + e.getMessage());
        }

        // Create a request
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Get_Station, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("responseA", response.toString());
                try {
                    srl.setRefreshing(false);
                    JSONArray array = response.getJSONArray("Getsdata");
                    stationDetailList.clear();
                    stationDetailIdList.clear();
                    stationDetailsMap.clear();

                    stationDetailList.add("Select Station");
                    stationDetailIdList.add("0");


                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        stationDetailList.add(obj.getString("station"));
                        stationDetailIdList.add(obj.getString("id"));
                        stationDetailsMap.put(obj.getString("id"), obj);
                    }
                } catch (JSONException e) {
                    srl.setRefreshing(false);
                    e.printStackTrace();
                }
                spStation.setAdapter(new ArrayAdapter<>(AttendanceOutActivity.this, android.R.layout.simple_list_item_1, stationDetailList));
                spStation.setSelection(0);
                spStation.setSelected(false);

                spStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        if (position == 0) {
                            selectedStation = "";
                            selectedIdstation = "";
                        } else {
                            selectedStation = stationDetailList.get(position);
                            selectedIdstation = stationDetailIdList.get(position);

                            // Fetch latitude, longitude, and location from the HashMap
                            JSONObject stationDetails = stationDetailsMap.get(selectedIdstation);
                            if (stationDetails != null) {
                                try {
                                    latitude = stationDetails.getString("latitude");
                                    longitude = stationDetails.getString("longitude");
                                    location = stationDetails.getString("location");


//                            Toast.makeText(getApplicationContext(), "Location -> "+location, Toast.LENGTH_SHORT).show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        // Do nothing
                    }
                });


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        srl.setRefreshing(false);

                    }
                });


        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjectRequest);
    }


    private void GetUploadOutData(String latitude, String longitude,String location,  String dateTime, String station) {
        final JSONObject jsonObject = new JSONObject();

        double lat = 0, lng = 0;
        String address = "";
        try {
//            lat = myLocation.getLocation().getLatitude();
//            lng = myLocation.getLocation().getLongitude();
            lat = Double.parseDouble(latitude);
            lng = Double.parseDouble(longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lat != 0 && lng != 0) {
            Geocoder coder = new Geocoder(AttendanceOutActivity.this, Locale.getDefault());
            try {
                List<Address> addressList = coder.getFromLocation(lat, lng, 1);
                if (addressList != null) {
                    address = addressList.get(0).getAddressLine(0);
                    String landmark = addressList.get(0).getFeatureName();
                    String country = addressList.get(0).getCountryName();
                    String city = addressList.get(0).getLocality();
                    String state = addressList.get(0).getLocality();
                    String district = addressList.get(0).getSubAdminArea();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {

            String EMPM_type = "";
            if (tv_userId.getText().toString().toUpperCase().contains("SUP"))
                EMPM_type = "SUP";
            else if (tv_userId.getText().toString().toUpperCase().contains("JAN"))
                EMPM_type = "JAN";
            else if (tv_userId.getText().toString().toUpperCase().contains("EHK"))
                EMPM_type = "EHK";
            jsonObject.put("EMPM_Name", tv_name.getText().toString());
            jsonObject.put("EMPM_type", EMPM_type);
            jsonObject.put("RAPD_BioID", tv_userId.getText().toString());
            jsonObject.put("RAPD_Field1", "OUT");
            jsonObject.put("RAPD_Field4", tv_trainNo.getText().toString().trim());
            jsonObject.put("RAPD_PunchDateTime", O.getDateTime());
            jsonObject.put("coach", tv_coach.getText().toString());
//            jsonObject.put("address", address);

            jsonObject.put("address", location);
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));
            jsonObject.put("attendance_by", PreferenceUtil.getUserIdSelected(this));
            jsonObject.put("user_image", imageresponse2);
            jsonObject.put("RAPD_latitude", lat);
            jsonObject.put("RAPD_longitude", lng);
            jsonObject.put("RAPD_PunchDateTime", dateTime);
            jsonObject.put("originating_datetime", punchDataTime);
            jsonObject.put("station", station);


        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("responseOut", requestBody);
        showLoading("Submit Attendance Out..");

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Upload_Out_Data
                , new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                try {
                    Log.e("responseAtt", response);
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // int status = jsonResponse.getInt("status");
                    if (jsonResponse != null && jsonResponse.has("message")) {
                        String message = jsonResponse.getString("message");
                        //Toast.makeText(AttOutActivity.this, message, Toast.LENGTH_LONG);
                        showConfirmationDialog(message);
                    } else {
                        showConfirmationDialog(response);
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
        RequestQueue requestQueue = Volley.newRequestQueue(AttendanceOutActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getAttendanceList() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("response", requestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                AttendanceId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                    hideLoading();
                Log.e("response", response);
                try {

                    GetAttById getAttList = new Gson().fromJson(response, GetAttById.class);
                    //this is user data
                    tv_trainNo.setText(getAttList.mUserList.get(0).RAPD_Field4);
                    tv_coach.setText(getAttList.mUserList.get(0).mCoach);
                    tv_name.setText(getAttList.mUserList.get(0).EMPM_Name);
                    tv_userId.setText(getAttList.mUserList.get(0).RAPD_BioID);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hideLoading();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileUri = O.cameraProcess(AttendanceOutActivity.this, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            hasBitmap = false;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,}, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("IntentData", "" + data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            gps = new GPSTracker(AttendanceOutActivity.this);
            if (gps.canGetLocation()) {
                if (myLocation == null) myLocation = new MyLocation(AttendanceOutActivity.this);
                if (myLocation != null && (myLocation.isGpsEnabled || myLocation.isNetworkEnabled)) {
                    Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                    try {
                        List<Address> addresses;
                        String cityName = "", stateName = "", countryName = "";
                        try {
                            double latitude = gps.getLocation().getLatitude();
                            double longitude = gps.getLocation().getLongitude();
                            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            cityName = addresses.get(0).getAddressLine(0);
                            stateName = addresses.get(0).getAddressLine(1);
                            countryName = addresses.get(0).getAddressLine(2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //cityname.setText(cityName);
                        Canvas cs = new Canvas(bitmap);
                        Paint paint = new Paint();
                        paint.setTextSize(35);
                        paint.setColor(Color.BLUE);
                        paint.setStyle(Paint.Style.FILL);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                        String datetime = sdf.format(Calendar.getInstance().getTime());
                        cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                        cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                        try {
                            file_path1 = O.savefile(AttendanceOutActivity.this, O.FOLDER_CAMIMG, bitmap, 80);
                            uploadBitmap(bitmap);
                            hasBitmap = true;
                            iv.setImageBitmap(BitmapFactory.decodeFile(file_path1));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "First allow Location",
                            Toast.LENGTH_SHORT).show();
                    MyLocation.displayPromptForEnablingGPS(AttendanceOutActivity.this);
                }
            }
        }
    }

    private void uploadBitmap(final Bitmap newBitmap1) {
        showLoading("Upoading image please wait");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_Image,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        imageresponse2 = new String(response.data);
                        Log.e("imageresponse1", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // params.put("tags", "ccccc");  add string parameters
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new VolleyMultipartRequest.DataPart(imagename + ".png", getFileDataFromDrawable(newBitmap1)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(AttendanceOutActivity.this);
        rQueue.add(volleyMultipartRequest);
        //Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                myLocation = new MyLocation(AttendanceOutActivity.this);
            }
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(AttendanceOutActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            myLocation = new MyLocation(AttendanceOutActivity.this);
        } else {
            ActivityCompat.requestPermissions(AttendanceOutActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
    }

    protected void showLoading(@NonNull String message0) {
        LinearLayout ll = new LinearLayout(this);
        ll.setPadding(16, 16, 16, 16);
        ll.setGravity(Gravity.CENTER);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setGravity(Gravity.CENTER);
        ll.setLayoutParams(llParam);

        TextView tv = new TextView(this);
        tv.setText(message0);
        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(llParam);
        tv.setPadding(8, 8, 8, 8);
        ll.addView(tv);

        RelativeLayout rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlParam = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rl.setLayoutParams(rlParam);

        ImageView iv = new ImageView(this);
        iv.setImageDrawable(getDrawable(R.drawable.progress));
        rlParam = new RelativeLayout.LayoutParams(100, 100);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlParam.addRule(RelativeLayout.BELOW, tv.getId());
        iv.setLayoutParams(rlParam);
        rl.addView(iv);
        iv.animate().setInterpolator(new DecelerateInterpolator()).rotation(-3600).setDuration(20000).start();

        ImageView iv_logo = new ImageView(this);
        iv_logo.setImageDrawable(getDrawable(R.drawable.logo));
        iv_logo.setPadding(20, 20, 20, 20);
        rlParam = new RelativeLayout.LayoutParams(100, 100);
        rlParam.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        rlParam.addRule(RelativeLayout.BELOW, tv.getId());
        iv_logo.setLayoutParams(rlParam);
        rl.addView(iv_logo);
        iv_logo.animate().setInterpolator(new DecelerateInterpolator()).rotation(3600).setDuration(20000).start();

        ll.addView(rl);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(ll);
        dialog = builder.create();
        dialog.show();
    }

    protected void hideLoading() {
        dialog.dismiss();
    }

    public void showConfirmationDialog(String strMessage) {
        final Dialog dialog = new Dialog(AttendanceOutActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(strMessage);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(AttendanceOutActivity.this, SupervisorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }

}