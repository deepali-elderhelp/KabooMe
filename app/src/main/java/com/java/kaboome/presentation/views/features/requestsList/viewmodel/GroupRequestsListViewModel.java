package com.java.kaboome.presentation.views.features.requestsList.viewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.data.repositories.DataGroupRequestRepository;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.domain.usecases.FinishRequestUseCase;
import com.java.kaboome.domain.usecases.GetGroupRequestsListUseCase;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.mappers.GroupRequestModelMapper;

import java.util.List;

public class GroupRequestsListViewModel extends ViewModel {

    private static final String TAG = "KMGrpReqsListViewMode";

    private GetGroupRequestsListUseCase getGroupRequestsListUseCase;
    private FinishRequestUseCase finishRequestUseCase;
    private GroupRequestRepository groupRequestRepository;
    private String groupId;
    private LiveData<DomainResource<List<DomainGroupRequest>>> repositorySource;
    private LiveData<DomainResource<List<DomainGroupRequest>>> finishGroupRequestSource;


    private MediatorLiveData<List<GroupRequestModel>> requests = new MediatorLiveData<>();
    private MediatorLiveData<List<GroupRequestModel>> finishRequests = new MediatorLiveData<>();


    public GroupRequestsListViewModel(String groupId) {

        this.groupId = groupId;
        groupRequestRepository = DataGroupRequestRepository.getInstance();
        getGroupRequestsListUseCase = new GetGroupRequestsListUseCase(groupRequestRepository);
        finishRequestUseCase = new FinishRequestUseCase(groupRequestRepository);
    }

    public LiveData<List<GroupRequestModel>> getRequests() {
        return requests;
    }

    public LiveData<List<GroupRequestModel>> getRequestsPostFinish() {
        return finishRequests;
    }

    public void getRequestsFromServer(){
        requests.removeSource(repositorySource);//if old one is still hanging around
        repositorySource = getGroupRequestsListUseCase.execute(GetGroupRequestsListUseCase.Params.getRequestsForGroup(groupId));

        requests.addSource(repositorySource, new Observer<DomainResource<List<DomainGroupRequest>>>() {
            @Override
            public void onChanged(@Nullable DomainResource<List<DomainGroupRequest>> listDomainGroupRequests) {
                if (listDomainGroupRequests != null) {

                    if (listDomainGroupRequests.status == DomainResource.Status.SUCCESS) {
                        if (listDomainGroupRequests.data != null) {

                            requests.setValue(GroupRequestModelMapper.transformAllFromDomainToModel(listDomainGroupRequests));
                        }
//                        invitations.removeSource(repositorySource); //commenting, so that if cache is updated on update, it gets reflected
                    }
                    else if (listDomainGroupRequests.status == DomainResource.Status.LOADING) {
                        if (listDomainGroupRequests.data != null) {
                            requests.setValue(GroupRequestModelMapper.transformAllFromDomainToModel(listDomainGroupRequests));
                        }
                    }
                    else if (listDomainGroupRequests.status == DomainResource.Status.ERROR) {
                        if (listDomainGroupRequests.data != null) {
                            requests.setValue(GroupRequestModelMapper.transformAllFromDomainToModel(listDomainGroupRequests));
                        }
                        Log.d(TAG, "Coming here when status is error");
                        requests.removeSource(repositorySource);
                    }


                } else {
                    Log.d(TAG, "Coming here when listDomainGroupRequests is null");
                    requests.removeSource(repositorySource);
                }
            }
        });
    }


    public void finishRequest(String groupId, GroupRequestModel groupRequestModel, boolean accept, String groupName, String privateGroup){

        finishRequests.removeSource(finishGroupRequestSource); //if old one is still hanging around
        finishGroupRequestSource = finishRequestUseCase.execute(FinishRequestUseCase.Params.finishForGroupRequest(groupId, GroupRequestModelMapper.getDomainFromGroupRequestModel(groupRequestModel), groupName, privateGroup, accept));
        finishRequests.addSource(finishGroupRequestSource,  new Observer<DomainResource<List<DomainGroupRequest>>>() {
            @Override
            public void onChanged(@Nullable DomainResource<List<DomainGroupRequest>> listDomainGroupRequests) {
                if (listDomainGroupRequests != null) {

                    if (listDomainGroupRequests.status == DomainResource.Status.SUCCESS) {
                        if (listDomainGroupRequests.data != null) {

                            finishRequests.setValue(GroupRequestModelMapper.transformAllFromDomainToModel(listDomainGroupRequests));
                        }
//                        invitations.removeSource(repositorySource); //commenting, so that if cache is updated on update, it gets reflected
                    }
                    else if (listDomainGroupRequests.status == DomainResource.Status.LOADING) {
                        if (listDomainGroupRequests.data != null) {
                            finishRequests.setValue(GroupRequestModelMapper.transformAllFromDomainToModel(listDomainGroupRequests));
                        }
                    }
                    else if (listDomainGroupRequests.status == DomainResource.Status.ERROR) {
                        if (listDomainGroupRequests.data != null) {
                            finishRequests.setValue(GroupRequestModelMapper.transformAllFromDomainToModel(listDomainGroupRequests));
                        }
                        Log.d(TAG, "Coming here when status is error");
                        finishRequests.removeSource(finishGroupRequestSource);
                    }


                } else {
                    Log.d(TAG, "Coming here when listDomainGroupRequests is null");
                    finishRequests.removeSource(finishGroupRequestSource);
                }
            }
        });


    }


}
