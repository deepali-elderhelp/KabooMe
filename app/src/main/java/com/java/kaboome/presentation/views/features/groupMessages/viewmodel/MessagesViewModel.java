package com.java.kaboome.presentation.views.features.groupMessages.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.annotation.Nullable;
import androidx.work.Data;
import androidx.work.ListenableWorker;

import android.util.Log;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.java.kaboome.constants.MessageActionConstants;
import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.constants.UserActionConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.MessageDataDomainMapper;
import com.java.kaboome.data.persistence.MessageDao;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.domain.usecases.AddNewMessageUseCase;
import com.java.kaboome.domain.usecases.DeleteLocalMessageUseCase;
//import com.java.kaboome.domain.usecases.DeleteMessageUseCase;
import com.java.kaboome.domain.usecases.DownloadAttachmentUseCase;
import com.java.kaboome.domain.usecases.GetLastOnlyGroupMessageInCacheSingleUseCase;
import com.java.kaboome.domain.usecases.GetMessagesUseCase;
import com.java.kaboome.domain.usecases.GetNetUnreadGroupAllConversationMessagesUseCase;
import com.java.kaboome.domain.usecases.GetNetUnreadGroupConversationMessagesUseCase;
import com.java.kaboome.domain.usecases.GetUserGroupOnlyLocalUseCase;
import com.java.kaboome.domain.usecases.UpdateMessageAttachmentDetailsUseCase;
import com.java.kaboome.domain.usecases.UpdateMessageLoadingProgressUseCase;
import com.java.kaboome.domain.usecases.UploadImageUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.WorkerBuilderHelper;
import com.java.kaboome.presentation.entities.IoTMessage;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.IoTHelper;
import com.java.kaboome.presentation.helpers.MessageGroupsHelper;
import com.java.kaboome.presentation.mappers.IoTDomainMessageMapper;
import com.java.kaboome.presentation.viewModelProvider.SingleMediatorLiveEvent;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.PublishMessageCallback;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

public class MessagesViewModel extends ViewModel {

    private static final String TAG = "KMIoTMessagesViewModel";
    private GetMessagesUseCase getMessagesUseCase;
    private GetUserGroupOnlyLocalUseCase getUserGroupOnlyLocalUseCase;
    private AddNewMessageUseCase addNewMessageUseCase;
    private DeleteLocalMessageUseCase deleteLocalMessageUseCase;
//    private DeleteMessageUseCase deleteMessageUseCase;
    private MessagesListRepository messagesListRepository;
    private UploadImageUseCase uploadImageUseCase;
    private DownloadAttachmentUseCase downloadAttachmentUseCase;
    private UpdateMessageAttachmentDetailsUseCase updateMessageAttachmentDetailsUseCase;
    private UpdateMessageLoadingProgressUseCase updateMessageLoadingProgressUseCase;
    private ImageUploadRepository imageUploadRepository;
    private GetNetUnreadGroupConversationMessagesUseCase getNetUnreadGroupConversationMessagesUseCase;
    private GetNetUnreadGroupAllConversationMessagesUseCase getNetUnreadGroupAllConversationMessagesUseCase;
    private GetLastOnlyGroupMessageInCacheSingleUseCase getLastOnlyGroupMessageInCacheSingleUseCase;


    private LiveData<PagedList<Message>> messagesList;
    private DataSource.Factory<Integer, Message> dataSourceFactory;

    private MediatorLiveData<DomainResource<List<DomainMessage>>> serverMessages =  new MediatorLiveData<>();

    private MediatorLiveData<List<DomainMessage>> unreadAdminMessages =  new MediatorLiveData<>();
    private MediatorLiveData<List<DomainMessage>> unreadAllConvMessages =  new MediatorLiveData<>();

    public SingleMediatorLiveEvent<DomainUpdateResource> getUploadMessageAttachment() {
        return uploadMessageAttachment;
    }

    private SingleMediatorLiveEvent<DomainUpdateResource> uploadMessageAttachment = new SingleMediatorLiveEvent<>();

//    public SingleMediatorLiveEvent<DomainUpdateResource> getDownloadedMessageAttachment() {
//        return downloadMessageAttachment;
//    }

//    private SingleMediatorLiveEvent<DomainUpdateResource> downloadMessageAttachment = new SingleMediatorLiveEvent<>();

    public MediatorLiveData<List<DomainMessage>> getUnreadAdminMessages() {
        return unreadAdminMessages;
    }

