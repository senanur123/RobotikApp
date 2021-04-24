 package com.example.robotikapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ClientActivity extends AppCompatActivity {


    EditText message;
    Button sendBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        message = (EditText) findViewById(R.id.editMessage);
        sendBtn = findViewById(R.id.btnSend);

    }


        public void send(View v){
            MessageSender messageSender = new MessageSender();
            messageSender.execute(message.getText().toString());
        }


}