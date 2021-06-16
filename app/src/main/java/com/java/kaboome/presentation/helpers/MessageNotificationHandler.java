package com.java.kaboome.presentation.helpers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.java.kaboome.R;
import com.java.kaboome.data.repositories.DataConversationsRepository;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.usecases.AddNewConversationUseCase;
import com.java.kaboome.domain.usecases.AddNewMessageUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.IoTMessage;
import com.java.kaboome.presentation.entities.NotificationsModel;
import com.java.kaboome.presentation.mappers.IoTDomainMessageMapper;
import com.java.kaboome.presentation.views.features.conversations.viewmodel.AddConversationViewModel;
import com.java.kaboome.presentation.views.features.home.HomeActivity;


import java.util.Iterator;
import java.util.Set;

import me.leolin.shortcutbadger.Badger;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.java.kaboome.helpers.AppConfigHelper.CHANNEL_ID;


public class MessageNotificationHandler extends BaseNotificationHandler {

    private static final String TAG = "KMMessageNotifHandler";
    private Notification summaryNotification;
    private AddNewMessageUseCase addNewMessageUseCase;
    private AddNewConversationUseCase addNewConversationUseCase;
//    private AddNewUnreadMessageUseCase addNewUnreadMessageUseCase;
    private MessagesListRepository messagesListRepository;
//    private GroupsUnreadCountRepository groupsUnreadCountRepository;


    public MessageNotificationHandler(RemoteMessage remoteMessage, Context context) {
        super(remoteMessage, context);
        messagesListRepository = DataGroupMessagesRepository.getInstance();
        addNewMessageUseCase = new AddNewMessageUseCase(messagesListRepository);
//        groupsUnreadCountRepository = DataGroupsUnreadCountRepository.getInstance();
//        addNewUnreadMessageUseCase = new AddNewUnreadMessageUseCase(groupsUnreadCountRepository);
        addNewConversationUseCase = new AddNewConversationUseCase(DataConversationsRepository.getInstance());
    }

    @Override
    public void handleNotification() {

        String remote_message_data = new Gson().toJson(remoteMessage.getData());
        Log.d(TAG, "Message Data: " + remote_message_data);

        //add the message to the cache
        IoTMessage messageObject = new Gson().fromJson(remote_message_data, IoTMessage.class);

        //only handle all this if the message was not sent by this user
        if(messageObject != null && AppConfigHelper.getUserId().equals(messageObject.getSentBy())){
            return;
        }
        if(messageObject != null){
            Log.d(TAG, "handleMessageArrival: message came - "+messageObject);
            messageObject.setUploadedToServer(true); //message is coming back from server, so it is uploaded to server already
            addNewMessageUseCase.execute(AddNewMessageUseCase.Params.newMessage(IoTDomainMessageMapper.transformFromIoTMessage(messageObject)));
            //also update the unread count
            //NEW: no unread message thing
            //addNewUnreadMessageUseCase.execute(AddNewUnreadMessageUseCase.Params.setMessage(IoTDomainMessageMapper.transformFromIoTMessage(messageObject)));

            //if the sentTo is some user, and this user is an Admin create a conversation for it
            //don;t worry if to check if the conversation already exist
            //because it will be ignored if existing
            String sentTo = messageObject.getSentTo();

            if(sentTo != null && !sentTo.equals("Group") && !sentTo.equals(AppConfigHelper.getUserId())){
                //this is  private message
                //also this user is not it is addressed to - this means this user is an admin
                DomainUserGroupConversation domainUserGroupConversation = new DomainUserGroupConversation();
                domainUserGroupConversation.setUserId(AppConfigHelper.getUserId());
                domainUserGroupConversation.setGroupId(messageObject.getGroupId());
                domainUserGroupConversation.setOtherUserId(messageObject.getSentTo());
                domainUserGroupConversation.setOtherUserRole(messageObject.getRole());
                domainUserGroupConversation.setOtherUserName(messageObject.getAlias());
                domainUserGroupConversation.setImageUpdateTimestamp(messageObject.getSentByImageTS());

                addNewConversationUseCase.execute(AddNewConversationUseCase.Params.newConversation(domainUserGroupConversation));
            }
        }




        //see if the user needs to be notified
        String groupId = messageObject.getGroupId();
        final int messageNotifyLevel = messageObject.getNotify();
        //for now building notification for all levels
        //TODO: add check for if notification is to be displayed
        //checking if the app is open, then we do not need the notification
        if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState() == Lifecycle.State.CREATED){
                //app is in the background
            Log.d(TAG, "handleNotification: App is in the background, building notification");
            buildNotificationForUser(context, messageObject);
        }
        if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)){
            //app is in the foreground
            Log.d(TAG, "handleNotification: App is in foreground, not building notification");
        }
