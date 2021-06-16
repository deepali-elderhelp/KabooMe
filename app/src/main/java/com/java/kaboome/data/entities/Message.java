/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "messages")
public class Message implements Serializable {

    @NonNull
    @PrimaryKey
    @SerializedName("messageId")
    private String messageId; //only a messageId is enough to identify a message uniquely, though ideally it will mostly be used to get messages by groupId

    @NonNull
    @SerializedName("groupId")
    private String groupId;

    @SerializedName("sentBy")
    private String sentBy;

    @SerializedName("sentTo")
    private String sentTo = "Group"; //default value is group

    @SerializedName("sentByImageTS")
    private Long sentByImageTS;

    @SerializedName("alias")
    private String alias;

    @SerializedName("isAdmin")
    private String isAdmin;

    @SerializedName("role")
    private String role;

    @SerializedName("sentAt")
    private Long sentAt;

    @SerializedName("notify")
    private int notify;

    @SerializedName("messageText")
    private String messageText;

    @SerializedName("hasAttachment")
    private Boolean hasAttachment;

    @SerializedName("attachmentExtension")
    private String attachmentExtension;

    @SerializedName("attachmentMime")
    private String attachmentMime;

    @SerializedName("attachmentUploaded")
    private Boolean attachmentUploaded;

    @SerializedName("isDeleted")
    private Boolean isDeleted;

    @SerializedName("tnBlob")
    private String tnBlob;

    @SerializedName("attachmentLoadingGoingOn")
    private Boolean attachmentLoadingGoingOn = false;

    /**
     * @ColumnInfo(name = "show_id") public transient String showId; – Ali Kazi Nov 30 '18 at 5:08
     * transient ones are only local, room uses them, but they are not sent to the server
     */

//    @ColumnInfo(name = "attachmentDownloaded")
//    private transient Boolean attachmentDownloaded;
//
//    @ColumnInfo(name = "attachmentPath")
//    private transient String attachmentPath;

    @ColumnInfo(name = "uploadedToServer")
    private transient boolean uploadedToServer = false;

    @ColumnInfo(name = "waitingToBeDeleted")
    private transient boolean waitingToBeDeleted = false;

//    @ColumnInfo(name = "attachmentLoadingGoingOn")
//    private transient boolean attachmentLoadingGoingOn = false;

    @ColumnInfo(name = "loadingProgress") //valid number between 0 and 100
    private transient int loadingProgress;

    @ColumnInfo(name = "unread")
    private transient int unread = 0; //it is 0 when unread and 1 when read

    @ColumnInfo(name = "attachmentUri")
    private transient String attachmentUri;

    @SerializedName("sentToUserName")
    private String sentToUserName;

    @SerializedName("sentToUserRole")
    private String sentToUserRole;

    @SerializedName("sentToImageTS")
    private Long sentToImageTS;

    @SerializedName("isSentToAdmin")
    private Boolean isSentToAdmin;