    public MediatorLiveData<List<DomainMessage>> getUnreadAllConvMessages() {
        return unreadAllConvMessages;
    }


    private final UserGroupModel group;
    private boolean isLoading = false;
    private boolean hasLoadedAll = false;
    private boolean cancelRequest;
    private Long lastAccessedTime = (new Date()).getTime();

    public MessagesViewModel(UserGroupModel group) {

        this.group = group;
        Log.d(TAG, "MessagesViewModel: GroupId "+this.group.getGroupId());

        messagesListRepository = DataGroupMessagesRepository.getInstance();
        getMessagesUseCase = new GetMessagesUseCase(messagesListRepository);
        getUserGroupOnlyLocalUseCase = new GetUserGroupOnlyLocalUseCase(DataUserGroupRepository.getInstance());
        addNewMessageUseCase = new AddNewMessageUseCase(messagesListRepository);
//        deleteMessageUseCase = new DeleteMessageUseCase(messagesListRepository);
        deleteLocalMessageUseCase = new DeleteLocalMessageUseCase(messagesListRepository);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(15).build();

//        dataSourceFactory = AppConfigHelper.getKabooMeDatabaseInstance().getMessageDao().getMessagesForGroup(this.group.getGroupId());
        dataSourceFactory = AppConfigHelper.getKabooMeDatabaseInstance().getMessageDao().getGroupMessagesForGroup(this.group.getGroupId());
        messagesList = new LivePagedListBuilder<>(dataSourceFactory, config)
                .build();

        imageUploadRepository = DataImageUploadRepository.getInstance();
        uploadImageUseCase = new UploadImageUseCase(imageUploadRepository);
        downloadAttachmentUseCase = new DownloadAttachmentUseCase(imageUploadRepository);
        updateMessageAttachmentDetailsUseCase = new UpdateMessageAttachmentDetailsUseCase(messagesListRepository);
        updateMessageLoadingProgressUseCase = new UpdateMessageLoadingProgressUseCase(messagesListRepository);
        //this one is needed to check if there are any new messages for the admin conversation
        //if yes, then the menu icon lites up
        //this is if the user is not an admin of the group
        getNetUnreadGroupConversationMessagesUseCase = new GetNetUnreadGroupConversationMessagesUseCase(messagesListRepository);
        getNetUnreadGroupAllConversationMessagesUseCase = new GetNetUnreadGroupAllConversationMessagesUseCase(messagesListRepository);
        getLastOnlyGroupMessageInCacheSingleUseCase = new GetLastOnlyGroupMessageInCacheSingleUseCase(messagesListRepository);

    }

    public LiveData<PagedList<Message>> getMessagesList() {
        return messagesList;
    }

    public LiveData<DomainUserGroup> getUserGroupFromCache(){
        return getUserGroupOnlyLocalUseCase.execute(GetUserGroupOnlyLocalUseCase.Params.forGroup(group.getGroupId()));
    }

    public LiveData<DomainResource<List<DomainMessage>>> getServerMessages() {
        Log.d(TAG, "getServerMessages: ");
        return serverMessages;
    }

