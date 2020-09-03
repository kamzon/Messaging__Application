package com.example.messaging__application;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.nl.smartreply.SmartReply;
import com.google.mlkit.nl.smartreply.SmartReplyGenerator;
import com.google.mlkit.nl.smartreply.SmartReplySuggestion;
import com.google.mlkit.nl.smartreply.SmartReplySuggestionResult;
import com.google.mlkit.nl.smartreply.TextMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private ArrayList<TextMessage> conversation = new ArrayList<TextMessage>();

    private ChatActivity chatActivity = new ChatActivity();

    MyCallback myCallback = null;

    public interface MyCallback {
        // Declaration of the template function for the interface
        public void updateMyText(String rec1,String rec2, String rec3);
    }



    public MessageAdapter (List<Messages> userMessagesList,MyCallback callback)
    {
        this.userMessagesList = userMessagesList;
        this.myCallback = callback;

    }



    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText,recomandedTextView;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture, messageReceiverPicture;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_image_view);
            //recomandedTextView = (TextView) itemView.findViewById(R.id.recomanded_text);

        }
    }





    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout, viewGroup, false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int position)
    {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("image"))
                {
                    String receiverImage = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);


        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessageText.setTextColor(Color.BLACK);
                messageViewHolder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());


                conversation.add(TextMessage.createForLocalUser(messages.getMessage(), System.currentTimeMillis()));

                SmartReplyGenerator smartReply = SmartReply.getClient();
                smartReply.suggestReplies(conversation)
                        .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                            @Override
                            public void onSuccess(SmartReplySuggestionResult result) {
                                if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                                    // The conversation's language isn't supported, so
                                    // the result doesn't contain any suggestions.

                                    //messageViewHolder.recomandedTextView.setText("");
                                    //chatActivity.recomendation.setText("");
                                    if (myCallback != null) {
                                        myCallback.updateMyText("","","");
                                    }



                                    //Toast.makeText(MessageAdapter.this,"language not detected...",Toast.LENGTH_SHORT).show();
                                } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                                    // Task completed successfully
                                    // ...
                                    //messageViewHolder.recomandedTextView.setText("");
                                    //chatActivity.recomendation.setText("");

                                    if (myCallback != null) {
                                        myCallback.updateMyText("","","");
                                    }



                                    int i=0;

                                    String rec1="",rec2="",rec3="";
                                    for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                        String replyText = suggestion.getText();

                                        //messageViewHolder.recomandedTextView.setText(replyText);
                                        //chatActivity.recomendation.setText("");
                                        //System.out.println("============================================================="+replyText);
                                        if (myCallback != null && i==0) {
                                            rec1=suggestion.getText();
                                            myCallback.updateMyText(rec1,rec2,rec3);

                                        }

                                        if (myCallback != null && i==1) {
                                            rec2= suggestion.getText();
                                            myCallback.updateMyText(rec1,rec2,rec3);
                                        }

                                        if (myCallback != null && i==2) {
                                            rec3= suggestion.getText();
                                            myCallback.updateMyText(rec1,rec2,rec3);
                                        }

                                        i++;



                                    }

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                //Toast.makeText(MessageAdapter.this,"Error :"+e,Toast.LENGTH_SHORT).show();
                                //messageViewHolder.recomandedTextView.setText("");
                                //chatActivity.recomendation.setText("");
                                if (myCallback != null) {
                                    myCallback.updateMyText("","","");
                                }


                            }
                        });
            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.reciever_messages_layout);
                messageViewHolder.receiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());

                conversation.add(TextMessage.createForRemoteUser(messages.getMessage(), System.currentTimeMillis(), fromUserID));


                SmartReplyGenerator smartReply = SmartReply.getClient();
                smartReply.suggestReplies(conversation)
                        .addOnSuccessListener(new OnSuccessListener<SmartReplySuggestionResult>() {
                            @Override
                            public void onSuccess(SmartReplySuggestionResult result) {
                                if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                                    // The conversation's language isn't supported, so
                                    // the result doesn't contain any suggestions.
                                    //messageViewHolder.recomandedTextView.setText("");
                                    //chatActivity.recomendation.setText("");

                                    if (myCallback != null) {
                                        myCallback.updateMyText("","","");
                                    }

                                    //Toast.makeText(MessageAdapter.this,"language not detected...",Toast.LENGTH_SHORT).show();
                                } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                                    // Task completed successfully
                                    // ...
                                    //messageViewHolder.recomandedTextView.setText("");
                                    //chatActivity.recomendation.setText("");
                                    if (myCallback != null) {
                                        myCallback.updateMyText("","","");
                                    }



                                    int i=0;
                                    String rec1="",rec2="",rec3="";
                                    for (SmartReplySuggestion suggestion : result.getSuggestions()) {
                                        String replyText = suggestion.getText();
                                        //messageViewHolder.recomandedTextView.setText(replyText);
                                        //System.out.println("==========================================="+replyText);
                                        //chatActivity.recomendation.setText("");
                                        if (myCallback != null && i==0) {
                                            rec1= suggestion.getText();
                                            myCallback.updateMyText(rec1,rec2,rec3);
                                        }

                                        if (myCallback != null && i==1) {
                                            rec2= suggestion.getText();
                                            myCallback.updateMyText(rec1,rec2,rec3);
                                        }

                                        if (myCallback != null && i==2) {
                                            rec3=suggestion.getText();
                                            myCallback.updateMyText(rec1,rec2,rec3);
                                        }

                                        i++;


                                    }

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                //Toast.makeText(MessageAdapter.this,"Error :"+e,Toast.LENGTH_SHORT).show();

                                //messageViewHolder.recomandedTextView.setText("");
                                //chatActivity.recomendation.setText("");

                                if (myCallback != null) {
                                    myCallback.updateMyText("","","");
                                }

                            }
                        });


            }
        }else if (fromMessageType.equals("image")){

            if (fromUserID.equals(messageSenderId)){

                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);

            }else {

                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);

            }

        }else {

            if (fromUserID.equals(messageSenderId)){

                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);

                messageViewHolder.messageSenderPicture.setBackgroundResource(R.drawable.file);

                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        messageViewHolder.itemView.getContext().startActivity(intent);
                    }
                });


            }else {

                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);

                messageViewHolder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

                messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(position).getMessage()));
                        messageViewHolder.itemView.getContext().startActivity(intent);
                    }
                });




            }





        }
    }




    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

}

