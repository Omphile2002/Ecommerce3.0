package com.example.mobilemarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilemarket.ui.home.HomeFragment;
import com.example.mobilemarket.ui.home.HomePage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogIn extends AppCompatActivity {
    private String JSON;
    Button login;
    private String Username, Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        TextView username = findViewById(R.id.editTextUsername);
        TextView password = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.buttonLogin);
        Button signup = findViewById(R.id.buttonSignup);
        HTTPRequest req = new HTTPRequest();



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Username = username.getText().toString().trim();
                Password = password.getText().toString().trim();
                if (Username.equals("") || Password.equals("")) {
                    Toast.makeText(LogIn.this, "Please enter both the username and password", Toast.LENGTH_SHORT).show();
                }
                else{
                    req.doRequest(LogIn.this, "https://lamp.ms.wits.ac.za/~s2347332/cars.php", new RequestHandler() {
                        @Override
                        public void processResponse(String response) throws JSONException {
                            Compare(response);
                        }
                    });
                }

            };

        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LogIn.this, SignUp.class);
                startActivity(i);
            }
        });
    }
    public void Compare(String json) throws JSONException {
        JSONArray ja = new JSONArray(json);
        for(int i=0; i<ja.length(); i++){
            JSONObject jo = ja.getJSONObject(i);
            String user = jo.getString("username");
            String pass = jo.getString("password");
            if(user.equals(Username) && pass.equals(Password)){
                Intent j = new Intent(LogIn.this, HomePage.class);
                startActivity(j);
                Toast.makeText(LogIn.this, "Log in successful", Toast.LENGTH_SHORT).show();
                break;
            }
            else{
                Toast.makeText(LogIn.this, "Invalid Login.Please try again", Toast.LENGTH_SHORT).show();
            }
        }

    }

}