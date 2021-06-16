package com.java.kaboome.presentation.views.features.home.viewmodel;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.constants.MessageActionConstants;
import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.mappers.MessageDataDomainMapper;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataInvitationsListRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.usecases.GetDownloadObserveUseCase;
import com.java.kaboome.domain.usecases.GetInvitationsListUseCase;
import com.java.kaboome.domain.usecases.GetUploadObserveUseCase;
import com.java.kaboome.domain.usecases.RemoveDeletedUserGroupsUseCase;
import com.java.kaboome.domain.usecases.UpdateMessageAttachmentDetailsUseCase;
import com.java.kaboome.domain.usecases.UpdateMessageLoadingProgressUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.entities.IoTMessage;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.IoTHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.helpers.MessageGroupsHelper;
import com.java.kaboome.presentation.mappers.InvitationModelMapper;
import com.java.kaboome.presentation.mappers.IoTDomainMessageMapper;
import com.java.kaboome.presentation.mappers.MessageDomainMessageMapper;
import com.java.kaboome.presentation.views.features.groupMessages.GroupMessagesFragment;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.PublishMessageCallback;
import com.java.kaboome.presentation.views.features.groupMessages.viewmodel.MessageTempDataHolder;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static com.java.kaboome.helpers.AppConfigHelper.getContext;

public class HomeViewModel extends ViewModel {

    private static final String TAG = "KMHomeViewModel";

    private GetInvitationsListUseCase getInvitationsListUseCase;
    private GetUploadObserveUseCase getUploadObserveUseCase;
    private GetDownloadObserveUseCase getDownloadObserveUseCase;
    private InvitationsListRepository invitationsListRepository;
    private ImageUploadRepository imageUploadRepository;
    private UpdateMessageLoadingProgressUseCase updateMessageLoadingProgressUseCase;
    private LiveData<DomainResource<List<DomainInvitation>>> repositorySource;
    private LiveData<DomainResource<HashMap<String, Object>>> observeUploadSource;
    private LiveData<DomainResource<HashMap<String, Object>>> observeDownloadSource;
    private UpdateMessageAttachmentDetailsUseCase updateMessageAttachmentDetailsUseCase;

//    MediatorLiveData<List<InvitationModel>> invitations = new MediatorLiveData<>();
    MediatorLiveData<Integer> numberOfInvitations = new MediatorLiveData<>();
    MediatorLiveData<DomainResource<HashMap<String, Object>>> observeUpload = new MediatorLiveData<>();
    MediatorLiveData<DomainResource<HashMap<String, Object>>> observeDownload = new MediatorLiveData<>();

    public HomeViewModel() {
        invitationsListRepository = DataInvitationsListRepository.getInstance();
        getInvitationsListUseCase = new GetInvitationsListUseCase(invitationsListRepository);
        imageUploadRepository = DataImageUploadRepository.getInstance();
        getUploadObserveUseCase = new GetUploadObserveUseCase(imageUploadRepository);
        getDownloadObserveUseCase = new GetDownloadObserveUseCase(imageUploadRepository);
        updateMessageAttachmentDetailsUseCase = new UpdateMessageAttachmentDetailsUseCase(DataGroupMessagesRepository.getInstance());
        updateMessageLoadingProgressUseCase = new UpdateMessageLoadingProgressUseCase(DataGroupMessagesRepository.getInstance());

    }

//    public MediatorLiveData<List<InvitationModel>> getInvitations() {
//        return invitations;
//    }

    public MediatorLiveData<Integer> getNumberOfInvitations() {
        return numberOfInvitations;
    }

    public MediatorLiveData<DomainResource<HashMap<String, Object>>> getObserveUpload() {
        return observeUpload;
    }

    public MediatorLiveData<DomainResource<HashMap<String, Object>>> getObserveDownload() {
        return observeDownload;
    }