    public void loadServerMessages(){

        Log.d(TAG, "loadServerMessages: with last accessed - "+lastAccessedTime);

        final int limit = 15;
        if(lastAccessedTime == null){
            lastAccessedTime = (new Date()).getTime();
        }


        if(isLoading || hasLoadedAll){
            return;
        }

        isLoading = true;


        Log.d(TAG, "loadServerMessages: Loading now");
        final LiveData<DomainResource<List<DomainMessage>>> messagesSource = getMessagesUseCase.execute(GetMessagesUseCase.Params.forGroupWithDetails(this.group.getGroupId(), lastAccessedTime, this.group.getCacheClearTS() == null? (new Date()).getTime() : this.group.getCacheClearTS(), limit, "backwards", MessageGroupsConstants.GROUP_MESSAGES, AppConfigHelper.getUserId()));

        serverMessages.addSource(messagesSource, new Observer<DomainResource<List<DomainMessage>>>() {
            @Override
            public void onChanged(@Nullable DomainResource<List<DomainMessage>> listDomainResource) {
                Log.d(TAG, "onChanged: new list came from server");

                if(!cancelRequest){
                    if (listDomainResource != null) {


                        if (listDomainResource.status == DomainResource.Status.SUCCESS) {

                            if (listDomainResource.data != null) {
                                if(listDomainResource.data.size() == 0){
                                    //there were only 15 records
                                    isLoading = false;
                                    hasLoadedAll = true;
                                    Log.d(TAG, "no more messages in the server...");
                                }
                                if (listDomainResource.data.size() > 0 && listDomainResource.data.size() < 15) {
                                    Log.d(TAG, "no more messages in the server...");
                                    isLoading = false;
                                    hasLoadedAll = true;
                                    lastAccessedTime = listDomainResource.data.get(listDomainResource.data.size()-1).getSentAt();
                                    Log.d(TAG, "onChanged: new last accessed becomes "+lastAccessedTime);
                                }
                                if(listDomainResource.data.size() == 15){
                                    Log.d(TAG, "There are more messages in the server...");
                                    isLoading = false;
                                    hasLoadedAll = false;
                                    //new last accessed time
                                    lastAccessedTime = listDomainResource.data.get(listDomainResource.data.size()-1).getSentAt();
                                    Log.d(TAG, "onChanged: new last accessed becomes "+lastAccessedTime);
                                }

                            }
                            serverMessages.removeSource(messagesSource);
                        } else if (listDomainResource.status == DomainResource.Status.LOADING) {
                            isLoading = true;
                        } else if (listDomainResource.status == DomainResource.Status.ERROR) {
                            Log.d(TAG, "Error...Error....Error..."+listDomainResource.message);
                            isLoading = false;
                            serverMessages.removeSource(messagesSource);
                        }
                    } else {
                        isLoading = false;
                        serverMessages.removeSource(messagesSource);
                    }
                }
                else{
                    isLoading = false;
                    serverMessages.removeSource(messagesSource);
                }

            }
        });
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isHasLoadedAll() {
        return hasLoadedAll;
    }


    public void startNetUnreadGroupConversationMessagesUseCase(){
        unreadAdminMessages.addSource(getNetUnreadGroupConversationMessagesUseCase.execute(GetNetUnreadGroupConversationMessagesUseCase.Params.getNetUnreadMessagesForGroup(group.getGroupId(), AppConfigHelper.getUserId())), new Observer<List<DomainMessage>>() {
            @Override
            public void onChanged(List<DomainMessage> domainMessages) {
                unreadAdminMessages.setValue(domainMessages);
            }
        });
    }

    public void startNetUnreadGroupAllConversationMessagesUseCase(){
        unreadAllConvMessages.addSource(getNetUnreadGroupAllConversationMessagesUseCase.execute(GetNetUnreadGroupAllConversationMessagesUseCase.Params.getNetUnreadAllConvMessagesForGroup(group.getGroupId())), new Observer<List<DomainMessage>>() {
            @Override
            public void onChanged(List<DomainMessage> domainMessages) {
                unreadAllConvMessages.setValue(domainMessages);
            }
        });
    }


    public LiveData<Boolean> getConnectionEstablished() {
        return IoTHelper.getInstance().getConnectionEstablished();
    }

    public MutableLiveData<IoTMessage> getIotMessageReceived(){
        return IoTHelper.getInstance().getIotMessageReceived();
    }



    @Override
    protected void onCleared() {
        super.onCleared();

        IoTHelper.getInstance().unsubscribeFromTopic(group.getGroupId());
        Log.d(TAG, "ViewModel cleared");

    }

    public boolean onPausePressed(){
        Log.d(TAG, "onPausePressed: ");
        if(isLoading){
            // cancel the query
            cancelRequest();
            isLoading = false;
        }
        updateLastAccess();

        return true;
    }

    public boolean onBackPressed(){
        Log.d(TAG, "onBackPressed: ");
        if(isLoading){
            // cancel the query
            cancelRequest();
            isLoading = false;
        }
        updateLastAccess();

        return true;
    }

    public void updateLastAccess(){

        //first set the corresponding messages to read in the cache
        messagesListRepository.updateMessagesToRead(group.getGroupId(), MessageGroupsConstants.GROUP_MESSAGES.toString());
        //first update the cache with the last seen ts update
        //get the latest message from cache, get its sentAt, it needs to be fetched on a background thread
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
//                DomainMessage lastMessageInCache = messagesListRepository.getLatestGroupMessageInCache(group.getGroupId());
//                DomainMessage lastMessageInCache = messagesListRepository.getLatestMessageInCache(group.getGroupId(), MessageGroupsConstants.GROUP_MESSAGES, AppConfigHelper.getUserId());
                final DomainMessage lastMessageInCache = getLastOnlyGroupMessageInCacheSingleUseCase.execute(GetLastOnlyGroupMessageInCacheSingleUseCase.Params.forGroup(group.getGroupId(), true));

//                DomainMessage lastMessageInCache = messagesListRepository.getLastMessageForOnlyGroupFromCacheSingle(group.getGroupId());
                if(lastMessageInCache != null && (lastMessageInCache.getSentAt() != null)){
                    Long lastAccessed = lastMessageInCache.getSentAt();

                    //update the UserGroup DAO with the new lastAccessed for this group
                    UserGroupsListRepository userGroupsListRepository = DataUserGroupsListRepository.getInstance();
                    userGroupsListRepository.updateUserGroupLastAccessed(group.getGroupId(), lastAccessed);

                    //directly calling the worker which will update the last seen TS to the server
                    WorkerBuilderHelper.callUpdateLastSeenTSWorker(group.getGroupId(), lastAccessed, null, true);
                }
            }
        });
    }




    public void cancelRequest(){
        if(isLoading){
            Log.d(TAG, "cancelMessageLoadingRequest: canceling the message loading request.");
            cancelRequest = true;
            isLoading = false;

        }
    }

    public void handleMessageArrival(IoTMessage message) {
        if(message != null){
            Log.d(TAG, "handleMessageArrival: message came - "+message);
            message.setUploadedToServer(true); //message is coming back from server, so it is uploaded to server already
            addNewMessageUseCase.execute(AddNewMessageUseCase.Params.newMessage(IoTDomainMessageMapper.transformFromIoTMessage(message)));

        }

    }

    public void publishIoTMessage(String messageId, String groupId, String sentBy, Long sentByImageTS, String sentByAlias, String sentByRole, String isSentByAdmin, String textEnteredByUser, Long sentAt, int priority, Boolean hasAttachment,
                                  Boolean attachmentUploaded, boolean isLoading, String fileExtension, String fileMime, Boolean isDeleted,
                                  String tnBlob,
                                  String attachmentUri,
                                  PublishMessageCallback callback){

        String sentTo = MessageGroupsHelper.sentToBasedUponMessageGroupsConstants(MessageGroupsConstants.GROUP_MESSAGES, AppConfigHelper.getUserId());
        IoTMessage message = new IoTMessage();
        message.setMessageId(messageId);
//        message.setGroupId(this.group.getGroupId()); //this one
        message.setGroupId(groupId); //this one
//        message.setSentBy(AppConfigHelper.getUserId()); //this one
        message.setSentBy(sentBy); //this one
        message.setSentTo(sentTo);
//        message.setSentByImageTS(this.group.getUserImageUpdateTimestamp()); //this one
        message.setSentByImageTS(sentByImageTS); //this one
//        message.setAlias(this.group.getAlias());//this one
        message.setAlias(sentByAlias);//this one
        message.setNotify(priority);
//        message.setRole(this.group.getRole());//this one
        message.setRole(sentByRole);//this one
//        message.setIsAdmin(this.group.getIsAdmin());//this one
        message.setIsAdmin(isSentByAdmin);//this one
        message.setMessageText(textEnteredByUser);
        message.setSentAt(sentAt);
        message.setHasAttachment(hasAttachment);
        message.setAttachmentUploaded(attachmentUploaded);
        message.setAttachmentExtension(fileExtension);
        message.setAttachmentMime(fileMime);
        message.setAttachmentUri(attachmentUri);
        message.setAttachmentLoadingGoingOn(isLoading);
        message.setDeleted(isDeleted);
        message.setTnBlob(tnBlob);

        IoTHelper.getInstance().publishIoTMessage(message, AppConfigHelper.getUserId(), callback);
    }


    public void publishIoTMessage(String textEnteredByUser, int priority, PublishMessageCallback callback) {

        String messageId = UUID.randomUUID().toString();
        Long sentAt = new Date().getTime();
        publishIoTMessage(messageId, group.getGroupId(), AppConfigHelper.getUserId(), group.getUserImageUpdateTimestamp(), group.getAlias(), group.getRole(), group.getIsAdmin(), textEnteredByUser, sentAt, priority, false, false, false, null, null, false,"", null, callback);

    }



