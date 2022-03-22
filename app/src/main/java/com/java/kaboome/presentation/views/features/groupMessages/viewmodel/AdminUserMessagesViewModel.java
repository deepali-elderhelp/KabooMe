package com.java.kaboome.presentation.views.features.groupMessages.viewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.java.kaboome.constants.MediaActionConstants;
import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.MessageDataDomainMapper;
import com.java.kaboome.data.repositories.DataConversationsRepository;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.data.workers.LoadMediaWorker;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.domain.usecases.AddNewMessageUseCase;
import com.java.kaboome.domain.usecases.DeleteLocalMessageUseCase;
//import com.java.kaboome.domain.usecases.DeleteMessageUseCase;
import com.java.kaboome.domain.usecases.DownloadAttachmentUseCase;
import com.java.kaboome.domain.usecases.GetConversationLastMessageCache;
import com.java.kaboome.domain.usecases.GetMessagesUseCase;
import com.java.kaboome.domain.usecases.GetNetUnreadOnlyGroupMessagesUseCase;
import com.java.kaboome.domain.usecases.GetUserGroupConversationOnlyLocalUseCase;
import com.java.kaboome.domain.usecases.UpdateMessageAttachmentDetailsUseCase;
import com.java.kaboome.domain.usecases.UpdateMessageLoadingProgressUseCase;
import com.java.kaboome.domain.usecases.UploadImageUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.WorkerBuilderHelper;
import com.java.kaboome.presentation.entities.IoTMessage;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.IoTHelper;
import com.java.kaboome.presentation.helpers.MessageGroupsHelper;
import com.java.kaboome.presentation.mappers.IoTDomainMessageMapper;
import com.java.kaboome.presentation.viewModelProvider.SingleMediatorLiveEvent;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.PublishMessageCallback;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * This viewmodel is for admins to see the messages sent by a particular user
 * All admins see the messages sent by a user to admins of the group
 * This User is an Admin
 */
public class AdminUserMessagesViewModel extends ViewModel {

    private static final String TAG = "KMAdmnUsrMsgViewModel";
    private String topicName;
    private GetUserGroupConversationOnlyLocalUseCase getUserGroupConversationOnlyLocalUseCase;
    private GetMessagesUseCase getMessagesUseCase;
    private AddNewMessageUseCase addNewMessageUseCase;
    private DeleteLocalMessageUseCase deleteLocalMessageUseCase;
    private GetNetUnreadOnlyGroupMessagesUseCase getNetUnreadOnlyGroupMessagesUseCase;
//    private DeleteMessageUseCase deleteMessageUseCase;
    private MessagesListRepository messagesListRepository;
    private UploadImageUseCase uploadImageUseCase;
    private DownloadAttachmentUseCase downloadAttachmentUseCase;
    private UpdateMessageAttachmentDetailsUseCase updateMessageAttachmentDetailsUseCase;
    private UpdateMessageLoadingProgressUseCase updateMessageLoadingProgressUseCase;
    private GetConversationLastMessageCache getUserGroupLastConvMessageCache;
    private ImageUploadRepository imageUploadRepository;

    private LiveData<PagedList<Message>> messagesList;
    private DataSource.Factory<Integer, Message> dataSourceFactory;

    private MediatorLiveData<DomainResource<List<DomainMessage>>> serverMessages =  new MediatorLiveData<>();
    private MediatorLiveData<List<DomainMessage>> groupUnreadMessages =  new MediatorLiveData<>();

    private HashMap<String, String[]> messageAttachments = new HashMap<>();

    public SingleMediatorLiveEvent<DomainUpdateResource> getUploadMessageAttachment() {
        return uploadMessageAttachment;
    }

    public MediatorLiveData<List<DomainMessage>> getGroupUnreadMessages() {
        return groupUnreadMessages;
    }

    private SingleMediatorLiveEvent<DomainUpdateResource> uploadMessageAttachment = new SingleMediatorLiveEvent<>();

    public SingleMediatorLiveEvent<DomainUpdateResource> getDownloadedMessageAttachment() {
        return downloadMessageAttachment;
    }

    public void addToMessageAttachmentMap(String messageId, String[] attachments){
        messageAttachments.put(messageId, attachments);
    }

    private void removeMessageFromAttachmentMap(String messageId){
        messageAttachments.remove(messageId);
    }

    private SingleMediatorLiveEvent<DomainUpdateResource> downloadMessageAttachment = new SingleMediatorLiveEvent<>();