//        buildNotificationForUser(context, messageObject);
//        if(groupId != null){
//
//            Log.d(TAG, "handleNotification: group id is not null");
//            DatabaseHelper.getInstance().getUserGroup(groupId, new DatabaseDataReadyCallback() {
//                @Override
//                public void onDataAccessSuccess(Object data) {
//                    Log.d(TAG, "onDataAccessSuccess: data returned from cache is "+data);
//
//                    if(data instanceof UserGroup){
//                        Log.d(TAG, "onDataAccessSuccess: data instance of UserGroup");
//                        UserGroup userGroup = (UserGroup)data;
//                        int notifyLevel = Integer.valueOf(userGroup.getNotify());
//                        Log.d(TAG, "NotifyLevel: "+notifyLevel);
//                        if(notifyLevel == 0 || messageNotifyLevel > notifyLevel){//user does not need any message notification
//                            Log.d(TAG, "Notification level of user for this group - "+notifyLevel);
//                            return;
//                        }
//                        else {
//                            buildNotificationForUser(context, message);
////                            buildNotificationForUser(message);
//                        }
//
//                    }
//                }
//
//                @Override
//                public void onDataAccessFailure(Exception err) {
//                    Log.d(TAG, "Error while getting user from the user group - "+err.getMessage());
//                }
//            });
//        }


    }

    private void buildNotificationForUser(Context context, IoTMessage message) {
        //form a notification
        Log.d(TAG, "buildNotificationForUser");

        int notificationIdToUse = AppConfigHelper.getNotificationId();

        //this pending intent later could be more specific to a place inside the app
        Intent activityIntent = new Intent(context, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);

        String contentText = message.getMessageText();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

//        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.account_group_grey);
//        Bitmap notifyIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_new);

//        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(message.getGroupCreatorAlias())
//                .setContentText(contentText)
//                .setLargeIcon(largeIcon)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText(bigContentText)
//                        .setBigContentTitle(message.getGroupCreatorAlias())
//                        .setSummaryText("Group Name"))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimaryDark, null))
//                .setContentIntent(contentIntent)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(true)
//                .build();

        Notification notification1 = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_notification_96)
//                .setSmallIcon(R.drawable.ic_launcher_new)
                .setContentTitle(message.getAlias())
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setGroup("kaboome_group")
                .setAutoCancel(true)
                .build();

        //if the message already exists, which can happen in the case of attachment
        //attachment messages are published twice, so the notification shows up twice
        //we need to update in that case, not add new
