package com.sedulous.obhsadi.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.adapter.AcFeedbackAdapterClass;
import com.sedulous.obhsadi.service.HttpResponseClass;
import com.sedulous.obhsadi.service.NetworkStatusClass;
import com.sedulous.obhsadi.service.PreferenceUtil;
import com.sedulous.obhsadi.service.Util;
import com.sedulous.obhsadi.service.WebServicesURLClass;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PassengerFeedbackAcActivity extends AppCompatActivity implements AcFeedbackAdapterClass.RatingSelectionInterface{

    public static final int RequestPermissionCode = 1;
    private LinearLayout linearLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private AcFeedbackAdapterClass adapter;
    private Button btnSubmit;
    private JSONArray arrayquestionAC;
    Map<String, String> map=new HashMap<>();
    private JSONArray jsonArray;

    private ImageButton btnRecordAudio;
    private String strSignatureFilePath="",strIdentityFilePath="",AudioSavePathInDevice = "",AudioSaveFileName="",strMobile,strMessage;
    MediaRecorder mediaRecorder ;
    private boolean startEnable=false;
    private ProgressBar progressBarAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_feedback_ac);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String strInspectionType=getIntent().getStringExtra("inspection_type");
        final String strPassengerName=getIntent().getStringExtra("strPassengerName");
        final String strTrainNumber=getIntent().getStringExtra("strTrainNumber");
        final String strCoachNumber=getIntent().getStringExtra("strCoachNumber");
        final String strSeatNumber=getIntent().getStringExtra("strSeatNumber");
        final String strPNRNumber=getIntent().getStringExtra("strPNRNumber");
        final String strIdentityNo=getIntent().getStringExtra("strIdentityNo");
        strMobile=getIntent().getStringExtra("strMobile");
        strSignatureFilePath=getIntent().getStringExtra("strSignatureFilePath");
        strIdentityFilePath=getIntent().getStringExtra("strIdentityFilePath");
        final String strOtpVerified = getIntent().getStringExtra("otp_verified");

        linearLayout=(LinearLayout)findViewById(R.id.topLayout);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        btnRecordAudio = (ImageButton) findViewById(R.id.btn_recode_video);
        progressBarAudio=(ProgressBar)findViewById(R.id.progressBarAudio);
        btnSubmit=(Button)findViewById(R.id.btn_submit);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager manager=new LinearLayoutManager(PassengerFeedbackAcActivity.this);
        recyclerView.setLayoutManager(manager);

        btnRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startEnable) {
                    AudioSaveFileName="OBHS_Aud_Rec_" + Util.createTimeAudioFileName() + ".3gp";
                    AudioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ AudioSaveFileName;
                    MediaRecorderReady();
                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    startEnable = true;
                    btnRecordAudio.setImageResource(R.drawable.icon_pause);
                    progressBarAudio.setIndeterminate(true);
                    Log.e("AudioPath:", AudioSavePathInDevice);
                    Toast.makeText(PassengerFeedbackAcActivity.this, "Recording started", Toast.LENGTH_LONG).show();
                } else {
                    mediaRecorder.stop();
                    startEnable = false;
                    btnRecordAudio.setImageResource(R.drawable.icon_voice_record);
                    progressBarAudio.setIndeterminate(false);
                    Toast.makeText(PassengerFeedbackAcActivity.this, "Recording Completed", Toast.LENGTH_LONG).show();

//                    uploadRecordedAudioFile();
                }

            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSubmit.setBackgroundResource(R.drawable.button_orange_bg);

                jsonArray=new JSONArray();
                for (HashMap.Entry<String , String > mmap:map.entrySet())
                {
                    JSONObject object=new JSONObject();
                    try {
                        object.put("question_id",mmap.getKey());
                        object.put("rating",mmap.getValue());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(object);
                }
                if (arrayquestionAC.length()==jsonArray.length()) {
                    new submitQuestionListService().execute(PreferenceUtil.getUserIdSelected(PassengerFeedbackAcActivity.this), strPassengerName, strTrainNumber, strCoachNumber, strSeatNumber, strPNRNumber, strMobile, strInspectionType,strIdentityNo, strOtpVerified);
                }else
                    Toast.makeText(PassengerFeedbackAcActivity.this, "Are all questions checked?", Toast.LENGTH_LONG).show();
            }
        });

        if (NetworkStatusClass.isNetworkStatusAvialable(PassengerFeedbackAcActivity.this))
        {
            new GetQuestionListService().execute(PreferenceUtil.getUserIdSelected(PassengerFeedbackAcActivity.this));
        }else {
            progressBar.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            Toast.makeText(this, R.string.internet_connection_text, Toast.LENGTH_SHORT).show();
        }

        if(checkPermission()) {

        }else {
            requestPermission();
        }

    }

    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_change_language,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_hindi)
        {
            adapter=new AcFeedbackAdapterClass(PassengerFeedbackAcActivity.this,arrayquestionAC,PassengerFeedbackAcActivity.this,false);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else if (item.getItemId()==R.id.action_english){
            adapter=new AcFeedbackAdapterClass(PassengerFeedbackAcActivity.this,arrayquestionAC,PassengerFeedbackAcActivity.this,true);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(PassengerFeedbackAcActivity.this, new
                String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(PassengerFeedbackAcActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PassengerFeedbackAcActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void ratingSelection(String id, float rating) {
        map.put(id,rating+"");
    }

    public class GetQuestionListService extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jObject=new JSONObject();
            try {
                jObject.put("user_id",strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            WebServicesURLClass webServicesURLClass=new WebServicesURLClass();
            JSONObject jsonObject=webServicesURLClass.getPassengerQuestionMethod(jObject);
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                linearLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Log.e("QuestionsListRes:", jsonObject.toString()+"");

                arrayquestionAC = jsonObject.getJSONObject("data").getJSONArray("ac");
                adapter=new AcFeedbackAdapterClass(PassengerFeedbackAcActivity.this,arrayquestionAC,PassengerFeedbackAcActivity.this,true);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class submitQuestionListService extends AsyncTask<String,String,JSONObject>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject jObject=new JSONObject();
            try {
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                entity.addPart("user_id", new StringBody(strings[0]));
                entity.addPart("passenger_name", new StringBody(strings[1]));
                entity.addPart("train_no", new StringBody(strings[2]));
                entity.addPart("coach_no", new StringBody(strings[3]));
                entity.addPart("seat_no", new StringBody(strings[4]));
                entity.addPart("pnr_no", new StringBody(strings[5]));
                entity.addPart("contact_no", new StringBody(strings[6]));
                entity.addPart("coach_type", new StringBody(strings[7]));
                entity.addPart("identity_id", new StringBody(strings[8]));
                entity.addPart("verify_pass", new StringBody(strings[9]));
                entity.addPart("depot_code", new StringBody(PreferenceUtil.getDepot(PassengerFeedbackAcActivity.this)));
                entity.addPart("question_rating", new StringBody(jsonArray.toString()));

                if (!strSignatureFilePath.isEmpty()) {
                    File image = new File(strSignatureFilePath);
                    entity.addPart("signature", new FileBody(image));
                }
                if (!strIdentityFilePath.isEmpty()) {
                    File image = new File(strIdentityFilePath);
                    entity.addPart("identity_image", new FileBody(image));
                }
                if (!AudioSavePathInDevice.isEmpty()) {
                    File image = new File(AudioSavePathInDevice);
                    entity.addPart("passenger_voice", new FileBody(image));
                }

                HttpResponseClass httpResponseClass=new HttpResponseClass();
                jObject=httpResponseClass.uploadImagesTextsMultipartPost(WebServicesURLClass.PASSENGER_FEEDBACK_URL,entity);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jObject;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            try {
                progressBar.setVisibility(View.GONE);
                if (response.getInt("status")==1) {

                    //Log.d("FeedbackMessage",response.getString("message"));
                    showConfirmationDialog(response.getString("message"));


                }else {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(PassengerFeedbackAcActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                btnSubmit.setEnabled(true);
                e.printStackTrace();
            }
        }
    }

    public void showConfirmationDialog(String strMessage)
    {
        final Dialog dialog=new Dialog(PassengerFeedbackAcActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.show();
        TextView tvMessage=dialog.findViewById(R.id.tv_message);
        tvMessage.setText(strMessage);
        TextView tvOk=dialog.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(PassengerFeedbackAcActivity.this, LoginUserSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


    //upload signature file
    public void uploadRecordedAudioFile()
    {
        if (NetworkStatusClass.isNetworkStatusAvialable(PassengerFeedbackAcActivity.this))
            new UploadAudioFileClass().execute();
        else
            Toast.makeText(this, R.string.internet_connection_text, Toast.LENGTH_SHORT).show();
    }


    public class UploadAudioFileClass extends AsyncTask<String,String,Integer>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... param) {

            HttpResponseClass httpResponseClass=new HttpResponseClass();
            int resonseCode= httpResponseClass.uploadImageFile("audio",AudioSavePathInDevice,AudioSaveFileName);
            return resonseCode;
        }

        @Override
        protected void onPostExecute(Integer response) {
            super.onPostExecute(response);
            try{
                Log.e("ResCode",response+"");
                progressBar.setVisibility(View.GONE);
                if (response==200){
//                    Toast.makeText(PassengerFeedbackNonAcActivity.this, "Audio is Uploaded", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(PassengerFeedbackAcActivity.this, "Audio File Uploading Error..", Toast.LENGTH_LONG).show();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
