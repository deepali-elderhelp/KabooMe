package com.java.kaboome.presentation.views.features.home.viewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataInvitationsListRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.usecases.GetDownloadObserveUseCase;
import com.java.kaboome.domain.usecases.GetInvitationsListUseCase;
import com.java.kaboome.domain.usecases.GetUploadObserveUseCase;
import com.java.kaboome.domain.usecases.RemoveDeletedUserGroupsUseCase;
import com.java.kaboome.helpers.AppConfigHelper;

import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private static final String TAG = "KMHomeViewModel";

    private GetInvitationsListUseCase getInvitationsListUseCase;
    private GetUploadObserveUseCase getUploadObserveUseCase;
    private GetDownloadObserveUseCase getDownloadObserveUseCase;
    private InvitationsListRepository invitationsListRepository;
    private ImageUploadRepository imageUploadRepository;
//    private UpdateMessageLoadingProgressUseCase updateMessageLoadingProgressUseCase;
    private LiveData<DomainResource<List<DomainInvitation>>> repositorySource;
    private LiveData<DomainResource<HashMap<String, Object>>> observeUploadSource;
    private LiveData<DomainResource<HashMap<String, Object>>> observeDownloadSource;
//    private UpdateMessageAttachmentDetailsUseCase updateMessageAttachmentDetailsUseCase;
//    private MutableLiveData<DomainResource<HashMap<String, Object>>> centralUpOrDownLoadLiveData = new MutableLiveData<>();

//    MediatorLiveData<List<InvitationModel>> invitations = new MediatorLiveData<>();
    MediatorLiveData<Integer> numberOfInvitations = new MediatorLiveData<>();
//    MediatorLiveData<DomainResource<HashMap<String, Object>>> observeUpload = new MediatorLiveData<>();
//    MediatorLiveData<DomainResource<HashMap<String, Object>>> observeDownload = new MediatorLiveData<>();

    public HomeViewModel() {
        invitationsListRepository = DataInvitationsListRepository.getInstance();
        getInvitationsListUseCase = new GetInvitationsListUseCase(invitationsListRepository);
        imageUploadRepository = DataImageUploadRepository.getInstance();
        getUploadObserveUseCase = new GetUploadObserveUseCase(imageUploadRepository);
        getDownloadObserveUseCase = new GetDownloadObserveUseCase(imageUploadRepository);
//        updateMessageAttachmentDetailsUseCase = new UpdateMessageAttachmentDetailsUseCase(DataGroupMessagesRepository.getInstance());
//        updateMessageLoadingProgressUseCase = new UpdateMessageLoadingProgressUseCase(DataGroupMessagesRepository.getInstance());

    }

//    public MutableLiveData<DomainResource<HashMap<String, Object>>> getCentralUpOrDownLoadLiveData() {
//        return centralUpOrDownLoadLiveData;
//    }

//    public void updateCentralUpOrDownLoadLiveData(DomainResource<HashMap<String, Object>> updateUpOrDownLoadLiveData) {
//        this.centralUpOrDownLoadLiveData.setValue(updateUpOrDownLoadLiveData);
//    }

    //    public MediatorLiveData<List<InvitationModel>> getInvitations() {
//        return invitations;
//    }

    public MediatorLiveData<Integer> getNumberOfInvitations() {
        return numberOfInvitations;
    }

//    public MediatorLiveData<DomainResource<HashMap<String, Object>>> getObserveUpload() {
//        return observeUpload;
//    }
//
//    public MediatorLiveData<DomainResource<HashMap<String, Object>>> getObserveDownload() {
//        return observeDownload;
//    }

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
        Log.d(TAG, "getNumberOfInvitationsFromBackend: being called");
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

//    public void startObservingUploadAndDownload(){
//
//        Log.d(TAG, "startObservingUploadAndDownload: ");
//        observeUpload.removeSource(observeUploadSource); //if there is any old hanging there
//        observeUploadSource = getUploadObserveUseCase.execute(null);
//
//        observeUpload.addSource(observeUploadSource, new Observer<DomainResource<HashMap<String, Object>>>() {
//            @Override
//            public void onChanged(DomainResource<HashMap<String, Object>> domainResource) {
//                Log.d(TAG, "onChanged: - upload - status - "+domainResource.status);
//                    centralUpOrDownLoadLiveData.setValue(domainResource); //this should trigger the observers
//                    observeUpload.setValue(domainResource);
//                }
//            });
//
//
//        observeDownload.removeSource(observeDownloadSource); //if there is any old hanging there
//        observeDownloadSource = getDownloadObserveUseCase.execute(null);
//
//        observeDownload.addSource(observeDownloadSource, new Observer<DomainResource<HashMap<String, Object>>>() {
//            @Override
//            public void onChanged(DomainResource<HashMap<String, Object>> domainResource) {
//                Log.d(TAG, "onChanged: - upload");
//                centralUpOrDownLoadLiveData.setValue(domainResource); //this should trigger the observers
//                observeDownload.setValue(domainResource);
//            }
//        });
//
//
//    }


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
