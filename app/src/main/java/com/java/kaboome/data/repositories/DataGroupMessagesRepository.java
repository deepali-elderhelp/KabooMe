package com.java.kaboome.data.repositories;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;
import android.util.Log;

import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.MessageDataDomainMapper;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.persistence.MessageDao;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.DeleteMessageResponse;
import com.java.kaboome.data.remote.responses.GroupMessagesResponse;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * This class gets the messages from the server and also updates the cache
 */
public class DataGroupMessagesRepository implements MessagesListRepository {

    private static final String TAG = "KMDataGroupMessagesRepo";

    private static DataGroupMessagesRepository instance;
    private MessageDao messageDao;
//    private MutableLiveData<Integer> noOfMessagesReturned;

    private DataGroupMessagesRepository() {
        messageDao = AppConfigHelper.getKabooMeDatabaseInstance().getMessageDao();
    }

    public static DataGroupMessagesRepository getInstance(){
        if(instance == null){
            instance = new DataGroupMessagesRepository();
        }
        return  instance;
    }

    @Override
    public LiveData<DomainResource<List<DomainMessage>>> getGroupMessages(String groupId, Long lastAccessed, Long cacheClearTS, int limit, String scanDirection, MessageGroupsConstants messageGroupsConstants, String userId) {
        if(messageGroupsConstants == null || messageGroupsConstants == MessageGroupsConstants.GROUP_MESSAGES){
            return getGroupMessages(groupId, lastAccessed, cacheClearTS, limit, scanDirection, MessageGroupsConstants.GROUP_MESSAGES.getStatus());
        }
        else if(messageGroupsConstants == MessageGroupsConstants.ADMIN_MESSAGES){
            return getGroupMessages(groupId, lastAccessed, cacheClearTS, limit, scanDirection, MessageGroupsConstants.ADMIN_MESSAGES.getStatus());
        }
        else{
            return getGroupMessages(groupId, lastAccessed, cacheClearTS, limit, scanDirection, userId);
        }
    }



    private LiveData<DomainResource<List<DomainMessage>>> getGroupMessages(String groupId, Long lastAccessed, Long cacheClearTS, int limit, String scanDirection, String sentTo) {

        
        return Transformations.map(getNewestGroupMessages(groupId, lastAccessed, cacheClearTS, limit, scanDirection, sentTo), new Function<Resource<GroupMessagesResponse>, DomainResource<List<DomainMessage>>>() {
            @Override
            public DomainResource<List<DomainMessage>> apply(Resource<GroupMessagesResponse> input) {
                Log.d(TAG, "apply: response is here");
                return ResourceDomainResourceMapper.transform(input.status, MessageDataDomainMapper.transformFromMessage(input.data), input.message);
            }
        });

    }

//    @Override
//    public LiveData<List<DomainMessage>> getGroupMessagesSentAfterLastAccess(final String groupId, Long lastAccessedTime) {
//        //just getting the data from cache and sending
//        //updating from the server happens either on a service task or when user goes to message window
////        Long groupLastMessageTime = AppConfigHelper.getGroupLastSeenMsgTS(groupId);
//        return Transformations.map(messageDao.getGroupMessagesSentAfter(groupId, lastAccessedTime), new Function<List<Message>, List<DomainMessage>>() {
//            @Override
//            public List<DomainMessage> apply(List<Message> input) {
//                Log.d(TAG, "For group - "+groupId+" number of unread - "+input.size());
//                return MessageDataDomainMapper.transformFromMessage(input);
//            }
//        });
//    }


//    @Override
//    public LiveData<List<DomainMessage>> getConversationMessagesSentAfterLastAccess(final String groupId, final String userId, Long lastAccessedTime) {
//        //just getting the data from cache and sending
//        //updating from the server happens either on a service task or when user goes to message window
////        Long groupLastMessageTime = AppConfigHelper.getGroupLastSeenMsgTS(groupId);
//        return Transformations.map(messageDao.getConversationMessagesSentAfter(groupId, userId, lastAccessedTime), new Function<List<Message>, List<DomainMessage>>() {
//            @Override
//            public List<DomainMessage> apply(List<Message> input) {
//                Log.d(TAG, "For group - "+groupId+"and user Id "+userId+" number of unread - "+input.size());
//                return MessageDataDomainMapper.transformFromMessage(input);
//            }
//        });
//    }


//    /**
//     * This should be called on a background thread - this is user's responsibility to
//     * call this method on a background thread, otherwise a runtime error is thrown.
//     * @param groupId
//     * @param lastAccessedTime
//     * @return List<DomainMessage>
//     */
//    @Override
//    public List<DomainMessage> getGroupMessagesSentAfterLastAccessCache(final String groupId, Long lastAccessedTime) {
//        return MessageDataDomainMapper.transformFromMessage(messageDao.getGroupMessagesSentAfterCache(groupId, lastAccessedTime));
//    }

//    /**
//     * This should be called on a background thread - this is user's responsibility to
//     * call this method on a background thread, otherwise a runtime error is thrown.
//     * @param groupId
//     * @param lastAccessedTime
//     * @return List<DomainMessage>
//     */
//    @Override
//    public List<DomainMessage> getConvMessagesSentAfterLastAccessCache(String groupId, String userId, Long lastAccessedTime) {
//        return MessageDataDomainMapper.transformFromMessage(messageDao.getConversationMessagesSentAfterCache(groupId, userId, lastAccessedTime));
//    }

