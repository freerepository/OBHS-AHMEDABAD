package com.sedulous.obhsadi.activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.activitylinen.SupervisorActivity;
import com.sedulous.obhsadi.service.O;
import com.sedulous.obhsadi.service.PreferenceUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        try{
            O.deleteRecursive(new File(getExternalFilesDir(null).getAbsolutePath(), O.FOLDER_TEMP));
        }catch (Exception e){
            e.printStackTrace();
        }try{
            O.deleteRecursive(new File(getExternalFilesDir(null).getAbsolutePath(), O.FOLDER_SIGN));
        }catch (Exception e){
            e.printStackTrace();
        }try{
            O.deleteRecursive(new File(getExternalFilesDir(null).getAbsolutePath(), O.FOLDER_CAMIMG));
        }catch (Exception e){
            e.printStackTrace();
        }
        final Thread thread=new Thread(){
            public void run(){
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    if(TextUtils.isEmpty(PreferenceUtil.getUserMobile(MainActivity.this))) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (PreferenceUtil.getUserType(MainActivity.this).equalsIgnoreCase("2"))
                        {
                            Intent intent=new Intent(MainActivity.this,InspectorReviewActivity.class);
                            intent.putExtra("user_type","Inspector");
                            startActivity(intent);
                        }else if (PreferenceUtil.getUserType(MainActivity.this).equalsIgnoreCase("3")){
                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            intent.putExtra("user_type","Passenger");
                            startActivity(intent);
                        }else if (PreferenceUtil.getUserType(MainActivity.this).equalsIgnoreCase("4")){
                            Intent intent=new Intent(MainActivity.this, SupervisorActivity.class);
                            intent.putExtra("user_type","Attendance");
                            startActivity(intent);
                        }
                        finish();
                    }
                }
            }
        };
        thread.start();
    }
}