    //    public void getInvitationsFromBackend(){
//        //false setting so that the data is only loaded from the cache and not from the server
//        //user can go to the invitations page to see if there is any new invitation data
//        final LiveData<DomainResource<List<DomainInvitation>>> repositorySource = getInvitationsListUseCase.execute(GetInvitationsListUseCase.Params.getInvitationsFromServer(false));
//
//        invitations.addSource(repositorySource, new Observer<DomainResource<List<DomainInvitation>>>() {
//            @Override
//            public void onChanged(@Nullable DomainResource<List<DomainInvitation>> listDomainInvitations) {
//                if (listDomainInvitations != null) {
//
//                    if (listDomainInvitations.status == DomainResource.Status.SUCCESS) {
//                        if (listDomainInvitations.data != null) {
//
//                            invitations.setValue(InvitationModelMapper.transformAllFromDomainToModel(listDomainInvitations));
//                        }
////                        invitations.removeSource(repositorySource); //commenting, so that if cache is updated on update, it gets reflected
//                    }
//                    else if (listDomainInvitations.status == DomainResource.Status.LOADING) {
//                        if (listDomainInvitations.data != null) {
//                            invitations.setValue(InvitationModelMapper.transformAllFromDomainToModel(listDomainInvitations));
//                        }
//                    }
//                    else if (listDomainInvitations.status == DomainResource.Status.ERROR) {
//                        if (listDomainInvitations.data != null) {
//                            invitations.setValue(InvitationModelMapper.transformAllFromDomainToModel(listDomainInvitations));
//                        }
//                        Log.d(TAG, "Coming here when status is error");
//                        invitations.removeSource(repositorySource);
//                    }
//
//
//                } else {
//                    Log.d(TAG, "Coming here when listDomainInvitations is null");
//                    invitations.removeSource(repositorySource);
//                }
//            }
//        });
//    }

    public void getNumberOfInvitationsFromBackend(){
        //false setting so that the data is only loaded from the cache and not from the server
        //user can go to the invitations page to see if there is any new invitation data

        //Feb 2020 - changing from false to true
        //reason : imagine scenario - cache is deleted by system, user logs in fresh
        //no invitations in the cache - user can see them only after going to invitation page
        //rather, it is all async - so, making a request to the server, anyway the UI will update
        //on server returned value
        numberOfInvitations.removeSource(repositorySource); //if there is any old hanging there
        repositorySource = getInvitationsListUseCase.execute(GetInvitationsListUseCase.Params.getInvitationsFromServer(true));

        numberOfInvitations.addSource(repositorySource, new Observer<DomainResource<List<DomainInvitation>>>() {
            @Override
            public void onChanged(@Nullable DomainResource<List<DomainInvitation>> listDomainInvitations) {
                if (listDomainInvitations != null) {

                    if (listDomainInvitations.status == DomainResource.Status.SUCCESS) {
                        if (listDomainInvitations.data != null) {

                            numberOfInvitations.setValue(listDomainInvitations.data.size());
                        }
//                        invitations.removeSource(repositorySource); //commenting, so that if cache is updated on update, it gets reflected
                    }
                    else if (listDomainInvitations.status == DomainResource.Status.LOADING) {
                        if (listDomainInvitations.data != null) {
                            numberOfInvitations.setValue(listDomainInvitations.data.size());
                        }
                    }
                    else if (listDomainInvitations.status == DomainResource.Status.ERROR) {
                        if (listDomainInvitations.data != null) {
                            numberOfInvitations.setValue(listDomainInvitations.data.size());
                        }
                        Log.d(TAG, "Coming here when status is error");
                        numberOfInvitations.removeSource(repositorySource);
                    }


                } else {
                    Log.d(TAG, "Coming here when listDomainInvitations is null");
                    numberOfInvitations.removeSource(repositorySource);
                }
            }
        });
    }

    public void startObservingUploadAndDownload(){

        Log.d(TAG, "startObservingUploadAndDownload: ");
        observeUpload.removeSource(observeUploadSource); //if there is any old hanging there
        observeUploadSource = getUploadObserveUseCase.execute(null);

        observeUpload.addSource(observeUploadSource, new Observer<DomainResource<HashMap<String, Object>>>() {
            @Override
            public void onChanged(DomainResource<HashMap<String, Object>> domainMessageDomainResource) {
                Log.d(TAG, "onChanged: - upload - status - "+domainMessageDomainResource.status);


                    HashMap<String, Object> userData = domainMessageDomainResource.data;
                    if(userData != null){
                        MessageActionConstants messageActionConstants = (MessageActionConstants) userData.get("action");
                        final Message messageUpOrDownloaded = (Message) userData.get("message");
                        File attachment = (File) userData.get("attachment");

                        if(MessageActionConstants.UPLOAD_ATTACHMENT.equals(messageActionConstants) && messageUpOrDownloaded != null ) {
                            if (domainMessageDomainResource.status == DomainResource.Status.SUCCESS) {
                                DomainMessage message = MessageDataDomainMapper.transformFromMessage(messageUpOrDownloaded);
                                final IoTMessage ioTMessage = IoTDomainMessageMapper.transformFromDomain(message);
                                ioTMessage.setAttachmentUploaded(true);
                                ioTMessage.setAttachmentLoadingGoingOn(false);
                                ioTMessage.setAttachmentUri(messageUpOrDownloaded.getAttachmentUri());
                                Log.d(TAG, "Upload successful");

                                //now delete the file from external folder
                                //only for version Q and up because they have a new file created in the directories for them
                                //unlike version P and below which is in the external folder and the same has been used as uri
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    FileUtils.deleteFile(attachment.getPath());
                                }

                                //publish again - but this time send the old messageId, sentAt and file extension, so that the message is updated not new created
                                IoTHelper.getInstance().publishIoTMessage(ioTMessage, AppConfigHelper.getUserId(), new PublishMessageCallback() {
                                    @Override
                                    public void publishSuccessful() {
                                        updateMessageAttachmentDetailsUseCase.execute(UpdateMessageAttachmentDetailsUseCase.Params.messageToBeUpdated(ioTMessage.getMessageId(), true, true, false, ioTMessage.getAttachmentMime(), ioTMessage.getAttachmentUri()));
                                    }

                                    @Override
                                    public void publishFailed() {
                                        Log.d(TAG, "publishFailed: ");
                                    }
                                });

                            }
                            else if(domainMessageDomainResource.status == DomainResource.Status.LOADING){
                                String percent = (String) userData.get("percent");
                                Log.d(TAG, "onChanged: - loading - "+percent);
                                int progress = 0;
                                try{
                                    progress = Integer.parseInt(percent);
                                }
                                catch (NumberFormatException e){
                                    progress = 0;
                                }
                                updateMessageLoadingProgressUseCase.execute(UpdateMessageLoadingProgressUseCase.Params.messageLoadingProgToBeUpdated(messageUpOrDownloaded.getMessageId(), progress));
                            }
                        }

                    }
                    observeUpload.setValue(domainMessageDomainResource);
                }


            });


