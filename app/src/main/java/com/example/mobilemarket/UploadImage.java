package com.example.mobilemarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class UploadImage extends AppCompatActivity {

    Bitmap bitmap;

    boolean check = true;

    Button SelectImageGallery, UploadImageServer;

    ImageView ImageView;

    EditText item_name, description, price, date, username;

    ProgressDialog progressDialog;

    String USERNAME, ITEMNAME, PRICE, DESCRI,DATE;
    JSONArray arr;

    String ServerUploadPath = "https://lamp.ms.wits.ac.za/~s2347332/insertImage.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        ImageView = (ImageView) findViewById(R.id.imageView);

        item_name = findViewById(R.id.ItemName);
        description = findViewById(R.id.descri);
        price = findViewById(R.id.Price);
        date = findViewById(R.id.Date);
        username = findViewById(R.id.username);
        HTTPRequest req = new HTTPRequest();

        SelectImageGallery = (Button) findViewById(R.id.buttonSelect);

        UploadImageServer = (Button) findViewById(R.id.buttonUpload);

        SelectImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();

                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);

            }
        });

        item_name.addTextChangedListener(loginTextWatcher2);
        price.addTextChangedListener(loginTextWatcher2);
        date.addTextChangedListener(loginTextWatcher2);

        req.doRequest(UploadImage.this, "https://lamp.ms.wits.ac.za/~s2347332/cars.php", new RequestHandler() {
            @Override
            public void processResponse(String response) throws JSONException {
                arr = getJSON(response);
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String user = username.getText().toString().trim();
                if(!user.equals("")){
                    for(int j=0; j<arr.length(); j++){
                        JSONObject jo = null;
                        try {
                            jo = arr.getJSONObject(j);
                            String usernm = jo.getString("username");
                            if(user.equals(usernm)) {
                                UploadImageServer.setEnabled(true);
                                username.setError(null);
                                break;
                            }
                            else{
                                username.setError("Username does not exist");
                                UploadImageServer.setEnabled(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        UploadImageServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ITEMNAME = item_name.getText().toString().trim();
                DESCRI = description.getText().toString();
                PRICE =price.getText().toString().trim();
                DATE = date.getText().toString().trim();
                USERNAME = username.getText().toString().trim();

                if(!hasImage(ImageView)){
                    Toast.makeText(UploadImage.this, "Add picture", Toast.LENGTH_SHORT).show();
                    System.out.println("Am here");
                }

                if(ITEMNAME.equals("") || USERNAME.equals("") || PRICE.equals("") || DATE.equals("") || !hasImage(ImageView)){
                    //Toast.makeText(UploadImage.this, "MISSING INFORMATION", Toast.LENGTH_SHORT).show();
                    if(ITEMNAME.equals("")){
                        item_name.setError("Required*");
                    }
                    if(PRICE.equals("")){
                        price.setError("Required*");
                    }
                    if(DATE.equals("")){
                        date.setError("Required*");
                    }
                    if(USERNAME.equals("")){
                        username.setError("Required*");
                    }
                }
                else{
                    ImageUploadToServerFunction();
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int RC, int RQC, Intent I) {

        super.onActivityResult(RC, RQC, I);

        if (RC == 1 && RQC == RESULT_OK && I != null && I.getData() != null) {

            Uri uri = I.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView.setImageBitmap(bitmap);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    public void ImageUploadToServerFunction() {

        ByteArrayOutputStream byteArrayOutputStreamObject;

        byteArrayOutputStreamObject = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);

        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();

        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                progressDialog = ProgressDialog.show(UploadImage.this, "Image is Uploading", "Please Wait", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(UploadImage.this, string1, Toast.LENGTH_LONG).show();

                // Setting image as transparent after done uploading.
                ImageView.setImageResource(android.R.color.transparent);


            }

            @Override
            protected String doInBackground(Void... params) {
                String ITEMNAME1 = item_name.getText().toString().trim();
                String DESCRI1 = description.getText().toString();
                String PRICE1 =price.getText().toString().trim();
                String DATE1 = date.getText().toString().trim();
                String USERNAME1 = username.getText().toString().trim();

                UploadImage.ImageProcessClass imageProcessClass = new UploadImage.ImageProcessClass();

                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put("item_name", ITEMNAME);

                HashMapParams.put("description", DESCRI);

                HashMapParams.put("item_price", PRICE);

                HashMapParams.put("username", USERNAME);

                HashMapParams.put("date_posted", DATE);

                HashMapParams.put("image_path", ConvertImage);

                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                httpURLConnectionObject.setReadTimeout(19000);

                httpURLConnectionObject.setConnectTimeout(19000);

                httpURLConnectionObject.setRequestMethod("POST");

                httpURLConnectionObject.setDoInput(true);

                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(

                        new OutputStreamWriter(OutPutStream, "UTF-8"));

                bufferedWriterObject.write(bufferedWriterDataFN(PData));

                bufferedWriterObject.flush();

                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null) {

                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;

            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)

                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilderObject.append("=");

                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }
    }
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    private final TextWatcher loginTextWatcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String itm_name = item_name.getText().toString().trim();
            String p = price.getText().toString().trim();
            String dt = date.toString().trim();
            String usnm = username.getText().toString().trim();

            if(!itm_name.equals("")){
                item_name.setError(null);
            }
            if(!p.equals("")){
                price.setError(null);
            }
            if(!dt.equals("")){
                date.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    public JSONArray getJSON(String json) throws JSONException {
        JSONArray ja = new JSONArray(json);
        return ja;
    }
}