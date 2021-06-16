package com.java.kaboome.presentation.entities;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class NotificationsModel implements Serializable {

    private int notificationId;
    private String messageId;
    private String bigContentTitle;

    public NotificationsModel(int notificationId, String messageId, String bigContentTitle) {
        this.notificationId = notificationId;
        this.bigContentTitle = bigContentTitle;
        this.messageId = messageId;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getBigContentTitle() {
        return bigContentTitle;
    }

    public void setBigContentTitle(String bigContentTitle) {
        this.bigContentTitle = bigContentTitle;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

}
