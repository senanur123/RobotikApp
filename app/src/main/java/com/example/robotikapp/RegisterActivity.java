package com.example.robotikapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    TextView toLogin;
    EditText regUsername, regEmail, regPassword;
    Button btnRegister;

    FirebaseDatabase rootNode;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        toLogin = findViewById(R.id.alreadyHaveAccount);
        regUsername = findViewById(R.id.regUsername);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);

        btnRegister = findViewById(R.id.btnRegister);

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("users");

                String username = regUsername.getEditableText().toString();
                String email = regEmail.getEditableText().toString();
                String password = regPassword.getEditableText().toString();

                User user = new User(username, email, password);
                if(!validateUsername() | !validatePassword() | !validateEmail()){
                    return;
                }else{
                    reference.child(username).setValue(user);
                }


            }
        });
    }


    private Boolean validateUsername(){
        String val = regUsername.getEditableText().toString();
        String usernameVal = "^" +
                "(?=.*[a-zA-Z])" +  // any letter
                "(?=\\S+$)"+        // no white spaces
                ".{4,}"+            // at least 4 chars
                "$";
        if(val.isEmpty()){
            regUsername.setError("Field can't be empty!");
            return false;
        }else if(val.length()>=10){
            regUsername.setError("Username too long!");
            return false;
        }else if(!val.matches(usernameVal)){
            regUsername.setError("Either username is too short or it contains white space!");
            return false;
        }else{
            regUsername.setError(null);
            return true;
        }

    }

    private Boolean validatePassword(){
        String val = regPassword.getEditableText().toString();
        String passwordVal = "^" +
                "(?=.*[a-zA-Z])"+   // any letter
                "(?=.*[@#$%^&+=])"+ // special char
                "(?=\\S+$)"+        // no white spaces
                ".{4,}"+            // at least 4 chars
                "$";

        if(val.isEmpty()){
            regPassword.setError("Field can't be empty");
            return false;
        }else if(val.length()>=15){
            regPassword.setError("Password too long!");
            return false;
        }else if(!val.matches(passwordVal)){
            regPassword.setError("Password too weak!");
            return false;
        }else{
            regPassword.setError(null);
            return true;
        }
    }

    private Boolean validateEmail(){
        String val = regEmail.getEditableText().toString();

        if(val.isEmpty()){
            regEmail.setError("Field can't be empty!");
            return false;
        }else{
            regEmail.setError(null);
            return true;
        }

        // confirm the email afterwards:


    }



}