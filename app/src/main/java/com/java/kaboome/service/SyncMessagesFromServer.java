package com.java.kaboome.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.persistence.MessageDao;
import com.java.kaboome.data.remote.responses.GroupMessagesResponse;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.usecases.GetLastOnlyGroupMessageInCacheSingleUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;


import retrofit2.Call;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SyncMessagesFromServer extends IntentService {

    private static final String TAG = "KMSyncMessagesFrmSrvr";
    private boolean continueBackgroundWork = true;
    private List<DomainUserGroup> groups;
    private ThreadLocal<Long> lastAccessed = new ThreadLocal<Long>();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SyncMessagesFromServer() {
        super("SyncMessagesFromServer");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        continueBackgroundWork = true;
        groups = (List<DomainUserGroup>) intent.getSerializableExtra("groups");
        //this pending intent later could be more specific to a place inside the app
//        Intent activityIntent = new Intent(this, HomeActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this,
//                0, activityIntent, 0);
//
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        Notification notification = new NotificationCompat.Builder(this, AppConfigHelper.CHANNEL_ID)
//                .setContentTitle("Sync Data From Server")
//                .setContentText("Running...")
//                .setContentIntent(contentIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .build();

//        startForeground(1, notification);


        doWork();

        return START_NOT_STICKY;
    }

    private void doWork() {

        Log.d(TAG, "onHandleIntent thread is " + Thread.currentThread().getName());

        if(groups == null){
            Log.d(TAG, "doWork: no groups passed");
            return;
        }

        for (final DomainUserGroup userGroup : groups) {
            //for every group, get a thread from the pool and start background work
            if (continueBackgroundWork) {
                if (userGroup.getDeleted()) {
                    continue;
                }
                AppExecutors2.getInstance().getServiceDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        GetLastOnlyGroupMessageInCacheSingleUseCase getLastOnlyGroupMessageInCacheSingleUseCase = new GetLastOnlyGroupMessageInCacheSingleUseCase(DataGroupMessagesRepository.getInstance());
                        final DomainMessage lastMessage = getLastOnlyGroupMessageInCacheSingleUseCase.execute(GetLastOnlyGroupMessageInCacheSingleUseCase.Params.forGroup(userGroup.getGroupId(), true));


                        String groupId = userGroup.getGroupId();
                        String scanDirection = "forward";
                        int limit = 10;
//                        lastAccessed.set(lastMessage != null ? lastMessage.getSentAt() : (new Date()).getTime());
                        //The idea behind the next line is -
                        //if there are messages in cache, then take the last one from there, take it's sent time
                        //as the last message TS and get the rest of the messages on a background thread
                        //but if the cache has been cleared, in that case, the last accessed message in the user group
                        //is the last message that the user had seen and the TS of that was stored with the UserGroup in
                        //the server. So, we use that time stamp and get all the messages sent after that time.
                        //If even the UserGroup lastAccessedTS is null - this happens in the case of joining a public group
                        //in that case, take the current time as the lastAccessed
                        //Test set -
                        //1. Delete cache, login again and check what messages come back when
                        //there are no new messages sent in the absence
                        //there are messages sent in the absence
                        lastAccessed.set((lastMessage != null && lastMessage.getSentAt() != null)? lastMessage.getSentAt() : userGroup.getLastAccessed() != null? userGroup.getLastAccessed() : (new Date()).getTime());

                        //In couple of cases, the cacheClearTS could be null
                        //1. JoinGroupDialog - join the group and then come to the main screen, no server access is made, so for the
                        //newly added group, the last access and cacheClearTS is null
                        //2. The user cleared the data of the app.
                        getMessagesFromServer(groupId, userGroup.getCacheClearTS() != null? userGroup.getCacheClearTS() : (new Date()).getTime(), limit, scanDirection);


                    }

                    ;
                });
            }
        }