//    public void startUploadingAttachment(final String messageId, final String groupId, final Long sentAt, final String fileExtension, final String fileMime, final File attachment){
    public void startUploadingAttachment(final Message message, final File attachment){

//        //first copy to app folder
//        File attachment = FileUtils.copyAttachmentToApp(filePath, messageId);



            Log.d(TAG, "uploadImage: ");
//            String key = groupId+"_"+messageId;
            String key = message.getGroupId()+"_"+message.getMessageId();
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("action", MessageActionConstants.UPLOAD_ATTACHMENT);
            userData.put("message", message);
            userData.put("attachment", attachment);
//            Object[] userData = new Object[3];
//            userData[0] = MessageActionConstants.UPLOAD_ATTACHMENT;
//            userData[1] = message;
//            userData[2] = attachment;
            uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(attachment, key, MessageActionConstants.UPLOAD_ATTACHMENT.getAction(), userData));

//            final LiveData<DomainUpdateResource<String>> uploadImageRepositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(attachment, key, MessageActionConstants.UPLOAD_ATTACHMENT.getAction(), userData));
//
//
//        uploadMessageAttachment.addSource(uploadImageRepositorySource, new Observer<DomainUpdateResource<String>>() {
//                @Override
//                public void onChanged(@Nullable DomainUpdateResource<String> userUpdateDomainResource) {
//                    Log.d(TAG, "onChanged: upload status changed");
//                    if (userUpdateDomainResource != null) {
//
//
//                        if (userUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//
//                            //image upload was successful
//                            Log.d(TAG, "success uploading image");
//                            uploadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.SUCCESS, new MessageTempDataHolder(message.getMessageId(), message.getSentAt(), message.getAttachmentExtension(), message.getAttachmentMime(), attachment.getAbsolutePath()), null));
//                            uploadMessageAttachment.removeSource(uploadImageRepositorySource);
//
//                        } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                            Log.d(TAG, "updating uploading image");
//                            uploadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.UPDATING, new MessageTempDataHolder(message.getMessageId(), message.getSentAt(), message.getAttachmentExtension(), message.getAttachmentMime(), attachment.getAbsolutePath()), null));
//
//                            updateMessageLoadingProgress(message.getMessageId(), userUpdateDomainResource.data);
//
//                        } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
//
//                            Log.d(TAG, "error uploading image");
//                            uploadMessageAttachment.removeSource(uploadImageRepositorySource);
//                            uploadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.ERROR, new MessageTempDataHolder(message.getMessageId(), message.getSentAt(), message.getAttachmentExtension(), message.getAttachmentMime(), attachment.getAbsolutePath()), null));
//                        }
//                    } else {
//                        uploadMessageAttachment.removeSource(uploadImageRepositorySource);
//                    }
//                }
//            });

        }