    @Override
    public void addNewMessage(DomainMessage message) {

        //if this message was sent by the current user, then unread can be set to 1
        if(message.getSentBy().equals(AppConfigHelper.getUserId())){
            message.setUnread(1);
        }

        if(message.isUploadedToServer()){
            //message is coming from server, needs to be updated in the cache
            insertRemoteMessageIntoCache(MessageDataDomainMapper.transformFromDomain(message));
        }
        else{
            //message has not been updated to the server yet
            insertLocalMessageIntoCache(MessageDataDomainMapper.transformFromDomain(message));
        }


    }

//    @Override
//    public void deleteMessage(DomainMessage domainMessage) {
//
//        final Message message = MessageDataDomainMapper.transformFromDomain(domainMessage);
//
//        if(message.isUploadedToServer()){
//            deleteMessage(message);
//        }
//        else{
//            //the message was not yet uploaded to the server
//            //just delete it from local cache
//            //needs to be done in a background thread
//            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//                @Override
//                public void run() {
//                    messageDao.delete(message);
//                    Log.d(TAG, "run: Message deleted from cache "+message.getMessageId());
//                }
//            });
//
//        }
//
//    }

    @Override
    public void clearMessagesOfGroup(final String groupId) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messageDao.clearMessagesOfGroup(groupId);
                    Log.d(TAG, "run: Messages of the group "+groupId+" are deleted");
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in clearMessagesOfGroup "+exception.getMessage());
                }
            }
        });
    }

    @Override
    public void clearMessagesOfConversation(final String groupId, final String otherUserId) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messageDao.clearMessagesOfGroupConversation(groupId, otherUserId);
                    Log.d(TAG, "run: Messages of the group "+groupId+" are deleted");
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in clearMessagesOfConversation "+exception.getMessage());
                }
            }
        });
    }


//    @Override
//    public void updateGroupsLastMessageSeen(final String groupId) {
//        //needs to be done in a background thread
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                Message lastMessage = messageDao.getLastMessageForGroup(groupId);
//                if(lastMessage != null){ //it could be null if there is no message in the group messages cache
////                    AppConfigHelper.setGroupLastSeenMsgTS(groupId, lastMessage.getSentAt());
////                    messageDao.insertMessage(lastMessage); //just adding for testing something
//
//                    //now call the worker to update the same info onto the server.
////                    WorkerBuilderHelper.callUpdateLastSeenTSWorker(groupId, lastMessage.getSentAt());
//                }
//
//            }
//        });
//    }



//    /**
//     * This should be called on a background thread - this is user's responsibility to
//     * call this method on a background thread, otherwise a runtime error is thrown.
//     * @param groupId
//     * @param sentTo
//     * @return DomainMessage
//     */
//    @Override
//    public DomainMessage getLatestGroupMessageInCache(String groupId, String sentTo) {
//        Message lastMessage = messageDao.getLastGroupMessageForGroup(groupId);
//        if(lastMessage == null) //there are no messages in the group at all
//            return null;
//        return MessageDataDomainMapper.transformFromMessage(lastMessage);
//    }

