package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.sedulous.obhsadi.Model.UserStationModel;
import com.sedulous.obhsadi.ModelLinen.EmpType;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceInActivity extends AppCompatActivity {
    public static String TRAIN_LIST = "http://obhsadi.projectrailway.in/api/atd/getTrain";
    public static String Upload_In_Data = "http://obhsadi.projectrailway.in/api/atd/save_attendancedata";
    public static String STORING_Image = "http://obhsadi.projectrailway.in/api/atd/upload_userimage";
    public static String Emp_type = "http://obhsadi.projectrailway.in/api/atd/get_employee_by_ID";
    public static String Get_Station = "http://obhsadi.projectrailway.in/api/atd/get_tstations";
    Button bt_submit, bt_viewAtt;
    ImageView iv_back, iv;

    EditText et_dateSelect, et_timeSelect;
    String latitude;
    String longitude;
    String location;

    EditText et_name, et_userId, et_coach;
    Spinner spTrainNo, spUserName, spStation;
    String file_path1 = "", imageresponse1 = "";
    String date, userId, depot_id;
    MyLocation myLocation;
    GPSTracker gps;
    RelativeLayout addImage1;
    int layout1_width, layout1_height;
    RequestQueue rQueue;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    boolean mLocationPermissionGranted = false;
    private boolean doubleBacktoExitpresone = false;
    int PERMISSIONS_REQUEST_LOCATION = 99;
    Geocoder coder;
    ArrayList<String> empTypeList = new ArrayList<>();
    ArrayList<String> trainNoList = new ArrayList<>();
    ArrayList<String> trainNoIdList = new ArrayList<>();
    ArrayList<String> stationDetailList = new ArrayList<>();
    ArrayList<String> stationDetailIdList = new ArrayList<>();
    ArrayAdapter<String> trainNoAdapter, stationAdapter;

    HashMap<String, JSONObject> stationDetailsMap = new HashMap<>();
//    HashMap<String, String> submitStationData = new HashMap<>();


    ArrayAdapter<String> empAdapter;
    String user_id = "", deport_code = "", designation = "", coach;
    private String fileUri;
    boolean hasBitmap;
    public String selectedTrain, selectedIdTrain, selectedEmptype, selectedStation, selectedIdstation;
    AlertDialog dialog;
    EmpType empTypes;
    final Calendar myCalendar = Calendar.getInstance();
    UserDataModel userDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_in);
        et_name = findViewById(R.id.et_name);
        et_userId = findViewById(R.id.et_userId);
        spTrainNo = findViewById(R.id.sp_train_no);
        spStation = findViewById(R.id.sp_train_no2);
        et_coach = findViewById(R.id.et_coach);
        iv_back = findViewById(R.id.v_back);
        bt_submit = findViewById(R.id.btn_submit);
        bt_viewAtt = findViewById(R.id.bt_viewAtt);
        user_id = getIntent().getStringExtra("user_id");
        designation = getIntent().getStringExtra("designation");
        deport_code = getIntent().getStringExtra("deport_code");

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
//       tv_station_code.setText(PreferenceUtil.getDepot(this));
        getLocationPermission();

        bt_viewAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceInActivity.this, EmpInListActvity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        spUserName = findViewById(R.id.sp_names);
        et_userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (empTypes != null && empTypes.mEmpType.size() > 0) {

                    ArrayList<EmpType.Item> mList = new ArrayList();
                    for (int n = 0; n < empTypes.mEmpType.size(); n++) {
                        if (!TextUtils.isEmpty(empTypes.mEmpType.get(n).mEMPM_name) && empTypes.mEmpType.get(n).mEMPM_type.toUpperCase().contains(s.toString().toUpperCase())) {
                            mList.add(empTypes.mEmpType.get(n));

                        }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                EmptypeData(et_userId.getText().toString());
            }
        });
        findViewById(R.id.et_userId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et_userId.getText().toString().trim())) {
                    Toast.makeText(AttendanceInActivity.this, "Autofill Name", Toast.LENGTH_SHORT).show();
                }

                EmptypeData(et_userId.getText().toString());
            }
        });
        empTypeList.add(0, "");
        empAdapter = new ArrayAdapter<String>(AttendanceInActivity.this, android.R.layout.simple_spinner_dropdown_item, empTypeList);
        spUserName.setAdapter(empAdapter);
        spUserName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedEmptype = "";

                } else {
                    selectedEmptype = empTypeList.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        myLocation = new MyLocation(AttendanceInActivity.this);
        iv = (ImageView) findViewById(R.id.img_train1);
        addImage1 = (RelativeLayout) findViewById(R.id.file_upload_layout1);
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
        GetTrain();
        trainNoList.add(0, "Select Train");
        trainNoAdapter = new ArrayAdapter<String>(AttendanceInActivity.this, android.R.layout.simple_spinner_dropdown_item, trainNoList);
        spTrainNo.setAdapter(trainNoAdapter);
        spTrainNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedTrain = "";
                    selectedIdTrain = "";
                } else {
                    selectedTrain = trainNoList.get(i);
                    selectedIdTrain = trainNoIdList.get(i);
                    GetStation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AttendanceInActivity.this.onBackPressed();
            }
        });

        stationDetailList.add(0, "Select Station");
        stationAdapter = new ArrayAdapter<String>(AttendanceInActivity.this, android.R.layout.simple_spinner_dropdown_item, stationDetailList);
        spStation.setAdapter(stationAdapter);