        observeDownload.removeSource(observeDownloadSource); //if there is any old hanging there
        observeDownloadSource = getDownloadObserveUseCase.execute(null);

        observeDownload.addSource(observeDownloadSource, new Observer<DomainResource<HashMap<String, Object>>>() {
            @Override
            public void onChanged(DomainResource<HashMap<String, Object>> domainMessageDomainResource) {
                Log.d(TAG, "onChanged: - upload");
                if(domainMessageDomainResource.status == DomainResource.Status.SUCCESS){
                    HashMap<String, Object> userData = domainMessageDomainResource.data;
                    MessageActionConstants messageActionConstants = (MessageActionConstants) userData.get("action");
                    final Message messageUpOrDownloaded = (Message) userData.get("message");
                    String groupName = (String) userData.get("groupName");
                    String filePath = (String) userData.get("filePath");

                    if(MessageActionConstants.DOWNLOAD_ATTACHMENT.equals(messageActionConstants) && messageUpOrDownloaded != null ){
                        String attachmentUri = null;

                        String newName = messageUpOrDownloaded.getGroupId() + "_Group_" + messageUpOrDownloaded.getMessageId() + messageUpOrDownloaded.getAttachmentExtension();
                        attachmentUri = MediaHelper.saveMediaToGallery(AppConfigHelper.getContext(), AppConfigHelper.getContext().getContentResolver(), filePath, newName, messageUpOrDownloaded.getAttachmentMime(), groupName);

                        //now delete the file from external folder
                        //only deleting for the build Q and up since in those builds, the image is copied to the new directory
                        //for older releases, just the path is attached to the uri, but the file is in the same place
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            FileUtils.deleteFile(filePath);
                        }
//                    }

                        //just a dummy update call, does nothing really, but updates the cache, so a refresh is forced
                        updateMessageAttachmentDetailsUseCase.execute(UpdateMessageAttachmentDetailsUseCase.Params.messageToBeUpdated(messageUpOrDownloaded.getMessageId(), true, true, false, messageUpOrDownloaded.getAttachmentMime(), attachmentUri));

                    }
                }
                else if(domainMessageDomainResource.status == DomainResource.Status.LOADING){

                    HashMap<String, Object> userData = domainMessageDomainResource.data;
                    final Message messageUpOrDownloaded = (Message) userData.get("message");
                    String percent = (String) userData.get("percent");
                    Log.d(TAG, "onChanged: - loading - "+percent);
                    int progress = 0;
                    try{
                        progress = Integer.parseInt(percent);
                    }
                    catch (NumberFormatException e){
                        progress = 0;
                    }
                    updateMessageLoadingProgressUseCase.execute(UpdateMessageLoadingProgressUseCase.Params.messageLoadingProgToBeUpdated(messageUpOrDownloaded.getMessageId(), progress));
                }
                observeDownload.setValue(domainMessageDomainResource);
            }
        });


    }


    public void cleanUpOldDeletedUserGroups(){
        RemoveDeletedUserGroupsUseCase removeDeletedUserGroupsUseCase = new RemoveDeletedUserGroupsUseCase(DataUserGroupsListRepository.getInstance());
        removeDeletedUserGroupsUseCase.execute(RemoveDeletedUserGroupsUseCase.Params.forUser(AppConfigHelper.getUserId()));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: - subscriptions removed");
    }
}
