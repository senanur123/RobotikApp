package com.example.robotikapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderClient;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    TextView toSignUp, loginUsername, loginPassword;
    Button loginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toSignUp = findViewById(R.id.signUp);
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.btnLogin);

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });


    }

    public void loginUser(View view){
        if(!validateUsername() | !validatePassword())
            return;
        else{
            isUser();
        }
    }

    private void isUser() {
        String userEnteredUsername = loginUsername.getEditableText().toString().trim();
        String userEnteredPassword = loginPassword.getEditableText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    loginUsername.setError(null);

                    String passwordFromDb = snapshot.child(userEnteredUsername).child("password").getValue(String.class);

                    if(passwordFromDb.equals(userEnteredPassword)){
                        loginPassword.setError(null);
                        String usernameFromDb = snapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String emailFromDb = snapshot.child(userEnteredUsername).child("email").getValue(String.class);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        startActivity(intent);
                    }else{
                        loginPassword.setError("Wrong Password!");
                        loginPassword.requestFocus();
                    }
                }else{
                    loginUsername.setError("No such user!");
                    loginUsername.requestFocus();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Boolean validateUsername(){
        String val = loginUsername.getEditableText().toString();
        if(val.isEmpty()){
            loginUsername.setError("Field can't be empty!");
            return false;
        }else {
            loginUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = loginPassword.getEditableText().toString();
        if(val.isEmpty()){
            loginPassword.setError("Field can't be empty");
            return false;
        }else{
            loginPassword.setError(null);
            return true;
        }
    }


}