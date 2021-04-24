package com.example.robotikapp;

import android.icu.lang.UCharacter;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String, Void, Void> {

    Socket s;
    DataOutputStream dos;
    PrintWriter pw;
    @Override
    protected Void doInBackground(String... voids) {
        String message = voids[0];
        try{
            s = new Socket("192.168.43.196", 7800);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();
            s.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
