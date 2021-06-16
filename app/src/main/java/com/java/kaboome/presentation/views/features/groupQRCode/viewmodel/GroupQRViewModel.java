package com.java.kaboome.presentation.views.features.groupQRCode.viewmodel;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.data.repositories.DataGroupRepository;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.usecases.GetGroupUseCase;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.mappers.GroupModelMapper;


public class GroupQRViewModel extends ViewModel {

    private static final String TAG = "KMGroupQRViewModel";
    private GetGroupUseCase getGroupUseCase;
    private LiveData<DomainResource<DomainGroup>> repositorySource;

    private GroupRepository groupRepository;

    private String groupId;

    private MediatorLiveData<GroupModel> group = new MediatorLiveData<>();


    public GroupQRViewModel(String groupIdPassed) {

        this.groupId = groupIdPassed;

        groupRepository = DataGroupRepository.getInstance();
        getGroupUseCase = new GetGroupUseCase(groupRepository);

    }



    public MediatorLiveData<GroupModel> getGroup() {
        return group;
    }


    public void loadGroup() {
        Log.d(TAG, "loadGroup: ");

        group.removeSource(repositorySource); //if any old one is hanging there
        repositorySource = getGroupUseCase.execute(GetGroupUseCase.Params.forGroup(groupId));

        group.addSource(repositorySource, new Observer<DomainResource<DomainGroup>>() {
            @Override
            public void onChanged(@Nullable DomainResource<DomainGroup> groupDomainResource) {
                if (groupDomainResource != null) {


                    if (groupDomainResource.status == DomainResource.Status.SUCCESS) {

                        if (groupDomainResource.data != null) {

                            group.setValue(GroupModelMapper.getModelFromGroupDomain(groupDomainResource.data));


                        }
                        group.removeSource(repositorySource); //we keep the source so that cache updates show up immediately
                    } else if (groupDomainResource.status == DomainResource.Status.LOADING) {
                        if (groupDomainResource.data != null) {
                            group.setValue(GroupModelMapper.getModelFromGroupDomain(groupDomainResource.data));
                        }
                    } else if (groupDomainResource.status == DomainResource.Status.ERROR) {
                        group.setValue(GroupModelMapper.getModelFromGroupDomain(groupDomainResource.data));
                        group.removeSource(repositorySource);
                    }
                } else {
                    group.removeSource(repositorySource);
                }
            }
        });



    }


}
















