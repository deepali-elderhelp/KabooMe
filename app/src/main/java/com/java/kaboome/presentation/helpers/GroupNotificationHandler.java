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
import com.java.kaboome.constants.GroupStatusConstants;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataInvitationsListRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.domain.usecases.AddNewGroupToCacheUseCase;
import com.java.kaboome.domain.usecases.AddNewMessageUseCase;
import com.java.kaboome.domain.usecases.RejectInvitationCacheSingleUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.NotificationsModel;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.home.HomeActivity;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import static com.java.kaboome.helpers.AppConfigHelper.CHANNEL_ID;


public class GroupNotificationHandler extends BaseNotificationHandler {

    private static final String TAG = "KMGroupNotifHandler";
    private Notification summaryNotification;
    private AddNewGroupToCacheUseCase addNewGroupToCacheUseCase;
    private RejectInvitationCacheSingleUseCase rejectInvitationCacheSingleUseCase;
    private UserGroupsListRepository userGroupsListRepository;
    private AddNewMessageUseCase addNewMessageUseCase;

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


public GroupNotificationHandler(RemoteMessage remoteMessage, Context context) {
        super(remoteMessage, context);
        userGroupsListRepository = DataUserGroupsListRepository.getInstance();
        addNewGroupToCacheUseCase = new AddNewGroupToCacheUseCase(userGroupsListRepository);
        rejectInvitationCacheSingleUseCase = new RejectInvitationCacheSingleUseCase(DataInvitationsListRepository.getInstance());
        addNewMessageUseCase = new AddNewMessageUseCase(DataGroupMessagesRepository.getInstance());
    }

    @Override
    public void handleNotification() {

        String remote_message_data = new Gson().toJson(remoteMessage.getData());
        Log.d(TAG, "Message Data: " + remote_message_data);

        //add the message to the cache
        DomainUserGroup groupObject = new Gson().fromJson(remote_message_data, DomainUserGroup.class);
        Log.d(TAG, "handleNotification: Group Model received "+groupObject.toString());
        if(groupObject != null){
            addNewGroupToCacheUseCase.execute(AddNewGroupToCacheUseCase.Params.newGroup(groupObject));
            //also if the group has been joined because the user created a request (which essentially should be the case
            //for a notification to be sent to the user on request acceptance, then the request needs to be deleted
            //this request only needs to be deleted locally since the server stuff has already happened
            //also, when the user goes to the invitations, this would be removed anyhow, but we should also proactively
            //remove that invitation/request because we are anyways observing it

            rejectInvitationCacheSingleUseCase.execute(RejectInvitationCacheSingleUseCase.Params.rejectInviForGroup(groupObject.getGroupId()));

            //add the default welcome message
            DomainMessage newDomainMessage = new DomainMessage();
            newDomainMessage.setMessageId(UUID.randomUUID().toString());
            newDomainMessage.setSentBy(GroupStatusConstants.JOINED_GROUP.getStatus());
            newDomainMessage.setSentTo(MessageGroupsConstants.GROUP_MESSAGES.toString());
            newDomainMessage.setDeleted(false);
            newDomainMessage.setAlias("");
            newDomainMessage.setGroupId(groupObject.getGroupId());
            newDomainMessage.setSentAt(groupObject.getLastAccessed());
            newDomainMessage.setUploadedToServer(true);
            newDomainMessage.setHasAttachment(false);
            newDomainMessage.setMessageText(AppConfigHelper.getContext().getString(R.string.new_group_welcome_message_1aa));
            addNewMessageUseCase.execute(AddNewMessageUseCase.Params.newMessage(newDomainMessage));

            //download the images for easier viewage

            String newTNKey = ImagesUtilHelper.getGroupUserImageName(groupObject.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
            String newKey = ImagesUtilHelper.getGroupUserImageName(groupObject.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);

            //now also download these images so that they are there in the cache when the user needs them
            ImageHelper.getInstance().downloadImage(newTNKey);
            ImageHelper.getInstance().downloadImage(newKey);


        }
        buildNotificationForUser(context, groupObject);
    }

    private void buildNotificationForUser(Context context, DomainUserGroup domainUserGroup) {
        //form a notification
        Log.d(TAG, "buildNotificationForUser");

        int notificationIdToUse = AppConfigHelper.getNotificationId();

        //this pending intent later could be more specific to a place inside the app
        Intent activityIntent = new Intent(context, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);

        String contentText = "You have been accepted in "+domainUserGroup.getGroupName();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.account_group_grey);


        Notification notification1 = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setSmallIcon(R.mipmap.ic_notification_96)
                .setContentTitle("Accepted")
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
//                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSmallIcon(R.mipmap.ic_notification_96)
                    .setStyle(new NotificationCompat.InboxStyle()
                            .addLine("Accepted - " + " " + contentText)
                            .setBigContentTitle("1 new acceptance")
                            .setSummaryText("1 new acceptance"))
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
            inboxStyle.addLine("Acceptance");
            inboxStyle.setSummaryText((numberOfOldMessages+1) + " new messages/acceptance");
            summaryNotification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSmallIcon(R.mipmap.ic_notification_96)
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
        AppConfigHelper.persistNotification(new NotificationsModel(notificationIdToUse, domainUserGroup.getGroupId(), "Acceptance"));

        notificationManager.notify(0, summaryNotification); //summary notification always uses 0


    }


}