//        spStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    selectedStation = "";
//                    selectedIdstation="";
//
//                } else {
//                    selectedStation = stationDetailList.get(i);
//                    selectedIdstation=stationDetailIdList.get(i);
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//            }
//        });
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

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coach = et_coach.getText().toString();
                userId = et_userId.getText().toString();

                if (et_dateSelect.getText().toString().isEmpty()) {
                    Toast.makeText(AttendanceInActivity.this, "select Date",
                            Toast.LENGTH_SHORT).show();
                } else if (et_timeSelect.getText().toString().isEmpty()) {
                    Toast.makeText(AttendanceInActivity.this, "select Time",
                            Toast.LENGTH_SHORT).show();
                } else if (spTrainNo.getSelectedItemPosition() == 0) {
                    Toast.makeText(AttendanceInActivity.this, "select Train",
                            Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(et_coach.getText().toString())) {
                    Toast.makeText(AttendanceInActivity.this, "Enter Coach",
                            Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_userId.getText().toString())) {
                    Toast.makeText(AttendanceInActivity.this, "Enter  Valid User Id",
                            Toast.LENGTH_LONG).show();
                } else {
                    try {
                        spTrainNo.setEnabled(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("sks", "kumar");
//                    Toast.makeText(getApplicationContext(), "Location -> "+location, Toast.LENGTH_SHORT).show();

                    GetUploadData(latitude, longitude, location, et_dateSelect.getText().toString(), et_timeSelect.getText().toString());

                }
            }
        });


    }
    /* private void SelectTime() {
           final Calendar calendar = Calendar.getInstance();
           int hour = calendar.get(Calendar.HOUR_OF_DAY);
           int minute = calendar.get(Calendar.MINUTE);
           TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                   R.style.CustomDatePickerTheme,
                   (view, selectedHour, selectedMinute) -> {
                       String selectedTime = String.format(Locale.US, "%02d:%02d", selectedHour, selectedMinute);
                       et_timeSelect.setText(selectedTime);
                   },
                   hour, minute, true);
           timePickerDialog.show();
       }*/
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

    private void EmptypeData(String s) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("employee_id", et_userId.getText().toString());
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));

        } catch (JSONException e) {
            e.printStackTrace();

        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Emp_type, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //  hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("Getdata");
                            empTypeList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                empTypeList.add(obj.getString("EMPM_name"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        spUserName.setAdapter(new ArrayAdapter<String>(AttendanceInActivity.this, android.R.layout.simple_list_item_1, empTypeList));
                        spUserName.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(AttendanceInActivity.this);
        requestQueue.add(objectRequest);

    }

    private void GetUploadData(String latitude, String longitude, String location, String date, String time) {
        String dateTime = date + " " + time;
        final JSONObject jsonObject = new JSONObject();
        double lat = 0, lng = 0;
        String address = "";
        try {
//            lat=myLocation.getLocation().getLatitude();
//            lng=myLocation.getLocation().getLongitude();

            lat = Double.parseDouble(latitude);
            lng = Double.parseDouble(longitude);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lat != 0 && lng != 0) {
            coder = new Geocoder(AttendanceInActivity.this, Locale.getDefault());
            try {
                List<Address> addressList = coder.getFromLocation(lat, lng, 1);
                if (addressList != null) {
                    Log.e("response", addressList.toString());
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
            if (et_userId.getText().toString().toUpperCase().contains("SUP"))
                EMPM_type = "SUP";
            else if (et_userId.getText().toString().toUpperCase().contains("JAN"))
                EMPM_type = "JAN";
            else if (et_userId.getText().toString().toUpperCase().contains("EHK"))
                EMPM_type = "EHK";
            jsonObject.put("EMPM_Name", empTypeList.get(spUserName.getSelectedItemPosition()));
            jsonObject.put("EMPM_type", EMPM_type);
            jsonObject.put("RAPD_BioID", et_userId.getText().toString());
            jsonObject.put("RAPD_Field1", "IN");
            jsonObject.put("RAPD_Field4", trainNoList.get(spTrainNo.getSelectedItemPosition()));
//            jsonObject.put("RAPD_PunchDateTime", O.getDateTime());
            jsonObject.put("RAPD_PunchDateTime",dateTime); // date + time
            jsonObject.put("coach", et_coach.getText().toString());
            jsonObject.put("RAPD_latitude", lat);
            jsonObject.put("RAPD_longitude", lng);
//            jsonObject.put("address",address);
            jsonObject.put("address", location);
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));
            jsonObject.put("attendance_by", PreferenceUtil.getUserIdSelected(this));
            jsonObject.put("user_image", imageresponse1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("responseA", requestBody);
        showLoading("Submit Attendance IN...");

        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, Upload_In_Data
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
                    if (jsonResponse != null && jsonResponse.has("message")) {
                        String message = jsonResponse.getString("message");

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
        RequestQueue requestQueue = Volley.newRequestQueue(AttendanceInActivity.this);
        requestQueue.add(stringRequest);
    }

    private void GetTrain() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("station_code", PreferenceUtil.getDepot(this));
//            showLoading("Loading TrainNo...");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, TRAIN_LIST, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("TrainList");
                            trainNoList.clear();
                            trainNoList.add(0, "Select Train");
                            trainNoIdList.clear();
                            trainNoIdList.add(0, "Select Train");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                trainNoList.add(obj.getString("train_no"));
                                trainNoIdList.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        spTrainNo.setAdapter(new ArrayAdapter<String>(AttendanceInActivity.this, android.R.layout.simple_spinner_dropdown_item, trainNoList));
                        spTrainNo.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(AttendanceInActivity.this);
        requestQueue.add(objectRequest);

    }

    //    private void GetStation() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("train_no",trainNoList.get(spTrainNo.getSelectedItemPosition()));
//            Toast.makeText(this, ""+trainNoList.get(spTrainNo.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
//        }catch (JSONException e){
//            Log.d("station", "GetStation: "+e.getMessage());
//        }
//
//        //create a request
//        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://obhsadi.projectrailway.in/api/atd/get_tstations", jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.e("response", response.toString());
//                try {
//                    JSONArray array = response.getJSONArray("Getsdata");
//                    stationDetailList.clear();
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject obj = array.getJSONObject(i);
//                        stationDetailList.add(obj.getString("station"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                spStation.setAdapter(new ArrayAdapter<String>(AttendanceInActivity.this, android.R.layout.simple_list_item_1, stationDetailList));
//                spStation.setSelected(false);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//
//        //create a request queue
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        queue.add(jsonObjectRequest);
//
//
//
//    }
    private void GetStation() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("train_no", trainNoList.get(spTrainNo.getSelectedItemPosition()));
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
                    e.printStackTrace();
                }
                spStation.setAdapter(new ArrayAdapter<>(AttendanceInActivity.this, android.R.layout.simple_list_item_1, stationDetailList));
                spStation.setSelection(0);
                spStation.setSelected(false);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });


        // Create a request queue
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjectRequest);
    }

    private void checkPermission() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            fileUri = O.cameraProcess(AttendanceInActivity.this, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
//            hasBitmap = false;
//        } else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,}, 100);
//        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d("PermissionCheck", "All permissions granted");
            fileUri = O.cameraProcess(AttendanceInActivity.this, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            hasBitmap = false;
        } else {
            Log.d("PermissionCheck", "Permissions missing");
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("IntentData", "" + data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            gps = new GPSTracker(AttendanceInActivity.this);
            if (gps.canGetLocation()) {
                if (myLocation == null) myLocation = new MyLocation(AttendanceInActivity.this);
                if (myLocation != null && (myLocation.isGpsEnabled || myLocation.isNetworkEnabled)) {
                    Bitmap bitmap = O.reduceScale(fileUri, 1280).copy(Bitmap.Config.ARGB_8888, true);
                    try {
                        double lat = 0, lng = 0;
                        String address = "";
                        try {
                            lat = myLocation.getLocation().getLatitude();
                            lng = myLocation.getLocation().getLongitude();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (lat != 0 && lng != 0) {
                            Geocoder coder = new Geocoder(AttendanceInActivity.this, Locale.getDefault());
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
                        //cityname.setText(cityName);
                        Canvas cs = new Canvas(bitmap);
                        Paint paint = new Paint();
                        paint.setTextSize(35);
                        paint.setColor(Color.BLUE);
                        paint.setStyle(Paint.Style.FILL);
//                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.CHINA);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.CHINA);
                        String datetime = sdf.format(Calendar.getInstance().getTime());
                        cs.drawText(address, 10, bitmap.getHeight() - 5, paint);
                        cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                        try {
                            file_path1 = O.savefile(AttendanceInActivity.this, O.FOLDER_CAMIMG, bitmap, 80);
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
                    MyLocation.displayPromptForEnablingGPS(AttendanceInActivity.this);
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
                        imageresponse1 = new String(response.data);
                        Log.e("imageresponse1", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
        rQueue = Volley.newRequestQueue(AttendanceInActivity.this);
        rQueue.add(volleyMultipartRequest);
        //Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                myLocation = new MyLocation(AttendanceInActivity.this);
            }
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(AttendanceInActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            myLocation = new MyLocation(AttendanceInActivity.this);
        } else {
            ActivityCompat.requestPermissions(AttendanceInActivity.this,
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
//        dialog.dismiss();
    }

    protected void hideLoading() {
        dialog.dismiss();
    }

    public void showConfirmationDialog(String strMessage) {
        final Dialog dialog = new Dialog(AttendanceInActivity.this);
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
                Intent intent = new Intent(AttendanceInActivity.this, AttendanceInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }


}
