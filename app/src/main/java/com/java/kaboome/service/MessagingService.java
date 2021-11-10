package com.java.kaboome.service;


import androidx.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.java.kaboome.constants.NotificationPurposeConstants;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.FirebaseTokenHelper;
import com.java.kaboome.presentation.helpers.ConversationUpdateNotificationHandler;
import com.java.kaboome.presentation.helpers.GroupNotificationHandler;
import com.java.kaboome.presentation.helpers.GroupRemoveNotificationHandler;
import com.java.kaboome.presentation.helpers.GroupUpdatedNotificationHandler;
import com.java.kaboome.presentation.helpers.InvitationNotificationHandler;
import com.java.kaboome.presentation.helpers.MessageNotificationHandler;
import com.java.kaboome.presentation.helpers.RequestNotificationHandler;
import com.java.kaboome.presentation.helpers.UserRemoveNotificationHandler;

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "KMMessagingService";
    private String token;

    @Override
    public void onNewToken(String s) {



        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.d(TAG, "getInstanceId failed : "+task.getException());
                            return;
                        }

                        //Get new Instance ID token
                        token = task.getResult().getToken();
                        Log.d(TAG, "new token: "+token);
                    }
                });

        //update the token to the server is session credentials have been established
        //otherwise, don;t worry, after session setup, it will be checked again.
        //check if user sign up/in has happened
        if(CognitoHelper.getCurrSession() != null){
            Log.d(TAG, "new token received when user is logged in");
            FirebaseTokenHelper.updateDeviceToken(); //update the database with the new deviceId
        }
        else{
            Log.d(TAG, "new token received but user not logged in");
            //this should be taken care of before launchUser in verification
            //where it is checked again
        }

    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: Message arrived, remote message is "+remoteMessage.toString());
        if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.NEW_MESSAGE.getPurpose())){
            MessageNotificationHandler messageNotificationHandler = new MessageNotificationHandler(remoteMessage, this);
            messageNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.REQUEST_CANCEL.getPurpose())){
            RequestNotificationHandler requestNotificationHandler = new RequestNotificationHandler(remoteMessage, this, true);
            requestNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.NEW_REQUEST.getPurpose())){
            RequestNotificationHandler requestNotificationHandler = new RequestNotificationHandler(remoteMessage, this, false);
            requestNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.NEW_INVITATION.getPurpose())){
            InvitationNotificationHandler invitationNotificationHandler = new InvitationNotificationHandler(remoteMessage, this);
            invitationNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.NEW_GROUP.getPurpose())){
            GroupNotificationHandler groupNotificationHandler = new GroupNotificationHandler(remoteMessage, this);
            groupNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.GROUP_DELETED.getPurpose())){
            GroupRemoveNotificationHandler groupRemoveNotificationHandler = new GroupRemoveNotificationHandler(remoteMessage, this);
            groupRemoveNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.GROUP_UPDATED.getPurpose())){
            GroupUpdatedNotificationHandler groupUpdatedNotificationHandler = new GroupUpdatedNotificationHandler(remoteMessage, this);
            groupUpdatedNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.USER_DELETED.getPurpose())){
            UserRemoveNotificationHandler userRemoveNotificationHandler = new UserRemoveNotificationHandler(remoteMessage, this);
            userRemoveNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.UPDATE_CONVERSATION_ALIAS_ROLE_TS.getPurpose())){
            ConversationUpdateNotificationHandler conversationUpdateNotificationHandler = new ConversationUpdateNotificationHandler(remoteMessage, this);
            conversationUpdateNotificationHandler.handleNotification();
        }
        else if(remoteMessage != null && remoteMessage.getData().containsValue(NotificationPurposeConstants.UPDATE_CONVERSATION_DELETE.getPurpose())){
            ConversationUpdateNotificationHandler conversationUpdateNotificationHandler = new ConversationUpdateNotificationHandler(remoteMessage, this);
            conversationUpdateNotificationHandler.handleNotification();
        }
//        if(remoteMessage != null && remoteMessage.getData().containsValue("NEW_MESSAGE")){
//            MessageNotificationHandler messageNotificationHandler = new MessageNotificationHandler(remoteMessage, this);
//            messageNotificationHandler.handleNotification();
//        }
    }
}
