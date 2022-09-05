package com.example.instagramclone;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity2 extends AppCompatActivity {

    public  static  final String TAG="LoginActivity2" ;
    private EditText etUsername;
    private  EditText etPassword;
    private Button btnLogin;
    private Button btnsignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        if(ParseUser.getCurrentUser()!= null){
            goMainActivity();

        }


        etUsername = findViewById(R.id.etUsername);
        etPassword= findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnsignup = findViewById(R.id.btnsignup);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                loginUser(username,password);
            }
        });
        
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"onClick sign up button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                signupUser(username,password);
                
            }
        });
    }

    private void signupUser(String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e!=null){
                    Log.e(TAG,"Issue with sign up",e);
                    Toast.makeText(LoginActivity2.this, "Issue with sign up", Toast.LENGTH_SHORT).show();
                    return;
                }

                goMainActivity();
                Toast.makeText(LoginActivity2.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loginUser(String username, String password) {
        Log.i(TAG,"Attemting to login user "+ username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e!=null){
                    Log.e(TAG,"Issue with login",e);
                    Toast.makeText(LoginActivity2.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }

                goMainActivity();
                Toast.makeText(LoginActivity2.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}