package com.java.kaboome.presentation.views.features.invitationsList.viewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.data.repositories.DataGroupRepository;
import com.java.kaboome.data.repositories.DataInvitationsListRepository;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.usecases.GetInvitationsListUseCase;
import com.java.kaboome.domain.usecases.RejectInvitationUseCase;
import com.java.kaboome.domain.usecases.UpdateGroupCacheUseCase;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.mappers.InvitationModelMapper;

import java.util.List;

public class InvitationsListViewModel extends ViewModel {

    private static final String TAG = "KMInviListViewMode";

    private GetInvitationsListUseCase getInvitationsListUseCase;
    private InvitationsListRepository invitationsListRepository;
    private RejectInvitationUseCase rejectInvitationUseCase;
    private LiveData<DomainResource<List<DomainInvitation>>> repositorySource;
    private LiveData<DomainResource<List<DomainInvitation>>> rejectInvitationSource;


    private MediatorLiveData<List<InvitationModel>> invitations = new MediatorLiveData<>();
//    private MediatorLiveData<DomainDeleteResource<String>> rejectInvitation = new MediatorLiveData<>();
    private MediatorLiveData<List<InvitationModel>> rejectInvitation = new MediatorLiveData<>();


    public InvitationsListViewModel() {

        invitationsListRepository = DataInvitationsListRepository.getInstance();
        getInvitationsListUseCase = new GetInvitationsListUseCase(invitationsListRepository);
        rejectInvitationUseCase = new RejectInvitationUseCase(invitationsListRepository);
    }

    public LiveData<List<InvitationModel>> getInvitations() {
        return invitations;
    }

//    public MediatorLiveData<DomainDeleteResource<String>> getRejectInvitation() {
//        return rejectInvitation;
//    }

    public LiveData<List<InvitationModel>> getRejectInvitation() {
        return rejectInvitation;
    }

    public void getInvitationsFromServer(){
//        final LiveData<DomainResource<List<DomainInvitation>>> repositorySource = getInvitationsListUseCase.execute(GetInvitationsListUseCase.Params.getInvitationsFromServer(true));

        invitations.removeSource(repositorySource); //remove old source if any
        repositorySource = getInvitationsListUseCase.execute(GetInvitationsListUseCase.Params.getInvitationsFromServer(true));

        invitations.addSource(repositorySource, new Observer<DomainResource<List<DomainInvitation>>>() {
            @Override
            public void onChanged(@Nullable DomainResource<List<DomainInvitation>> listDomainInvitations) {
                if (listDomainInvitations != null) {

                    if (listDomainInvitations.status == DomainResource.Status.SUCCESS) {
                        if (listDomainInvitations.data != null) {

                            invitations.setValue(InvitationModelMapper.transformAllFromDomainToModel(listDomainInvitations));
                        }
//                        invitations.removeSource(repositorySource); //commenting, so that if cache is updated on update, it gets reflected
                    }
                    else if (listDomainInvitations.status == DomainResource.Status.LOADING) {
                        if (listDomainInvitations.data != null) {
                            invitations.setValue(InvitationModelMapper.transformAllFromDomainToModel(listDomainInvitations));
                        }
                    }
                    else if (listDomainInvitations.status == DomainResource.Status.ERROR) {
                        if (listDomainInvitations.data != null) {
                            invitations.setValue(InvitationModelMapper.transformAllFromDomainToModel(listDomainInvitations));
                        }
                        Log.d(TAG, "Coming here when status is error");
                        invitations.removeSource(repositorySource);
                    }


                } else {
                    Log.d(TAG, "Coming here when listDomainInvitations is null");
                    invitations.removeSource(repositorySource);
                }
            }
        });
    }

//    public void rejectInvitation(String groupId){
//
//        final LiveData<DomainDeleteResource<String>> rejectInvitationSource = rejectInvitationUseCase.execute(RejectInvitationUseCase.Params.forGroup(groupId));
//        rejectInvitation.addSource(rejectInvitationSource, new Observer<DomainDeleteResource<String>>() {
//            @Override
//            public void onChanged(DomainDeleteResource<String> stringDomainDeleteResource) {
//                if(stringDomainDeleteResource != null){
//                    rejectInvitation.setValue(stringDomainDeleteResource);
//                }
//
//                if(!(stringDomainDeleteResource.status == DomainDeleteResource.Status.DELETING)){
//                    rejectInvitation.removeSource(rejectInvitationSource);
//                }
//            }
//        });
//    }


    public void rejectInvitation(final String groupId){

        rejectInvitation.removeSource(rejectInvitationSource); //remove old source if any
        rejectInvitationSource = rejectInvitationUseCase.execute(RejectInvitationUseCase.Params.forGroup(groupId));
        rejectInvitation.addSource(rejectInvitationSource, new Observer<DomainResource<List<DomainInvitation>>>() {
            @Override
            public void onChanged(DomainResource<List<DomainInvitation>> listDomainInvitations) {
                if (listDomainInvitations != null) {

                    if (listDomainInvitations.status == DomainResource.Status.SUCCESS) {
                        if (listDomainInvitations.data != null) {
                            //the invitation has been removed - if there is a group in local DB, then update it's current status from pending to none
                            DomainGroup domainGroup = new DomainGroup();
                            domainGroup.setGroupId(groupId);
                            UpdateGroupCacheUseCase updateGroupCacheUseCase = new UpdateGroupCacheUseCase(DataGroupRepository.getInstance());
                            updateGroupCacheUseCase.execute(UpdateGroupCacheUseCase.Params.forGroup(domainGroup, GroupActionConstants.UPDATE_GROUP_CURRENT_STATUS.getAction()));

                            invitations.setValue(InvitationModelMapper.transformAllFromDomainToModel(listDomainInvitations));
                        }
                        invitations.removeSource(rejectInvitationSource);
                    }
                    else if (listDomainInvitations.status == DomainResource.Status.LOADING) {
                        //do nothing, assuming old invitations are still showing
                    }
                    else if (listDomainInvitations.status == DomainResource.Status.ERROR) {
                        //show user error that invitation could not be removed
                        Log.d(TAG, "Coming here when status is error");
                        invitations.removeSource(rejectInvitationSource);
                    }


                } else {
                    Log.d(TAG, "Coming here when listDomainInvitations is null");
                    invitations.removeSource(rejectInvitationSource);
                }
            }
        });

    }
}
