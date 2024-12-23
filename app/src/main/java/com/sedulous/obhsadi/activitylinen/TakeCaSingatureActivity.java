package com.sedulous.obhsadi.activitylinen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sedulous.obhsadi.ModelLinen.QanswerData;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TakeCaSingatureActivity extends AppCompatActivity {
    public final static String STORING_Image = "http://obhsadi.projectrailway.in/api/atd/upload_userimage";
    public final static String STORING_sig = "http://obhsadi.projectrailway.in/api/atd/upload_signature";
    public static String LINEN_SAVE= "http://obhsadi.projectrailway.in/api/atd/saveLostLinen";
    Button btnsubmit;
    RequestQueue rQueue;
    String message;
    boolean isSubmited=false;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 = 400;
    String train_no, coach,jdate,emp_id, emp_name,requestBody;
    String imageresponse1 = "", imageresponse2="", signatureresponse1="", signatureresponse2="",
            strSignatureFileName = "", strSignatureFilePath = "", strSignatureFileName2 = "",user_id = "", deport_code = "", designation = "",
            strSignatureFilePath2 = "" ,fileUri="";
    LinearLayout signaturelayout;
    ImageView signclick1, signclick2, iv_sign1, iv_sign2;
    public static final int SIGNATURE_ACTIVITY = 1, SIGNATURE_ACTIVITY2 = 2;
    ImageView iv_image1, iv_image2;
    LinearLayout cameralayout;
    String file_path1, file_path2;
    GPSTracker gps;
    Boolean image1 = false, image2 = false;
    ProgressDialog mProgressDialog;
    public HashMap<String, QanswerData> qmaps=new HashMap<>();
    public static Map<String,String> map=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_ca_singature);
        user_id=getIntent().getStringExtra("user_id");
        designation=getIntent().getStringExtra("designation");
        deport_code = getIntent().getStringExtra("deport_code");
        qmaps= (HashMap<String, QanswerData>) getIntent().getSerializableExtra("qdata");


        cameralayout = (LinearLayout) findViewById(R.id.camera_layout);
        iv_image1 = findViewById(R.id.iv_train1);
        iv_image2 = findViewById(R.id.iv_train2);
        signaturelayout = findViewById(R.id.signature_layout);
        signclick1 = findViewById(R.id.click1);
        signclick2 = findViewById(R.id.click2);
        iv_sign1 = findViewById(R.id.img_sign1);
        iv_sign2 = findViewById(R.id.img_sign2);
        btnsubmit = findViewById(R.id.btn_submit);

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
                Intent intent = new Intent(TakeCaSingatureActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        signclick2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TakeCaSingatureActivity.this, CaptureSignatureActivity.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY2);
            }
        });

        Intent intent = getIntent();
        train_no = intent.getStringExtra("train_no");
        coach = intent.getStringExtra("coach");
        emp_id = intent.getStringExtra("emp_id");
        emp_name = intent.getStringExtra("emp_name");
        jdate = intent.getStringExtra("jdate");


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isSubmited) {
                    if (TextUtils.isEmpty(imageresponse1) || TextUtils.isEmpty(imageresponse2) ||
                            TextUtils.isEmpty(strSignatureFilePath)||TextUtils.isEmpty(strSignatureFilePath2)) {
                        Toast.makeText(TakeCaSingatureActivity.this, "Please both take image & signature", Toast.LENGTH_SHORT).show();

                    } else {
                        JSONArray jsonArray=new JSONArray();
                        for (QanswerData qanswerData : qmaps.values()) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("quest_id",qanswerData.quest_id);
                                jsonObject.put("total_given",qanswerData.total_given);
                                jsonObject.put("total_return",qanswerData.total_return);
                                jsonObject.put("amount",qanswerData.rate);
                                jsonObject.put("shortfall",qanswerData.shortfall);
                                jsonObject.put("total_amount",qanswerData.total_penalty_amount);
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            JSONObject obj1 = new JSONObject();
                            obj1.put("depot_code", PreferenceUtil.getDepot(TakeCaSingatureActivity.this));
                            obj1.put("train_no",train_no);
                            obj1.put("coach",coach);
                            obj1.put("journey_date",jdate);
                            obj1.put("supervisor_id",PreferenceUtil.getUserIdSelected(TakeCaSingatureActivity.this));
                            obj1.put("emp_id", emp_id);
                            obj1.put("emp_name",emp_name);
                            obj1.put("image", imageresponse1);
                            obj1.put("image1", imageresponse2);
                            obj1.put("signature", signatureresponse1);
                            obj1.put("signature1", signatureresponse2);

                            obj1.put("linen_Data", jsonArray);

                            requestBody = obj1.toString();
                        }catch (Exception e){

                        }
                        Log.v("requestBody",requestBody);

//
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, LINEN_SAVE,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        hideLoading();

                                        JSONObject jsonObject = null;
                                        try {
                                            jsonObject = new JSONObject(response);
                                            message = jsonObject.getString("message");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        map.clear();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(TakeCaSingatureActivity.this);
                                        //  builder.setTitle("Message")
                                        builder.setMessage(response)
                                                .setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int a) {
                                                        Intent i = new Intent(TakeCaSingatureActivity.this, SupervisorActivity.class);
                                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(i);
                                                    }
                                                });

                                        AlertDialog dialog = builder.create();
                                        dialog.show();

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(TakeCaSingatureActivity.this, "Error" + error, Toast.LENGTH_SHORT).show();

                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return String.format("application/json; charset=utf-8");
                            }

                            @Override

                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    return null;
                                }
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(TakeCaSingatureActivity.this);
                        requestQueue.add(stringRequest);
                    }
                }
            }
        });
    }
    private void checkPermission(final int REQUEST_CODE) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            fileUri = O.cameraProcess(TakeCaSingatureActivity.this, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,}, 100);
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
            gps = new GPSTracker(TakeCaSingatureActivity.this);
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
                        file_path1 = O.savefile(TakeCaSingatureActivity.this, O.FOLDER_CAMIMG, bitmap, 80);
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
                MyLocation.displayPromptForEnablingGPS(TakeCaSingatureActivity.this);
            }

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 && resultCode == RESULT_OK) {
            gps = new GPSTracker(TakeCaSingatureActivity.this);
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
                        file_path2 = O.savefile(TakeCaSingatureActivity.this, O.FOLDER_CAMIMG, bitmap, 80);
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
                MyLocation.displayPromptForEnablingGPS(TakeCaSingatureActivity.this);
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
        rQueue = Volley.newRequestQueue(TakeCaSingatureActivity.this);
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
        rQueue = Volley.newRequestQueue(TakeCaSingatureActivity.this);
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
        mProgressDialog = new ProgressDialog(TakeCaSingatureActivity.this);
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
        final Dialog dialog = new Dialog(TakeCaSingatureActivity.this);
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
                Intent intent = new Intent(TakeCaSingatureActivity.this, SupervisorActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

}