//    /**
//     * This should be called on a background thread - this is user's responsibility to
//     * call this method on a background thread, otherwise a runtime error is thrown.
//     * @param groupId
//     * @return DomainMessage
//     */
//    @Override
//    public DomainMessage getLatestWholeGroupMessageInCache(String groupId) {
//
//        Message lastMessage = messageDao.getLastMessageForWholeGroup(groupId);
//        if(lastMessage == null) //there are no messages in the group at all
//            return null;
//        return MessageDataDomainMapper.transformFromMessage(lastMessage);
//    }


//    /**
//     * This should be called on a background thread - this is user's responsibility to
//     * call this method on a background thread, otherwise a runtime error is thrown.
//     * @param groupId
//     * @return DomainMessage
//     */
//    @Override
//    public DomainMessage getLatestMessageInCache(String groupId, MessageGroupsConstants messageGroupsConstants, String userId) {
//        Message lastMessage = null;
//        if(messageGroupsConstants == MessageGroupsConstants.GROUP_MESSAGES){
////            lastMessage = messageDao.getLastGroupMessageForGroup(groupId);
//            lastMessage = messageDao.getLastMessageForOnlyGroup(groupId);
//        }
//        if(messageGroupsConstants == MessageGroupsConstants.ADMIN_MESSAGES){
//            lastMessage = messageDao.getLastInterAdminMessageForGroup(groupId);
//        }
//        if(messageGroupsConstants == MessageGroupsConstants.USER_ADMIN_MESSAGES){
////            lastMessage = messageDao.getLastUserAdminMessageForGroup(groupId, userId);
//            lastMessage = messageDao.getLastMessageForConv(groupId, userId);
//        }
//
//        if(lastMessage == null) //there are no messages in the group at all
//            return null;
//
//        return MessageDataDomainMapper.transformFromMessage(lastMessage);
//    }

//    @Override
//    public LiveData<DomainMessage> getLatestWholeGroupMessageInCacheAsLiveData(String groupId) {
//
//        return Transformations.map(messageDao.getLastMessageForWholeGroupAsLiveData(groupId), new Function<Message, DomainMessage>() {
//            @Override
//            public DomainMessage apply(Message input) {
////                Log.d(TAG, "Coming in the map now");
//                if(input == null){
////                    DomainMessage domainMessage = new DomainMessage();
////                    domainMessage.setGroupId("loading");
////                    return domainMessage;
//                    return null;
//                }
//
//                return MessageDataDomainMapper.transformFromMessage(input);
//            }
//        });
//
//    }