//    public void startDownloadingAttachment(final String messageId, final String groupId, final Long sentAt, final String attachmentExtension, final String filePath, final String fileMime){
    public void startDownloadingAttachment(final Message message, final String filePath){

        Log.d(TAG, "downloadAttachment");
        String key = message.getGroupId()+"_"+message.getMessageId();

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("action", MessageActionConstants.DOWNLOAD_ATTACHMENT);
        userData.put("message", message);
        userData.put("groupName", group.getGroupName());
        userData.put("filePath", filePath);

        downloadAttachmentUseCase.execute(DownloadAttachmentUseCase.Params.downloadAttachment(new File(filePath), key, MessageActionConstants.DOWNLOAD_ATTACHMENT.getAction(), userData));

//        final LiveData<DomainUpdateResource<String>> downloadAttachmentRepositorySource = downloadAttachmentUseCase.execute(DownloadAttachmentUseCase.Params.downloadAttachment(new File(filePath), key, MessageActionConstants.DOWNLOAD_ATTACHMENT.getAction(), userData));
//        final SingleMediatorLiveEvent<DomainUpdateResource> downloadMessageAttachment = new SingleMediatorLiveEvent<>();
//
//        downloadMessageAttachment.addSource(downloadAttachmentRepositorySource, new Observer<DomainUpdateResource<String>>() {
//            @Override
//            public void onChanged(@Nullable DomainUpdateResource<String> userUpdateDomainResource) {
//                Log.d(TAG, "onChanged: upload status changed");
//                //only taking care of updating and updating the cache with it
//                //the successful case does not need to be handled, since that observer is also
//                //at the HomeViewModel level
//                if (userUpdateDomainResource != null) {
//                    if (userUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//                        downloadMessageAttachment.removeSource(downloadAttachmentRepositorySource);
//                    }
//                    else if (userUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                        updateMessageLoadingProgress(message.getMessageId(), userUpdateDomainResource.data);
//                    }
//                    else if (userUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
//                        downloadMessageAttachment.removeSource(downloadAttachmentRepositorySource);
//                    }
//                }
//                else {
//                    downloadMessageAttachment.removeSource(downloadAttachmentRepositorySource);
//                }
//
////                if (userUpdateDomainResource != null) {
////
////
////                    if (userUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
////
////                        //image upload was successful
////
////                        downloadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.SUCCESS, new MessageTempDataHolder(messageId, sentAt, attachmentExtension, fileMime, filePath), null));
////                        downloadMessageAttachment.removeSource(downloadAttachmentRepositorySource);
////
////                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
////
////                        downloadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.UPDATING, new MessageTempDataHolder(messageId, sentAt, attachmentExtension, fileMime, filePath), null));
////                        updateMessageLoadingProgress(messageId, userUpdateDomainResource.data);
////
////                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
////
////                        downloadMessageAttachment.removeSource(downloadAttachmentRepositorySource);
////                        downloadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.ERROR, new MessageTempDataHolder(messageId, sentAt, attachmentExtension, fileMime, filePath), null));
////                    }
////                } else {
////                    downloadMessageAttachment.removeSource(downloadAttachmentRepositorySource);
////                }
//            }
//        });

    }



    public boolean connectionErrorToBeDisplayed(Exception e){
        if(IoTHelper.getInstance().getCurrentStatus() == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected ||
                IoTHelper.getInstance().getCurrentStatus() == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting ||
                IoTHelper.getInstance().getCurrentStatus() == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting){
            return false; //either it is connecting or is trying to reconnect
        }
        if(IoTHelper.getInstance().getCurrentStatus() == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost){
            return true;
        }

        return false;

    }

    public void deleteMessage(Message message, PublishMessageCallback callback) {

        String messageText = "Message Deleted By "+group.getAlias()+","+group.getRole();

        //all you need to do is to publish the message again with isDeleted set to true
        //AppConfigHelper.getUserId(), group.getUserImageUpdateTimestamp(), group.getAlias(), group.getRole()
        publishIoTMessage(message.getMessageId(), message.getGroupId(), message.getSentBy(), message.getSentByImageTS(), message.getAlias(),
                message.getRole(),message.getIsAdmin(),
                messageText, message.getSentAt(), message.getNotify(), message.getHasAttachment(),
                message.getAttachmentUploaded(), message.isAttachmentLoadingGoingOn(), message.getAttachmentExtension(),
                message.getAttachmentMime(), true, message.getTnBlob(),message.getAttachmentUri(), callback);

        //do the delete attachment first
        //if it has attachment downloaded, then delete it, or make a worker thread do it
        if(message.getHasAttachment()){
            WorkerBuilderHelper.callDeleteMessageAttachmentWorker(message, group.getGroupName());
        }


    }

    /**
     * This method only deletes the message from the cache.
     * Created for deleting the welcome message when user chooses close button
     * But also being used for when the user selects a particular message to be
     * only "Delete for only me"
     * @param message
     */
    public void deleteLocalMessage(Message message){
        deleteLocalMessageUseCase.execute(DeleteLocalMessageUseCase.Params.messageToBeDeleted(MessageDataDomainMapper.transformFromMessage(message)));

        if(message.getHasAttachment()){
            WorkerBuilderHelper.callDeleteMessageAttachmentWorker(message, group.getGroupName());
        }
    }


    public void updateMessageForAttachment(String messageId, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentLoadingGoingOn, String mimeType, String attachmentUri){
        updateMessageAttachmentDetailsUseCase.execute(UpdateMessageAttachmentDetailsUseCase.Params.messageToBeUpdated(messageId, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, mimeType, attachmentUri));
    }

    public void clearMessages() {
        Long cacheClearTime = (new Date()).getTime();
        //update the cache
        UserGroupsListRepository userGroupsListRepository = DataUserGroupsListRepository.getInstance();
        userGroupsListRepository.updateUserGroupAdminCacheClearTS(group.getGroupId(), cacheClearTime);
        WorkerBuilderHelper.callUpdateCacheClearTSWorker(group.getGroupId(), (new Date()).getTime(), true, true);
        WorkerBuilderHelper.callDeleteGroupAttachmentsWorker(group.getGroupName(), "Group");
    }

    public void clearMedia() {
        WorkerBuilderHelper.callDeleteGroupAttachmentsWorker(group.getGroupName(), "Group");
    }


    private void updateMessageLoadingProgress(String messageId, String progressStr){
        int progress = 0;
        try{
           progress = Integer.parseInt(progressStr);
        }
        catch (NumberFormatException e){
            progress = 0;
        }
        updateMessageLoadingProgressUseCase.execute(UpdateMessageLoadingProgressUseCase.Params.messageLoadingProgToBeUpdated(messageId, progress));
    }

}
