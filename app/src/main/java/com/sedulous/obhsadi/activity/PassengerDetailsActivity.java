package com.sedulous.obhsadi.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.sedulous.obhsadi.FileUtils;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.HttpResponseClass;
import com.sedulous.obhsadi.service.NetworkStatusClass;
import com.sedulous.obhsadi.service.Util;
import com.sedulous.obhsadi.service.WebServicesURLClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class PassengerDetailsActivity extends AppCompatActivity {

    public static final int SIGNATURE_ACTIVITY = 1,CAPTURE_IDENTITY=2;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private EditText etPassengerName,etTrainNumber,etCoachNumber,etSeatNumber,etPNRNumber,etIdentityNo,etMobile;
    private String strPassengerName,strTrainNumber,strSeatNumber,strPNRNumber,strIdentityNo,strMobile;
    private String strSignatureFileName="",strIdentityFilePath="",strSignatureFilePath="";
    private Spinner spCoachNumber;
    ImageView ivIdentity;
    int strCoachNumber=0;
    private ArrayList<String> strCoachArrayList, coachTypeList;
    private String[] strCoachList={"Select Coach Number",
            "S1","S2","S3","S4","S5","S6","S7","S8","S9","S10","S11","S12","S13","S14","S15","S16","S17",
            "B1","B2","B3","B4","B5","B6","B7","B8","B9","B10","B11","B12",
            "HA1","HA2","HA3","A1","A2","A3","A4","A5","A6",
            "G1","G2","G3","G4","G5","G6","G7","G8","G9","G10","G11","G12","G13","G14","G15","G16","G17","G18"};

    private boolean doubleBackToExitPressedOnce=false;
    private ArrayAdapter coachSpinerAdapter;
    String file_uri="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        strCoachArrayList=new ArrayList<>();
        strCoachArrayList.add("Select Coach Number");
        coachTypeList=new ArrayList<>();
        coachTypeList.add("");

        ivIdentity= findViewById(R.id.iv_identity);
        etPNRNumber=(EditText)findViewById(R.id.et_pnr_number);
        etTrainNumber=(EditText)findViewById(R.id.et_train_number);
        etCoachNumber=(EditText)findViewById(R.id.et_coach_number);
        etSeatNumber=(EditText)findViewById(R.id.et_seat_number);
        etPassengerName=(EditText)findViewById(R.id.et_passenger_name);
        etMobile=(EditText)findViewById(R.id.et_mobile_number);
        etIdentityNo=(EditText)findViewById(R.id.et_identity_no);
        btnSubmit=(Button)findViewById(R.id.btn_submit);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        spCoachNumber=findViewById(R.id.sp_coach_number);
        coachSpinerAdapter=new ArrayAdapter(PassengerDetailsActivity.this,android.R.layout.simple_list_item_1,strCoachArrayList);
        spCoachNumber.setAdapter(coachSpinerAdapter);

        spCoachNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strCoachNumber=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etPNRNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()==10)
                {
//                    String url="https://api.railwayapi.com/v2/pnr-status/pnr/"+editable+"/apikey/bqdqlatb0w/";
                    String url="https://api.railwayapi.com/v2/pnr-status/pnr/"+editable+"/apikey/"+getResources().getString(R.string.pnr_key)+"/";

                    if (NetworkStatusClass.isNetworkStatusAvialable(PassengerDetailsActivity.this))
                        new GetPnrStatusClass().execute(url);
                    else
                        Toast.makeText(PassengerDetailsActivity.this, R.string.internet_connection_text, Toast.LENGTH_LONG).show();

                }
            }
        });

        etTrainNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()==5)
                {
                    if (NetworkStatusClass.isNetworkStatusAvialable(PassengerDetailsActivity.this))
                        new GetTrainWiseCoachListClass().execute(editable.toString().trim());
                    else
                        Toast.makeText(PassengerDetailsActivity.this, R.string.internet_connection_text, Toast.LENGTH_LONG).show();

                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View view) {
                strPNRNumber=etPNRNumber.getText().toString();
                strTrainNumber=etTrainNumber.getText().toString();
//                strCoachNumber=etCoachNumber.getText().toString();
                strSeatNumber=etSeatNumber.getText().toString();
                strPassengerName=etPassengerName.getText().toString();
                strMobile=etMobile.getText().toString();
                strIdentityNo=etIdentityNo.getText().toString();

                if (!strPassengerName.isEmpty()&&!strTrainNumber.isEmpty()&& strCoachNumber>0 &&!strSeatNumber.isEmpty()&&!strPNRNumber.isEmpty()&&!strIdentityNo.isEmpty()&&!strMobile.isEmpty()) {
                    if (strMobile.length()==10) {
                        if (strPNRNumber.length()==10){
                            if (NetworkStatusClass.isNetworkStatusAvialable(PassengerDetailsActivity.this)) {
                                //btnSubmit.setEnabled(false);//akm
                                //new VerifyOtpServiceClass().execute(strMobile);
                                if(coachTypeList.get(strCoachNumber).equalsIgnoreCase("NON AC")){

                                    Intent intent = new Intent(PassengerDetailsActivity.this, PassengerFeedbackNonAcActivity.class);
                                    intent.putExtra("inspection_type", "Non AC");
                                    intent.putExtra("strPassengerName", strPassengerName);
                                    intent.putExtra("strTrainNumber", strTrainNumber);
                                    intent.putExtra("strCoachNumber", strCoachArrayList.get(strCoachNumber));
                                    intent.putExtra("strSeatNumber", strSeatNumber);
                                    intent.putExtra("strPNRNumber", strPNRNumber);
                                    intent.putExtra("strIdentityNo", strIdentityNo);
                                    intent.putExtra("strMobile", strMobile);
                                    intent.putExtra("strSignatureFileName", strSignatureFileName);
                                    intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                                    intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                                    intent.putExtra("feedback_type", "passenger");
                                    intent.putExtra("otp_verified", "Y");
                                    startActivity(intent);
                                    finish();
                                }else{
                                    //AC
                                    Intent intent = new Intent(PassengerDetailsActivity.this, PassengerFeedbackAcActivity.class);
                                    intent.putExtra("inspection_type", "AC");
                                    intent.putExtra("strPassengerName", strPassengerName);
                                    intent.putExtra("strTrainNumber", strTrainNumber);
                                    intent.putExtra("strCoachNumber", strCoachArrayList.get(strCoachNumber));
                                    intent.putExtra("strSeatNumber", strSeatNumber);
                                    intent.putExtra("strPNRNumber", strPNRNumber);
                                    intent.putExtra("strIdentityNo", strIdentityNo);
                                    intent.putExtra("strMobile", strMobile);
                                    intent.putExtra("strSignatureFileName", strSignatureFileName);
                                    intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                                    intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                                    intent.putExtra("feedback_type", "passenger");
                                    intent.putExtra("otp_verified", "Y");
                                    startActivity(intent);
                                    finish();
                                }

                            }else
                            Toast.makeText(PassengerDetailsActivity.this, R.string.internet_connection_text, Toast.LENGTH_LONG).show();
                        }else
                            Toast.makeText(PassengerDetailsActivity.this, "PNR number should be 10 digits long", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PassengerDetailsActivity.this, "Mobile number should be 10 digits long", Toast.LENGTH_LONG).show();
                    }

                }else if (strPNRNumber.isEmpty()){
                    Toast.makeText(PassengerDetailsActivity.this, "Please enter PNR number", Toast.LENGTH_LONG).show();
                }else if (strTrainNumber.isEmpty()) {
                    Toast.makeText(PassengerDetailsActivity.this, "Please enter train number", Toast.LENGTH_LONG).show();
                }else if (strCoachNumber==0) {
                    Toast.makeText(PassengerDetailsActivity.this, "Please select coach number", Toast.LENGTH_LONG).show();
                }else if (strSeatNumber.isEmpty()) {
                    Toast.makeText(PassengerDetailsActivity.this, "Please enter seat number", Toast.LENGTH_LONG).show();
                }else if (strPassengerName.isEmpty()){
                    Toast.makeText(PassengerDetailsActivity.this, "Please enter passenger name", Toast.LENGTH_LONG).show();
                }else if (strMobile.isEmpty()) {
                    Toast.makeText(PassengerDetailsActivity.this, "Please enter mobile number", Toast.LENGTH_LONG).show();
                }else if (strIdentityNo.isEmpty()) {
                    Toast.makeText(PassengerDetailsActivity.this, "Please enter identity number", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(PassengerDetailsActivity.this, "Please enter the detail", Toast.LENGTH_SHORT).show();
                }

            }
        });
        //amardeep
        findViewById(R.id.tv_capture_id_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(CAPTURE_IDENTITY);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_logout)
        {
            Intent intent=new Intent(PassengerDetailsActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void takeSignature(View view) {
        Intent intent = new Intent(PassengerDetailsActivity.this, CaptureSignatureActivity.class);
        startActivityForResult(intent,SIGNATURE_ACTIVITY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode) {
                case SIGNATURE_ACTIVITY:
                    Bundle bundle = data.getExtras();
                    String status  = bundle.getString("status");
                    if(status.equalsIgnoreCase("done")){
                        strSignatureFilePath=bundle.getString("signature_image_url");
                        String filePath=bundle.getString("FilePath");
                        Log.e("filePath",filePath);
                        ImageView imageView = (ImageView)findViewById(R.id.imageView);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        //Picasso.get().load(image_url).into(imageView);
                        strSignatureFileName = "sign_"+Util.createTimeAudioFileName()+".png";//file extension should be in lowe case(Php Server side issue)

                    }
                    break;
                case CAPTURE_IDENTITY:
                    //amardeep
                    if (!TextUtils.isEmpty(file_uri)) {
                        strIdentityFilePath = FileUtils.getReducePic(PassengerDetailsActivity.this, file_uri);
                    }
                    file_uri = "";
                    if(new File(strIdentityFilePath).exists()) {
                        ImageView ivIdentity = (ImageView)findViewById(R.id.iv_identity);
                        ivIdentity.setImageBitmap(FileUtils.getBitmap(strIdentityFilePath,512));
                    }else{
                        Toast.makeText(PassengerDetailsActivity.this,
                                "Select file from another location", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(this, "GET_ACCOUNTS Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public class UploadImageFileClass extends AsyncTask<String,String,Integer>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... param) {

            HttpResponseClass httpResponseClass=new HttpResponseClass();
            int resonseCode= httpResponseClass.uploadImageFile("signature",strSignatureFilePath,strSignatureFileName);
            return resonseCode;
        }

        @Override
        protected void onPostExecute(Integer response) {
            super.onPostExecute(response);
            try{
                Log.e("ResCode",response+"");
                progressBar.setVisibility(View.GONE);
                if (response==200){
                    Toast.makeText(PassengerDetailsActivity.this, "Signature Uploaded", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(PassengerDetailsActivity.this, "Signature File Uploading Error..", Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    public class GetPnrStatusClass extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... param) {
            HttpResponseClass httpResponseClass=new HttpResponseClass();
            JSONObject jsonObject=httpResponseClass.getDetailByPnr(param[0]);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                progressBar.setVisibility(View.GONE);
                Log.e("Pnr-Response:",jsonObject.toString());
                int status=jsonObject.getInt("response_code");
                if (status==200){
                    JSONObject jsonObjectT=jsonObject.getJSONObject("train");
                    int trainNumber=jsonObjectT.getInt("number");
                    JSONArray jsonArray=jsonObject.getJSONArray("passengers");
                    JSONObject jsonObject1=jsonArray.getJSONObject(0);

                    String booking_status=jsonObject1.getString("booking_status").trim();
                    String[] strBookingStatus=booking_status.split("/");
                    String coachNumReal="";
                    String seatNumReal="";

                    if (strBookingStatus[0].equalsIgnoreCase("CNF")){
                        coachNumReal =strBookingStatus[1] ;
                        seatNumReal = strBookingStatus[2];
                        for (int i=0;i<strCoachArrayList.size();i++) {
                            if (coachNumReal.equalsIgnoreCase(strCoachArrayList.get(i))){
                                spCoachNumber.setSelection(i);
                            }
                        }
                    }else {
                        String current_status=jsonObject1.getString("current_status").trim();
                        String[] strCurrentStatus=current_status.split("/");
                        if (strCurrentStatus[0].equalsIgnoreCase("CNF")){
                            coachNumReal =strCurrentStatus[1] ;
                            seatNumReal = strCurrentStatus[2];
                            for (int i=0;i<strCoachArrayList.size();i++) {
                                if (coachNumReal.equalsIgnoreCase(strCoachArrayList.get(i))){
                                    spCoachNumber.setSelection(i);
                                }
                            }
                        }
                    }

                    etTrainNumber.setText(trainNumber+"");
                    etCoachNumber.setText(coachNumReal);
                    etSeatNumber.setText(seatNumReal);

                }else  if (status==220){
                    Toast.makeText(PassengerDetailsActivity.this, "Please enter a valid PNR number", Toast.LENGTH_LONG).show();
                }else {
//                    Toast.makeText(PassengerDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public class GetTrainWiseCoachListClass extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... param) {

            JSONObject json = new JSONObject();
            try {
                json.put("train_no", param[0]);
            }catch (JSONException e){
                e.printStackTrace();
            }

            WebServicesURLClass urlClass=new WebServicesURLClass();
            JSONObject jsonObject=urlClass.getTrainWiseCoachListMethod(json);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                progressBar.setVisibility(View.GONE);
                Log.e("CoachListResponse:",jsonObject.toString());
                int status=jsonObject.getInt("status");
                if (status==1){
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    strCoachArrayList.clear();
                    strCoachArrayList.add("Select Coach Number");
                    for (int i=0;i<jsonArray.length();i++){
                        strCoachArrayList.add(jsonArray.getJSONObject(i).getString("coach_no"));
                        coachTypeList.add(jsonArray.getJSONObject(i).getString("coach_type"));

                    }
                    coachSpinerAdapter.notifyDataSetChanged();
                }else {
                    strCoachArrayList.clear();
                    strCoachArrayList.add("Select Coach Number");
                    coachSpinerAdapter.notifyDataSetChanged();
                    Toast.makeText(PassengerDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public class VerifyOtpServiceClass extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... param) {

            JSONObject json = new JSONObject();
            try {
                json.put("contact_no", param[0]);
            }catch (JSONException e){
                e.printStackTrace();
            }

            WebServicesURLClass urlClass=new WebServicesURLClass();
            JSONObject jsonObject=urlClass.otpVerificationMethod(json);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            btnSubmit.setEnabled(true);

            try{
                progressBar.setVisibility(View.GONE);
                //Log.e("OtpResponse:",jsonObject.toString());
                int status=jsonObject.getInt("status");
                if (status==1){
                    //non AC
                    if(coachTypeList.get(strCoachNumber).equalsIgnoreCase("NON AC")){

                        Intent intent = new Intent(PassengerDetailsActivity.this, OtpVerifyActivity.class);
                        intent.putExtra("inspection_type", "Non AC");
                        intent.putExtra("strPassengerName", strPassengerName);
                        intent.putExtra("strTrainNumber", strTrainNumber);
                        intent.putExtra("strCoachNumber", strCoachArrayList.get(strCoachNumber));
                        intent.putExtra("strSeatNumber", strSeatNumber);
                        intent.putExtra("strPNRNumber", strPNRNumber);
                        intent.putExtra("strIdentityNo", strIdentityNo);
                        intent.putExtra("strMobile", strMobile);
                        intent.putExtra("feedback_type", "passenger");
                        intent.putExtra("strSignatureFileName", strSignatureFileName);
                        intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                        intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                        intent.putExtra("strOtp", jsonObject.getJSONObject("data").getString("otp"));
                        intent.putExtra("strMessage", jsonObject.getString("message"));
                        startActivity(intent);
                        finish();
                    }else{
                        //AC
                        Intent intent = new Intent(PassengerDetailsActivity.this, OtpVerifyActivity.class);
                        intent.putExtra("inspection_type", "AC");
                        intent.putExtra("strPassengerName", strPassengerName);
                        intent.putExtra("strTrainNumber", strTrainNumber);
                        intent.putExtra("strCoachNumber", strCoachArrayList.get(strCoachNumber));
                        intent.putExtra("strSeatNumber", strSeatNumber);
                        intent.putExtra("strPNRNumber", strPNRNumber);
                        intent.putExtra("strIdentityNo", strIdentityNo);
                        intent.putExtra("strMobile", strMobile);
                        intent.putExtra("feedback_type", "passenger");
                        intent.putExtra("strSignatureFileName", strSignatureFileName);
                        intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                        intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                        intent.putExtra("strOtp", jsonObject.getJSONObject("data").getString("otp"));
                        intent.putExtra("strMessage", jsonObject.getString("message"));
                        startActivity(intent);
                        finish();
                    }

                }else {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(PassengerDetailsActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    //amardeep
    private void checkPermission(final int REQUEST_CODE) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = FileUtils.cameraProcess(this);
                file_uri = intent.getStringExtra("filepath");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
