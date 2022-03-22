package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;

import java.util.List;

public interface MessagesListRepository {

//    LiveData<DomainResource<List<DomainMessage>>> getGroupMessages(String groupId, Long lastAccessed, Long cacheClearTS, int limit, String scanDirection);
    LiveData<DomainResource<List<DomainMessage>>> getGroupMessages(String groupId, Long lastAccessed, Long cacheClearTS, int limit, String scanDirection, MessageGroupsConstants messageGroupsConstants, String userId);

    DomainMessage getMessage(String messageId);

//    LiveData<List<DomainMessage>> getGroupMessagesSentAfterLastAccess(String groupId, Long lastAccessedTime);

//    LiveData<List<DomainMessage>> getNetUnreadGroupMessages(String groupId);

//    List<DomainMessage> getNetUnreadGroupMessagesCache(String groupId);

//    LiveData<List<DomainMessage>> getNetUnreadGroupConversationMessages(String groupId, String sentTo);



//    LiveData<List<DomainMessage>> getOnlyGroupUnreadMessages(String groupId);

//    List<DomainMessage> getNetUnreadGroupConversationMessagesCache(String groupId, String sentTo);

//    LiveData<List<DomainMessage>> getConversationMessagesSentAfterLastAccess(String groupId, String userId, Long lastAccessedTime);

//    List<DomainMessage> getGroupMessagesSentAfterLastAccessCache(String groupId, Long lastAccessedTime);

//    List<DomainMessage> getConvMessagesSentAfterLastAccessCache(String groupId, String userId, Long lastAccessedTime);

    void addNewMessage(DomainMessage message);

//    void deleteMessage(DomainMessage message);

    void clearMessagesOfGroup(String groupId);

    void clearMessagesOfConversation(String groupId, String otherUserId);

//    void updateGroupsLastMessageSeen(String groupId);

//    DomainMessage getLatestGroupMessageInCache(String groupId, String sentTo);

//    DomainMessage getLatestWholeGroupMessageInCache(String groupId);

//    DomainMessage getLatestMessageInCache(String groupId, MessageGroupsConstants messageGroupsConstants, String userId);

//    LiveData<DomainMessage> getLatestWholeGroupMessageInCacheAsLiveData(String groupId);

//    LiveData<DomainMessage> getLatestConvMessageInCacheAsLiveData(String groupId, String userId);

    void updateMessageAttachmentDetails(String messageId, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentLoadingGoingOn, String mimeType, String attachmentUri);

    void updateMessageAttachmentUploadFailed(String messageId, Boolean attachmentLoadingGoingOn);

    void updateMessageLoadingProgress(String messageId, int progress);

    void deleteLocalMessage(DomainMessage domainMessage);

    void updateMessagesToRead(String groupId, String sentTo);


//    LiveData<Integer> refreshGroupMessages(String groupId, Long lastAccessed, int limit);


    //last message from cache as livedata
    LiveData<DomainMessage> getLastMessageForWholeGroupFromCacheLiveData(String groupId);
    LiveData<DomainMessage> getLastMessageForOnlyGroupFromCacheLiveData(String groupId);
    LiveData<DomainMessage> getLastMessageForAllConvsFromCacheLiveData(String groupId);
    LiveData<DomainMessage> getLastMessageForConvFromCacheLiveData(String groupId, String userId);

    //last message from cache single
    DomainMessage getLastMessageForWholeGroupFromCacheSingle(String groupId, boolean includeDeleted);
    DomainMessage getLastMessageForOnlyGroupFromCacheSingle(String groupId, boolean includeDeleted);
    DomainMessage getLastMessageForAllConvsFromCacheSingle(String groupId, boolean includeDeleted);
    DomainMessage getLastMessageForConvFromCacheSingle(String groupId, String userId, boolean includeDeleted);

    //unread messages since last access from cache as LiveData
    LiveData<List<DomainMessage>> getUnreadMessagesForWholeGroupFromCacheLiveData(String groupId);
    LiveData<List<DomainMessage>> getUnreadMessagesForOnlyGroupFromCacheLiveData(String groupId);
    LiveData<List<DomainMessage>> getUnreadMessagesForConvFromCacheLiveData(String groupId, String userId);

    //unread messages since last access from cache as single
    List<DomainMessage> getUnreadMessagesForWholeGroupFromCacheSingle(String groupId);
    List<DomainMessage> getUnreadMessagesForOnlyGroupFromCacheSingle(String groupId);
    List<DomainMessage> getUnreadMessagesForConvFromCacheSingle(String groupId, String userId);

    //unread messages for all conversations together from cache as LiveData
    LiveData<List<DomainMessage>> getUnreadMessagesForAllConvFromCacheLiveData(String groupId);
    //unread messages for all conversations together from cache as Single
    List<DomainMessage> getUnreadMessagesForAllConvFromCacheSingle(String groupId);





}
