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
import com.java.kaboome.constants.NotificationPurposeConstants;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.domain.usecases.UpdateUserGroupCacheUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.NotificationsModel;
import com.java.kaboome.presentation.views.features.home.HomeActivity;

import java.util.Iterator;
import java.util.Set;

import static com.java.kaboome.helpers.AppConfigHelper.CHANNEL_ID;


public class GroupUpdatedNotificationHandler extends BaseNotificationHandler {

    private static final String TAG = "KMGroupUpdNotifHandler";
    private Notification summaryNotification;
    private UpdateUserGroupCacheUseCase updateUserGroupCacheUseCase;
    private UserGroupRepository userGroupRepository;

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


public GroupUpdatedNotificationHandler(RemoteMessage remoteMessage, Context context) {
        super(remoteMessage, context);
        userGroupRepository = DataUserGroupRepository.getInstance();
        updateUserGroupCacheUseCase = new UpdateUserGroupCacheUseCase(userGroupRepository);
    }

    @Override
    public void handleNotification() {

        String remote_message_data = new Gson().toJson(remoteMessage.getData());
        Log.d(TAG, "Message Data: " + remote_message_data);

        DomainUserGroup groupObject = new Gson().fromJson(remote_message_data, DomainUserGroup.class);
        groupObject.setUserId(AppConfigHelper.getUserId());
        if(remoteMessage != null && remoteMessage.getData().containsValue(GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction())){
            updateUserGroupCacheUseCase.execute(UpdateUserGroupCacheUseCase.Params.forUserGroup(groupObject, GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction()));
        }
        if(remoteMessage != null && remoteMessage.getData().containsValue(GroupActionConstants.UPDATE_GROUP_EXPIRY.getAction())){
            updateUserGroupCacheUseCase.execute(UpdateUserGroupCacheUseCase.Params.forUserGroup(groupObject, GroupActionConstants.UPDATE_GROUP_EXPIRY.getAction()));
        }

        //no notification - quietly change
//        buildNotificationForUser(context, groupObject);
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