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
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.List;

public class TextRecognitionActivity extends AppCompatActivity {


    private Toolbar textRecognitonToolBar;

    private Button selectImageGallery,selctImageCamera,detctText;
    private ImageView selectedImage;
    private TextView detectedTextView;

    private Bitmap imageBitmap;


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int GalleryPick = 1;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition);

        textRecognitonToolBar = (Toolbar) findViewById(R.id.text_recogntion_toolbar);
        setSupportActionBar(textRecognitonToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Text Detection ...");


        selectImageGallery = (Button) findViewById(R.id.text_recogntion_select_image_frome_gallery);
        selctImageCamera = (Button) findViewById(R.id.text_recogntion_select_image_frome_camera);
        detctText = (Button) findViewById(R.id.text_recogntion_detect_text);

        selectedImage = (ImageView) findViewById(R.id.text_recognition_image_view);

        detectedTextView = (TextView) findViewById(R.id.text_recogntion_text_display);



        selctImageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

                detectedTextView.setText("");


            }
        });

        selectImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToTextRecognitionGalleryActivity();

                detectedTextView.setText("");

            }
        });

        detctText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
            }
        });

        



    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            selectedImage.setImageBitmap(imageBitmap);
        }




    }


    private void detectTextFromImage() {

        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result =
                detector.processImage(firebaseVisionImage)
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

                                        Toast.makeText(TextRecognitionActivity.this,"Error : "+ e,Toast.LENGTH_SHORT).show();

                                        Log.d("ERROE", e.getMessage());
                                    }
                                });


    }

    private void displayText(FirebaseVisionText firebaseVisionText) {

        List<FirebaseVisionText.TextBlock> textBlocks = firebaseVisionText.getTextBlocks();

        if (textBlocks.size() ==0){

            Toast.makeText(TextRecognitionActivity.this,"No text found ...",Toast.LENGTH_SHORT).show();

        }
        else{
            for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()){

                String text = block.getText();
                detectedTextView.setText(text);

            }
        }

    }




    private void SendUserToTextRecognitionGalleryActivity()
    {
        Intent mediasIntent = new Intent(TextRecognitionActivity.this, TextrecognitionGalleryActivity.class);
        startActivity(mediasIntent);
    }



}
