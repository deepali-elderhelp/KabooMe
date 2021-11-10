package com.java.kaboome.data.remote.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HelpFeedbackRequest implements Serializable {


    @SerializedName("subject")
    private String subject;

    @SerializedName("messageText")
    private String messageText;

    @SerializedName("allowedContact")
    private Boolean allowedContact;


    public HelpFeedbackRequest(String subject, String messageText, Boolean allowedContact) {
        this.subject = subject;
        this.messageText = messageText;
        this.allowedContact = allowedContact;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Boolean getAllowedContact() {
        return allowedContact;
    }

    public void setAllowedContact(Boolean allowedContact) {
        this.allowedContact = allowedContact;
    }
}
