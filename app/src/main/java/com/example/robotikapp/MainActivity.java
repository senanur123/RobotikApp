package com.example.robotikapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private static final int IMAGE_REQUEST = 1;
    Button captureBtn;
    Button displayBtn;
    String currentImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureBtn = findViewById(R.id.btnCapture);
        displayBtn = findViewById(R.id.btnDisplay);

    captureBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            captureImage(v);
        }
    });

    displayBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayImage(v);
        }
    });


    }

    public void captureImage(View view)

    {
        // opens camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // check if the activity can handle this intent
        if (cameraIntent.resolveActivity(getPackageManager()) !=null)
        {
            File imageFile = null;

            try{
                imageFile = getImageFile();
            }catch (IOException e){
                e.printStackTrace();
            }

            if(imageFile!=null){
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.robotikapp.provider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent,IMAGE_REQUEST);
            }

        }

    }
    public void displayImage(View view){
        Intent intent = new Intent(this, DisplayImageActivity.class);
        intent.putExtra("image_path", currentImagePath);
        startActivity(intent);
    }



    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "jpg_" + timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }
}