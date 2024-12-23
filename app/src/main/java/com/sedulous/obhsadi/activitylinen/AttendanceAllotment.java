package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.sedulous.obhsadi.ModelLinen.GetCoach;
import com.sedulous.obhsadi.ModelLinen.UserDataModel;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.activity.CaptureSignatureActivity;
import com.sedulous.obhsadi.service.GPSTracker;
import com.sedulous.obhsadi.service.MyLocation;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;
import com.sedulous.obhsadi.service.Util;
import com.sedulous.obhsadi.service.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AttendanceAllotment extends AppCompatActivity {
    public static String API_Train = "http://obhsranchi.projectrailway.in/api/Linen/getTrain";
    public static String API_Coach = "http://obhsranchi.projectrailway.in/api/Linen/getCoach";
    public static String API_Class = "http://obhsranchi.projectrailway.in/api/Linen/getClass";
    public final static String STORING_Image = "http://obhsranchi.projectrailway.in/api/Linen/upload_userimage";
    public final static String STORING_sig = "http://obhsranchi.projectrailway.in/api/Linen/upload_signature";
    public static String API_Save_Attendance = "http://obhsranchi.projectrailway.in/api/Linen/saveAttendent";
    public static String API_getUser = "http://obhsranchi.projectrailway.in/api/Linen/getUsers";
    ImageView v_close;
    Button btn_submit;
    EditText et_name_accn,et_load, et_icardno, etDate, et_station, et_bed_sheet, et_pillow, et_pillow_cover, et_bath_towel,
            et_face_towel, et_blanket;
    String date, station,load, bedsheet, pillow,pillowcover, bathtowel, facetowel, blanket, accn_name, IdCard;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 = 400;
    String imageresponse1 = "", imageresponse2="", signatureresponse1="", signatureresponse2="",
            strSignatureFileName = "", strSignatureFilePath = "", strSignatureFileName2 = "",
            strSignatureFilePath2 = "" ;
    LinearLayout signaturelayout;
    ImageView signclick1, signclick2, iv_sign1, iv_sign2;
    public static final int SIGNATURE_ACTIVITY = 1, SIGNATURE_ACTIVITY2 = 2;
    ImageView iv_image1, iv_image2;
    LinearLayout cameralayout;
    String file_path1, file_path2;
    GPSTracker gps;
    Boolean image1 = false, image2 = false;
    ProgressDialog mProgressDialog;
    RequestQueue rQueue;
    Spinner sp_train;
    Spinner sp_coach;
    Spinner sp_class;

    public String fileUri = "", selectTrain="", selectedTrainId, selectCoach = "", selectClassAC ="",
            id="",user_id = "", deport_code = "", designation = "";
    final Calendar myCalendar = Calendar.getInstance();
    ArrayList<String> train_list = new ArrayList<>();
    ArrayList<String> train_type_list = new ArrayList<>();
    ArrayList<String> coach_list = new ArrayList<>();
    ArrayList<String> coach_type_list = new ArrayList<>();
    ArrayList<String> classAC_list = new ArrayList<>();
    ArrayList<String> classAC_list_type = new ArrayList<>();
    ArrayAdapter<String> trainAdapter,coachAdapter,classAdapter;
    UserDataModel userDataModel=null;
    GetCoach getCoach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_allotment);
        user_id=getIntent().getStringExtra("user_id");
        designation=getIntent().getStringExtra("designation");
        deport_code = getIntent().getStringExtra("deport_code");

        try {
            getCoach = (GetCoach) getIntent().getSerializableExtra("id");
            userDataModel = new Gson().fromJson(O.getPreference(this,O.USER_DATA), UserDataModel.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        et_name_accn = findViewById(R.id.et_name_accn);
        et_load=findViewById(R.id.et_load);
        et_icardno = findViewById(R.id.et_id_card_number);
        etDate = findViewById(R.id.et_journey_date);
        et_station = findViewById(R.id.et_station);
        sp_coach = findViewById(R.id.sp_coach);
        sp_class = findViewById(R.id.sp_class);
        et_bed_sheet = findViewById(R.id.et_bed_sheet_AC);
        et_pillow = findViewById(R.id.et_pillow_AC);
        et_pillow_cover = findViewById(R.id.et_pillow_cover);
        et_bath_towel = findViewById(R.id.et_bath_towel);
        et_face_towel = findViewById(R.id.et_face_towel);
        et_blanket = findViewById(R.id.et_blanket);
        cameralayout = (LinearLayout) findViewById(R.id.camera_layout);
        iv_image1 = findViewById(R.id.iv_train1);
        iv_image2 = findViewById(R.id.iv_train2);
        signaturelayout = findViewById(R.id.signature_layout);
        signclick1 = findViewById(R.id.click1);
        signclick2 = findViewById(R.id.click2);
        iv_sign1 = findViewById(R.id.img_sign1);
        iv_sign2 = findViewById(R.id.img_sign2);
        btn_submit = findViewById(R.id.btn_submit);

        et_station.setText(PreferenceUtil.getDepot(this));

        v_close = findViewById(R.id.v_back);
        v_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Registeration.this.onBackPressed();
                AlertDialog.Builder alertbox = new AlertDialog.Builder(AttendanceAllotment.this);

                alertbox.setTitle("Exit ? All data & progress will be lost!");
                alertbox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // finish used for destroyed activity
                        finish();
                    }
                });

                alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Nothing will be happened when clicked on no button
                        // of Dialog
                    }
                });
                alertbox.show();
            }
        });

        et_icardno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_icardno.getText().toString().length() == 8) {
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                getUserName(et_icardno.getText().toString());



            }
        });

        getTrain();
        train_list.add(0, "Select Train Type.");
        sp_train = findViewById(R.id.sp_train);
        trainAdapter=new ArrayAdapter<String>(AttendanceAllotment.this, android.R.layout.simple_spinner_item, train_list);
        trainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_train.setAdapter(trainAdapter);
        sp_train.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long myID) {
                if (i == 0) {
                    selectTrain = "";
                    selectedTrainId="";

                } else {
                    selectTrain = train_list.get(i);
                    selectedTrainId=train_type_list.get(i);

                    getCoach(selectedTrainId);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        coach_list.add(0, "Select Coach Type.");
        coachAdapter=new ArrayAdapter<String>(AttendanceAllotment.this, android.R.layout.simple_spinner_item, coach_list);
        coachAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_coach.setAdapter(coachAdapter);
        sp_coach.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long myID) {
                if (i == 0) {
                    selectCoach = "";
                } else {

                    selectCoach = coach_list.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getClassAC();
        classAC_list.add(0, "Select Class Type.");
        classAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classAC_list);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        sp_class.setAdapter(classAdapter);
        sp_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectClassAC = "";
                } else {

                    selectClassAC = classAC_list.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final DatePickerDialog.OnDateSetListener shiftDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy", Locale.US);
                String dt = "" + dayOfMonth;
                if (dt.length() == 1) dt = "0" + dt;
                String mnth = "" + (monthOfYear + 1);
                if (mnth.length() == 1) mnth = "0" + mnth;
                etDate.setText(year + "-" + mnth + "-" + dt);
            }
        };
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog dpd = new DatePickerDialog(AttendanceAllotment.this, shiftDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(new Date().getTime());
                dpd.show();

            }
        });

        iv_image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image1 = true;
                image2 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

            }
        });
        iv_image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image2 = true;
                image1 = false;
                checkPermission(CAMERA_CAPTURE_IMAGE_REQUEST_CODE2);
            }

        });
        signclick1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceAllotment.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        signclick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AttendanceAllotment.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY2);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                date =        etDate.getText().toString();
                station =     et_station.getText().toString();
                load=         et_load.getText().toString();
                accn_name =   et_name_accn.getText().toString();
                IdCard =      et_icardno.getText().toString();
                bedsheet =    et_bed_sheet.getText().toString();
                pillow =      et_pillow.getText().toString();
                pillowcover = et_pillow_cover.getText().toString();
                bathtowel =   et_bath_towel.getText().toString();
                facetowel =   et_face_towel.getText().toString();
                blanket =     et_blanket.getText().toString();
                if (TextUtils.isEmpty(etDate.getText().toString())) {
                    Toast.makeText(AttendanceAllotment.this, "Please Select Date", Toast.LENGTH_LONG).show();
                } else if(et_icardno.getText().toString().isEmpty()){
                    Toast.makeText(AttendanceAllotment.this, "Please Enter Id Card no", Toast.LENGTH_SHORT).show();
                } else if(et_name_accn.getText().toString().isEmpty()){
                    Toast.makeText(AttendanceAllotment.this, "Please Enter ACCN Name", Toast.LENGTH_SHORT).show();
                } else if(et_load.getText().toString().isEmpty()){
                    Toast.makeText(AttendanceAllotment.this, "Please Enter Load", Toast.LENGTH_SHORT).show();
                } else {

                    SaveDataAttendance();
                }
            }
        });
    }

    private void SaveDataAttendance() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("supervisor_id", PreferenceUtil.getUserIdSelected(this));
            jsonObject.put("journey_date", etDate.getText().toString());
            jsonObject.put("station", et_station.getText().toString());
            jsonObject.put("train_no", selectTrain);
            jsonObject.put("coach", selectCoach);
            jsonObject.put("load", et_load.getText().toString());
            jsonObject.put("class", selectClassAC);
            jsonObject.put("attendent_name", et_name_accn.getText().toString());
            jsonObject.put("idcard_no", et_icardno.getText().toString());
            jsonObject.put("bedsheet", et_bed_sheet.getText().toString());
            jsonObject.put("pillow", et_pillow.getText().toString());
            jsonObject.put("pillow_cover", et_pillow_cover.getText().toString());
            jsonObject.put("bath_towel", et_bath_towel.getText().toString());
            jsonObject.put("face_towel", et_face_towel.getText().toString());
            jsonObject.put("blanket", et_blanket.getText().toString());
            jsonObject.put("item_image", imageresponse1);
            jsonObject.put("item_image1", imageresponse2);
            jsonObject.put("signature", signatureresponse1);
            jsonObject.put("signature1", signatureresponse2);

        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("response", requestBody);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                API_Save_Attendance, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();

                try {
                    Log.e("response_Allotment",response);
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if (jsonResponse!=null && jsonResponse.has("message")) {
                        String message = jsonResponse.getString("message");

                        showConfirmationDialog(message);
                    }else{
                        showConfirmationDialog(response);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getTrain() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("station_code",PreferenceUtil.getDepot(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, API_Train, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("array", response.toString());
                            JSONArray array = response.getJSONArray("TrainList");
                            train_list.clear();
                            train_list.add(0,"Select Train No.");
                            train_type_list.clear();
                            train_type_list.add(0,"Select Train No.");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                train_list.add(obj.getString("train_no"));
                                train_type_list.add(obj.getString("id"));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_train.setAdapter(new ArrayAdapter<String>(AttendanceAllotment.this, android.R.layout.simple_dropdown_item_1line, train_list));
                        sp_train.setSelected(false);

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);
    }
    private void getCoach(String selectedTrainId ) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("station_code", PreferenceUtil.getDepot(this));
            jsonObject.put("train_id",selectedTrainId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, API_Coach, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("array", response.toString());
                            JSONArray array = response.getJSONArray("coachList");
                            coach_list.clear();
                            coach_list.add(0,"Select Coach Type");
                            coach_type_list.clear();
                            coach_type_list.add(0,"Select Coch Type");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                coach_list.add(obj.getString("coach"));
                                coach_type_list.add(obj.getString("id"));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_coach.setAdapter(new ArrayAdapter<String>(AttendanceAllotment.this, android.R.layout.simple_dropdown_item_1line, coach_list));
                        sp_coach.setSelected(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }
    private void getClassAC() {
        JSONObject jsonObject = new JSONObject();

        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, API_Class, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("array", response.toString());
                            JSONArray array = response.getJSONArray("classType");
                            classAC_list.clear();
                            classAC_list.add(0,"Select Class Type");
                            classAC_list_type.add(0,"Select Class Type");
                            classAC_list_type.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                classAC_list.add(obj.getString("class"));
                                classAC_list_type.add(obj.getString("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_class.setAdapter(new ArrayAdapter<String>(AttendanceAllotment.this, android.R.layout.simple_dropdown_item_1line, classAC_list));
                        sp_class.setSelected(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);

    }

    private void getUserName(String s) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_no",et_icardno.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, API_getUser, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                            Log.e("username", response.toString());
                            try {
                                JSONArray array = response.getJSONArray("user_data");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject obj = array.getJSONObject(i);
                                    et_name_accn.setText(obj.getString("name"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(objectRequest);


    }

    private void checkPermission(final int REQUEST_CODE) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileUri = O.cameraProcess(AttendanceAllotment.this, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,}, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("IntentData", "" + data);
        switch (requestCode) {
            case SIGNATURE_ACTIVITY:
                Bundle bundle = data.getExtras();
                String status = bundle.getString("status");
                if (status.equalsIgnoreCase("done")) {
                    String image_url = bundle.getString("signature_image_url");
                    String filePath = bundle.getString("FilePath");
                    String bitmap = bundle.getString("bitmap");
                    iv_sign1.setImageBitmap(BitmapFactory.decodeFile(image_url));
                    byte[] encodeByte = Base64.decode(bitmap, Base64.DEFAULT);
                    uploadSign1(encodeByte);
                    try {
                        File sd = new File(getExternalFilesDir(null), O.FOLDER_SIGN);
                        if (!sd.exists()) sd.mkdir();
                        strSignatureFileName = "sign_" + Util.createTimeAudioFileName() + ".png";//file extension should be in lowe case(Php Server side issue)
                        File dest = new File(sd, strSignatureFileName);
                        Uri savedImageURI = Uri.parse(image_url);
                        saveFile(savedImageURI, dest);
                        strSignatureFilePath = dest.getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case SIGNATURE_ACTIVITY2:
                Bundle bundle1 = data.getExtras();
                String statuss = bundle1.getString("status");
                if (statuss.equalsIgnoreCase("done")) {
                    String image_url = bundle1.getString("signature_image_url");
                    String filePath = bundle1.getString("FilePath");
                    Log.e("filePath", filePath);
                    String bitmap = bundle1.getString("bitmap");
                    iv_sign2.setImageBitmap(BitmapFactory.decodeFile(image_url));
                    byte[] encodeByte = Base64.decode(bitmap, Base64.DEFAULT);
                    uploadSign2(encodeByte);

                    try {
                        File sd = new File(getExternalFilesDir(null), O.FOLDER_SIGN);
                        if (!sd.exists()) sd.mkdir();
                        strSignatureFileName2 = "sign_" + Util.createTimeAudioFileName() + ".png";//file extension should be in lowe case(Php Server side issue)
                        File dest = new File(sd, strSignatureFileName2);
                        Uri savedImageURI = Uri.parse(image_url);
                        saveFile(savedImageURI, dest);
                        strSignatureFilePath2 = dest.getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        //...................CAMERA_CAPTURE_IMAGE_REQUEST_CODE2......................
        Log.e("IntentData", "" + data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            gps = new GPSTracker(AttendanceAllotment.this);
            if (gps.canGetLocation()) {
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
                        file_path1 = O.savefile(AttendanceAllotment.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap(file_path1);
                        iv_image1.setImageBitmap(BitmapFactory.decodeFile(file_path1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(AttendanceAllotment.this);
            }

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 && resultCode == RESULT_OK) {
            gps = new GPSTracker(AttendanceAllotment.this);
            if (gps.canGetLocation()) {
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
                    paint.setTextSize(32);
                    paint.setColor(Color.BLUE);
                    paint.setStyle(Paint.Style.FILL);
                    float height = paint.measureText("yY");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy, hh:mm a", Locale.US);
                    String datetime = sdf.format(Calendar.getInstance().getTime());
                    cs.drawText(cityName, 10, bitmap.getHeight() - 5, paint);
                    cs.drawText(datetime, 10, bitmap.getHeight() - 35, paint);
                    try {
                        file_path2 = O.savefile(AttendanceAllotment.this, O.FOLDER_CAMIMG, bitmap, 80);
                        uploadBitmap2(file_path2);
                        iv_image2.setImageBitmap(BitmapFactory.decodeFile(file_path2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "First allow Location",
                        Toast.LENGTH_SHORT).show();
                MyLocation.displayPromptForEnablingGPS(AttendanceAllotment.this);
            }
        }
    }

    private void uploadSign1(byte[] bytes) {
        showLoading("Uploading Signature...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_sig,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        signatureresponse1 = new String(response.data);
                        Log.e("signatureresponse1", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("sign", new DataPart(imagename + ".png", bytes));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(AttendanceAllotment.this);
        rQueue.add(volleyMultipartRequest);
    }

    private void uploadSign2(final byte[] bytes) {
        showLoading("Uploading Signature...");
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, STORING_sig,
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        hideLoading();
                        signatureresponse2 = new String(response.data);
                        Log.e("signatureresponse2", new String(response.data));
                        rQueue.getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                long imagename = System.currentTimeMillis();
                params.put("sign", new DataPart(imagename + ".png", bytes));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(AttendanceAllotment.this);
        rQueue.add(volleyMultipartRequest);
    }

    private boolean saveFile(Uri sourceUri, File destination) {

        final int chunkSize = 1024;  // We'll read in one kB at a time
        byte[] imageData = new byte[chunkSize];

        try {
            InputStream in = getContentResolver().openInputStream(sourceUri);
            OutputStream out = new FileOutputStream(destination);
            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }
            in.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return true;
        }
    }

    private void uploadBitmap(final String filepath) {
        showLoading("Uploading Image, Please wait...");
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
                    public void onErrorResponse(com.android.volley.VolleyError error) {
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
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }

    private void uploadBitmap2(final String filepath) {
        showLoading("Uploading Image, Please wait...");
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
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", O.getBytes(filepath)));
                return params;
            }
        };
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(getApplicationContext());
        rQueue.add(volleyMultipartRequest);
    }

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

    protected void showLoading(@NonNull String message0) {
        mProgressDialog = new ProgressDialog(AttendanceAllotment.this);
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

    public void showConfirmationDialog(String strMessage) {
        final Dialog dialog = new Dialog(AttendanceAllotment.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.show();
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(strMessage);
        TextView tvOk = dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(AttendanceAllotment.this, SupervisorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

}