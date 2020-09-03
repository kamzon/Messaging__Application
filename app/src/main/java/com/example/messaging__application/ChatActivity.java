package com.example.messaging__application;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements MessageAdapter.MyCallback
{
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private Toolbar ChatToolBar;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ImageButton SendMessageButton, SendFilesButton,textRecognitionButton;
    private EditText MessageInputText;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;


    private String saveCurrentTime, saveCurrentDate;
    private String checker = "", myUrl;

    private StorageTask uploadTask;
    private Uri fileUri;

    private ProgressDialog loadingBar;

    private String imageLabel="";
    private float labelConfidence= 0;

    private String label1,label2,label3,label4;
    private float conf1,conf2,conf3,conf4;

    public TextView recomendation, recomendation2, recomendation3;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();

        recomendation = (TextView) findViewById(R.id.recomanded_text_view);
        recomendation2 = (TextView) findViewById(R.id.recomanded_text_view2);
        recomendation3 = (TextView) findViewById(R.id.recomanded_text_view3);




        IntializeControllers();


        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendMessage();
            }
        });


        DisplayLastSeen();

        textRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToTextRecognitionActivity();

            }
        });


        SendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{

                        "Images",
                        "PDF Files",
                        "Ms Word Files"

                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select The File");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (i == 0)
                        {
                            checker = "image";



                            Intent intent = new Intent();

                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select Image"), 1);
                        }
                        if (i == 1)
                        {
                            checker = "pdf";

                            Intent intent = new Intent();

                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent, "Select PDF Files"), 1);
                        }
                        if (i == 2)
                        {
                            checker = "docx";

                            Intent intent = new Intent();
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                            intent.setAction(Intent.ACTION_GET_CONTENT);

                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent, "Select Ms Word Files"), 1);
                        }
                    }
                });
                builder.show();
                }


        });

        recomendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recomendation.getText().toString();

                MessageInputText.setText(text);

            }
        });

        recomendation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recomendation2.getText().toString();

                MessageInputText.setText(text);

            }
        });

        recomendation3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = recomendation3.getText().toString();

                MessageInputText.setText(text);

            }
        });

    }




    private void IntializeControllers()
    {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        SendFilesButton = (ImageButton) findViewById(R.id.send_files_btn);
        textRecognitionButton = (ImageButton) findViewById(R.id.text_recognition_btn);
        MessageInputText = (EditText) findViewById(R.id.input_message);


        MessageAdapter.MyCallback mc = new MessageAdapter.MyCallback() {

            @Override
            public void updateMyText(String rec1, String rec2,String rec3) {
                ((TextView)findViewById(R.id.recomanded_text_view)).setText(rec1);
                ((TextView)findViewById(R.id.recomanded_text_view2)).setText(rec2);

                ((TextView)findViewById(R.id.recomanded_text_view3)).setText(rec3);
            }
        };

        messageAdapter = new MessageAdapter(messagesList,mc);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        loadingBar = new ProgressDialog(this);


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.ressours_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_medias)

            SendUserToMediasActivity();
        {


        }
        if (item.getItemId() == R.id.menu_chat_activity_setings)
        {

        }


        return true;
    }

    private void SendUserToMediasActivity()
    {
        Intent mediasIntent = new Intent(ChatActivity.this, MediasActivity.class);
        mediasIntent.putExtra("senderID",messageSenderID);
        mediasIntent.putExtra("recieverID",messageReceiverID);
        startActivity(mediasIntent);
    }

    private void SendUserToTextRecognitionActivity()
    {
        Intent mediasIntent = new Intent(ChatActivity.this, TextRecognitionActivity.class);
        startActivity(mediasIntent);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==1 && resultCode==RESULT_OK) {

            loadingBar.setTitle("sending File");
            loadingBar.setMessage("Please wait, sending...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            if (data.getClipData() != null) {

                Toast.makeText(ChatActivity.this, "Slected multiple files...",Toast.LENGTH_SHORT).show();

                int cout = data.getClipData().getItemCount();

                for (int i = 0; i < cout; i++) {

                    fileUri = data.getClipData().getItemAt(i).getUri();


                    if (!checker.equals(("image"))) {


                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Documents Files");

                        final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                        final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                        DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                                .child(messageSenderID).child(messageReceiverID).push();

                        final String messagePushID = userMessageKeyRef.getKey();

                        final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

                        filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();

                                        Map messageImageBody = new HashMap();
                                        messageImageBody.put("message", downloadUrl);
                                        messageImageBody.put("name", fileUri.getLastPathSegment());
                                        messageImageBody.put("type", checker);

                                        messageImageBody.put("from", messageSenderID);
                                        messageImageBody.put("to", messageReceiverID);
                                        messageImageBody.put("messageID", messagePushID);
                                        messageImageBody.put("time", saveCurrentTime);
                                        messageImageBody.put("date", saveCurrentDate);
                                        messageImageBody.put("seen", "false");


                                        Map messageBodyDetail = new HashMap();
                                        messageBodyDetail.put(messageSenderRef + "/" + messagePushID, messageImageBody);
                                        messageBodyDetail.put(messageReceiverRef + "/" + messagePushID, messageImageBody);

                                        RootRef.updateChildren(messageBodyDetail);
                                        loadingBar.dismiss();

                                        RootRef.child("message notifications").child(messageSenderID).child(messageReceiverID).child("seen").setValue("false");

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                loadingBar.setMessage((int) p + " % Uploading...");
                            }
                        });


                    } else if (checker.equals(("image"))) {


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
                                            int i = 0;
                                            conf1 = 0;
                                            conf2 = 0;
                                            conf3 = 0;
                                            conf4 = 0;
                                            for (ImageLabel label : labels) {
                                                String text = label.getText();
                                                float confidence = label.getConfidence();
                                                int index = label.getIndex();
                                                RootRef.child("Classes").child(text).setValue(text);
                                                if (confidence >= conf1) {
                                                    label1 = text;
                                                    conf1 = confidence;
                                                } else if (confidence >= conf2) {
                                                    label2 = text;
                                                    conf2 = confidence;
                                                } else if (confidence >= conf3) {
                                                    label3 = text;
                                                    conf3 = confidence;
                                                } else if (confidence >= conf4) {
                                                    label4 = text;
                                                    conf4 = confidence;
                                                }
                                                i++;

                                                if (confidence >= 0.5) {


                                                    imageLabel += " label: " + text + " confidence : " + confidence;
                                                }
                                            }
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

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");


                        final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                        final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                        DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                                .child(messageSenderID).child(messageReceiverID).push();

                        final String messagePushID = userMessageKeyRef.getKey();

                        final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                        uploadTask = filePath.putFile(fileUri);

                        RootRef.child("message notifications").child(messageSenderID).child(messageReceiverID).child("seen").setValue("false");


                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {

                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }


                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {

                                    Uri downloadUrl = task.getResult();
                                    myUrl = downloadUrl.toString();


                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", myUrl);
                                    messageTextBody.put("name", fileUri.getLastPathSegment());
                                    messageTextBody.put("type", checker);
                                    messageTextBody.put("label1", label1);
                                    messageTextBody.put("conf1", conf1);
                                    messageTextBody.put("label2", label2);
                                    messageTextBody.put("conf2", conf2);
                                    messageTextBody.put("label3", label3);
                                    messageTextBody.put("conf3", conf3);
                                    messageTextBody.put("label4", label4);
                                    messageTextBody.put("conf4", conf4);
                                    //messageTextBody.put("confidence", labelConfidence);
                                    messageTextBody.put("from", messageSenderID);
                                    messageTextBody.put("to", messageReceiverID);
                                    messageTextBody.put("messageID", messagePushID);
                                    messageTextBody.put("time", saveCurrentTime);
                                    messageTextBody.put("date", saveCurrentDate);
                                    messageTextBody.put("seen", "false");

                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                    messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);


                                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                            } else {
                                                loadingBar.dismiss();
                                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                            MessageInputText.setText("");
                                        }
                                    });

                                }


                            }
                        });


                    }
                }

            } else {

                fileUri = data.getData();

                if (!checker.equals(("image"))) {


                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Documents Files");

                    final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                    final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                    DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                            .child(messageSenderID).child(messageReceiverID).push();

                    final String messagePushID = userMessageKeyRef.getKey();

                    final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

                    filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();

                                    Map messageImageBody = new HashMap();
                                    messageImageBody.put("message", downloadUrl);
                                    messageImageBody.put("name", fileUri.getLastPathSegment());
                                    messageImageBody.put("type", checker);

                                    messageImageBody.put("from", messageSenderID);
                                    messageImageBody.put("to", messageReceiverID);
                                    messageImageBody.put("messageID", messagePushID);
                                    messageImageBody.put("time", saveCurrentTime);
                                    messageImageBody.put("date", saveCurrentDate);
                                    messageImageBody.put("seen", "false");


                                    Map messageBodyDetail = new HashMap();
                                    messageBodyDetail.put(messageSenderRef + "/" + messagePushID, messageImageBody);
                                    messageBodyDetail.put(messageReceiverRef + "/" + messagePushID, messageImageBody);

                                    RootRef.updateChildren(messageBodyDetail);
                                    loadingBar.dismiss();

                                    RootRef.child("message notifications").child(messageSenderID).child(messageReceiverID).child("seen").setValue("false");

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingBar.dismiss();
                                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            loadingBar.setMessage((int) p + " % Uploading...");
                        }
                    });


                } else if (checker.equals(("image"))) {


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
                                        int i = 0;
                                        conf1 = 0;
                                        conf2 = 0;
                                        conf3 = 0;
                                        conf4 = 0;
                                        for (ImageLabel label : labels) {
                                            String text = label.getText();
                                            float confidence = label.getConfidence();
                                            int index = label.getIndex();
                                            RootRef.child("Classes").child(text).setValue(text);
                                            if (confidence >= conf1) {
                                                label1 = text;
                                                conf1 = confidence;
                                            } else if (confidence >= conf2) {
                                                label2 = text;
                                                conf2 = confidence;
                                            } else if (confidence >= conf3) {
                                                label3 = text;
                                                conf3 = confidence;
                                            } else if (confidence >= conf4) {
                                                label4 = text;
                                                conf4 = confidence;
                                            }
                                            i++;

                                            if (confidence >= 0.5) {


                                                imageLabel += " label: " + text + " confidence : " + confidence;
                                            }
                                        }
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

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");


                    final String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                    final String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                    DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                            .child(messageSenderID).child(messageReceiverID).push();

                    final String messagePushID = userMessageKeyRef.getKey();

                    final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                    uploadTask = filePath.putFile(fileUri);

                    RootRef.child("message notifications").child(messageSenderID).child(messageReceiverID).child("seen").setValue("false");


                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {

                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }


                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {

                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();


                                Map messageTextBody = new HashMap();
                                messageTextBody.put("message", myUrl);
                                messageTextBody.put("name", fileUri.getLastPathSegment());
                                messageTextBody.put("type", checker);
                                messageTextBody.put("label1", label1);
                                messageTextBody.put("conf1", conf1);
                                messageTextBody.put("label2", label2);
                                messageTextBody.put("conf2", conf2);
                                messageTextBody.put("label3", label3);
                                messageTextBody.put("conf3", conf3);
                                messageTextBody.put("label4", label4);
                                messageTextBody.put("conf4", conf4);
                                //messageTextBody.put("confidence", labelConfidence);
                                messageTextBody.put("from", messageSenderID);
                                messageTextBody.put("to", messageReceiverID);
                                messageTextBody.put("messageID", messagePushID);
                                messageTextBody.put("time", saveCurrentTime);
                                messageTextBody.put("date", saveCurrentDate);
                                messageTextBody.put("seen", "false");

                                Map messageBodyDetails = new HashMap();
                                messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);


                                RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            loadingBar.dismiss();
                                            Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                        } else {
                                            loadingBar.dismiss();
                                            Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                        MessageInputText.setText("");
                                    }
                                });

                            }


                        }
                    });


                }


            }
        }




            else {
                loadingBar.dismiss();
                Toast.makeText(ChatActivity.this,"Nothing selected ERROR ...",Toast.LENGTH_SHORT).show();
            }






    }

    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Last Seen: " + date + " " + time);
                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    private void SendMessage()
    {
        String messageText = MessageInputText.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);
            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("seen","false");

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                        RootRef.child("message notifications").child(messageSenderID).child(messageReceiverID).child("seen").setValue("false");

                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }

    @Override
    public void updateMyText(String rec1,String rec2, String rec3) {
        ((TextView)findViewById(R.id.recomanded_text_view)).setText(rec1);

        ((TextView)findViewById(R.id.recomanded_text_view2)).setText(rec2);

        ((TextView)findViewById(R.id.recomanded_text_view3)).setText(rec3);

    }
}
