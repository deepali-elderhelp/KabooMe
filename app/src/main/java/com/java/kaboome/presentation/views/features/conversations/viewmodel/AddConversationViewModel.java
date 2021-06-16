package com.java.kaboome.presentation.views.features.conversations.viewmodel;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.constants.GeneralStatusContants;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.data.repositories.DataGroupRepository;
import com.java.kaboome.data.repositories.DataGroupsUsersRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.repositories.GroupsUsersRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.domain.usecases.DeleteGroupUseCase;
import com.java.kaboome.domain.usecases.DeleteGroupUserUseCase;
import com.java.kaboome.domain.usecases.GetGroupUseCase;
import com.java.kaboome.domain.usecases.GetGroupUsersUseCase;
import com.java.kaboome.domain.usecases.UpdateGroupUseCase;
import com.java.kaboome.domain.usecases.UpdateGroupUserUseCase;
import com.java.kaboome.domain.usecases.UpdateUserGroupCacheUseCase;
import com.java.kaboome.domain.usecases.UploadImageSingleUseCase;
import com.java.kaboome.domain.usecases.UploadImageUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.mappers.GroupActionContantsMapper;
import com.java.kaboome.presentation.mappers.GroupModelMapper;
import com.java.kaboome.presentation.mappers.GroupUserModelMapper;
import com.java.kaboome.presentation.viewModelProvider.SingleMediatorLiveEvent;
import com.java.kaboome.presentation.views.features.groupInfo.GroupDeleteDetails;
import com.java.kaboome.presentation.views.features.groupInfo.GroupEditDetails;

import java.io.File;
import java.util.Date;
import java.util.List;


public class AddConversationViewModel extends ViewModel {

    private static final String TAG = "KMAddConvViewModel";
    private GetGroupUsersUseCase getGroupUsersUseCase;
    private GetGroupUseCase getGroupUseCase;

    private UpdateUserGroupCacheUseCase updateUserGroupCacheUseCase;
    private GroupRepository groupRepository;
    private GroupsUsersRepository groupsUsersRepository;
    private UserGroupRepository userGroupRepository;

    private String groupId;
//    private LiveData<DomainResource<List<DomainGroupUser>>> repositorySource;
    private MediatorLiveData<DomainResource<DomainGroup>> groupData = new MediatorLiveData<>();
    private LiveData<DomainResource<DomainGroup>> repositorySource;

//    private MediatorLiveData<List<GroupUserModel>> groupUsersForView = new MediatorLiveData<>();

    private LiveData<GroupModel> groupForView;

    public AddConversationViewModel(String groupIdPassed) {

        this.groupId = groupIdPassed;

        groupRepository = DataGroupRepository.getInstance();
        userGroupRepository = DataUserGroupRepository.getInstance();
        updateUserGroupCacheUseCase = new UpdateUserGroupCacheUseCase(userGroupRepository);

        groupsUsersRepository = DataGroupsUsersRepository.getInstance();
        getGroupUsersUseCase = new GetGroupUsersUseCase(groupsUsersRepository);

        groupRepository = DataGroupRepository.getInstance();
        getGroupUseCase = new GetGroupUseCase(groupRepository);

        groupForView = Transformations.switchMap(groupData, new Function<DomainResource<DomainGroup>, LiveData<GroupModel>>() {
            @Override
            public LiveData<GroupModel> apply(final DomainResource<DomainGroup> input) {
                return Transformations.map(getGroupUsersUseCase.execute(GetGroupUsersUseCase.Params.forGroup(groupId, false)),
                        new Function<DomainResource<List<DomainGroupUser>>, GroupModel>() {
                            @Override
                            public GroupModel apply(DomainResource<List<DomainGroupUser>> domainGroupUsers) {
                                if (domainGroupUsers != null) {
                                    return GroupModelMapper.transformAll(input, domainGroupUsers.data);

                                }
                                return null;
                            }

                        });
            }
        });

    }

    public LiveData<GroupModel> getGroupForView() {
        return groupForView;
    }



//    public MediatorLiveData<GroupModel> getGroup() {
//        return group;
//    }

//    public LiveData<List<GroupUserModel>> getGroupUsersForView() {
//        return groupUsersForView;
//    }

    public void loadGroup() {
        Log.d(TAG, "loadInitialList: ");


//        final LiveData<DomainResource<DomainGroup>> repositorySource = getGroupUseCase.execute(GetGroupUseCase.Params.forGroup(groupId));
        groupData.removeSource(repositorySource); //if any old hanging there
        repositorySource = getGroupUseCase.execute(GetGroupUseCase.Params.forGroup(groupId));

        groupData.addSource(repositorySource, new Observer<DomainResource<DomainGroup>>() {
            @Override
            public void onChanged(@Nullable DomainResource<DomainGroup> groupDomainResource) {
                if (groupDomainResource != null) {


                    if (groupDomainResource.status == DomainResource.Status.SUCCESS) {

                        if (groupDomainResource.data != null) {
                            Log.d(TAG, "Status: SUCCESS");
                            groupData.setValue(groupDomainResource);


                        }
//                        group.removeSource(repositorySource); //we keep the source so that cache updates show up immediately
                    } else if (groupDomainResource.status == DomainResource.Status.LOADING) {
                        if (groupDomainResource.data != null) {
                            Log.d(TAG, "Status: LOADING");
                            groupData.setValue(groupDomainResource);
                        }
                    } else if (groupDomainResource.status == DomainResource.Status.ERROR) {
                        Log.d(TAG, "Status: ERROR");
                        groupData.setValue(groupDomainResource);
//                        group.removeSource(repositorySource);
                        groupData.removeSource(repositorySource);
//                        showNoNetworkErrorToast.setValue(true);
                    }
                } else {
//                    group.removeSource(repositorySource);
                    groupData.removeSource(repositorySource);
                }
            }
        });



    }

//    public void loadGroupUsers() {
//        Log.d(TAG, "loadInitialList: ");
//
//
////        final LiveData<DomainResource<DomainGroup>> repositorySource = getGroupUseCase.execute(GetGroupUseCase.Params.forGroup(groupId));
//        repositorySource = getGroupUsersUseCase.execute(GetGroupUsersUseCase.Params.forGroup(groupId, true));
//
//        groupUsersForView.addSource(repositorySource, new Observer<DomainResource<List<DomainGroupUser>>>() {
//                    @Override
//                    public void onChanged(DomainResource<List<DomainGroupUser>> domainGroupUsers) {
//                        if (domainGroupUsers != null) {
//                            if (domainGroupUsers.status == DomainResource.Status.SUCCESS) {
//                                if (domainGroupUsers.data != null) {
//                                    groupUsersForView.setValue(GroupUserModelMapper.transformAllFromDomainToModel(domainGroupUsers.data));
//                                }
//                            }
//                            else if (domainGroupUsers.status == DomainResource.Status.LOADING) {
//                                if (domainGroupUsers.data != null) {
//                                    Log.d(TAG, "Status: LOADING");
//                                    groupUsersForView.setValue(GroupUserModelMapper.transformAllFromDomainToModel(domainGroupUsers.data));
//                                }
//                            } else if (domainGroupUsers.status == DomainResource.Status.ERROR) {
//                                Log.d(TAG, "Status: ERROR");
//                                    groupUsersForView.setValue(GroupUserModelMapper.transformAllFromDomainToModel(domainGroupUsers.data));
//                                    groupUsersForView.removeSource(repositorySource);
//                            }
//                        } else {
//                                groupUsersForView.removeSource(repositorySource);
//                        }
//                    }
//        });
//
//    }




}
