    private final UserGroupConversationModel conversation;
    private final UserGroupModel userGroupModel;
    private boolean isLoading = false;
    private boolean hasLoadedAll = false;
    private boolean cancelRequest;
    private Long lastAccessedTime = (new Date()).getTime();
    private int limit = 30;

    public AdminUserMessagesViewModel(UserGroupConversationModel conversation, UserGroupModel userGroupModel) {

        this.conversation = conversation;
        this.userGroupModel = userGroupModel;
        Log.d(TAG, "MessagesViewModel: GroupId "+this.conversation.getGroupId());

        messagesListRepository = DataGroupMessagesRepository.getInstance();
        getMessagesUseCase = new GetMessagesUseCase(messagesListRepository);
        getUserGroupConversationOnlyLocalUseCase = new GetUserGroupConversationOnlyLocalUseCase(DataConversationsRepository.getInstance());
        addNewMessageUseCase = new AddNewMessageUseCase(messagesListRepository);
//        deleteMessageUseCase = new DeleteMessageUseCase(messagesListRepository);
        deleteLocalMessageUseCase = new DeleteLocalMessageUseCase(messagesListRepository);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(15).build();

//        dataSourceFactory = AppConfigHelper.getKabooMeDatabaseInstance().getMessageDao().getMessagesForGroup(this.group.getGroupId());
        dataSourceFactory = AppConfigHelper.getKabooMeDatabaseInstance().getMessageDao().getUserAdminMessagesForGroup(this.conversation.getGroupId(), this.conversation.getOtherUserId());
        messagesList = new LivePagedListBuilder<>(dataSourceFactory, config)
                .build();

        imageUploadRepository = DataImageUploadRepository.getInstance();
        uploadImageUseCase = new UploadImageUseCase(imageUploadRepository);
        downloadAttachmentUseCase = new DownloadAttachmentUseCase(imageUploadRepository);
        updateMessageAttachmentDetailsUseCase = new UpdateMessageAttachmentDetailsUseCase(messagesListRepository);
        updateMessageLoadingProgressUseCase = new UpdateMessageLoadingProgressUseCase(messagesListRepository);
        getNetUnreadOnlyGroupMessagesUseCase = new GetNetUnreadOnlyGroupMessagesUseCase(messagesListRepository);
        getUserGroupLastConvMessageCache = new GetConversationLastMessageCache(messagesListRepository);

    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public LiveData<PagedList<Message>> getMessagesList() {
        return messagesList;
    }

    public LiveData<DomainUserGroupConversation> getUserGroupConversationFromCache(){
        return getUserGroupConversationOnlyLocalUseCase.execute(GetUserGroupConversationOnlyLocalUseCase.Params.forGroup(userGroupModel.getGroupId(), conversation.getOtherUserId()));
    }

    public LiveData<DomainResource<List<DomainMessage>>> getServerMessages() {
        Log.d(TAG, "getServerMessages: ");
        return serverMessages;
    }

    public void loadServerMessages(){

        Log.d(TAG, "loadServerMessages: with last accessed - "+lastAccessedTime);

//        final int limit = 15;
        if(lastAccessedTime == null){
            lastAccessedTime = (new Date()).getTime();
        }


        if(isLoading || hasLoadedAll){
            return;
        }

        isLoading = true;


        Log.d(TAG, "loadServerMessages: Loading now");
        final LiveData<DomainResource<List<DomainMessage>>> messagesSource = getMessagesUseCase.execute(GetMessagesUseCase.Params.forGroupWithDetails(this.conversation.getGroupId(), lastAccessedTime, this.conversation.getCacheClearTS() == null? (new Date()).getTime() : this.conversation.getCacheClearTS(), limit, "backwards", MessageGroupsConstants.USER_ADMIN_MESSAGES, conversation.getOtherUserId()));

        serverMessages.addSource(messagesSource, new Observer<DomainResource<List<DomainMessage>>>() {
            @Override
            public void onChanged(@Nullable DomainResource<List<DomainMessage>> listDomainResource) {
                Log.d(TAG, "onChanged: new list came from server");

                if(!cancelRequest){
                    if (listDomainResource != null) {


                        if (listDomainResource.status == DomainResource.Status.SUCCESS) {

                            if (listDomainResource.data != null) {
                                Log.d(TAG, "Messages Received  - "+listDomainResource.data.size());
                                if(listDomainResource.data.size() == 0){
                                    //there were only 15 records
                                    isLoading = false;
                                    hasLoadedAll = true;
                                    Log.d(TAG, "no more messages in the server...");
                                }
                                if (listDomainResource.data.size() > 0 && listDomainResource.data.size() < limit) {
                                    Log.d(TAG, "no more messages in the server...");
                                    isLoading = false;
                                    hasLoadedAll = true;
                                    lastAccessedTime = listDomainResource.data.get(listDomainResource.data.size()-1).getSentAt();
                                    Log.d(TAG, "onChanged: new last accessed becomes "+lastAccessedTime);
                                }
                                if(listDomainResource.data.size() >= limit){
                                    Log.d(TAG, "There are more messages in the server...");
                                    isLoading = false;
                                    hasLoadedAll = false;
                                    //new lastaccessed time
                                    lastAccessedTime = listDomainResource.data.get(listDomainResource.data.size()-1).getSentAt();
                                    Log.d(TAG, "onChanged: new last accessed becomes "+lastAccessedTime);
                                }

                            }
                            serverMessages.setValue(listDomainResource);
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



    public LiveData<Boolean> getConnectionEstablished() {
        return IoTHelper.getInstance().getConnectionEstablished();
    }

    public MutableLiveData<IoTMessage> getIotMessageReceived(){
        return IoTHelper.getInstance().getIotMessageReceived();
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        IoTHelper.getInstance().unsubscribeFromTopic(topicName);
        Log.d(TAG, "ViewModel cleared");

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
        messagesListRepository.updateMessagesToRead(conversation.getGroupId(), conversation.getOtherUserId());

        //first update the cache with the last seen ts update
        //get the latest message from cache, get its sentAt, it needs to be fetched on a background thread
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
//                DomainMessage lastMessageInCache = messagesListRepository.getLatestMessageInCache(conversation.getGroupId(), MessageGroupsConstants.USER_ADMIN_MESSAGES, conversation.getOtherUserId());
//                DomainMessage lastMessageInCache = messagesListRepository.getLastMessageForConvFromCacheSingle(conversation.getGroupId(), conversation.getOtherUserId());
                DomainMessage lastMessageInCache = getUserGroupLastConvMessageCache.execute(GetConversationLastMessageCache.Params.forGroupConversation(conversation.getGroupId(), conversation.getOtherUserId(), true));
                if(lastMessageInCache != null && (lastMessageInCache.getSentAt() != null)){
                    Log.d(TAG, "Last message is there, hence updating the last access");
                    Long lastAccessed = lastMessageInCache.getSentAt();

                    //update the UserGroupConversation DAO with the new lastAccessed for this group user admin messages
                    ConversationsRepository conversationsRepository = DataConversationsRepository.getInstance();
//                    userGroupsListRepository.updateUserGroupLastAccessed(group.getGroupId(), lastAccessed);
                    conversationsRepository.updateUserGroupConversationLastAccessed(conversation.getGroupId(), conversation.getOtherUserId(), lastAccessed);

                    //directly calling the worker which will update the last seen TS to the server
                    WorkerBuilderHelper.callUpdateLastSeenConvTSWorker(conversation.getGroupId(), conversation.getOtherUserId(), lastAccessed);
                }
                else{
                    Log.d(TAG, "Last message is NOT there, hence NOT updating the last access");
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

    public void handleMessageArrival(IoTMessage ioTMessage) {
        if(ioTMessage != null){
            Log.d(TAG, "handleMessageArrival: message came - "+ioTMessage);
            ioTMessage.setUploadedToServer(true); //message is coming back from server, so it is uploaded to server already
            addNewMessageUseCase.execute(AddNewMessageUseCase.Params.newMessage(IoTDomainMessageMapper.transformFromIoTMessage(ioTMessage)));

            if(ioTMessage.getHasAttachment() && !ioTMessage.getAttachmentUploaded() && ioTMessage.getSentBy().equals(AppConfigHelper.getUserId())){
                startUploadingAttachment(ioTMessage.getMessageId(), null);
            }
        }

    }

    public void publishIoTMessage(String messageId, String textEnteredByUser, String sentBy, Long sentAt, int priority, Boolean hasAttachment,
                                  Boolean attachmentUploaded, boolean isLoading, String fileExtension, String fileMime, Boolean isDeleted,
                                  String tnBlob,
                                  MessageGroupsConstants messageGroupsConstants,
                                  String attachmentUri,
                                  String otherUserId,
                                  String otherUserName,
                                  String otherUserRole,
                                  Long otherUserImageTS,
                                  PublishMessageCallback callback){



        String sentTo = MessageGroupsHelper.sentToBasedUponMessageGroupsConstants(messageGroupsConstants, otherUserId);
        IoTMessage message = new IoTMessage();
        message.setMessageId(messageId);
        message.setGroupId(this.conversation.getGroupId());
//        message.setSentBy(AppConfigHelper.getUserId());
        message.setSentBy(sentBy);
        message.setSentByImageTS(AppConfigHelper.getCurrentUserGroupUserImageTS(conversation.getGroupId()));
        message.setSentTo(sentTo);
        message.setAlias(this.userGroupModel.getAlias());
        message.setNotify(priority); //change it, just for now
        message.setRole(this.userGroupModel.getRole());
        message.setIsAdmin(this.userGroupModel.getIsAdmin());
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

        message.setSentToUserName(otherUserName);
        message.setSentToUserRole(otherUserRole);
        message.setSentToImageTS(otherUserImageTS);

        IoTHelper.getInstance().publishIoTMessage(message, sentTo, callback);
    }


    public void publishIoTMessage(String textEnteredByUser, String sentBy, int priority, MessageGroupsConstants messageGroupsConstants,
                                  String otherUserId, String otherUserName, String otherUserRole, Long otherUserImageTS,
                                  PublishMessageCallback callback) {
//        public void publishIoTMessage(String textEnteredByUser, int priority, PublishMessageCallback callback) {

        String messageId = UUID.randomUUID().toString();
        Long sentAt = new Date().getTime();
//        String sentTo = MessageGroupsHelper.sentToBasedUponMessageGroupsConstants(messageGroupsConstants, userId);
        publishIoTMessage(messageId, textEnteredByUser, sentBy, sentAt, priority, false, false, false, null, null, false,"",  MessageGroupsConstants.USER_ADMIN_MESSAGES, null,  otherUserId, otherUserName, otherUserRole, otherUserImageTS, callback);

    }



//    public void startUploadingAttachment(final String messageId, final String groupId, final Long sentAt, final String fileExtension, final String fileMime, final File attachment){
//        public void startUploadingAttachment(final Message message, final File attachment){
//        public void startUploadingAttachment(final Message message, final String attachmentPath){
//        //first copy to app folder
//        File attachment = FileUtils.copyAttachmentToApp(filePath, messageId);

    public void startUploadingAttachment(final String messageId, String[] attachments){

            Log.d(TAG, "uploadImage: ");

        if(attachments == null){
            //get the attachment URI/path from the map
            attachments = messageAttachments.get(messageId);
        }

        if(attachments == null){
            return;
        }
        //now remove the attachment path from the map
        messageAttachments.remove(messageId);

        Data inputData = new Data.Builder()
                .putString("messageId", messageId)
                .putString("groupId", userGroupModel.getGroupId())
                .putString("action", MediaActionConstants.UPLOAD_ATTACHMENT.getAction())
                .putString("attachment_path", attachments[0])
                .putString("attachment_uri", attachments[1]) //this is not in the database because it gets overwritten by the message published
                .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            //now start a worker to do the same in the backend
            OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                    .Builder(LoadMediaWorker.class)
                    .addTag("upload_attachment")
                    .setInputData(inputData)
                    .setConstraints(constraints)
                    .build();


            final Operation resultOfOperation = WorkManager.getInstance().enqueue(simpleRequest);

            try {
                resultOfOperation.getResult().addListener(new Runnable() {
                    @Override
                    public void run() {
                        //only comes here for SUCCESS
                        try {
                            Log.d(TAG, "Message attachment uploaded successfully");
                            resultOfOperation.getResult().get();

                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            //if the update API gave error, it gets wrapped in ExecutionException
                            Log.d(TAG, "Message attachment upload failed due to "+e.getCause().getMessage());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.d(TAG, "Message attachment upload failed due to "+e.getMessage());
                        }
                    }
                }, AppExecutors2.getInstance().diskIO());
            } catch (Exception e) {
                Log.d(TAG, "Message attachment upload failed due to - "+e.getMessage());
            }



//            String key = message.getGroupId()+"_"+message.getMessageId();
//            HashMap<String, Object> userData = new HashMap<>();
//            userData.put("action", MessageActionConstants.UPLOAD_ATTACHMENT);
//            userData.put("message", message);
//            userData.put("attachment", attachment);
//
////            final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(attachment, key, MessageActionConstants.UPLOAD_ATTACHMENT.getAction(), userData));
//            uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(attachment, key, MessageActionConstants.UPLOAD_ATTACHMENT.getAction(), userData));

//        uploadMessageAttachment.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//                @Override
//                public void onChanged(@Nullable DomainUpdateResource<String> userUpdateDomainResource) {
//                    Log.d(TAG, "onChanged: upload status changed");
//                    if (userUpdateDomainResource != null) {
//
//
//                        if (userUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//
//                            //image upload was successful
//
//                            uploadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.SUCCESS, new MessageTempDataHolder(messageId, sentAt, fileExtension, fileMime, attachment.getAbsolutePath()), null));
//                            uploadMessageAttachment.removeSource(uploadImageRespositorySource);
//
//                        } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//
//                            uploadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.UPDATING, new MessageTempDataHolder(messageId, sentAt, fileExtension, fileMime, attachment.getAbsolutePath()), null));
//
//                            updateMessageLoadingProgress(messageId, userUpdateDomainResource.data);
//
//                        } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
//
//                            uploadMessageAttachment.removeSource(uploadImageRespositorySource);
//                            uploadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.ERROR, new MessageTempDataHolder(messageId, sentAt, fileExtension, fileMime, attachment.getAbsolutePath()), null));
//                        }
//                    } else {
//                        uploadMessageAttachment.removeSource(uploadImageRespositorySource);
//                    }
//                }
//            });

        }


//    public void startDownloadingAttachment(final String messageId, final String groupId, final Long sentAt, final String attachmentExtension, final String filePath, final String fileMime){
public void startDownloadingAttachment(final Message message, final String filePath){

        Log.d(TAG, "downloadAttachment");

    /**
     * String messageId = getInputData().getString(MESSAGE_ID);
     *                     String groupId = getInputData().getString(GROUP_ID);
     *                     String groupName = getInputData().getString(GROUP_NAME);
     *                     String attachmentPath = getInputData().getString(ATTACHMENT_PATH);
     */

    Data inputData = new Data.Builder()
            .putString("messageId", message.getMessageId())
            .putString("groupId", message.getGroupId())
            .putString("groupName", userGroupModel.getGroupName())
            .putString("action", MediaActionConstants.DOWNLOAD_ATTACHMENT.getAction())
            .putString("attachment_path", filePath)
            .build();

    Constraints constraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();

    //now start a worker to do the same in the backend
    OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
            .Builder(LoadMediaWorker.class)
            .addTag("download_attachment")
            .setInputData(inputData)
            .setConstraints(constraints)
            .build();


    final Operation resultOfOperation = WorkManager.getInstance().enqueue(simpleRequest);

    try {
        resultOfOperation.getResult().addListener(new Runnable() {
            @Override
            public void run() {
                //only comes here for SUCCESS
                try {
                    Log.d(TAG, "Message attachment downloaded successfully");
                    resultOfOperation.getResult().get();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                    //if the update API gave error, it gets wrapped in ExecutionException
                    Log.d(TAG, "Message attachment download failed due to "+e.getCause().getMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Message attachment download failed due to "+e.getMessage());
                }
            }
        }, AppExecutors2.getInstance().diskIO());
    } catch (Exception e) {
        Log.d(TAG, "Message attachment download failed due to - "+e.getMessage());
    }


//    String key = message.getGroupId()+"_"+message.getMessageId();
//
//    HashMap<String, Object> userData = new HashMap<>();
//    userData.put("action", MessageActionConstants.DOWNLOAD_ATTACHMENT);
//    userData.put("message", message);
//    userData.put("groupName", userGroupModel.getGroupName());
//    userData.put("filePath", filePath);
//
//    downloadAttachmentUseCase.execute(DownloadAttachmentUseCase.Params.downloadAttachment(new File(filePath), key, MessageActionConstants.DOWNLOAD_ATTACHMENT.getAction(), userData));

//        final LiveData<DomainUpdateResource<String>> downloadAttachmentRespositorySource = downloadAttachmentUseCase.execute(DownloadAttachmentUseCase.Params.downloadAttachment(new File(filePath), key, MessageActionConstants.DOWNLOAD_ATTACHMENT.getAction(), null));


//        downloadMessageAttachment.addSource(downloadAttachmentRespositorySource, new Observer<DomainUpdateResource<String>>() {
//            @Override
//            public void onChanged(@Nullable DomainUpdateResource<String> userUpdateDomainResource) {
//                Log.d(TAG, "onChanged: upload status changed");
//                if (userUpdateDomainResource != null) {
//
//
//                    if (userUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//
//                        //image upload was successful
//
//                        downloadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.SUCCESS, new MessageTempDataHolder(messageId, sentAt, attachmentExtension, fileMime, filePath), null));
//                        downloadMessageAttachment.removeSource(downloadAttachmentRespositorySource);
//
//                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//
//                        downloadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.UPDATING, new MessageTempDataHolder(messageId, sentAt, attachmentExtension, fileMime, filePath), null));
//                        updateMessageLoadingProgress(messageId, userUpdateDomainResource.data);
//
//                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
//
//                        downloadMessageAttachment.removeSource(downloadAttachmentRespositorySource);
//                        downloadMessageAttachment.setValue(new DomainUpdateResource(DomainUpdateResource.Status.ERROR, new MessageTempDataHolder(messageId, sentAt, attachmentExtension, fileMime, filePath), null));
//                    }
//                } else {
//                    downloadMessageAttachment.removeSource(downloadAttachmentRespositorySource);
//                }
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

        /*
        publishIoTMessage(String messageId, String textEnteredByUser, Long sentAt, int priority, Boolean hasAttachment,
                                  Boolean attachmentUploaded, boolean isLoading, String fileExtension, String fileMime, Boolean isDeleted,
                                  String tnBlob,
                                  MessageGroupsConstants messageGroupsConstants,
                                  String otherUserId,
                                  String otherUserName,
                                  String otherUserRole,
                                  Long otherUserImageTS,
                                  PublishMessageCallback callback){
         */

        //all you need to do is to publish the message again with isDeleted set to true
        String messageText = "Message Deleted By "+userGroupModel.getAlias()+","+userGroupModel.getRole();

        publishIoTMessage(message.getMessageId(), messageText, message.getSentBy(), message.getSentAt(), message.getNotify(), message.getHasAttachment(),
                message.getAttachmentUploaded(), message.isAttachmentLoadingGoingOn(), message.getAttachmentExtension(),
                message.getAttachmentMime(), true, message.getTnBlob(),MessageGroupsHelper.getMessageGroupConstantBySentTo(message.getSentTo()),
                message.getAttachmentUri(),
                message.getSentTo(), message.getSentToUserName(), message.getSentToUserRole(), message.getSentToImageTS(), callback);

        //if it has attachment downloaded, then delete it, or make a worker thread do it
        if(message.getHasAttachment()){
            WorkerBuilderHelper.callDeleteMessageAttachmentWorker(message, userGroupModel.getGroupName());
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
            WorkerBuilderHelper.callDeleteMessageAttachmentWorker(message, userGroupModel.getGroupName());
        }
    }


    public void updateMessageForAttachment(String messageId, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentLoadingGoingOn, String mimeType, String attachmentUri){
        updateMessageAttachmentDetailsUseCase.execute(UpdateMessageAttachmentDetailsUseCase.Params.messageToBeUpdated(messageId, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, mimeType, attachmentUri));
    }

    public void clearMessages() {
        Long cacheClearTime = (new Date()).getTime();
        //update the cache
        UserGroupsListRepository userGroupsListRepository = DataUserGroupsListRepository.getInstance();
        userGroupsListRepository.updateUserGroupAdminCacheClearTS(conversation.getGroupId(), cacheClearTime);

        WorkerBuilderHelper.callUpdateConvCacheClearTSWorker(conversation.getGroupId(), conversation.getOtherUserId(), cacheClearTime);
//        WorkerBuilderHelper.callUpdateCacheClearTSWorker(conversation.getGroupId(), (new Date()).getTime(), true, true);
        WorkerBuilderHelper.callDeleteGroupAttachmentsWorker(userGroupModel.getGroupName(), conversation.getOtherUserId());
    }

//    public void clearMedia() {
//        WorkerBuilderHelper.callDeleteGroupAttachmentsWorker(userGroupModel.getGroupName(), conversation.getOtherUserId());
//    }


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

    public void startNetGroupUnreadMessages(){
        groupUnreadMessages.addSource(getNetUnreadOnlyGroupMessagesUseCase.execute(GetNetUnreadOnlyGroupMessagesUseCase.Params.getNetUnreadMessagesForGroup(userGroupModel.getGroupId())), new Observer<List<DomainMessage>>() {
            @Override
            public void onChanged(List<DomainMessage> domainMessages) {
                groupUnreadMessages.setValue(domainMessages);
            }
        });
    }

}
