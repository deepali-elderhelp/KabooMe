package com.java.kaboome.presentation.views.features;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.data.repositories.DataInvitationsListRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.usecases.GetInvitationsListUseCase;
import com.java.kaboome.domain.usecases.RemoveDeletedUserGroupsUseCase;
import com.java.kaboome.helpers.AppConfigHelper;

import java.util.List;

public class BaseViewModel extends ViewModel {

    private static final String TAG = "KMBaseViewModel";

//    MutableLiveData<Boolean> userLoggedIn = new MutableLiveData<>();

    ConnectivityLiveData connectivityLiveData;
//    MediatorLiveData<Integer> numberOfInvitations = new MediatorLiveData<>();
//    private InvitationsListRepository invitationsListRepository;
//    private GetInvitationsListUseCase getInvitationsListUseCase;
//    private LiveData<DomainResource<List<DomainInvitation>>> repositorySource;

//    public MutableLiveData<Boolean> getUserLoggedIn() {
//        return userLoggedIn;
//    }
//
//    public void setUserLoggedIn(Boolean userLoggedInBoolean) {
//        userLoggedIn.setValue(userLoggedInBoolean);
//    }


//    public BaseViewModel() {
//        invitationsListRepository = DataInvitationsListRepository.getInstance();
//        getInvitationsListUseCase = new GetInvitationsListUseCase(invitationsListRepository);
//    }

    public void setConnectivityLiveData(ConnectivityLiveData connectivityLiveData) {
        this.connectivityLiveData = connectivityLiveData;
    }

    public ConnectivityLiveData getConnectivityLiveData() {
        return connectivityLiveData;
    }

//    public void cleanUpOldDeletedUserGroups(){
//        RemoveDeletedUserGroupsUseCase removeDeletedUserGroupsUseCase = new RemoveDeletedUserGroupsUseCase(DataUserGroupsListRepository.getInstance());
//        removeDeletedUserGroupsUseCase.execute(RemoveDeletedUserGroupsUseCase.Params.forUser(AppConfigHelper.getUserId()));
//    }

//    public void getNumberOfInvitationsFromBackend(){
//        //false setting so that the data is only loaded from the cache and not from the server
//        //user can go to the invitations page to see if there is any new invitation data
//
//        //Feb 2020 - changing from false to true
//        //reason : imagine scenario - cache is deleted by system, user logs in fresh
//        //no invitations in the cache - user can see them only after going to invitation page
//        //rather, it is all async - so, making a request to the server, anyway the UI will update
//        //on server returned value
//        numberOfInvitations.removeSource(repositorySource); //if there is any old hanging there
//        repositorySource = getInvitationsListUseCase.execute(GetInvitationsListUseCase.Params.getInvitationsFromServer(true));
//
//        numberOfInvitations.addSource(repositorySource, new Observer<DomainResource<List<DomainInvitation>>>() {
//            @Override
//            public void onChanged(@Nullable DomainResource<List<DomainInvitation>> listDomainInvitations) {
//                if (listDomainInvitations != null) {
//
//                    if (listDomainInvitations.status == DomainResource.Status.SUCCESS) {
//                        if (listDomainInvitations.data != null) {
//
//                            numberOfInvitations.setValue(listDomainInvitations.data.size());
//                        }
////                        invitations.removeSource(repositorySource); //commenting, so that if cache is updated on update, it gets reflected
//                    }
//                    else if (listDomainInvitations.status == DomainResource.Status.LOADING) {
//                        if (listDomainInvitations.data != null) {
//                            numberOfInvitations.setValue(listDomainInvitations.data.size());
//                        }
//                    }
//                    else if (listDomainInvitations.status == DomainResource.Status.ERROR) {
//                        if (listDomainInvitations.data != null) {
//                            numberOfInvitations.setValue(listDomainInvitations.data.size());
//                        }
//                        Log.d(TAG, "Coming here when status is error");
//                        numberOfInvitations.removeSource(repositorySource);
//                    }
//
//
//                } else {
//                    Log.d(TAG, "Coming here when listDomainInvitations is null");
//                    numberOfInvitations.removeSource(repositorySource);
//                }
//            }
//        });
//    }
}
