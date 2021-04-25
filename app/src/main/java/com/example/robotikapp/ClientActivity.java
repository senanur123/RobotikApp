 package com.example.robotikapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;


 public class ClientActivity extends AppCompatActivity {

     private Socket socket;
     Button sendBtn, selectBtn;
     ImageView imageview;
     Intent myIntent;
     Bitmap bitmap;

     private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
     private static final int REQUEST_CODE_SELECT_IMAGE = 2;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_client);

         // new Thread(new ClientThread()).start();

         sendBtn = findViewById(R.id.btnSend);
         selectBtn = findViewById(R.id.btnSelect);

         imageview = findViewById(R.id.selectedImage);

         selectBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)
                 !=PackageManager.PERMISSION_GRANTED){
                     ActivityCompat.requestPermissions(ClientActivity.this,
                             new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                             REQUEST_CODE_STORAGE_PERMISSION);
                 }else{
                     selectImage();
                 }

             }
         });
/*
         sendBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onClickSend();
             }
         });

*/

     }





     private void selectImage() {

         myIntent = new Intent(Intent.ACTION_GET_CONTENT);
         myIntent.setType("*/*");
         startActivityForResult(myIntent, REQUEST_CODE_SELECT_IMAGE);

     }
         @Override
         public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
         @NonNull int[] grantResults){
             super.onRequestPermissionsResult(requestCode, permissions, grantResults);
             if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
                 if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     selectImage();
                 } else {
                     Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                 }

             }
         }


/*

     class ClientThread implements Runnable {

         @Override
         public void run() {

             try {


                 socket = new Socket("192.168.43.196", 7800);

             } catch (UnknownHostException e1) {
                 e1.printStackTrace();
             } catch (IOException e1) {
                 e1.printStackTrace();
             }

         }


     }


     public void onClickSend() {
         try {
             System.out.println("HEYY oncliksend!");

             ByteArrayOutputStream bos = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.PNG, 0 , bos);
             byte[] array = bos.toByteArray();
                if(socket==null){
                    System.out.println("Scoket null!!");
                }else{
                    OutputStream out = socket.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(out);
                    dos.writeInt(array.length);
                    dos.write(array, 0, array.length);
                }


         } catch (UnknownHostException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } catch (Exception e) {
             e.printStackTrace();
         }
     }



     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
             if(data!=null){
                 Uri selectedImageUri = data.getData();
                 if(selectedImageUri!=null){
                     try{
                         InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                         bitmap = BitmapFactory.decodeStream(inputStream);
                         imageview.setImageBitmap(bitmap);

                     }catch(Exception e){
                         Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 }
             }
         }

     }

 */


}
