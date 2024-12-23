package com.sedulous.obhsadi.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.activitylinen.SupervisorActivity;
import com.sedulous.obhsadi.service.NetworkStatusClass;
import com.sedulous.obhsadi.service.PreferenceUtil;
import com.sedulous.obhsadi.service.WebServicesURLClass;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText etMobileNo,etPassword;
    private Button btnLogin;
    private String userType, strMobile,strPassword;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        userType=getIntent().getStringExtra("user_type");

        etMobileNo=(EditText)findViewById(R.id.et_phone_number);
        etPassword=(EditText)findViewById(R.id.et_password);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        btnLogin=(Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setBackgroundResource(R.drawable.button_orange_bg);
                strMobile=etMobileNo.getText().toString();
                strPassword=etPassword.getText().toString();
                if (strMobile.length()==10 && !strPassword.isEmpty())
                {
                    if (NetworkStatusClass.isNetworkStatusAvialable(LoginActivity.this)) {
                        progressBar.setVisibility(View.VISIBLE);
                        new LoginServiceClass().execute(strMobile, strPassword);
                    }else
                        Toast.makeText(LoginActivity.this, R.string.internet_connection_text, Toast.LENGTH_LONG).show();
                }else if (strMobile.length()!=10){
                    Toast.makeText(LoginActivity.this, R.string.mobile_number_length_text, Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(LoginActivity.this, R.string.password_length_text, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class LoginServiceClass extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected JSONObject doInBackground(String... param) {
            List<NameValuePair> userdata=new ArrayList<>();
            userdata.add(new BasicNameValuePair("phone_no",param[0]));
            userdata.add(new BasicNameValuePair("password",param[1]));

            JSONObject json = new JSONObject();
            try {
                json.put("phone_no", param[0]);
                json.put("password", param[1]);
            }catch (JSONException e){
                e.printStackTrace();
            }

            WebServicesURLClass urlClass=new WebServicesURLClass();
            JSONObject jsonObject=urlClass.userLoginMethod(json);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                progressBar.setVisibility(View.GONE);
                Log.e("LoginResponse:",jsonObject.toString());
                int status=jsonObject.getInt("status");
                if (status==1){
                    PreferenceUtil.setUserMobile(LoginActivity.this,strMobile);
                    PreferenceUtil.setUserPassword(LoginActivity.this,strPassword);
                    PreferenceUtil.setUserIdSelected(LoginActivity.this,jsonObject.getJSONObject("data").getString("user_id"));
                    PreferenceUtil.setUserEmail(LoginActivity.this,jsonObject.getJSONObject("data").getString("user_email"));
                    PreferenceUtil.setUserType(LoginActivity.this, jsonObject.getJSONObject("data").getString("user_type"));
                    PreferenceUtil.setDepot(LoginActivity.this, jsonObject.getJSONObject("data").getString("depot_code"));
                    PreferenceUtil.setDesignation(LoginActivity.this, jsonObject.getJSONObject("data").getString("designation"));
                    if (jsonObject.getJSONObject("data").getString("user_type").equalsIgnoreCase("2"))
                    {
                        Intent intent=new Intent(LoginActivity.this,InspectorReviewActivity.class);
                        intent.putExtra("user_type","Inspector");
                        startActivity(intent);
                    }else if (jsonObject.getJSONObject("data").getString("user_type").equalsIgnoreCase("3")){
                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                        intent.putExtra("user_type","Passenger");
                        startActivity(intent);
                    }else if (jsonObject.getJSONObject("data").getString("user_type").equalsIgnoreCase("4")){
                        Intent intent=new Intent(LoginActivity.this, SupervisorActivity.class);
                        intent.putExtra("user_type","Linen");
                        startActivity(intent);
                    }
                    finishAffinity();
                }else {
                    Toast.makeText(LoginActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
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

}