//    @Override
//    public LiveData<DomainMessage> getLatestConvMessageInCacheAsLiveData(String groupId, String userId) {
//        return Transformations.map(messageDao.getLastMessageForConvAsLiveData(groupId, userId), new Function<Message, DomainMessage>() {
//            @Override
//            public DomainMessage apply(Message input) {
////                Log.d(TAG, "Coming in the map now");
//                if(input == null){
////                    DomainMessage domainMessage = new DomainMessage();
////                    domainMessage.setGroupId("loading");
////                    return domainMessage;
//                    return null;
//                }
//
//                return MessageDataDomainMapper.transformFromMessage(input);
//            }
//        });
//    }

    @Override
    public void updateMessageAttachmentDetails(final String messageId, final Boolean hasAttachment, final Boolean attachmentUploaded, final Boolean attachmentLoadingGoingOn, final String mimeType, final String attachmentUri) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messageDao.updateMessageAttachmentData(messageId, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, mimeType, attachmentUri);
                    Log.d(TAG, "run: message updated for attachments");
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in updateMessageAttachmentDetails "+exception.getMessage());
                }
            }
        });
    }

    @Override
    public void updateMessageLoadingProgress(final String messageId, final int progress) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messageDao.updateMessageLoadingProgress(messageId, progress);
                    Log.d(TAG, "run: message attachment loading progress updated progress - "+progress);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in updateMessageLoadingProgress "+exception.getMessage());
                }
            }
        });
    }

    @Override
    public void deleteLocalMessage(final DomainMessage domainMessage) {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                messageDao.delete(MessageDataDomainMapper.transformFromDomain(domainMessage));
//                Log.d(TAG, "run: message deleted from cache");
//            }
//        });
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messageDao.setLocalMessageToDelete("Message deleted for you", domainMessage.getGroupId(), domainMessage.getSentTo(), domainMessage.getMessageId());
                    Log.d(TAG, "run: set local message to deleted");
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in deleteLocalMessage "+exception.getMessage());
                }
            }
        });
    }


    private void insertRemoteMessageIntoCache(final Message message){

        //needs to be done in a background thread
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Message existingMessage = messageDao.getMessage(message.getMessageId());
                    if(existingMessage != null){
                        message.setUnread(existingMessage.getUnread());
                        message.setAttachmentUri(existingMessage.getAttachmentUri());
                    }
                    messageDao.insertMessage(message);
                    Log.d(TAG, "run: Message inserted in cache "+message.getMessageId());
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in insertRemoteMessageIntoCache "+exception.getMessage());
                }
            }
        });
    }

    private void insertLocalMessageIntoCache(final Message message){
        //only update if the server one does not exist yet
        //needs to be done in a background thread
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messageDao.insertMessageIfDoesNotExist(message);
                    Log.d(TAG, "run: Message inserted in cache "+message.getMessageId());
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in insertLocalMessageIntoCache "+exception.getMessage());
                }
            }
        });
    }


    private LiveData<Resource<GroupMessagesResponse>> getNewestGroupMessages(final String groupId, final Long lastAccessed, final Long cacheClearTS, final int limit, final String scanDirection, final String sentTo) {
        return new MessageNetworkBoundResource<GroupMessagesResponse>(AppExecutors2.getInstance()){

            @Override
            protected void saveCallResult(@NonNull GroupMessagesResponse item) {
                Log.d(TAG, "saveCallResult");
                if (item.getMessages() != null) {

                    //doing the following so that replacing on conflict keeps this local cache data intact
                    //this data is not sent on the server
                    Message[] messages = new Message[item.getMessages().size()];
                    for(Message message: item.getMessages()){
                        Log.d(TAG, "saveCallResult: message Id "+message.getMessageId()+"  text -  "+message.getMessageText());
                        message.setUploadedToServer(true);
                        Message messageFromCache = messageDao.getMessage(message.getMessageId());
                        if(messageFromCache != null){
//                            if(messageFromCache.getDeleted()){
//                                message.setDeleted(true);
//                            }
                            if(messageFromCache.getDeletedLocally() != null && messageFromCache.getDeletedLocally()){
                                message.setDeletedLocally(true);
                            }
                            message.setUnread(messageFromCache.getUnread());
                            message.setAttachmentUri(messageFromCache.getAttachmentUri());
                        }
//                        if(message.getHasAttachment() != null && message.getHasAttachment() && message.getAttachmentUploaded()){ //message attachment has been completely uploaded
//                            Message messageFromCache = messageDao.getMessage(message.getMessageId());
//                            if(messageFromCache != null && messageFromCache.getAttachmentDownloaded() != null && messageFromCache.getAttachmentDownloaded()){ // message was downloaded before
//                                message.setAttachmentDownloaded(true);
//                                message.setAttachmentPath(messageFromCache.getAttachmentURI());
//                            }
//                        }
                    }


                    int index = 0;
                    for (long rowid : messageDao.insertMessages((Message[]) (item.getMessages().toArray(messages)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }

//                    //testing - check what is in cache here -
//                    List<Message> messagesFromCache = messageDao.getAllMessagesForGroup(groupId);
//                    for(Message message: messagesFromCache){
//                        Log.d(TAG, "cache: message Id "+message.getMessageId()+"  text -  "+message.getMessageText());
//                    }


                }
            }

            @Override
            protected boolean shouldFetch() {
                Log.d(TAG, "shouldFetch: ");
                if(NetworkHelper.isOnline())
                    return true;
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupMessagesResponse>> createCall() {
                Log.d(TAG, "createCall: ");
                return AppConfigHelper.getBackendApiServiceProvider().getMessagesForGroup(AppConfigHelper.getUserId(), groupId, lastAccessed, cacheClearTS, limit, scanDirection, sentTo);
            }
        }.getAsLiveData();

    }


//    public void deleteMessage(final Message message) {
//        //delete from cache and server both by doing the following
//        //set waitingToBeDeleted true on the cached entry of this message
//        //call backend api to delete the message from server
//        //if it is successful -update the cache with the updated message, set waiting to be deleted false
//        //if it is errored, display error toast to the user, set waiting to be deleted false
//
//        message.setWaitingToBeDeleted(true);
//
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                messageDao.updateMessageWaitingToBeDeletedStatus(message.getMessageId(), true);
//                Log.d(TAG, "run: Message deleted from cache "+message.getMessageId());
//            }
//        });
//        //backend call to delete the message
//        AppConfigHelper.getBackendApiServiceProvider().deleteMessage(AppConfigHelper.getUserId(), message.getGroupId(), message.getMessageId()).enqueue(new Callback<DeleteMessageResponse>() {
//            @Override
//            public void onResponse(Call<DeleteMessageResponse> call, Response<DeleteMessageResponse> response) {
//                //update the local cache as well
//                final Message newMessage = response.body().getMessage();
//                newMessage.setWaitingToBeDeleted(false);
//                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        messageDao.insertMessage(newMessage);
//                    }
//                });
//
//            }
//
//            @Override
//            public void onFailure(Call<DeleteMessageResponse> call, final Throwable t) {
//                //set waiting to be deleted false
//                message.setWaitingToBeDeleted(false);
//                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        messageDao.insertMessage(message);
//                        Log.d(TAG, "Could not delete message due to - "+t.getMessage());
//                        //deal with broadcasting error later
//                    }
//                });
//
//            }
//        });
//    }

    @Override
    public void updateMessagesToRead(final String groupId, final String sentTo) {
        //whatever the sentTo is, you set all the messages of that groupId and sentTo to read
        //so, that means set the unread to 0 for all of them
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messageDao.updateMessagesToRead(groupId, sentTo);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in updateMessagesToRead "+exception.getMessage());
                }
            }
        });
    }
