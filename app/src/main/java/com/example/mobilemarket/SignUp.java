package com.example.mobilemarket;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilemarket.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    RequestQueue requestQueue;
    Button create;
    private String insertUrl= "https://lamp.ms.wits.ac.za/~s2347332/insert.php";
    EditText name1,phone_number,password,confirm_password,username;
    JSONArray arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        create = findViewById(R.id.create);
        name1 =findViewById(R.id.editTextName);
        phone_number =findViewById(R.id.editTextPhone);
        password =findViewById(R.id.editTextPassword2);
        confirm_password =findViewById(R.id.editTextPassword3);
        username =findViewById(R.id.editTextUsername2);
        requestQueue= Volley.newRequestQueue(getApplicationContext());

        HTTPRequest req = new HTTPRequest();

        phone_number.addTextChangedListener(loginTextWatcher);
        name1.addTextChangedListener(loginTextWatcher);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pass = password.getText().toString().trim();
                if(!pass.equals("")){
                    if(pass.length() < 4){
                        password.setError("Short password*");
                    }
                    else{
                        password.setError(null);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String user = username.getText().toString().trim();
                if(username.getText().toString().equals("")){
                    create.setEnabled(false);
                    username.setError("Required...Enter username");
                }
                else{
                    for(int j=0; j<arr.length(); j++){
                        JSONObject jo = null;
                        try {
                            jo = arr.getJSONObject(j);
                            String usernm = jo.getString("username");
                            if(user.equals(usernm)) {
                                username.setError("Username already exists");
                                create.setEnabled(false);
                                break;
                            }
                            else{
                                create.setEnabled(true);
                                username.setError(null);
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
        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(confirm_password.getText().toString().equals("") || password.getText().toString().equals("")){
                    create.setEnabled(false);
                }
                else{
                    create.setEnabled(true);
                }
            if(!confirm_password.getText().toString().equals(password.getText().toString())){
                create.setEnabled(false);
                confirm_password.setError("password does not match");
            }else{
                confirm_password.setError(null);
                create.setEnabled(true);
            }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        req.doRequest(SignUp.this, "https://lamp.ms.wits.ac.za/~s2347332/cars.php", new RequestHandler() {
            @Override
            public void processResponse(String response) throws JSONException {
                arr = getJSON(response);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= name1.getText().toString().trim();
                String phone_num= phone_number.getText().toString().trim();
                String passwrd= password.getText().toString().trim();
                String confrim_pass= confirm_password.getText().toString().trim();
                String user= username.getText().toString().trim();
                if(name.equals("") || phone_num.equals("") || passwrd.equals("") || user.equals("") || confrim_pass.equals("")){
                    Toast.makeText(SignUp.this, "FILL ALL THE INFORMATION CORRECTLY", Toast.LENGTH_SHORT).show();
                }
                else{
                    StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
                    ){
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError{
                            Map<String, String>params = new HashMap<>();
                            params.put("name",name);
                            params.put("username",user);
                            params.put("password",passwrd);
                            params.put("contact_num",phone_num);
                            return params;
                        }
                    };
                    requestQueue.add(request);
                    Intent k = new Intent(SignUp.this, HomeFragment.class );
                    startActivity(k);
                }

            }
        });
    }
    public JSONArray getJSON(String json) throws JSONException {
        JSONArray ja = new JSONArray(json);
        return ja;
    }
    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String phone_num = phone_number.getText().toString().trim();
            String name = name1.getText().toString().trim();
            if(phone_num.equals("") || name.equals("")){
                create.setEnabled(false);
            }
            else{
                create.setEnabled(true);
            }
            if(phone_num.length() != 10 || phone_num.charAt(0) != '0'){
                phone_number.setError("Incorrect phone numbers ");
            }
            else{
                phone_number.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}