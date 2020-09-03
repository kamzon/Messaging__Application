package com.example.messaging__application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.util.List;

public class TextrecognitionGalleryActivity extends AppCompatActivity {



    private Toolbar textRecognitonGalleryToolBar;

    private Button detctText;
    private ImageView selectedImage;
    private TextView detectedTextView;

    private Bitmap imageBitmap;


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int GalleryPick = 1;

    private Uri fileUri;

    private FirebaseVisionImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textrecognition_gallery);


        textRecognitonGalleryToolBar = (Toolbar) findViewById(R.id.text_recogntion_gallery_toolbar);
        setSupportActionBar(textRecognitonGalleryToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Text Detection ...");

        detctText = (Button) findViewById(R.id.text_recogntion_gallery_detect_text);

        selectedImage = (ImageView) findViewById(R.id.text_recognition_gallery_image_view);

        detectedTextView = (TextView) findViewById(R.id.text_recogntion_gallery_text_display);


        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);


        detctText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                detectTextFromImage();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {

            fileUri = data.getData();



            try {
                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), fileUri);
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
                selectedImage.setImageBitmap(imageBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }




        }



    }



    private void detectTextFromImage() {



        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                // Task completed successfully
                                // ...
                                displayText(firebaseVisionText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...

                                        Toast.makeText(TextrecognitionGalleryActivity.this,"Error : "+ e,Toast.LENGTH_SHORT).show();

                                        Log.d("ERROE", e.getMessage());
                                    }
                                });


    }

    private void displayText(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.TextBlock> textBlocks = firebaseVisionText.getTextBlocks();

        if (textBlocks.size() ==0){

            Toast.makeText(TextrecognitionGalleryActivity.this,"No text found ...",Toast.LENGTH_SHORT).show();

        }
        else{
            for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){

                String text = block.getText();
                detectedTextView.setText(text);

            }
        }

    }




}