//
//    @Override
//    public LiveData<List<DomainMessage>> getNetUnreadGroupMessages(final String groupId) {
//        return Transformations.map(messageDao.getGroupNetUnreadMessagesLiveData(groupId), new Function<List<Message>, List<DomainMessage>>() {
//            @Override
//            public List<DomainMessage> apply(List<Message> input) {
//                Log.d(TAG, "For group - "+groupId+" number of unread - "+input.size());
//                return MessageDataDomainMapper.transformFromMessage(input);
//            }
//        });
//    }

//    /**
//     * /**
//     *      * This should be called on a background thread - this is user's responsibility to
//     *      * call this method on a background thread, otherwise a runtime error is thrown.
//     * @param groupId
//     * @return
//     */
//    @Override
//    public List<DomainMessage> getNetUnreadGroupMessagesCache(String groupId) {
//        return MessageDataDomainMapper.transformFromMessage(messageDao.getGroupNetUnreadMessages(groupId));
//    }

//    @Override
//    public LiveData<List<DomainMessage>> getNetUnreadGroupConversationMessages(String groupId, String sentTo) {
//        return Transformations.map(messageDao.getGroupConversationNetUnreadMessagesLiveData(groupId, sentTo), new Function<List<Message>, List<DomainMessage>>() {
//            @Override
//            public List<DomainMessage> apply(List<Message> input) {
//                return MessageDataDomainMapper.transformFromMessage(input);
//            }
//        });
//    }



//    @Override
//    public LiveData<List<DomainMessage>> getOnlyGroupUnreadMessages(String groupId) {
//        return Transformations.map(messageDao.getOnlyGroupUnreadMessagesLiveData(groupId), new Function<List<Message>, List<DomainMessage>>() {
//            @Override
//            public List<DomainMessage> apply(List<Message> input) {
//                return MessageDataDomainMapper.transformFromMessage(input);
//            }
//        });
//    }

