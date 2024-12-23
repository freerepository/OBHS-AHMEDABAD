package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RegistraionActivity extends AppCompatActivity {
    public static String Save_Employee="http://obhsadi.projectrailway.in/api/atd/save_employee";
    public static String User_Type="http://obhsadi.projectrailway.in/api/atd/get_usertype";
    public static String Skill_type="http://obhsadi.projectrailway.in/api/atd/get_skilledtype";
    public static String STORING_Image="http://obhsadi.projectrailway.in/api/atd/upload_userimage";

    Button btn_save;
    ImageView iv,v_back;
    Spinner sp_employee_type,sp_skill_type;
    EditText et_userName,et_employeeId,et_mobileNo,et_password,et_emailId,et_address;
    RadioGroup rg_police_verification, rg_idcard;
    RadioButton rg_policev_n, rg_policev_y,rg_idcard_n,rg_idcard_y;
    String file_path1="", imageresponse1 = "";
    private String fileUri;
    ArrayList<String> employeeType = new ArrayList<>();
    ArrayList<String> skillType = new ArrayList<>();
    ArrayAdapter<String> skillTypeAdapter;
    ArrayAdapter<String> employeeAdapter;
    String  user_id="",deport_code="",designation="" ;
    RelativeLayout addImage1;
    int layout1_width, layout1_height;
    MyLocation myLocation;
    GPSTracker gps;
    RequestQueue rQueue;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    boolean mLocationPermissionGranted = false;
    private boolean doubleBacktoExitpresone = false;
    int PERMISSIONS_REQUEST_LOCATION = 99;
    Geocoder coder;
    boolean hasBitmap;
    public String selectedEmplopeeType,selectedSkilledType;
    AlertDialog dialog;
    UserDataModel userDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registraion);
        try {

            userDataModel = new Gson().fromJson(O.getPreference(this,O.USER_DATA), UserDataModel.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_save=findViewById(R.id.btn_submit);
        et_userName=findViewById(R.id.et_name);
        et_employeeId=findViewById(R.id.et_employeeID);
        et_mobileNo=findViewById(R.id.et_mobile_no);
        et_password=findViewById(R.id.et_password);
        et_emailId=findViewById(R.id.et_emailId);
        et_address=findViewById(R.id.et_address);
        sp_employee_type=findViewById(R.id.sp_employee_type);
        sp_skill_type=findViewById(R.id.sp_skill_type);
        rg_police_verification=findViewById(R.id.rg_police_verification);
        rg_idcard=findViewById(R.id.rg_idcard);
        rg_policev_n=findViewById(R.id.rb_yes);
        rg_policev_y=findViewById(R.id.rb_no);
        rg_idcard_n=findViewById(R.id.rb_idcard_yes);
        rg_idcard_y=findViewById(R.id.rb_idcard_no);
        user_id=getIntent().getStringExtra("user_id");
        designation=getIntent().getStringExtra("designation");
        deport_code = getIntent().getStringExtra("deport_code");
        v_back=findViewById(R.id.v_back);

        v_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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
        EmployeeType();
        employeeType.add(0, "Select Employee Type");
        employeeAdapter = new ArrayAdapter<String>(RegistraionActivity.this, android.R.layout.simple_spinner_dropdown_item, employeeType);
        sp_employee_type.setAdapter(employeeAdapter);
        sp_employee_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedEmplopeeType = "";

                } else {
                    selectedEmplopeeType = employeeType.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        Skilltype();
        skillType.add(0, "Select Skilled Type.");
        skillTypeAdapter = new ArrayAdapter<String>(RegistraionActivity.this, android.R.layout.simple_spinner_dropdown_item, skillType);
        sp_skill_type.setAdapter(skillTypeAdapter);
        sp_skill_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    selectedSkilledType = "";

                } else {
                    selectedSkilledType = skillType.get(i);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(et_userName.getText().toString())) {
                    Toast.makeText(RegistraionActivity.this, "Enter Name", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_employeeId.getText().toString())) {
                    Toast.makeText(RegistraionActivity.this, "Enter Employee ID", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_mobileNo.getText().toString())) {
                    Toast.makeText(RegistraionActivity.this, "Enter Mobile No", Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(et_password.getText().toString())){
                    Toast.makeText(RegistraionActivity.this,"Enter Password",Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(et_address.getText().toString())) {
                    Toast.makeText(RegistraionActivity.this, "Enter Address", Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(rg_policev_n.getText().toString())){
                    Toast.makeText(RegistraionActivity.this,"YES",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(rg_policev_y.getText().toString())){
                    Toast.makeText(RegistraionActivity.this,"NO",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(rg_idcard_n.getText().toString())){
                    Toast.makeText(RegistraionActivity.this,"YES",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(rg_idcard_y.getText().toString())){
                    Toast.makeText(RegistraionActivity.this,"NO",Toast.LENGTH_LONG).show();
                } else if (sp_employee_type.getSelectedItemPosition() == 0) {
                    Toast.makeText(RegistraionActivity.this, "Enter Employee Type", Toast.LENGTH_SHORT).show();
                } else if (sp_skill_type.getSelectedItemPosition() == 0) {
                    Toast.makeText(RegistraionActivity.this, "Enter Skilled", Toast.LENGTH_SHORT).show();
                }else {
                    final String uname= et_userName.getText().toString().trim();
                    final String emp= et_employeeId.getText().toString().trim();
                    final String mobile= et_mobileNo.getText().toString().trim();
                    final String password =et_password.getText().toString().trim();
                    final String address= et_address.getText().toString().trim();
                   // final String email= et_emailId.getText().toString().trim();

                    uploadSaveEmpData();
                }
            }
        });
    }


    private void uploadSaveEmpData() {
        final String radioPoiceVer = ((RadioButton) findViewById(rg_police_verification.getCheckedRadioButtonId())).getText().toString();
        final String radioIdCard = ((RadioButton) findViewById(rg_idcard.getCheckedRadioButtonId())).getText().toString();

        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));
            jsonObject.put("EMPM_name", et_userName.getText().toString());
            jsonObject.put("EMPM_type", employeeType.get(sp_employee_type.getSelectedItemPosition()));
            jsonObject.put("EMPM_skill_type",skillType.get(sp_skill_type.getSelectedItemPosition()));
            jsonObject.put("EMPM_password", et_password.getText().toString());
            jsonObject.put("EMPM_phone_no",et_mobileNo.getText().toString());
            jsonObject.put("EMPM_userID", et_employeeId.getText().toString());
            jsonObject.put("EMPM_address", et_address.getText().toString());
            jsonObject.put("police_verification", radioPoiceVer);
            jsonObject.put("EMPM_idcard", radioIdCard);
            jsonObject.put("EMPM_picture", imageresponse1);



        } catch (Exception e) {
            e.printStackTrace();
        }
        final String requestBody = jsonObject.toString();
        Log.e("response", requestBody);
        showLoading("Registration submitted successfully....");
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST,Save_Employee
                , new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideLoading();
                try {
                    Log.e("responseAtt",response);
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    // int status = jsonResponse.getInt("status");
                    if (jsonResponse!=null && jsonResponse.has("message")) {
                        String message1 = jsonResponse.getString("message");
                        //Toast.makeText(AttOutActivity.this, message, Toast.LENGTH_LONG);
                        showConfirmationDialog(message1);
                    }else{
                        showConfirmationDialog(response);
                    }

                } catch(Exception e){
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
        RequestQueue requestQueue = Volley.newRequestQueue(RegistraionActivity.this);
        requestQueue.add(stringRequest);


    }

    private void Skilltype() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, Skill_type, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("Getskilledtype");
                            skillType.clear();
                            skillType.add(0,"Select Skilled Type");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                skillType.add(obj.getString("skilled_type"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_skill_type.setAdapter(new ArrayAdapter<String>(RegistraionActivity.this, android.R.layout.simple_spinner_dropdown_item, skillType));
                        sp_skill_type.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(RegistraionActivity.this);
        requestQueue.add(objectRequest);
    }

    private void EmployeeType() {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("depot_code", PreferenceUtil.getDepot(this));
            showLoading("Loading employee id...");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, User_Type, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoading();
                        Log.e("response", response.toString());
                        try {
                            JSONArray array = response.getJSONArray("Getuserstype");
                            employeeType.clear();
                            employeeType.add(0,"Select Employee Type");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                employeeType.add(obj.getString("user_type"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sp_employee_type.setAdapter(new ArrayAdapter<String>(RegistraionActivity.this, android.R.layout.simple_spinner_dropdown_item, employeeType));
                        sp_employee_type.setSelected(false);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoading();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(RegistraionActivity.this);
        requestQueue.add(objectRequest);

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileUri = O.cameraProcess(RegistraionActivity.this, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            hasBitmap = false;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,}, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("IntentData", "" + data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            gps = new GPSTracker(RegistraionActivity.this);
            if (gps.canGetLocation()) {
                if (myLocation == null) myLocation = new MyLocation(RegistraionActivity.this);
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
                            file_path1 = O.savefile(RegistraionActivity.this, O.FOLDER_CAMIMG, bitmap, 80);
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
                    MyLocation.displayPromptForEnablingGPS(RegistraionActivity.this);
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
        rQueue = Volley.newRequestQueue(RegistraionActivity.this);
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
                myLocation = new MyLocation(RegistraionActivity.this);
            }
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
        final Dialog dialog = new Dialog(RegistraionActivity.this);
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
                Intent intent = new Intent(RegistraionActivity.this, RegistraionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }

}