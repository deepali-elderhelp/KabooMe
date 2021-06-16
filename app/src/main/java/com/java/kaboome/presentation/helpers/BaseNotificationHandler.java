package com.java.kaboome.presentation.helpers;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

public abstract class BaseNotificationHandler {

    private static final int MESSAGE_TYPE = 0;
    RemoteMessage remoteMessage;
    Context context;

    public BaseNotificationHandler(RemoteMessage remoteMessage, Context context) {
        this.remoteMessage = remoteMessage;
        this.context = context;

    }

    public abstract void handleNotification();
}