//    /**
//     * /**
//     *      * This should be called on a background thread - this is user's responsibility to
//     *      * call this method on a background thread, otherwise a runtime error is thrown.
//     * @param groupId
//     * @return
//     */
//    @Override
//    public List<DomainMessage> getNetUnreadGroupConversationMessagesCache(String groupId, String sentTo) {
//        return MessageDataDomainMapper.transformFromMessage(messageDao.getGroupConversationNetUnreadMessages(groupId, sentTo));
//    }

    //--------------------------------------------------

    @Override
    public DomainMessage getMessage(String messageId) {
        if(messageId == null) {
            return null;
        }
        return MessageDataDomainMapper.transformFromMessage(messageDao.getMessage(messageId));
    }

    @Override
    public LiveData<DomainMessage> getLastMessageForWholeGroupFromCacheLiveData(String groupId) {
        return Transformations.map(messageDao.getLastMessageForWholeGroupAsLiveData(groupId), new Function<Message, DomainMessage>() {
            @Override
            public DomainMessage apply(Message input) {
                if(input == null){
                    return null;
                }
                return MessageDataDomainMapper.transformFromMessage(input);
            }
        });

    }

    @Override
    public LiveData<DomainMessage> getLastMessageForOnlyGroupFromCacheLiveData(String groupId) {
        return Transformations.map(messageDao.getLastMessageForOnlyGroupAsLiveData(groupId), new Function<Message, DomainMessage>() {
            @Override
            public DomainMessage apply(Message input) {
                if(input == null){
                    return null;
                }
                return MessageDataDomainMapper.transformFromMessage(input);
            }
        });
    }

    @Override
    public LiveData<DomainMessage> getLastMessageForAllConvsFromCacheLiveData(String groupId) {
        return Transformations.map(messageDao.getLastMessageForAllConvsAsLiveData(groupId), new Function<Message, DomainMessage>() {
            @Override
            public DomainMessage apply(Message input) {
                if(input == null){
                    return null;
                }
                return MessageDataDomainMapper.transformFromMessage(input);
            }
        });
    }

    @Override
    public LiveData<DomainMessage> getLastMessageForConvFromCacheLiveData(String groupId, String userId) {
        return Transformations.map(messageDao.getLastMessageForConvAsLiveData(groupId, userId), new Function<Message, DomainMessage>() {
            @Override
            public DomainMessage apply(Message input) {
                if(input == null){
                    return null;
                }
                return MessageDataDomainMapper.transformFromMessage(input);
            }
        });
    }

    /**
     * /**
     *      * This should be called on a background thread - this is user's responsibility to
     *      * call this method on a background thread, otherwise a runtime error is thrown.
     * @param groupId
     * @return
     */
    @Override
    public DomainMessage getLastMessageForWholeGroupFromCacheSingle(String groupId, boolean includeDeleted) {
        if(includeDeleted){
            Message lastMessage = messageDao.getLastMessageForWholeGroupIncludingDeleted(groupId);
            if(lastMessage == null) //there are no messages in the group at all
                return null;
            return MessageDataDomainMapper.transformFromMessage(lastMessage);
        }
        else {
            Message lastMessage = messageDao.getLastMessageForWholeGroup(groupId);
            if (lastMessage == null) //there are no messages in the group at all
                return null;
            return MessageDataDomainMapper.transformFromMessage(lastMessage);
        }
    }

    /**
     *      * This should be called on a background thread - this is user's responsibility to
     *      * call this method on a background thread, otherwise a runtime error is thrown.
     * @param groupId
     * @return
     */
    @Override
    public DomainMessage getLastMessageForOnlyGroupFromCacheSingle(String groupId, boolean includeDeleted) {
        if(includeDeleted){
            Message lastMessage = messageDao.getLastMessageForOnlyGroupIncludingDeleted(groupId);
            if(lastMessage == null) //there are no messages in the group at all
                return null;
            return MessageDataDomainMapper.transformFromMessage(lastMessage);
        }
        else {
            Message lastMessage = messageDao.getLastMessageForOnlyGroup(groupId);
            if (lastMessage == null) //there are no messages in the group at all
                return null;
            return MessageDataDomainMapper.transformFromMessage(lastMessage);
        }
    }

    @Override
    public DomainMessage getLastMessageForAllConvsFromCacheSingle(String groupId, boolean includeDeleted) {
        if(includeDeleted){
            Message lastMessage = messageDao.getLastMessageForAllConvsIncludingDeleted(groupId);
            if(lastMessage == null) //there are no messages in the group at all
                return null;
            return MessageDataDomainMapper.transformFromMessage(lastMessage);
        }
        else {
            Message lastMessage = messageDao.getLastMessageForAllConvs(groupId);
            if (lastMessage == null) //there are no messages in the group at all
                return null;
            return MessageDataDomainMapper.transformFromMessage(lastMessage);
        }
    }

    /**
     *      * This should be called on a background thread - this is user's responsibility to
     *      * call this method on a background thread, otherwise a runtime error is thrown.
     * @param groupId
     * @return
     */
    @Override
    public DomainMessage getLastMessageForConvFromCacheSingle(String groupId, String userId, boolean includeDeleted) {
        if(includeDeleted){
            Message lastMessage = messageDao.getLastMessageForConvIncludingDeleted(groupId, userId);
            if (lastMessage == null) //there are no messages in the group at all
                return null;
            return MessageDataDomainMapper.transformFromMessage(lastMessage);
        }
        else {
            Message lastMessage = messageDao.getLastMessageForConv(groupId, userId);
            if (lastMessage == null) //there are no messages in the group at all
                return null;
            return MessageDataDomainMapper.transformFromMessage(lastMessage);
        }
    }

    @Override
    public LiveData<List<DomainMessage>> getUnreadMessagesForWholeGroupFromCacheLiveData(String groupId) {
        return Transformations.map(messageDao.getWholeGroupNetUnreadMessagesLiveData(groupId), new Function<List<Message>, List<DomainMessage>>() {
            @Override
            public List<DomainMessage> apply(List<Message> input) {
                return MessageDataDomainMapper.transformFromMessage(input);
            }
        });
    }

    @Override
    public LiveData<List<DomainMessage>> getUnreadMessagesForOnlyGroupFromCacheLiveData(String groupId) {
        return Transformations.map(messageDao.getOnlyGroupNetUnreadMessagesLiveData(groupId), new Function<List<Message>, List<DomainMessage>>() {
            @Override
            public List<DomainMessage> apply(List<Message> input) {
                return MessageDataDomainMapper.transformFromMessage(input);
            }
        });
    }

    @Override
    public LiveData<List<DomainMessage>> getUnreadMessagesForConvFromCacheLiveData(String groupId, String userId) {
        return Transformations.map(messageDao.getGroupConversationNetUnreadMessagesLiveData(groupId, userId), new Function<List<Message>, List<DomainMessage>>() {
            @Override
            public List<DomainMessage> apply(List<Message> input) {
                return MessageDataDomainMapper.transformFromMessage(input);
            }
        });
    }

    /**
     * /**
     *      * This should be called on a background thread - this is user's responsibility to
     *      * call this method on a background thread, otherwise a runtime error is thrown.
     * @param groupId
     * @return
     */
    @Override
    public List<DomainMessage> getUnreadMessagesForWholeGroupFromCacheSingle(String groupId) {
        return MessageDataDomainMapper.transformFromMessage(messageDao.getWholeGroupNetUnreadMessages(groupId));
    }

    /**
     * /**
     *      * This should be called on a background thread - this is user's responsibility to
     *      * call this method on a background thread, otherwise a runtime error is thrown.
     * @param groupId
     * @return
     */
    @Override
    public List<DomainMessage> getUnreadMessagesForOnlyGroupFromCacheSingle(String groupId) {
        return MessageDataDomainMapper.transformFromMessage(messageDao.getOnlyGroupNetUnreadMessages(groupId));
    }

    /**
     * /**
     *      * This should be called on a background thread - this is user's responsibility to
     *      * call this method on a background thread, otherwise a runtime error is thrown.
     * @param groupId
     * @param userId
     * @return
     */
    @Override
    public List<DomainMessage> getUnreadMessagesForConvFromCacheSingle(String groupId, String userId) {
        return MessageDataDomainMapper.transformFromMessage(messageDao.getGroupConversationNetUnreadMessages(groupId, userId));
    }

    @Override
    public LiveData<List<DomainMessage>> getUnreadMessagesForAllConvFromCacheLiveData(String groupId) {
        return Transformations.map(messageDao.getGroupAllConversationNetUnreadMessagesLiveData(groupId), new Function<List<Message>, List<DomainMessage>>() {
            @Override
            public List<DomainMessage> apply(List<Message> input) {
                return MessageDataDomainMapper.transformFromMessage(input);
            }
        });
    }

    /**
     * /**
     *      * This should be called on a background thread - this is user's responsibility to
     *      * call this method on a background thread, otherwise a runtime error is thrown.
     * @param groupId
     * @return
     */
    @Override
    public List<DomainMessage> getUnreadMessagesForAllConvFromCacheSingle(String groupId) {
        return MessageDataDomainMapper.transformFromMessage(messageDao.getGroupAllConversationNetUnreadMessages(groupId));
    }
}
