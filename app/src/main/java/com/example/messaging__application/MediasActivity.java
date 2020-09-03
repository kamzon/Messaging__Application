package com.example.messaging__application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MediasActivity extends AppCompatActivity {


    private Toolbar MediasToolBar;

    private DatabaseReference ClassesRef, UsersRef;

    private FirebaseAuth mAuth;

    private List<Folder_label> labels_List;

    public String senderID,recieverID;

    private RecyclerView ClassesRecyclerList;

    private EditText classSaersh;
    private Button searshBtn;

    private ImageButton searshClassImageBTN;

    private static final int GalleryPick = 1;

    private String label1,label2,label3,label4;
    private float conf1,conf2,conf3,conf4;

    private Uri fileUri;
    private int nb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medias);



        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Classes");

        labels_List =new ArrayList<>();

        MediasToolBar = (Toolbar) findViewById(R.id.medias_toolbar);
        setSupportActionBar(MediasToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Ressources");

        classSaersh = (EditText) findViewById(R.id.chercher_image_with_class);
        searshBtn = (Button) findViewById(R.id.button_chercher_image_with_class);
        searshClassImageBTN = (ImageButton) findViewById(R.id.searsh_with_image_button) ;

        recieverID = getIntent().getExtras().get("recieverID").toString();
        senderID = getIntent().getExtras().get("senderID").toString();

        label1="x";label2="x";label3="x";label4="x";

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_of_images_classes);
        final FolderLabelsAdapter mAdapter = new FolderLabelsAdapter(this, labels_List);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(mAdapter);


        ClassesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                labels_List.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    labels_List.add(new Folder_label(dataSnapshot.getValue().toString(),senderID,recieverID));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searshClassImageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);

            }
        });


        searshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String classes = classSaersh.getText().toString();



                Intent classIntent = new Intent(MediasActivity.this, SearshImageActivity.class);
                classIntent.putExtra("class", classes);
                classIntent.putExtra("label1",label1 );
                classIntent.putExtra("label2",label2 );
                classIntent.putExtra("label3",label3 );
                classIntent.putExtra("label4",label4 );
                classIntent.putExtra("senderID",senderID);
                classIntent.putExtra("recieverID",recieverID);

                classes="x";label1="x";label2="x";label3="x";label4="x";

                startActivity(classIntent);
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null){

            fileUri= data.getData();


            InputImage image;
            try {
                image = InputImage.fromFilePath(getApplicationContext(), fileUri);
                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

                labeler.process(image)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {
                                // Task completed successfully
                                // ...
                                int i=0;
                                conf1=0;conf2=0;conf3=0;conf4=0;
                                for (ImageLabel label : labels) {
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();

                                    if (confidence>=conf1){
                                        label1 =text;
                                        conf1 =confidence;
                                    }
                                    else if (confidence>=conf2){
                                        label2 ="x";
                                        conf2 =confidence;
                                    }
                                    else if (confidence>=conf3){
                                        label3 ="x";
                                        conf3 =confidence;
                                    }
                                    else if (confidence>=conf4){
                                        label4 ="x";
                                        conf4 =confidence;
                                    }
                                    i++;


                                }

                                classSaersh.setText("label1: "+label1+" label2: "+ label2 +"..." );
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });

            } catch (IOException e) {
                e.printStackTrace();
            }









        }
    }
}
