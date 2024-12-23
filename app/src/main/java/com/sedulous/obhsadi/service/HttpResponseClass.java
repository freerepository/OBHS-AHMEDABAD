package com.sedulous.obhsadi.service;

import android.app.ProgressDialog;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HttpResponseClass {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    private JSONObject jsonObject2;

    public JSONObject getResponseByPost(String url, List<NameValuePair> params) {
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.e("JSON", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return jObj;
    }

    public JSONObject getResponseByPut(String url, JSONObject params) {
        JSONObject jsonObject=null;
        try {
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPut putConnection = new HttpPut(url);
            putConnection.setHeader("json", params.toString());
            StringEntity se = new StringEntity(params.toString(), "UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            putConnection.setEntity(se);
            try {
                response = httpClient.execute(putConnection);
                String JSONString = EntityUtils.toString(response.getEntity(), "UTF-8");
                jsonObject=new JSONObject(JSONString);
                Log.e("JSONResponse:",JSONString);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return JSON String
        return jsonObject;
    }

    public JSONObject uploadDataWithFileByPut(String url, JSONObject params) {
        JSONObject jsonObject=null;
        try {
            HttpResponse response;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
            HttpConnectionParams.setSoTimeout(httpParameters, 5000);
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpPut putConnection = new HttpPut(url);
            putConnection.setHeader("json", params.toString());
            StringEntity se = new StringEntity(params.toString(), "UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            putConnection.setEntity(se);
            try {
                response = httpClient.execute(putConnection);
                String JSONString = EntityUtils.toString(response.getEntity(), "UTF-8");
                jsonObject=new JSONObject(JSONString);
                Log.e("JSONResponse:",JSONString);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return JSON String
        return jsonObject;
    }



    public JSONObject uploadImagesTextsMultipartPost(String url,MultipartEntity entity){

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost poster = new HttpPost(url);
//            poster.addHeader( "apiKey" , KeyGenerationClass.getEncryptedKey());
            poster.setEntity(entity );
            try {

                Log.e("akm multipart entity", "" + entity.toString());
            }catch (Exception e){
                e.printStackTrace();
            }
            client.execute(poster, new ResponseHandler<Object>() {
                public Object handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    HttpEntity respEntity = response.getEntity();
                    String responseString = EntityUtils.toString(respEntity);


                    Log.e("ResString:", responseString);
                    // try parse the string to a JSON object
                    try {
                        jsonObject2 = new JSONObject(responseString);
                    } catch (JSONException e) {
                        Log.e("JSON Parser", "Error parsing data " + e.toString());
                    }
                    return jsonObject2;
                }
            });
        } catch (Exception e){
            //do something with the error
            e.printStackTrace();
            Log.e("ResString:", e.getMessage());
        }

        return jsonObject2;
    }


    public int uploadImageFile(String fileType, String sourceFileUri, String fileName) {

        int serverResponseCode = 0;
        try {
            ProgressDialog dialog = null;
            String upLoadServerUri = WebServicesURLClass.FILE_UPLOAD_URL;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            // this will contain "Fruit"
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);
            if (!sourceFile.isFile()) {
                Log.e("uploadFile", "Source File Does not exist");

            }
            try { // open a URL connection to the Servlet

                URL url = new URL(upLoadServerUri);
                conn = (HttpURLConnection) url.openConnection(); // Open a HTTP
                // connection to
                // the URL
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);

                if (fileType.equalsIgnoreCase("signature")) {
                    dos.writeBytes("Content-Disposition: form-data; name=\"signature\";filename=\"" + fileName + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: " + "image/PNG" + lineEnd);
                }else {
                    dos.writeBytes("Content-Disposition: form-data; name=\"passenger_voice\";filename=\"" + fileName + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: " + "audio/3gp" + lineEnd);
                }

                dos.writeBytes(lineEnd);

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                bytesAvailable = fileInputStream.available();
                // create a buffer of maximum size

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.e("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
                if (serverResponseCode == 200) {

                }

                // close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

                } catch (MalformedURLException ex) {
                    dialog.dismiss();
                    ex.printStackTrace();
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Upload file to server-", "Exception : " + e.getMessage(), e);
                }

            // End else block
        }catch (Exception ee) {
            ee.printStackTrace();
        }

        return serverResponseCode;

    }

    public JSONObject getDetailByPnr(String url1) {

        JSONObject jsonObject=null;
        try {
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();
            InputStream it = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader read = new InputStreamReader(it);
            BufferedReader buff = new BufferedReader(read);
            StringBuilder dta = new StringBuilder();
            String chunks ;
            while((chunks = buff.readLine()) != null)
            {
                dta.append(chunks);
            }
            Log.e("PNR-Res:",dta.toString());
            jsonObject=new JSONObject(dta.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // return JSON String
        return jsonObject;
    }

}