    public Message(String messageId, String groupId, String sentBy, String alias, String isAdmin, String role, Long sentAt, int notify, String messageText) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.sentBy = sentBy;
        this.alias = alias;
        this.isAdmin = isAdmin;
        this.role = role;
        this.sentAt = sentAt;
        this.notify = notify;
        this.messageText = messageText;
    }

    public Message(@NonNull String messageId, @NonNull String groupId, String sentBy, String alias, String isAdmin, String role, Long sentAt, int notify, String messageText, boolean uploadedToServer) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.sentBy = sentBy;
        this.alias = alias;
        this.isAdmin = isAdmin;
        this.role = role;
        this.sentAt = sentAt;
        this.notify = notify;
        this.messageText = messageText;
        this.uploadedToServer = uploadedToServer;
    }

    public Message(@NonNull String messageId, @NonNull String groupId, String sentBy, String alias, String isAdmin, String role, Long sentAt, int notify, String messageText, boolean uploadedToServer, boolean waitingToBeDeleted) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.sentBy = sentBy;
        this.alias = alias;
        this.isAdmin = isAdmin;
        this.role = role;
        this.sentAt = sentAt;
        this.notify = notify;
        this.messageText = messageText;
        this.uploadedToServer = uploadedToServer;
        this.waitingToBeDeleted = waitingToBeDeleted;
    }

    public Message(@NonNull String messageId, @NonNull String groupId, String sentBy, String sentTo, Long sentByImageTS, String alias, String isAdmin, String role, Long sentAt, int notify, String messageText, Boolean hasAttachment, String attachmentExtension, String attachmentMime, Boolean attachmentUploaded, Boolean isDeleted, String tnBlob, boolean uploadedToServer, boolean waitingToBeDeleted, boolean attachmentLoadingGoingOn, int loadingProgress) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.sentBy = sentBy;
        this.sentTo = sentTo;
        this.sentByImageTS = sentByImageTS;
        this.alias = alias;
        this.isAdmin = isAdmin;
        this.role = role;
        this.sentAt = sentAt;
        this.notify = notify;
        this.messageText = messageText;
        this.hasAttachment = hasAttachment;
        this.attachmentExtension = attachmentExtension;
        this.attachmentMime = attachmentMime;
        this.attachmentUploaded = attachmentUploaded;
        this.isDeleted = isDeleted;
        this.tnBlob = tnBlob;
        this.uploadedToServer = uploadedToServer;
        this.waitingToBeDeleted = waitingToBeDeleted;
        this.attachmentLoadingGoingOn = attachmentLoadingGoingOn;
        this.loadingProgress = loadingProgress;
    }

    public Message() {

    }

    public Message(String messageId) {
        this.messageId = messageId;
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

    public String getTnBlob() {
        return tnBlob;
    }

    public void setTnBlob(String tnBlob) {
        this.tnBlob = tnBlob;
    }

    public Boolean isAttachmentLoadingGoingOn() {
        return attachmentLoadingGoingOn;
    }

    public void setAttachmentLoadingGoingOn(Boolean attachmentLoadingGoingOn) {
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

    /**
     * This method is used by MessageListViewAdapter PagedListAdapter's
     * DIFF_CALLBACK to compare if the contentsAreSame
     * If contentsAreSame returns false, the view is reloaded causing the flicker
     * @param newMessage
     * @return
     */
    public boolean equals(Message newMessage){
        if(this == newMessage)
            return true;
        if(newMessage == null)
            return  false;
//        if(messageId.equals(newMessage.messageId) &&
//                (groupId.equals(newMessage.groupId)) &&
//                (sentBy.equals(newMessage.sentBy)) &&
//                (alias.equals(newMessage.alias)) &&
//                (isAdmin.equals(newMessage.isAdmin)) &&
//                (role.equals(newMessage.role)) &&
//                (sentAt.longValue() == newMessage.sentAt.longValue()) &&
//                (notify == newMessage.notify) &&
//                (messageText.equals(newMessage.messageText)) &&
//                (hasAttachment == newMessage.hasAttachment) &&
//                (Objects.equals(attachmentExtension, newMessage.attachmentExtension)) && //could be null
////                (attachmentExtension.equals(newMessage.attachmentExtension)) &&
////                (attachmentMime).equals(newMessage.attachmentMime) &&
//                (Objects.equals(attachmentMime, newMessage.attachmentMime)) &&
//                (attachmentUploaded == newMessage.attachmentUploaded) &&
//                (uploadedToServer == newMessage.uploadedToServer) &&
//                (waitingToBeDeleted == newMessage.waitingToBeDeleted) &&
//                (attachmentLoadingGoingOn == newMessage.attachmentLoadingGoingOn) &&
//                (loadingProgress == newMessage.loadingProgress) &&
//                (isDeleted == newMessage.isDeleted)){
//            return true;

        if(Objects.equals(messageId, newMessage.messageId) &&
                Objects.equals(groupId, newMessage.groupId) &&
                Objects.equals(sentBy, newMessage.sentBy) &&
                Objects.equals(sentByImageTS, newMessage.sentByImageTS) &&
//                (sentByImageTS.longValue() == newMessage.sentByImageTS.longValue()) &&
                Objects.equals(alias, newMessage.alias) &&
                Objects.equals(isAdmin, newMessage.isAdmin) &&
                Objects.equals(role, newMessage.role) &&
                (sentAt.longValue() == newMessage.sentAt.longValue()) &&
                (notify == newMessage.notify) &&
                (unread == newMessage.unread) &&
                Objects.equals(messageText, newMessage.messageText) &&
                Objects.equals(hasAttachment, newMessage.hasAttachment) &&
                Objects.equals(attachmentExtension, newMessage.attachmentExtension) && //could be null
                Objects.equals(attachmentMime, newMessage.attachmentMime) &&
                Objects.equals(attachmentUploaded, newMessage.attachmentUploaded) &&
                Objects.equals(uploadedToServer, newMessage.uploadedToServer) &&
                Objects.equals(attachmentUri, newMessage.attachmentUri) &&
                Objects.equals(tnBlob, newMessage.tnBlob) &&
                Objects.equals(waitingToBeDeleted, newMessage.waitingToBeDeleted) &&
                Objects.equals(attachmentLoadingGoingOn, newMessage.attachmentLoadingGoingOn) &&
                Objects.equals(loadingProgress, newMessage.loadingProgress) &&
                Objects.equals(isDeleted, newMessage.isDeleted)){
            return true;
        }
        return false;
    }

}

