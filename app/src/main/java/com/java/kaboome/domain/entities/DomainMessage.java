package com.java.kaboome.domain.entities;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class DomainMessage {

    private String messageId; //only a messageId is enough to identify a message uniquely, though ideally it will mostly be used to get messages by groupId

    private String groupId;

    private String sentBy;

    private Long sentByImageTS;

    private String alias;

    private String isAdmin;

    private String role;

    private Long sentAt;

    private String sentTo;

    private int notify;

    private String messageText;

    private Boolean hasAttachment;

    private Boolean attachmentUploaded;

    private String attachmentExtension;

    private String attachmentMime;

    private Boolean isDeleted;

    private String tnBlob;

//    private Boolean attachmentDownloaded;
//
//    private String attachmentPath;

    private boolean uploadedToServer;

    private boolean waitingToBeDeleted;

    private boolean attachmentLoadingGoingOn;

    private String attachmentUri;

    private int loadingProgress;

    private String sentToUserName;

    private String sentToUserRole;

    private Long sentToImageTS;

    private Boolean isSentToAdmin;

    private int unread = 0;


    public DomainMessage(String messageId, String groupId, String sentBy, String alias, String isAdmin, String role, Long sentAt, int notify, String messageText, Boolean hasAttachment, Boolean attachmentUploaded, String attachmentExtension, boolean uploadedToServer, boolean waitingToBeDeleted) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.sentBy = sentBy;
        this.alias = alias;
        this.isAdmin = isAdmin;
        this.role = role;
        this.sentAt = sentAt;
        this.notify = notify;
        this.messageText = messageText;
        this.hasAttachment = hasAttachment;
        this.attachmentUploaded = attachmentUploaded;
        this.attachmentExtension = attachmentExtension;
        this.uploadedToServer = uploadedToServer;
        this.waitingToBeDeleted = waitingToBeDeleted;
    }

    //    public DomainMessage(String messageId, String groupId, String sentBy, String alias, String isAdmin, String role, Long sentAt, int notify, String messageText, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentDownloaded, String attachmentPath, boolean uploadedToServer, boolean waitingToBeDeleted) {
//        this.messageId = messageId;
//        this.groupId = groupId;
//        this.sentBy = sentBy;
//        this.alias = alias;
//        this.isAdmin = isAdmin;
//        this.role = role;
//        this.sentAt = sentAt;
//        this.notify = notify;
//        this.messageText = messageText;
//        this.hasAttachment = hasAttachment;
//        this.attachmentUploaded = attachmentUploaded;
//        this.attachmentDownloaded = attachmentDownloaded;
//        this.attachmentPath = attachmentPath;
//        this.uploadedToServer = uploadedToServer;
//        this.waitingToBeDeleted = waitingToBeDeleted;
//    }

    public DomainMessage() {

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

    public Long getSentAt() {
        return sentAt;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
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

    public Boolean getHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(Boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public Boolean getAttachmentUploaded() {
        return attachmentUploaded;
    }

    public void setAttachmentUploaded(Boolean attachmentUploaded) {
        this.attachmentUploaded = attachmentUploaded;
    }

    public String getAttachmentExtension() {
        return attachmentExtension;
    }

    public void setAttachmentExtension(String attachmentExtension) {
        this.attachmentExtension = attachmentExtension;
    }

    public String getAttachmentMime() {
        return attachmentMime;
    }

    public void setAttachmentMime(String attachmentMime) {
        this.attachmentMime = attachmentMime;
    }


    public boolean isUploadedToServer() {
        return uploadedToServer;
    }

    public void setUploadedToServer(boolean uploadedToServer) {
        this.uploadedToServer = uploadedToServer;
    }

    public boolean isWaitingToBeDeleted() {
        return waitingToBeDeleted;
    }

    public void setWaitingToBeDeleted(boolean waitingToBeDeleted) {
        this.waitingToBeDeleted = waitingToBeDeleted;
    }

    public String getTnBlob() {
        return tnBlob;
    }

    public void setTnBlob(String tnBlob) {
        this.tnBlob = tnBlob;
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

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getAttachmentUri() {
        return attachmentUri;
    }

    public void setAttachmentUri(String attachmentUri) {
        this.attachmentUri = attachmentUri;
    }
}
