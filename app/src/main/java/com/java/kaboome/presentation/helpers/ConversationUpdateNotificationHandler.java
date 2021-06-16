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

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.java.kaboome.R;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.data.repositories.DataConversationsRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.domain.usecases.RemoveDeletedUserGroupConvUseCase;
import com.java.kaboome.domain.usecases.UpdateUserGroupCacheUseCase;
import com.java.kaboome.domain.usecases.UpdateUserGroupConvCacheUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.NotificationsModel;
import com.java.kaboome.presentation.views.features.home.HomeActivity;

import java.util.Iterator;
import java.util.Set;

import static com.java.kaboome.helpers.AppConfigHelper.CHANNEL_ID;


public class ConversationUpdateNotificationHandler extends BaseNotificationHandler {

    private static final String TAG = "KMConvUpdNotifHandler";
    private Notification summaryNotification;
    private UpdateUserGroupConvCacheUseCase updateUserGroupConvCacheUseCase;
    private RemoveDeletedUserGroupConvUseCase removeDeletedUserGroupConvUseCase;
    private ConversationsRepository conversationsRepository;

    /*
    data: {

                purpose: "GROUP_UPDATED",
                action: "updateGroupNamePrivacyImage" or "updateGroupExpiry",
                groupId: groupId,
                groupName: groupName,
                privateGroup: groupPrivacy,
                imageUpdateTimestamp: groupImageTS,
                expiry: expiry
             }
        };


     */


public ConversationUpdateNotificationHandler(RemoteMessage remoteMessage, Context context) {
        super(remoteMessage, context);
        conversationsRepository = DataConversationsRepository.getInstance();
        updateUserGroupConvCacheUseCase = new UpdateUserGroupConvCacheUseCase(conversationsRepository);
        removeDeletedUserGroupConvUseCase = new RemoveDeletedUserGroupConvUseCase(conversationsRepository);
    }

    @Override
    public void handleNotification() {

        String remote_message_data = new Gson().toJson(remoteMessage.getData());
        Log.d(TAG, "Message Data: " + remote_message_data);

        DomainUserGroupConversation domainUserGroupConversation = new Gson().fromJson(remote_message_data, DomainUserGroupConversation.class);
        domainUserGroupConversation.setUserId(AppConfigHelper.getUserId());
        if(remoteMessage != null && remoteMessage.getData().containsValue(GroupActionConstants.UPDATE_GROUP_CONV_ROLE_AND_ALIAS_AND_IMAGE.getAction())){
            updateUserGroupConvCacheUseCase.execute(UpdateUserGroupConvCacheUseCase.Params.forUserGroupConv(domainUserGroupConversation.getGroupId(), domainUserGroupConversation.getOtherUserId(), domainUserGroupConversation.getOtherUserName(), domainUserGroupConversation.getOtherUserRole(), domainUserGroupConversation.getImageUpdateTimestamp()));
        }
        if(remoteMessage != null && remoteMessage.getData().containsValue(GroupActionConstants.REMOVE_GROUP_CONV_FOR_USER.getAction())){
            removeDeletedUserGroupConvUseCase.execute(RemoveDeletedUserGroupConvUseCase.Params.forUserGroupConv(domainUserGroupConversation.getGroupId(), domainUserGroupConversation.getOtherUserId()));
        }

        //no notification - quietly change
//        buildNotificationForUser(context, domainUserGroupConversation);
    }

    private void buildNotificationForUser(Context context, DomainUserGroup domainUserGroup) {
        //form a notification
        Log.d(TAG, "buildNotificationForUser");

        int notificationIdToUse = AppConfigHelper.getNotificationId();

        //this pending intent later could be more specific to a place inside the app
        Intent activityIntent = new Intent(context, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);

        String contentText = "Group deleted - "+domainUserGroup.getGroupName();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.account_group_grey);


        Notification notification1 = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Deleted")
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(contentIntent)
                .setGroup("kaboome_group")
                .setAutoCancel(true)
                .build();

        notificationManager.notify(notificationIdToUse, notification1);


        Set<String> olderNotifications = AppConfigHelper.getPersistedNotifications();
        if(olderNotifications == null || olderNotifications.isEmpty()){
            summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine("Deleted - " + " " + contentText)
                            .setBigContentTitle("Group deleted")
                            .setSummaryText("1 Group deleted"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setGroup("kaboome_group")
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setGroupSummary(true)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();
        }
        else{
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            int numberOfOldMessages = olderNotifications.size();
            inboxStyle.setBigContentTitle((numberOfOldMessages+1) + " new messages/acceptance");
            Iterator<String> notificationStringIterator = olderNotifications.iterator();
            while(notificationStringIterator.hasNext()){
                NotificationsModel notificationsBean = new Gson().fromJson(notificationStringIterator.next(), NotificationsModel.class);
                inboxStyle.addLine(notificationsBean.getBigContentTitle());
            }
            //now add the current one
            inboxStyle.addLine("Deleted");
            inboxStyle.setSummaryText((numberOfOldMessages+1) + " Group Deleted");
            summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(inboxStyle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setGroup("kaboome_group")
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .setGroupSummary(true)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .build();

        }


        //now persist this one too
        AppConfigHelper.persistNotification(new NotificationsModel(notificationIdToUse, domainUserGroup.getGroupId(), "Deletion"));

        notificationManager.notify(0, summaryNotification); //summary notification always uses 0


    }


}
