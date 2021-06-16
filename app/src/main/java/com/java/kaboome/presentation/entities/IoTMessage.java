package com.java.kaboome.presentation.entities;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class IoTMessage {

    private String messageId;

    private String groupId;

    private String sentBy;

    private String sentTo;

    private Long sentByImageTS;

    private String alias;

    private String isAdmin;

    private String role;

    private int notify;

    private String messageText;

    private Long sentAt;

    private Boolean hasAttachment;

    private String attachmentExtension;

    private String attachmentMime;

    private Boolean attachmentUploaded;

    private Boolean isDeleted;

    private String tnBlob;
//
//    private Boolean attachmentDownloaded;

//    private String attachmentPath;

    private String attachmentUri;

    private boolean uploadedToServer;

    private boolean attachmentLoadingGoingOn;

    private int loadingProgress;

    private String sentToUserName;

    private String sentToUserRole;

    private Long sentToImageTS;

    private Boolean isSentToAdmin;


    public IoTMessage() {
    }

    public IoTMessage(String messageId, String groupId, String sentBy, String alias, String isAdmin, String role, int notify, String messageText, Long sentAt) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.sentBy = sentBy;
        this.alias = alias;
        this.isAdmin = isAdmin;
        this.role = role;
        this.notify = notify;
        this.messageText = messageText;
        this.sentAt = sentAt;
    }

//    public IoTMessage(String messageId, String groupId, String sentBy, String alias, String isAdmin, String role, int notify, String messageText, Long sentAt, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentDownloaded, String attachmentPath, boolean uploadedToServer) {
//        this.messageId = messageId;
//        this.groupId = groupId;
//        this.sentBy = sentBy;
//        this.alias = alias;
//        this.isAdmin = isAdmin;
//        this.role = role;
//        this.notify = notify;
//        this.messageText = messageText;
//        this.sentAt = sentAt;
//        this.hasAttachment = hasAttachment;
//        this.attachmentUploaded = attachmentUploaded;
//        this.attachmentDownloaded = attachmentDownloaded;
//        this.attachmentPath = attachmentPath;
//        this.uploadedToServer = uploadedToServer;
//    }


    public IoTMessage(String messageId, String groupId, String sentBy, String alias, String isAdmin, String role, int notify, String messageText, Long sentAt, Boolean hasAttachment, String attachmentExtension, String attachmentMime, Boolean attachmentUploaded, boolean uploadedToServer) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.sentBy = sentBy;
        this.alias = alias;
        this.isAdmin = isAdmin;
        this.role = role;
        this.notify = notify;
        this.messageText = messageText;
        this.sentAt = sentAt;
        this.hasAttachment = hasAttachment;
        this.attachmentExtension = attachmentExtension;
        this.attachmentMime = attachmentMime;
        this.attachmentUploaded = attachmentUploaded;
        this.uploadedToServer = uploadedToServer;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getNotify() {
        return notify;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public Boolean getHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(Boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getAttachmentExtension() {
        return attachmentExtension;
    }

    public void setAttachmentExtension(String attachmentExtension) {
        this.attachmentExtension = attachmentExtension;
    }

        public Boolean getAttachmentUploaded() {
        return attachmentUploaded;
    }

    public void setAttachmentUploaded(Boolean attachmentUploaded) {
        this.attachmentUploaded = attachmentUploaded;
    }

    public String getAttachmentMime() {
        return attachmentMime;
    }

    public void setAttachmentMime(String attachmentMime) {
        this.attachmentMime = attachmentMime;
    }

    //
//    public Boolean getAttachmentDownloaded() {
//        return attachmentDownloaded;
//    }
//
//    public void setAttachmentDownloaded(Boolean attachmentDownloaded) {
//        this.attachmentDownloaded = attachmentDownloaded;
//    }

//    public String getAttachmentURI() {
//        return attachmentPath;
//    }
//
//    public void setAttachmentPath(String attachmentPath) {
//        this.attachmentPath = attachmentPath;
//    }

    public boolean isUploadedToServer() {
        return uploadedToServer;
    }

    public void setUploadedToServer(boolean uploadedToServer) {
        this.uploadedToServer = uploadedToServer;
    }

    public boolean isAttachmentLoadingGoingOn() {
        return attachmentLoadingGoingOn;
    }

    public void setAttachmentLoadingGoingOn(boolean attachmentLoadingGoingOn) {
        this.attachmentLoadingGoingOn = attachmentLoadingGoingOn;
    }

    public int getLoadingProgress() {
        return loadingProgress;
    }

    public void setLoadingProgress(int loadingProgress) {
        this.loadingProgress = loadingProgress;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getTnBlob() {
        return tnBlob;
    }

    public void setTnBlob(String tnBlob) {
        this.tnBlob = tnBlob;
    }

    public Long getSentByImageTS() {
        return sentByImageTS;
    }

    public void setSentByImageTS(Long sentByImageTS) {
        this.sentByImageTS = sentByImageTS;
    }

    public String getSentToUserName() {
        return sentToUserName;
    }

    public void setSentToUserName(String sentToUserName) {
        this.sentToUserName = sentToUserName;
    }

    public String getSentToUserRole() {
        return sentToUserRole;
    }

    public void setSentToUserRole(String sentToUserRole) {
        this.sentToUserRole = sentToUserRole;
    }

    public Long getSentToImageTS() {
        return sentToImageTS;
    }

    public void setSentToImageTS(Long sentToImageTS) {
        this.sentToImageTS = sentToImageTS;
    }

    public Boolean getIsSentToAdmin() {
        return isSentToAdmin;
    }

    public void setIsSentToAdmin(Boolean sentToAdmin) {
        isSentToAdmin = sentToAdmin;
    }

    public String getAttachmentUri() {
        return attachmentUri;
    }

    public void setAttachmentUri(String attachmentUri) {
        this.attachmentUri = attachmentUri;
    }

    @Override
    public String toString() {
        return "IoTMessage{" +
                "messageId='" + messageId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", sentBy='" + sentBy + '\'' +
                ", sentByImageTS=" + sentByImageTS +
                ", alias='" + alias + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                ", role='" + role + '\'' +
                ", notify=" + notify +
                ", messageText='" + messageText + '\'' +
                ", sentAt=" + sentAt +
                ", hasAttachment=" + hasAttachment +
                ", attachmentExtension='" + attachmentExtension + '\'' +
                ", attachmentMime='" + attachmentMime + '\'' +
                ", attachmentUploaded=" + attachmentUploaded +
                ", isDeleted=" + isDeleted +
                ", tnBlob='" + tnBlob + '\'' +
                ", uploadedToServer=" + uploadedToServer +
                ", attachmentLoadingGoingOn=" + attachmentLoadingGoingOn +
                ", loadingProgress=" + loadingProgress +
                '}';
    }
}


