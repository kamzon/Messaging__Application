package com.example.messaging__application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearshImageActivity extends AppCompatActivity {


    private String searshClass="x",recieverID="x",senderID="x";
    private String label1="x",label2="x",label3="x",label4="x";

    private Toolbar searshImageToolbar;

    private DatabaseReference messagesRef;

    private List<LabelsImages> imagess_List;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searsh_image);


        searshClass = getIntent().getExtras().get("class").toString();
        recieverID = getIntent().getExtras().get("recieverID").toString();
        senderID = getIntent().getExtras().get("senderID").toString();

        label1 = getIntent().getExtras().get("label1").toString();
        label2 = getIntent().getExtras().get("label2").toString();
        label3 = getIntent().getExtras().get("label3").toString();
        label4 = getIntent().getExtras().get("label4").toString();


        if (searshClass==null){
            searshClass="x";
        }
        if (recieverID==null){
            recieverID="x";
        }
        if (senderID==null){
            senderID="x";
        }
        if (label1==null){
            label1="x";
        }
        if (label2==null){
            label2="x";
        }
        if (label3==null){
            label3="x";
        }
        if (label4==null){
            label4="x";
        }



        searshImageToolbar = (Toolbar) findViewById(R.id.searsh_class_toolbar);
        setSupportActionBar(searshImageToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(searshClass + "...");


        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(senderID).child(recieverID);

        imagess_List =new ArrayList<>();

        imagess_List.clear();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_of_images_searshing_by_class);
        final LabelsImagesAdapter mAdapter = new LabelsImagesAdapter(this, imagess_List);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(mAdapter);


        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imagess_List.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    if (dataSnapshot.hasChild("label1")){

                        String class1 = dataSnapshot.child("label1").getValue().toString();
                        String class2 = dataSnapshot.child("label2").getValue().toString();
                        String class3 = dataSnapshot.child("label3").getValue().toString();
                        String class4 = dataSnapshot.child("label4").getValue().toString();

                        String conf1 = dataSnapshot.child("conf1").getValue().toString();
                        String conf2 = dataSnapshot.child("conf2").getValue().toString();
                        String conf3 = dataSnapshot.child("conf3").getValue().toString();
                        String conf4 = dataSnapshot.child("conf4").getValue().toString();

                        if (searshClass.equals(class1) || searshClass.equals(class2) || searshClass.equals(class3) || searshClass.equals(class4)){


                            LabelsImages image =new LabelsImages(dataSnapshot.child("message").getValue().toString(),searshClass);

                            if (imagess_List.indexOf(image)==-1){
                                imagess_List.add(image);
                            }



                        }

                        if (label1.equals(class1) || label1.equals(class2) || label1.equals(class3) || label1.equals(class4)){



                            LabelsImages image =new LabelsImages(dataSnapshot.child("message").getValue().toString(),searshClass);

                            if (imagess_List.indexOf(image)==-1){
                                imagess_List.add(image);
                            }
                        }
                        if (label2.equals(class1) || label2.equals(class2) || label2.equals(class3) || label2.equals(class4)){


                            LabelsImages image =new LabelsImages(dataSnapshot.child("message").getValue().toString(),searshClass);

                            if (imagess_List.indexOf(image)==-1){
                                imagess_List.add(image);
                            }
                        }
                        if (label3.equals(class1) || label3.equals(class2) || label3.equals(class3) || label3.equals(class4)){


                            LabelsImages image =new LabelsImages(dataSnapshot.child("message").getValue().toString(),searshClass);

                            if (imagess_List.indexOf(image)==-1){
                                imagess_List.add(image);
                            }
                        }
                        if (label4.equals(class1) || label4.equals(class2) || label4.equals(class3) || label4.equals(class4)){


                            LabelsImages image =new LabelsImages(dataSnapshot.child("message").getValue().toString(),searshClass);

                            if (imagess_List.indexOf(image)==-1){
                                imagess_List.add(image);
                            }
                        }

                    }



                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}
