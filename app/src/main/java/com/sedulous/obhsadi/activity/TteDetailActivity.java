package com.sedulous.obhsadi.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.sedulous.obhsadi.R;
import com.sedulous.obhsadi.service.NetworkStatusClass;
import com.sedulous.obhsadi.service.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class TteDetailActivity extends AppCompatActivity {

    public static final int SIGNATURE_ACTIVITY = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private EditText etTTEName,etTrainNumber,etTTEIdNo,etMobile,etTteHeadOfficeName;
    private String strTTEName,strTrainNumber,strTTEIdNumber,strMobile,strTteHeadOfficeName,strSignature;
    private String strSignatureFileName="",strSignatureFilePath="";

    private boolean doubleBackToExitPressedOnce=false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tte_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        etTrainNumber=(EditText)findViewById(R.id.et_train_number);
        etTTEName=(EditText)findViewById(R.id.et_tte_name);
        etTTEIdNo=(EditText)findViewById(R.id.et_tte_id_no);
        etMobile=(EditText)findViewById(R.id.et_mobile_number);
        etTteHeadOfficeName=(EditText)findViewById(R.id.et_tte_head_office_name);
        btnSubmit=(Button)findViewById(R.id.btn_submit);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        if (checkPermissionREAD_WRITE_EXTERNAL_STORAGE(this)) {
            // do your stuff..
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strTTEName=etTTEName.getText().toString();
                strTrainNumber=etTrainNumber.getText().toString();
                strTTEIdNumber=etTTEIdNo.getText().toString();
                strMobile=etMobile.getText().toString();
                strTteHeadOfficeName=etTteHeadOfficeName.getText().toString();

                if (!strTTEName.isEmpty()&&!strTrainNumber.isEmpty()&&!strTTEIdNumber.isEmpty()&&!strMobile.isEmpty()&&!strTteHeadOfficeName.isEmpty()) {
                    if (strMobile.length()==10) {
                        if (NetworkStatusClass.isNetworkStatusAvialable(TteDetailActivity.this)) {
                            Intent intent = new Intent(TteDetailActivity.this, TteFeedbackActivity.class);
                            intent.putExtra("strTTEName", strTTEName);
                            intent.putExtra("strTrainNumber", strTrainNumber);
                            intent.putExtra("strTTEIdNumber", strTTEIdNumber);
                            intent.putExtra("strDOJ", "");
                            intent.putExtra("strMobile", strMobile);
                            intent.putExtra("strTteHeadOfficeName", strTteHeadOfficeName);
                            intent.putExtra("strSignatureFileName", strSignatureFileName);
                            intent.putExtra("strSignatureFilePath", strSignatureFilePath);
                            intent.putExtra("feedback_type", "tte");
                            startActivity(intent);
                            finish();

                        }else
                            Toast.makeText(TteDetailActivity.this, R.string.internet_connection_text, Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(TteDetailActivity.this, "Mobile number should be 10 digits long", Toast.LENGTH_LONG).show();
                    }

                }else if (strTrainNumber.isEmpty()) {
                    Toast.makeText(TteDetailActivity.this, "Enter train number", Toast.LENGTH_LONG).show();
                } else if (strTTEName.isEmpty()) {
                    Toast.makeText(TteDetailActivity.this, "Enter TTE name", Toast.LENGTH_LONG).show();
                } else if (strTTEIdNumber.isEmpty()) {
                    Toast.makeText(TteDetailActivity.this, "Enter TTE Id number", Toast.LENGTH_LONG).show();
                }else if (strTteHeadOfficeName.isEmpty()) {
                    Toast.makeText(TteDetailActivity.this, "Enter TTE head office Name", Toast.LENGTH_LONG).show();
                }

            }
        });
        
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
            Intent intent=new Intent(TteDetailActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void takeSignature(View view) {
        Intent intent = new Intent(TteDetailActivity.this, CaptureSignatureActivity.class);
        startActivityForResult(intent,SIGNATURE_ACTIVITY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case SIGNATURE_ACTIVITY:
                if (resultCode == RESULT_OK) {

                    Bundle bundle = data.getExtras();
                    String status  = bundle.getString("status");
                    if(status.equalsIgnoreCase("done")){
                        strSignatureFilePath=bundle.getString("signature_image_url");
                        String filePath=bundle.getString("FilePath");

                        ImageView imageView = (ImageView)findViewById(R.id.imageView);
                        imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                        //Picasso.get().load(image_url).into(imageView);
                        strSignatureFileName = "sign_"+ Util.createTimeAudioFileName()+".png";

                    }else if (status.equalsIgnoreCase("bill")) {
                        String myStringImage=bundle.getString("pdf_file_url");
//                        tv_attach_bill_pdf_file_name.setText(myStringImage);
                    }
                }
                break;
        }
    }

    private boolean saveFile(Uri sourceUri, File destination) {

        final int chunkSize = 1024;  // We'll read in one kB at a time
        byte[] imageData = new byte[chunkSize];

        try {
            InputStream in = getContentResolver().openInputStream(sourceUri);
            OutputStream out = new FileOutputStream(destination);  // I'm assuming you already have the File object for where you're writing to

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


    public boolean checkPermissionREAD_WRITE_EXTERNAL_STORAGE( final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context, Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
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


    //upload signature file
    public void uploadSignatureImageFile()
    {
        if (NetworkStatusClass.isNetworkStatusAvialable(TteDetailActivity.this)) {
//            new TteDetailActivity.UploadImageFileClass().execute();
        }else
            Toast.makeText(this, R.string.internet_connection_text, Toast.LENGTH_SHORT).show();
    }


}
