package com.example.robotikapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{

    Button captureBtn, selectBtn, displayBtn;
    String currentImagePath = null;
    Intent selectIntent;
    Bitmap bitmap;
    ImageView imageview;

    private static final int IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureBtn = findViewById(R.id.btnCapture);
        displayBtn = findViewById(R.id.btnDisplay);
        selectBtn = findViewById(R.id.btnSelect);
        imageview = findViewById(R.id.viewImage);

    captureBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                captureImage(v);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    });

    displayBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayImage(v);
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

    }

    public void captureImage(View view) throws FileNotFoundException {
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
}