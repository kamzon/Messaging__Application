package com.example.messaging__application;

public class Folder_label {

    private String labelName;
    private String senderID,recieverId;

    public Folder_label() {
    }


    public Folder_label(String labelName, String senderID, String recieverId) {
        this.labelName = labelName;
        this.senderID = senderID;
        this.recieverId = recieverId;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public void setRecieverId(String recieverId) {
        this.recieverId = recieverId;
    }

    public String getRecieverId() {
        return recieverId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
