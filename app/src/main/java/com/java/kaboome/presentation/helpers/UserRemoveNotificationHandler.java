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
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataGroupRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.data.workers.UpdateConvCacheClearTSWorker;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.domain.usecases.DeleteGroupFromCacheUseCase;
import com.java.kaboome.domain.usecases.DeleteGroupUserFromCacheUseCase;
import com.java.kaboome.domain.usecases.DeleteUserGroupConvFromCacheUseCase;
import com.java.kaboome.domain.usecases.UpdateUserGroupCacheUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.NotificationsModel;
import com.java.kaboome.presentation.views.features.home.HomeActivity;

import java.util.Iterator;
import java.util.Set;

import static com.java.kaboome.helpers.AppConfigHelper.CHANNEL_ID;

/**
 * If a user has been removed from the group then the following needs to happen -
 * 1. If this user of the group is not an Admin -
 * DAOs to clear - GroupUserDAO for that user
 * 2. If this user of the group is an Admin -
 * DAOs to clear -
 * a. GroupUserDAO for that user
 * b. UserGroupConversations for that user
 * c. Private messages (within the conversation) from that user
 */
public class UserRemoveNotificationHandler extends BaseNotificationHandler {

    private static final String TAG = "KMUserRmNotifHandler";
    private Notification summaryNotification;
    private DeleteGroupUserFromCacheUseCase deleteGroupUserFromCacheUseCase;
    private DeleteUserGroupConvFromCacheUseCase deleteUserGroupConvFromCacheUseCase;
    private GroupRepository groupRepository;

    /*
    data: {
                PURPOSE: "NEW_GROUP",
                groupId: event.groupId,
                groupName: event.groupName,
                dateJoined: date_user_accepted,
                isAdmin: isAdmin,
                isCreator: isCreator,
                alias: event.alias,
                groupAdminRole: userRole,
                expiry: groupExpiry,
                lastAccessed: date_user_accepted,
                notify: notify,
                privateGroup: privateGroup,
                isDeleted: isDeleted,
                deviceId: deviceId,
                imageUpdateTimestamp: imageUpdateTimestamp,
                cacheClearTS: date_user_accepted
             }
        };


     */


public UserRemoveNotificationHandler(RemoteMessage remoteMessage, Context context) {
        super(remoteMessage, context);
        groupRepository = DataGroupRepository.getInstance();
        deleteGroupUserFromCacheUseCase = new DeleteGroupUserFromCacheUseCase(groupRepository);
        deleteUserGroupConvFromCacheUseCase = new DeleteUserGroupConvFromCacheUseCase(DataConversationsRepository.getInstance());

    }

    @Override
    public void handleNotification() {

        String remote_message_data = new Gson().toJson(remoteMessage.getData());
        Log.d(TAG, "Message Data: " + remote_message_data);

        //only the groupId and userId which is being deleted is the payload
        DomainGroupUser groupUserObject = new Gson().fromJson(remote_message_data,  DomainGroupUser.class);
        if(groupUserObject != null && groupUserObject.getGroupId() != null && groupUserObject.getUserId() != null){
            deleteGroupUserFromCacheUseCase.execute(DeleteGroupUserFromCacheUseCase.Params.deleteGroup(groupUserObject.getGroupId(), groupUserObject.getUserId()));
            deleteUserGroupConvFromCacheUseCase.execute(DeleteUserGroupConvFromCacheUseCase.Params.deleteUserGroupConversation(groupUserObject.getGroupId(), groupUserObject.getUserId()));
            //also delete the messages from the local cache
            //TODO: create a use case for it and call it via domain
            MessagesListRepository messagesListRepository = DataGroupMessagesRepository.getInstance();
            messagesListRepository.clearMessagesOfConversation(groupUserObject.getGroupId(), groupUserObject.getUserId());

        }

        //no notification - quietly delete
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
        AppConfigHelper.persistNotification(new NotificationsModel(notificationIdToUse, domainUserGroup.getUserId(), "Deletion"));

        notificationManager.notify(0, summaryNotification); //summary notification always uses 0


    }


}