//        AppExecutors2.getInstance().getServiceDiskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    if(!continueBackgroundWork){
//                        return;
//                    }
//                    Log.d(TAG, "Sleeping - " + i +" in thread "+Thread.currentThread().getName());
//                    SystemClock.sleep(2000);
//                }
//
////                //all work done
////                if(continueBackgroundWork){
////                    stopForeground(true);
////                    stopSelf();
////                    continueBackgroundWork = false;
////                }
//            }
//
//
//        });



    }

    private long checkForRemainingMessages(String groupId, GroupMessagesResponse groupMessagesResponse, int limit) {
        List<Message> messagesList = groupMessagesResponse.getMessages();
        if(messagesList != null && messagesList.size() > 0 && messagesList.size() >= limit){
            return (messagesList.get(messagesList.size() - 1)).getSentAt();
        }
        return -1;
    }

    private void insertRetrievedMessagesInCache(GroupMessagesResponse groupMessagesResponse){
        List<Message> messagesList = groupMessagesResponse.getMessages();
        if(messagesList != null && messagesList.size() > 0){
            Message[] messagesArray = new Message[messagesList.size()];
            MessageDao messageDao = AppConfigHelper.getKabooMeDatabaseInstance().getMessageDao();
            messageDao.insertMessages((Message[]) (messagesList.toArray(messagesArray)));
        }
    }

    private void getMessagesFromServer(final String groupId, final Long cacheClearTS, final int limit, final String scanDirection){

        //no sense running this function if network is off
        if(!NetworkHelper.isOnline()){
            return;
        }
        Log.d(TAG, "Last Accessed time for group before - " + groupId + " is - " + lastAccessed.get());
        Log.d(TAG, "getMessagesFromServer: current thread name - "+Thread.currentThread().getName());
        Call<GroupMessagesResponse> groupMessagesResponseCall = AppConfigHelper.getBackendApiServiceProvider().getMessagesForGroupInBackground(AppConfigHelper.getUserId(), groupId, lastAccessed.get(),cacheClearTS, limit, scanDirection, "Group");

        try {
            //synchronous call
            GroupMessagesResponse groupMessagesResponse = groupMessagesResponseCall.execute().body();
            if(groupMessagesResponse == null || groupMessagesResponse.getMessages() == null){
                return;
            }
            //1. insert messages in the cache
            //here check if the number of messages is 30, then go back for more, there might be more
            //else do nothing, we are good
            //2. checkForRemainingMessages returns -1 if the number of messages are less than the limit
            Log.d(TAG, "onResponse: current thread name - "+Thread.currentThread().getName());
            Log.d(TAG, "Messages received for Group "+groupId+" are "+groupMessagesResponse.getMessages().size());
            insertRetrievedMessagesInCache(groupMessagesResponse);
            lastAccessed.set(checkForRemainingMessages(groupId, groupMessagesResponse, limit));
            Log.d(TAG, "Last Accessed time for group after - " + groupId + " is - " + lastAccessed.get());
            if(lastAccessed.get() != -1){ //call it in a loop
                getMessagesFromServer(groupId, cacheClearTS, limit, scanDirection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         * below is async call, the reason trying to move to sync call (execute not enqueue) (look up - sync call) is
         * that by default retrofit async call returns on the main thread, not on the thread calling the call
         * then inserting in to DB was throwing exception because it was happening on the main thread
         * Hence, sync call, so we never leave the background thread
         */
//        AppConfigHelper.getBackendApiServiceProvider().getMessagesForGroupInBackground(AppConfigHelper.getUserId(), groupId, lastAccessed.get(), limit, scanDirection).enqueue(new Callback<GroupMessagesResponse>() {
//            @Override
//            public void onResponse(Call<GroupMessagesResponse> call, Response<GroupMessagesResponse> response) {
//                GroupMessagesResponse groupMessagesResponse = response.body();
//                //1. insert messages in the cache
//                //here check if the number of messages is 30, then go back for more, there might be more
//                //else do nothing, we are good
//                //2. checkForRemainingMessages returns -1 if the number of messages are less than the limit
//                Log.d(TAG, "onResponse: current thread name - "+Thread.currentThread().getName());
//                insertRetrievedMessagesInCache(groupMessagesResponse);
//                lastAccessed.set(checkForRemainingMessages(groupId, groupMessagesResponse, limit));
//                Log.d(TAG, "Last Accessed time for group after - " + groupId + " is - " + lastAccessed.get());
//                if(lastAccessed.get() != -1){ //call it in a loop
//                    getMessagesFromServer(groupId, limit, scanDirection);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GroupMessagesResponse> call, Throwable t) {
//                t.printStackTrace();
//                Log.d(TAG, "run: Exception - " + t.getMessage());
//            }
//        });

    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        continueBackgroundWork = false;
        stopForeground(true);
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        doWork();
    }

}


