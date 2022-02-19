package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.entities.User;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.UserDataDomainMapper;
import com.java.kaboome.data.persistence.MessageDao;
import com.java.kaboome.data.remote.responses.GroupMessagesResponse;
import com.java.kaboome.data.repositories.DataConversationsRepository;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.data.repositories.DataUserRepository;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.domain.usecases.GetConversationLastMessageCache;
import com.java.kaboome.domain.usecases.GetGroupConversationFromCacheSingleUseCase;
import com.java.kaboome.domain.usecases.GetLastOnlyGroupMessageInCacheSingleUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

/**
 * This method launches a worker or replaces if one already exists which gets all the new messages
 * for a particular user from the server (sync of messages between server and app).
 * This is called in a periodic request to run every 15 minutes
 *
 */
public class SyncMessagesFromServerWorker extends Worker {

    private static final String TAG = "KMSyncMessagesWorker";



    public SyncMessagesFromServerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //first get the groups from the cache
        //then go over each group one by one
        //get the messages sent after last message in cache for that group
        //then go over conversations of that group and do the same thing
        //that should be all

        UserGroupsListRepository userGroupsListRepository = DataUserGroupsListRepository.getInstance();
        List<DomainUserGroup> groups = userGroupsListRepository.getGroupsListOnlyFromCacheNonLive();

        final ThreadLocal<Long> lastAccessed = new ThreadLocal<Long>();
        if(groups == null || groups.size() <= 0){
            Log.d(TAG, "doWork: no groups passed");
            Data outPut = new Data.Builder()
                    .putString(WORK_RESULT,WORK_SUCCESS)
                    .putString(WORK_RESPONSE, "success")
                    .build();
            return Result.success(outPut);
        }



        for (final DomainUserGroup userGroup : groups) {
            //for every group, get a thread from the pool and start background work
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
                        getMessagesFromServer(groupId, userGroup.getCacheClearTS() != null? userGroup.getCacheClearTS() : (new Date()).getTime(), limit, scanDirection, "Group", lastAccessed);

                        //now get the admin messages if the user is a regular member
                        if(userGroup.getIsAdmin().equals("false")){
                            Log.d(TAG, "User is a regular member");
                            GetConversationLastMessageCache getConversationLastMessageCache = new GetConversationLastMessageCache(DataGroupMessagesRepository.getInstance());
                            final DomainMessage lastAdminMessage = getConversationLastMessageCache.execute(GetConversationLastMessageCache.Params.forGroupConversation(userGroup.getGroupId(), AppConfigHelper.getUserId(), true));
                            lastAccessed.set((lastAdminMessage != null && lastAdminMessage.getSentAt() != null)? lastAdminMessage.getSentAt() : userGroup.getAdminsLastAccessed() != null? userGroup.getAdminsLastAccessed() : (new Date()).getTime());
                            if(groupId.equals("0091d9e3-f4fe-4968-a043-ae4278079086")){
                                Log.d(TAG, "Last Access - "+lastAccessed.get());
                                Log.d(TAG, "Last Message sent at - "+(lastAdminMessage != null? lastAdminMessage.getSentAt(): 0));
                                Log.d(TAG, "User Group last accessed admin - "+userGroup.getAdminsLastAccessed());

                            }
                            getMessagesFromServer(groupId, userGroup.getAdminsCacheClearTS() != null? userGroup.getAdminsCacheClearTS() : (new Date()).getTime(), limit, scanDirection, AppConfigHelper.getUserId(), lastAccessed);
                        }
                        else{ //user is an admin, so now, get all the conversations and then their messages after last access one by one
                            //now get all the conversations and get messages for those conversations
                            //this is background thread already, so it should be good
                            Log.d(TAG, "User is an admin");
                            GetGroupConversationFromCacheSingleUseCase getGroupConversationFromCacheSingleUseCase = new GetGroupConversationFromCacheSingleUseCase(DataConversationsRepository.getInstance());
                            List<DomainUserGroupConversation> listOfConversations = getGroupConversationFromCacheSingleUseCase.execute(GetGroupConversationFromCacheSingleUseCase.Params.getConversationsForGroup(userGroup.getGroupId()));
                            if(listOfConversations != null && listOfConversations.size() > 0){
                                for(DomainUserGroupConversation userGroupConversation: listOfConversations){
                                    GetConversationLastMessageCache getConversationLastMessageCache = new GetConversationLastMessageCache(DataGroupMessagesRepository.getInstance());
                                    final DomainMessage lastConversationMessage = getConversationLastMessageCache.execute(GetConversationLastMessageCache.Params.forGroupConversation(userGroup.getGroupId(), userGroupConversation.getOtherUserId(), true));
                                    lastAccessed.set((lastConversationMessage != null && lastConversationMessage.getSentAt() != null)? lastConversationMessage.getSentAt() : userGroupConversation.getLastAccessed() != null? userGroupConversation.getLastAccessed() : (new Date()).getTime());
                                    getMessagesFromServer(groupId, userGroupConversation.getCacheClearTS() != null? userGroupConversation.getCacheClearTS() : (new Date()).getTime(), limit, scanDirection, userGroupConversation.getOtherUserId(), lastAccessed);
                                }
                            }

                        }


                    }

                    ;
                });

        }

        Data outPut = new Data.Builder()
                .putString(WORK_RESULT,WORK_SUCCESS)
                .putString(WORK_RESPONSE, "success")
                .build();
        return Result.success(outPut);

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
            Log.d(TAG, "insertRetrievedMessagesInCache: messages number - "+messagesList.size());
            Message[] messagesArray = new Message[messagesList.size()];
            MessageDao messageDao = AppConfigHelper.getKabooMeDatabaseInstance().getMessageDao();
            messageDao.insertMessages((Message[]) (messagesList.toArray(messagesArray)));
        }
    }

    private void getMessagesFromServer(final String groupId, final Long cacheClearTS, final int limit, final String scanDirection, final String sentTo, final ThreadLocal<Long> lastAccessed){

        //no sense running this function if network is off
        if(!NetworkHelper.isOnline()){
            return;
        }
        Log.d(TAG, "Last Accessed time for group before - " + groupId +" - "+sentTo+ " is - " + lastAccessed.get());
        Log.d(TAG, "getMessagesFromServer: current thread name - "+Thread.currentThread().getName());
        Call<GroupMessagesResponse> groupMessagesResponseCall = AppConfigHelper.getBackendApiServiceProvider().getMessagesForGroupInBackground(AppConfigHelper.getUserId(), groupId, lastAccessed.get(),cacheClearTS, limit, scanDirection, sentTo);

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
                getMessagesFromServer(groupId, cacheClearTS, limit, scanDirection, sentTo, lastAccessed);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
