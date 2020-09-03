package com.example.messaging__application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LabelsActivity extends AppCompatActivity {

    private Toolbar LabelToolBar;
    private String recieverID,senderID,labelName;

    private DatabaseReference messagesRef, UsersRef;

    private List<LabelsImages> imagess_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labels);


        labelName = getIntent().getExtras().get("label").toString();
        recieverID = getIntent().getExtras().get("recieverID").toString();
        senderID = getIntent().getExtras().get("senderID").toString();

        LabelToolBar = (Toolbar) findViewById(R.id.labels_toolbar);
        setSupportActionBar(LabelToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle(labelName);


        messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(senderID).child(recieverID);



        imagess_List =new ArrayList<>();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_of_images_in_the_classes);
        final LabelsImagesAdapter mAdapter = new LabelsImagesAdapter(this, imagess_List);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(mAdapter);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imagess_List.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

                    if (dataSnapshot.hasChild("label1")){

                        String label1 = dataSnapshot.child("label1").getValue().toString();
                        String label2 = dataSnapshot.child("label2").getValue().toString();
                        String label3 = dataSnapshot.child("label3").getValue().toString();
                        String label4 = dataSnapshot.child("label4").getValue().toString();

                        String conf1 = dataSnapshot.child("conf1").getValue().toString();
                        String conf2 = dataSnapshot.child("conf2").getValue().toString();
                        String conf3 = dataSnapshot.child("conf3").getValue().toString();
                        String conf4 = dataSnapshot.child("conf4").getValue().toString();

                        if (labelName.equals(label1) || labelName.equals(label2) || labelName.equals(label3) || labelName.equals(label4)){


                            imagess_List.add(new LabelsImages(dataSnapshot.child("message").getValue().toString(),labelName));

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
