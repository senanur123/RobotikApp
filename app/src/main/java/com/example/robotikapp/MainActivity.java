package com.example.robotikapp;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;





public class MainActivity extends AppCompatActivity {

    Button captureBtn, selectBtn, sendBtn;
    String currentImagePath = null;
    Intent selectIntent;
    Bitmap bitmap;
    ImageView imageview;
    File imageFile = null;




    private static final int IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private String serverIpAddress = "";
    private boolean connected = false;
    private Handler handler = new Handler();
    private Socket socket;
    private byte [] imgbyte;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureBtn = findViewById(R.id.btnCapture);
        selectBtn = findViewById(R.id.btnSelect);
        imageview = findViewById(R.id.viewImage);
        sendBtn = findViewById(R.id.btnSend);


        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    captureImage(v);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                }else{
                    selectImage();
                }

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendPhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void sendPhoto() throws IOException {
        // currentImagePath shows the
        System.out.println("current image: " + currentImagePath);

        if(currentImagePath == null){
            Toast.makeText(this, "You havent chosen anything!", Toast.LENGTH_SHORT).show();
        }else{
            File imagefile = imageFile;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }



            Bitmap bm = BitmapFactory.decodeStream(fis);
            if(bm == null){
                System.out.println("What the heck");
            }else {
                imgbyte = getBytesFromBitmap(bm);
            }


            if (!connected) {
                serverIpAddress = "127.0.0.1";
                if (!serverIpAddress.equals("")) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }

        }

    }
    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public class ClientThread implements Runnable {

        @Override
        public void run() {
            try {

                System.out.println("Client is running for a connection!");
                // HATA OLAN KISIM:
                socket = new Socket("127.0.0.1", 5000);
                System.out.println("HEYYYYYYYYYY");
                System.out.println(socket.getInetAddress());

                if(connected){
                    OutputStream output = socket.getOutputStream();
                    System.out.println("Image writing!");
                    output.write(imgbyte);
                    output.flush();
                    System.out.println("Image writing DONE!");
                }

            } catch (UnknownHostException e) {
                System.out.println("Unknown host!");
                e.printStackTrace();
            } catch (IOException e){
                System.out.println("Io Exception!");
                e.printStackTrace();
            }


        }
    }


    /*

    public class ClientThread implements Runnable {

        public void run() {
            try {
                System.out.println("Cthread check");
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                socket = new Socket(serverAddr, 5000);
                connected = true;
                while (connected) {
                    try {




                        OutputStream output = socket.getOutputStream();
                        Log.d("ClientActivity", "C: image writing.");
                        output.write(imgbyte);
                        output.flush();

    protected void onStop() {
        super.onStop();
        try {
            // MAKE SURE YOU CLOSE THE SOCKET UPON EXITING
            if(socket==null){
                System.out.println("Somethings wrong!");
            }else{
                socket.close();
                connected = false;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    */



    public void captureImage(View view) throws FileNotFoundException {
        // opens camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // check if the activity can handle this intent
        if (cameraIntent.resolveActivity(getPackageManager()) !=null)
        {

            try{
                imageFile = getImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }

            if(imageFile!=null){

                Uri imageUri = FileProvider.getUriForFile(this, "com.example.robotikapp.provider", imageFile);
                System.out.println("uri: " + imageUri);
                String pp = imageFile.getPath();
                System.out.println("uri path: " + pp);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, IMAGE_REQUEST);


            }

        }

    }

    private void selectImage() {

        selectIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectIntent.setType("*/*");
        startActivityForResult(selectIntent, REQUEST_CODE_SELECT_IMAGE);

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


    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_" + timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data!=null){
                Uri selectedImageUri = data.getData();

                if(selectedImageUri!=null){
                    try{
                        imageFile = getImageFile();
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        imageview.setImageBitmap(bitmap);

                    }catch(Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if(requestCode == IMAGE_REQUEST ){

            System.out.println("uri in act result: "  + currentImagePath);
            Bitmap bmap = BitmapFactory.decodeFile(currentImagePath);
            imageview.setImageBitmap(bmap);



        }

    }


}