//        Set<String> olderNotifications = AppConfigHelper.getPersistedNotifications();

        Set<NotificationsModel> olderNotifications = AppConfigHelper.getPersistedNotificationModels();
        Log.d(TAG, "Beginning count - "+olderNotifications.size());
        boolean notificationExists = false;
        for(NotificationsModel notificationsModel: olderNotifications){
            Log.d(TAG, "Existing - "+notificationsModel.getMessageId());
            //notification is already there, we need to use the same notification id, not add new one
            if(notificationsModel.getMessageId().equals(message.getMessageId())){
                //it is same, get the notificationIdToUse
                Log.d(TAG, "Notification already exists");
                notificationExists = true;
                notificationIdToUse = notificationsModel.getNotificationId();
            }

        }
        if(!notificationExists){
            Log.d(TAG, "This was a new notification");
            //now persist this one too
            NotificationsModel newNotification = new NotificationsModel(notificationIdToUse, message.getMessageId(), message.getAlias());
            olderNotifications.add(newNotification);
            AppConfigHelper.persistNotification(olderNotifications);
            Log.d(TAG, "Now all - "+olderNotifications.size());
        }


        notificationManager.notify(notificationIdToUse, notification1);



        if(olderNotifications == null || olderNotifications.isEmpty()){
            ShortcutBadger.applyCount(context, 1);
            summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_notification_96)
//                    .setSmallIcon(R.drawable.ic_launcher_new)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine(message.getAlias() + " " + contentText)
                            .setBigContentTitle("1 new message")
                            .setSummaryText("1 new message"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setGroup("kaboome_group")
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setGroupSummary(true)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
//                    .setNumber(1)
                    .build();
        }
        else{
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            int numberOfOldMessages = olderNotifications.size();
            ShortcutBadger.applyCount(context, numberOfOldMessages+1);
            inboxStyle.setBigContentTitle((numberOfOldMessages+1) + " new messages");
//            Iterator<String> notificationStringIterator = olderNotifications.iterator();
//            while(notificationStringIterator.hasNext()){
//                NotificationsModel notificationsBean = new Gson().fromJson(notificationStringIterator.next(), NotificationsModel.class);
//                inboxStyle.addLine(notificationsBean.getBigContentTitle());
//            }
            for(NotificationsModel notificationsModel: olderNotifications){
                inboxStyle.addLine(notificationsModel.getBigContentTitle());
            }

            //now add the current one
            inboxStyle.addLine(message.getAlias());
            inboxStyle.setSummaryText((numberOfOldMessages+1) + " new messages");
            summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_notification_96)
//                    .setSmallIcon(R.drawable.ic_launcher_new)
                    .setStyle(inboxStyle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setGroup("kaboome_group")
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setGroupSummary(true)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
//                    .setNumber(numberOfOldMessages+1)
                    .setNumber(0)
                    .build();

        }


//        //now persist this one too
//        AppConfigHelper.persistNotification(newNotification);

        notificationManager.notify(0, summaryNotification); //summary notification always uses 0



//        if(notificationIdToUse == 1){ //this is the first message, there should not be any summary yet
//            //first add the notification details to the shared preference
//
//
//            summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setStyle(new NotificationCompat.InboxStyle()
//                            .addLine(message.getGroupCreatorAlias() + " " + contentText)
//                            .setBigContentTitle("1 new message")
//                            .setSummaryText("For You"))
//                    .setPriority(NotificationCompat.PRIORITY_HIGH)
//                    .setGroup("kaboome_group")
//                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
//                    .setGroupSummary(true)
//                    .build();
//        }
//        else{ //summary already exists, get all the notifications from the shared preferences and create the summary again
//
//        }
    }

//    private void buildNotificationForUser(Message message) {
//
//        String contentText = message.getMessageText().substring(0, 20);
//
//        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(message.getGroupCreatorAlias())
//                .setContentText(message.getMessageText())
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setGroup("kaboome_group")
//                .build();
//
//        Notification summaryNotification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
//                .setSmallIcon(R.drawable.ic_reply)
//                .setStyle(new NotificationCompat.InboxStyle()
//                        .addLine(title2 + " " + message2)
//                        .addLine(title1 + " " + message1)
//                        .setBigContentTitle("2 new messages")
//                        .setSummaryText("user@example.com"))
//                .setPriority(NotificationCompat.PRIORITY_LOW)
//                .setGroup("example_group")
//                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
//                .setGroupSummary(true)
//                .build();
//    }
}
