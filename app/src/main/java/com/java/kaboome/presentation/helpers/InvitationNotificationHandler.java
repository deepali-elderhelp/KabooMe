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
import com.java.kaboome.constants.InvitationStatusConstants;
import com.java.kaboome.data.repositories.DataInvitationsListRepository;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.usecases.AddNewInvitationUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.entities.NotificationsModel;
import com.java.kaboome.presentation.mappers.InvitationModelMapper;
import com.java.kaboome.presentation.views.features.home.HomeActivity;

import java.util.Iterator;
import java.util.Set;

import static com.java.kaboome.helpers.AppConfigHelper.CHANNEL_ID;


public class InvitationNotificationHandler extends BaseNotificationHandler {

    private static final String TAG = "KMInviNotifHandler";
    private Notification summaryNotification;
    private AddNewInvitationUseCase addNewInvitationUseCase;
    private InvitationsListRepository invitationsListRepository;

    /**
     * var payload = {
     *
     *                 data: {
     *                     purpose: "NEW_INVITATION",
     *                     groupId: groupId,
     *                     groupName: groupName,
     *                     invitedBy: invitedBy,
     *                     invitedByAlias: invitedByAlias,
     *                     privateGroup: privateGroup,
     *                     invitationStatus: status
     *                 }
     *             };
     * @param remoteMessage
     * @param context
     */

    public InvitationNotificationHandler(RemoteMessage remoteMessage, Context context) {
        super(remoteMessage, context);
        invitationsListRepository = DataInvitationsListRepository.getInstance();
        addNewInvitationUseCase = new AddNewInvitationUseCase(invitationsListRepository);
    }

    @Override
    public void handleNotification() {

        String remote_message_data = new Gson().toJson(remoteMessage.getData());
//        Log.d(TAG, "Message Data: " + remote_message_data);

        //add the message to the cache
        InvitationModel invitationObject = new Gson().fromJson(remote_message_data, InvitationModel.class);
        Log.d(TAG, "handleNotification: Invitation Model received "+invitationObject.toString());
        if(invitationObject != null){
            invitationObject.setInvitationStatus(InvitationStatusConstants.NO_ACTION); //new invitation - no_action status
            addNewInvitationUseCase.execute(AddNewInvitationUseCase.Params.newInvitation(InvitationModelMapper.transformFromInvitation(invitationObject)));
        }
        buildNotificationForUser(context, invitationObject);
    }

    private void buildNotificationForUser(Context context, InvitationModel invitationModel) {
        //form a notification
        Log.d(TAG, "buildNotificationForUser");

        int notificationIdToUse = AppConfigHelper.getNotificationId();

        //this pending intent later could be more specific to a place inside the app
        Intent activityIntent = new Intent(context, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);

        String contentText = "You have been invited to join "+invitationModel.getGroupName();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.account_group_grey);


        Notification notification1 = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setSmallIcon(R.mipmap.ic_notification_96)
                .setContentTitle("Invitation")
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
                            .addLine("Invitation - " + " " + contentText)
                            .setBigContentTitle("1 new invitation")
                            .setSummaryText("1 new invitation"))
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
            inboxStyle.setBigContentTitle((numberOfOldMessages+1) + " new messages/invitations");
            Iterator<String> notificationStringIterator = olderNotifications.iterator();
            while(notificationStringIterator.hasNext()){
                NotificationsModel notificationsBean = new Gson().fromJson(notificationStringIterator.next(), NotificationsModel.class);
                inboxStyle.addLine(notificationsBean.getBigContentTitle());
            }
            //now add the current one
            inboxStyle.addLine("Invitations");
            inboxStyle.setSummaryText((numberOfOldMessages+1) + " new messages/invitations");
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
        AppConfigHelper.persistNotification(new NotificationsModel(notificationIdToUse, invitationModel.getGroupId(), "Invitation"));

        notificationManager.notify(0, summaryNotification); //summary notification always uses 0


    }


}
