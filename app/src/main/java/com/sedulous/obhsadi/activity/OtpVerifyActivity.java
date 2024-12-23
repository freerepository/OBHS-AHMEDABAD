package com.sedulous.obhsadi.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.NetworkStatusClass;
import com.sedulous.obhsadi.service.WebServicesURLClass;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpVerifyActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerify;
    private String strOtp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        strOtp=getIntent().getStringExtra("strOtp");
        final String strMessage=getIntent().getStringExtra("strMessage");

        final String strInspectionType=getIntent().getStringExtra("inspection_type");
        final String strFeedbackType=getIntent().getStringExtra("feedback_type");
        final String strPassengerName=getIntent().getStringExtra("strPassengerName");
        final String strTrainNumber=getIntent().getStringExtra("strTrainNumber");
        final String strCoachNumber=getIntent().getStringExtra("strCoachNumber");
        final String strSeatNumber=getIntent().getStringExtra("strSeatNumber");
        final String strPNRNumber=getIntent().getStringExtra("strPNRNumber");
        final String strIdentityNo=getIntent().getStringExtra("strIdentityNo");
        final String strMobile=getIntent().getStringExtra("strMobile");
        final String strSignatureFileName=getIntent().getStringExtra("strSignatureFileName");
        final String strSignatureFilePath=getIntent().getStringExtra("strSignatureFilePath");
        final String strIdentityFilePath=getIntent().getStringExtra("strIdentityFilePath");

        etOtp=(EditText)findViewById(R.id.et_otp);
        btnVerify=(Button)findViewById(R.id.btn_verify);

        TextView tvResend=findViewById(R.id.tv_resend);
        TextView tvSkip=findViewById(R.id.tv_skip);

        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkStatusClass.isNetworkStatusAvialable(OtpVerifyActivity.this)) {
                    new ResendOtpServiceClass().execute(strMobile);
                }else Toast.makeText(OtpVerifyActivity.this, R.string.internet_connection_text, Toast.LENGTH_SHORT).show();
            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (strInspectionType.equalsIgnoreCase("AC"))
                    intent = new Intent(OtpVerifyActivity.this, PassengerFeedbackAcActivity.class);
                else intent = new Intent(OtpVerifyActivity.this, PassengerFeedbackNonAcActivity.class);
                intent.putExtra("inspection_type", strInspectionType);
                intent.putExtra("strPassengerName", strPassengerName);
                intent.putExtra("strTrainNumber", strTrainNumber);
                intent.putExtra("strCoachNumber", strCoachNumber);
                intent.putExtra("strSeatNumber", strSeatNumber);
                intent.putExtra("strPNRNumber", strPNRNumber);
                intent.putExtra("strIdentityNo", strIdentityNo);
                intent.putExtra("strMobile", strMobile);
                intent.putExtra("strSignatureFileName", strSignatureFileName);
                intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                intent.putExtra("feedback_type", strFeedbackType);
                intent.putExtra("otp_verified", "N");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stOtp=etOtp.getText().toString();
                if (strOtp.matches(stOtp)) {

                    if (strInspectionType.equalsIgnoreCase("NON AC")){

                        Intent intent = new Intent(OtpVerifyActivity.this, PassengerFeedbackNonAcActivity.class);
                        intent.putExtra("inspection_type", "Non AC");
                        intent.putExtra("strPassengerName", strPassengerName);
                        intent.putExtra("strTrainNumber", strTrainNumber);
                        intent.putExtra("strCoachNumber", strCoachNumber);
                        intent.putExtra("strSeatNumber", strSeatNumber);
                        intent.putExtra("strPNRNumber", strPNRNumber);
                        intent.putExtra("strIdentityNo", strIdentityNo);
                        intent.putExtra("strMobile", strMobile);
                        intent.putExtra("strSignatureFileName", strSignatureFileName);
                        intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                        intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                        intent.putExtra("feedback_type", strFeedbackType);
                        intent.putExtra("otp_verified", "Y");
                        startActivity(intent);
                        finish();
                    }else{
                        //AC

                        Intent intent = new Intent(OtpVerifyActivity.this, PassengerFeedbackAcActivity.class);
                        intent.putExtra("inspection_type", "AC");
                        intent.putExtra("strPassengerName", strPassengerName);
                        intent.putExtra("strTrainNumber", strTrainNumber);
                        intent.putExtra("strCoachNumber", strCoachNumber);
                        intent.putExtra("strSeatNumber", strSeatNumber);
                        intent.putExtra("strPNRNumber", strPNRNumber);
                        intent.putExtra("strIdentityNo", strIdentityNo);
                        intent.putExtra("strMobile", strMobile);
                        intent.putExtra("strSignatureFileName", strSignatureFileName);
                        intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                        intent.putExtra("strIdentityFilePath", strIdentityFilePath);
                        intent.putExtra("feedback_type", strFeedbackType);
                        intent.putExtra("otp_verified", "Y");
                        startActivity(intent);
                        finish();
                    }

                }else
                    Toast.makeText(OtpVerifyActivity.this, "Please enter a valid OTP.", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class ResendOtpServiceClass extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... param) {

            JSONObject json = new JSONObject();
            try {
                json.put("contact_no", param[0]);
            }catch (JSONException e){
                e.printStackTrace();
            }

            WebServicesURLClass urlClass=new WebServicesURLClass();
            JSONObject jsonObject=urlClass.otpVerificationMethod(json);
            return jsonObject.toString();
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            try{
                progressBar.setVisibility(View.GONE);
                Log.e("ResendOtpRes:",jsonString);
                int status;
                JSONObject jsonObject;
                if (jsonString.contains("</div>")) {
                    int index = jsonString.indexOf("{\"status\"");
                    String string = jsonString.substring(index);
                    jsonObject = new JSONObject(string);
                    status=jsonObject.getInt("status");
                }else {
                    jsonObject = new JSONObject(jsonString);
                    status=jsonObject.getInt("status");
                }
                if (status==1){
                    strOtp=jsonObject.getJSONObject("data").getString("otp");
                    Log.e("ResendOtp:",strOtp);
                }else {
                    strOtp="";
                    Toast.makeText(OtpVerifyActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }


}
