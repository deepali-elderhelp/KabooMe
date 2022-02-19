package com.java.kaboome.presentation.views.features.groupInfo.viewmodel;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

import com.java.kaboome.constants.CreateGroupStatusContants;
import com.java.kaboome.constants.GeneralStatusContants;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.MediaActionConstants;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.repositories.DataGroupRepository;
import com.java.kaboome.data.repositories.DataGroupsUsersRepository;
import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.data.workers.LoadMediaWorker;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.repositories.GroupsUsersRepository;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GroupViewModel extends ViewModel {

    private static final String TAG = "KMGroupInfoViewModel";
    private GetGroupUseCase getGroupUseCase;
    private GetGroupUsersUseCase getGroupUsersUseCase;
    private UpdateGroupUseCase updateGroupUseCase;
    private UpdateGroupUserUseCase updateGroupUserUseCase;
    private DeleteGroupUseCase deleteGroupUseCase;
    private DeleteGroupUserUseCase deleteGroupUserUseCase;
    private UpdateUserGroupCacheUseCase updateUserGroupCacheUseCase;
    private GroupRepository groupRepository;
    private GroupsUsersRepository groupsUsersRepository;
    private UserGroupRepository userGroupRepository;
    private UploadImageUseCase uploadImageUseCase;
    private UploadImageSingleUseCase uploadImageSingleUseCase;
    private ImageUploadRepository imageUploadRepository;
    private String groupId;
    private Long timestamp;
    private LiveData<DomainResource<DomainGroup>> repositorySource;
    private LiveData<DomainUpdateResource<String>> updateRepositorySource;
    private LiveData<DomainUpdateResource<String>> updateGroupUserRepositorySource;
    private LiveData<DomainUpdateResource<String>> deleteGroupRepositorySource;


//    private MutableLiveData<DomainResource<DomainGroup>> groupData = new MutableLiveData<>();
    private MediatorLiveData<DomainResource<DomainGroup>> groupData = new MediatorLiveData<>();
//    private MediatorLiveData<GroupEditDetails> groupEditActionUpdate = new MediatorLiveData<>();
    private SingleMediatorLiveEvent<GroupEditDetails> groupEditActionUpdate = new SingleMediatorLiveEvent<>();
//    private MediatorLiveData<GroupEditDetails> groupUserEditActionUpdate = new MediatorLiveData<>();
    private SingleMediatorLiveEvent<GroupEditDetails> groupUserEditActionUpdate = new SingleMediatorLiveEvent<>();
//    private MutableLiveData<GroupDeleteDetails> groupDeleteActionResult = new MutableLiveData<>();
//    private MediatorLiveData<GroupModel> group = new MediatorLiveData<>();
//    private MediatorLiveData<String> groupDelete = new MediatorLiveData<>();
    private MediatorLiveData<GroupDeleteDetails> groupDelete = new MediatorLiveData<>();
    private SingleMediatorLiveEvent<GeneralStatusContants> manageImage;
//    private MediatorLiveData<GeneralStatusContants> manageImage = new MediatorLiveData<>();
//    private SingleMediatorLiveEvent<GeneralStatusContants> manageImage = new SingleMediatorLiveEvent<>();


//    private MutableLiveData<GroupEditDetails> groupEditActionUpdate = new MutableLiveData<>();
//    private MutableLiveData<GroupEditDetails> groupUserEditActionUpdate = new MutableLiveData<>();


//    private MediatorLiveData<String> groupUpdate = new MediatorLiveData<>();
//    private MediatorLiveData<String> groupUserUpdate = new MediatorLiveData<>();

//    private MediatorLiveData<GroupEditDetails> uploadingGroupImage = new MediatorLiveData<>();


    private LiveData<GroupModel> groupForView;

    public GroupViewModel(String groupIdPassed) {

        this.groupId = groupIdPassed;

        //register the receivers
//        registerReceivers();
        groupRepository = DataGroupRepository.getInstance();
        getGroupUseCase = new GetGroupUseCase(groupRepository);

        userGroupRepository = DataUserGroupRepository.getInstance();
        updateUserGroupCacheUseCase = new UpdateUserGroupCacheUseCase(userGroupRepository);

        groupsUsersRepository = DataGroupsUsersRepository.getInstance();
        getGroupUsersUseCase = new GetGroupUsersUseCase(groupsUsersRepository);

        updateGroupUseCase = new UpdateGroupUseCase(groupRepository);
        updateGroupUserUseCase = new UpdateGroupUserUseCase(groupsUsersRepository);
        deleteGroupUseCase = new DeleteGroupUseCase(groupRepository);
        deleteGroupUserUseCase = new DeleteGroupUserUseCase(groupRepository);

        imageUploadRepository = DataImageUploadRepository.getInstance();
        uploadImageUseCase = new UploadImageUseCase(imageUploadRepository);
        uploadImageSingleUseCase = new UploadImageSingleUseCase(imageUploadRepository);


//        showToast.setValue(false);

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



//        groupForView = Transformations.switchMap(groupData, new Function<DomainResource<DomainGroup>, LiveData<GroupModel>>() {
//            @Override
//            public LiveData<GroupModel> apply(final DomainResource<DomainGroup> input) {
//                return Transformations.map(getGroupUsersUseCase.execute(GetGroupUsersUseCase.Params.forGroup(groupId, false)), new Function<List<DomainGroupUser>, GroupModel>() {
//                    @Override
//                    public GroupModel apply(List<DomainGroupUser> groupUsers) {
//                        Log.d(TAG, "apply: count " + groupUsers.size());
//                        return GroupModelMapper.transformAll(input, groupUsers);
//                    }
//                });
//            }


//            @Override
//            public LiveData<List<UserGroupModel>> apply(final DomainResource<List<DomainUserGroup>> input) {
//                Log.d(TAG, "apply: input " + input);
//                return Transformations.map(getGroupsUnreadCountUseCase.execute(null), new Function<List<DomainGroupUnreadData>, List<UserGroupModel>>() {
//                    @Override
//                    public List<UserGroupModel> apply(List<DomainGroupUnreadData> counts) {
//                        Log.d(TAG, "apply: count " + counts);
//                        return UserGroupModelMapper.transformAll(input, counts);
//                    }
//                });
//
//
//            }
//    });


//    }



//    public MediatorLiveData<GroupModel> getGroup() {
//        return group;
//    }

    public LiveData<GroupModel> getGroupForView() {
        return groupForView;
    }

//    public MediatorLiveData<String> getGroupUpdate() {return groupUpdate; }

//    public MediatorLiveData<String> getGroupUserUpdate() {return groupUserUpdate; }

//    public MediatorLiveData<String> getGroupDelete() {return groupDelete; }
    public MediatorLiveData<GroupDeleteDetails> getGroupDelete() {return groupDelete; }

    public SingleMediatorLiveEvent<GroupEditDetails> getGroupEditActionUpdate() {
        return groupEditActionUpdate;
    }

    public SingleMediatorLiveEvent<GroupEditDetails> getGroupUserEditActionUpdate() {
        return groupUserEditActionUpdate;
    }

//    public MutableLiveData<GroupDeleteDetails> getGroupDeleteActionResult() {
//        return groupDeleteActionResult;
//    }

//    public MediatorLiveData<GroupEditDetails> getUploadingGroupImage() {
//        return uploadingGroupImage;
//    }

//    public void loadGroup() {
//        Log.d(TAG, "loadInitialList: ");
//
//
////        final LiveData<DomainResource<DomainGroup>> repositorySource = getGroupUseCase.execute(GetGroupUseCase.Params.forGroup(groupId));
//        repositorySource = getGroupUseCase.execute(GetGroupUseCase.Params.forGroup(groupId));
//
//        group.addSource(repositorySource, new Observer<DomainResource<DomainGroup>>() {
//            @Override
//            public void onChanged(@Nullable DomainResource<DomainGroup> groupDomainResource) {
//                if (groupDomainResource != null) {
//
//
//                    if (groupDomainResource.status == DomainResource.Status.SUCCESS) {
//
//                        if (groupDomainResource.data != null) {
//
//                            groupData.setValue(groupDomainResource);
//
//
//                        }
////                        group.removeSource(repositorySource); //we keep the source so that cache updates show up immediately
//                    } else if (groupDomainResource.status == DomainResource.Status.LOADING) {
//                        if (groupDomainResource.data != null) {
//                            groupData.setValue(groupDomainResource);
//                        }
//                    } else if (groupDomainResource.status == DomainResource.Status.ERROR) {
//                        groupData.setValue(groupDomainResource);
//                        group.removeSource(repositorySource);
////                        showNoNetworkErrorToast.setValue(true);
//                    }
//                } else {
//                    group.removeSource(repositorySource);
//                }
//            }
//        });
//
//
//
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

    public void updateGroup(final GroupModel groupModel, final String action) {

        if(action.equals(GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction()) && groupModel.isImageChanged()){
            groupModel.setGroupPicLoadingGoingOn(true);
            groupModel.setGroupPicUploaded(false);
        }
        else{
            groupModel.setGroupPicLoadingGoingOn(false);
            groupModel.setGroupPicUploaded(true);
        }
        updateGroupToServer(groupModel, action);
    }

//    public void updateGroup(final GroupModel groupModel, final String action) {
//        manageImage = new SingleMediatorLiveEvent<>();
//        groupEditActionUpdate.addSource(manageImage, new Observer<GeneralStatusContants>() {
//            @Override
//            public void onChanged(GeneralStatusContants generalStatusContants) {
//                Log.d(TAG, "onChanged: Status is - "+generalStatusContants);
//                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.SUCCESS){
//                    if(groupModel.isImageChanged()){
//                        //update the timestamp only if image changed
//                        timestamp = new Date().getTime();
//                        groupModel.setImageUpdateTimestamp(timestamp);
//                    }
//                    updateGroupToServer(groupModel, action);
//                    groupEditActionUpdate.removeSource(manageImage);
//                }
//                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.LOADING){
//                    groupEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(action), GroupEditDetails.Status.UPDATING, timestamp));
//                }
//                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.ERROR){
//                    Log.d(TAG, "Error uploading new image, not going ahead with saving anything on server");
//                    groupEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(action), GroupEditDetails.Status.ERROR, timestamp));
//                    groupEditActionUpdate.removeSource(manageImage);
//                }
//            }
//        });
//
//        uploadGroupImage(groupModel);
//    }

    /**
     * This method is called when the SAVE button is pressed on any update
     * @param groupModel
     */
    public void updateGroupToServer(final GroupModel groupModel, final String action) {

        Log.d(TAG, "updateGroup: Group Id here is "+groupModel.getGroupId());

//        updateRepositorySource = updateGroupUseCase.execute(UpdateGroupUseCase.Params.groupUpdated(GroupModelMapper.getDomainFromGroupModel(groupModel), action));
        updateRepositorySource = updateGroupUseCase.execute(UpdateGroupUseCase.Params.groupUpdated(GroupModelMapper.getDomainFromGroupModelWithUsers(groupModel), action));

        groupEditActionUpdate.addSource(updateRepositorySource, new Observer<DomainUpdateResource<String>>() {
            @Override
            public void onChanged(DomainUpdateResource<String> stringDomainUpdateResource) {

                Log.d(TAG, "onChanged: update changed - coming here");
                if (stringDomainUpdateResource != null) {


                    if (stringDomainUpdateResource.status == DomainUpdateResource.Status.SUCCESS) {

                        //also update the user group cache
                        //so that the changes get reflected in the group home page
                        updateUserGroupCache(groupModel, action);
                        //if the action was for uploading the image, start the image upload now
                        if(action.equals(GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction()) && groupModel.isImageChanged()){
                            uploadGroupTNImage(groupModel);
                            uploadGroupMainImage(groupModel);
                        }
                        if (stringDomainUpdateResource.data != null) {
                            groupEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(stringDomainUpdateResource.data), GroupEditDetails.Status.SUCCESS, timestamp, groupModel.getImagePath()));
                        }
                        groupEditActionUpdate.removeSource(updateRepositorySource);
                    } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.UPDATING) {
                        if (stringDomainUpdateResource.data != null) {
//                            showToast.setValue(stringDomainUpdateResource.data +" updating");
                            groupEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(stringDomainUpdateResource.data), GroupEditDetails.Status.UPDATING, timestamp, groupModel.getImagePath()));
                        }
                    } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.ERROR) {
//                        showToast.setValue(stringDomainUpdateResource.data +" errored");
                        groupEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(stringDomainUpdateResource.data), GroupEditDetails.Status.ERROR, timestamp, groupModel.getImagePath()));
                        groupEditActionUpdate.removeSource(updateRepositorySource);
//                        showNoNetworkErrorToast.setValue(true);
                    }
                } else {
                    groupEditActionUpdate.removeSource(updateRepositorySource);
                }
            }
        });
    }

    /**
     * This method is called when the SAVE button is pressed on any update
     * of the Group User
     * @param groupUserModel
     */

    public void updateGroupUser(final GroupUserModel groupUserModel, final String action){

        if(action.equals(GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS.getAction()) && groupUserModel.isImageChanged()){
            groupUserModel.setGroupUserPicLoadingGoingOn(true);
            groupUserModel.setGroupUserPicUploaded(false);
        }
        else{
            groupUserModel.setGroupUserPicLoadingGoingOn(false);
            groupUserModel.setGroupUserPicUploaded(true);
        }
        updateGroupUserToServer(groupUserModel, action);

        //first upload image and then data to server
        //it should work in any case because for any case, if the image is not changed
        //it comes back right away
//        manageImage = new SingleMediatorLiveEvent<>();
//        groupUserEditActionUpdate.addSource(manageImage, new Observer<GeneralStatusContants>() {
//            @Override
//            public void onChanged(GeneralStatusContants generalStatusContants) {
//                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.SUCCESS){
//                    if(groupUserModel.isImageChanged()){
//                        //update the timestamp only if image changed
//                        timestamp = new Date().getTime();
//                        groupUserModel.setImageUpdateTimestamp(timestamp);
//                    }
//                    updateGroupUserToServer(groupUserModel, action);
//                    groupUserEditActionUpdate.removeSource(manageImage);
//                }
//                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.LOADING){
//                    groupUserEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(action), GroupEditDetails.Status.UPDATING, timestamp));
//                }
//                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.ERROR){
//                    Log.d(TAG, "Error uploading new image, not going ahead with saving anything on server");
//                    groupUserEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(action), GroupEditDetails.Status.ERROR, timestamp));
//                    groupUserEditActionUpdate.removeSource(manageImage);
//                }
//            }
//        });

//        uploadGroupUserImage(groupUserModel);


    }




    private void updateGroupUserToServer(final GroupUserModel groupUserModel, final String action) {


        updateGroupUserRepositorySource = updateGroupUserUseCase.execute(UpdateGroupUserUseCase.Params.groupUserUpdated(GroupUserModelMapper.getDomainFromGroupUserModel(groupUserModel), action));


        groupUserEditActionUpdate.addSource(updateGroupUserRepositorySource, new Observer<DomainUpdateResource<String>>() {
            @Override
            public void onChanged(DomainUpdateResource<String> stringDomainUpdateResource) {

                Log.d(TAG, "onChanged: update changed - coming here");
                if (stringDomainUpdateResource != null) {


                    if (stringDomainUpdateResource.status == DomainUpdateResource.Status.SUCCESS) {
                        //update the user group cache to see these changes
                        updateUserGroupCache(groupUserModel, action);
                        //if the action was for uploading the image, start the image upload now
                        if(action.equals(GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS.getAction()) && groupUserModel.isImageChanged()){
                            uploadGroupUserImage(groupUserModel, ImageTypeConstants.MAIN);
                            uploadGroupUserImage(groupUserModel, ImageTypeConstants.THUMBNAIL);
                        }
                        if (stringDomainUpdateResource.data != null) {
                            groupUserEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(stringDomainUpdateResource.data), GroupEditDetails.Status.SUCCESS, timestamp));
                        }
                        groupUserEditActionUpdate.removeSource(updateGroupUserRepositorySource);
                    } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.UPDATING) {
                        if (stringDomainUpdateResource.data != null) {
//                            showToast.setValue(stringDomainUpdateResource.data +" updating");
                            groupUserEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(stringDomainUpdateResource.data), GroupEditDetails.Status.UPDATING, timestamp));
                        }
                    } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.ERROR) {
//                        showToast.setValue(stringDomainUpdateResource.data +" errored");
                        groupUserEditActionUpdate.setValue(new GroupEditDetails(GroupActionContantsMapper.getConstant(stringDomainUpdateResource.data), GroupEditDetails.Status.ERROR, timestamp));
                        groupUserEditActionUpdate.removeSource(updateGroupUserRepositorySource);
//                        showNoNetworkErrorToast.setValue(true);
                    }
                } else {
                    groupUserEditActionUpdate.removeSource(updateGroupUserRepositorySource);
                }
            }
        });
    }



    /**
     * TODO:Are we updating local UserGroup cache for this?
     * @param groupId
     * @param userDeleting
     * @param userGettingDeleted
     * @param action
     */
    public void deleteGroup(final String groupId, String userDeleting, final String userGettingDeleted, final GroupActionConstants action) {

        if(action == GroupActionConstants.REMOVE_GROUP_FOR_ALL){
            deleteGroupRepositorySource = deleteGroupUseCase.execute(DeleteGroupUseCase.Params.groupToBeDeleted(groupId, action.getAction()));
        }
        if(action == GroupActionConstants.REMOVE_GROUP_FOR_USER){
            deleteGroupRepositorySource = deleteGroupUserUseCase.execute(DeleteGroupUserUseCase.Params.groupToBeDeleted(groupId, userDeleting, userGettingDeleted, action.getAction()));
        }
        if(action == GroupActionConstants.REMOVE_GROUP_FOR_OTHER_USER){
            deleteGroupRepositorySource = deleteGroupUserUseCase.execute(DeleteGroupUserUseCase.Params.groupToBeDeleted(groupId, userDeleting, userGettingDeleted, action.getAction()));
        }


        groupDelete.addSource(deleteGroupRepositorySource, new Observer<DomainUpdateResource<String>>() {
            @Override
            public void onChanged(DomainUpdateResource<String> stringDomainDeleteResource) {

                Log.d(TAG, "onChanged: update changed - coming here");
                if (stringDomainDeleteResource != null) {


                    if (stringDomainDeleteResource.status == DomainUpdateResource.Status.SUCCESS) {

                        //also update the user group cache if the user deleted the group for himself
                        //so that the changes get reflected in the group home page
                        if(action.equals(GroupActionConstants.REMOVE_GROUP_FOR_OTHER_USER)){
                            //remove the user from GroupUsers

                        }
                        else{
                            //delete the local messages and media


                            //the group has been deleted either for all or for this user
                            //following things need to happen -
                            //Deleted only for this user -
                            //1. Update UserGroup cache for the marking the group isDeleted
                            //2. Delete the local cache messages for this group
                            //3. Delete the local downloaded media for this group
                            //Deleted for all -
                            //Do all the above three things and also
                            //4. On the server delete all the messages of the group
                            //5. On S3 delete all the media of the group
                            // For all the other users, when the user starts the app
                            // the group will be gone and also, all the group media and group messages
                            // will be deleted on app startup.

                            //the line below will take care of 1.
                            updateUserGroupCache(new GroupModel(groupId), action.getAction());
                            //no need to handle 2. and 3. because the user will go back to GLF from here
                            //and GLF will take care of that

                        }

                        if (stringDomainDeleteResource.data != null) {
//                            groupDeleteActionResult.setValue(new GroupDeleteDetails(GroupActionContantsMapper.getConstant(stringDomainDeleteResource.data), GroupDeleteDetails.Status.SUCCESS));
                            groupDelete.setValue(new GroupDeleteDetails(GroupActionContantsMapper.getConstant(stringDomainDeleteResource.data), GroupDeleteDetails.Status.SUCCESS));
                        }
                        groupDelete.removeSource(deleteGroupRepositorySource);
                    } else if (stringDomainDeleteResource.status == DomainUpdateResource.Status.UPDATING) {
                        if (stringDomainDeleteResource.data != null) {
//                            showToast.setValue(stringDomainUpdateResource.data +" updating");
                            groupDelete.setValue(new GroupDeleteDetails(GroupActionContantsMapper.getConstant(stringDomainDeleteResource.data), GroupDeleteDetails.Status.DELETING));
                        }
                    } else if (stringDomainDeleteResource.status == DomainUpdateResource.Status.ERROR) {
//                        showToast.setValue(stringDomainUpdateResource.data +" errored");
                        groupDelete.setValue(new GroupDeleteDetails(GroupActionContantsMapper.getConstant(stringDomainDeleteResource.data), GroupDeleteDetails.Status.ERROR));
                        groupDelete.removeSource(deleteGroupRepositorySource);
//                        showNoNetworkErrorToast.setValue(true);
                    }
                } else {
                    groupDelete.removeSource(deleteGroupRepositorySource);
                }
            }
        });
    }

    /**
     * This method updates the local "UserGroup" based upon the changes done to the group
     * (do not confuse it with GroupUser)
     * @param groupModel
     * @param action
     */
    private void updateUserGroupCache(GroupModel groupModel, String action){
        DomainUserGroup domainUserGroup = new DomainUserGroup();
        domainUserGroup.setUserId(AppConfigHelper.getUserId());
        domainUserGroup.setGroupId(groupModel.getGroupId());
        domainUserGroup.setGroupName(groupModel.getGroupName());
        domainUserGroup.setPrivateGroup(groupModel.getGroupPrivate());
        domainUserGroup.setExpiry(groupModel.getExpiryDate());
        domainUserGroup.setImageUpdateTimestamp(groupModel.getImageUpdateTimestamp());
        domainUserGroup.setGroupPicLoadingGoingOn(groupModel.getGroupPicLoadingGoingOn());
        domainUserGroup.setGroupPicUploaded(groupModel.getGroupPicUploaded());
        updateUserGroupCacheUseCase.execute(UpdateUserGroupCacheUseCase.Params.forUserGroup(domainUserGroup, action));
    }

    /**
     * This method updates the local "UserGroup" based upon the changes done to the GroupUser
     * @param groupUserModel
     * @param action
     */
    private void updateUserGroupCache(GroupUserModel groupUserModel, String action){
        DomainUserGroup domainUserGroup = new DomainUserGroup();
        domainUserGroup.setUserId(AppConfigHelper.getUserId());
        domainUserGroup.setGroupId(groupUserModel.getGroupId());
        domainUserGroup.setAlias(groupUserModel.getAlias());
        domainUserGroup.setGroupAdminRole(groupUserModel.getRole());
        domainUserGroup.setNotify(groupUserModel.getNotify());
        domainUserGroup.setIsAdmin(groupUserModel.getIsAdmin());
        updateUserGroupCacheUseCase.execute(UpdateUserGroupCacheUseCase.Params.forUserGroup(domainUserGroup, action));


    }


    public void uploadGroupMainImage(final GroupModel groupModel) {

        //here you should call the worker to upload the group image
        Data inputData = new Data.Builder()
                .putString("groupId", groupModel.getGroupId())
                .putString("groupName", groupModel.getGroupName())
                .putString("imageType", ImageTypeConstants.MAIN.getType())
                .putString("action", MediaActionConstants.UPLOAD_GROUP_PIC.getAction())
                .putString("attachment_path", groupModel.getImagePath())
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(LoadMediaWorker.class)
                .addTag("upload_group_pic")
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
                        Log.d(TAG, "Group Pic Uploaded successfully");
                        resultOfOperation.getResult().get();

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //if the update API gave error, it gets wrapped in ExecutionException
                        Log.d(TAG, "Group Pic upload failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Group Pic upload failed due to "+e.getMessage());
                    }
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Group Pic upload failed due to - "+e.getMessage());
        }

/***
        if(!groupModel.isImageChanged()){
            manageImage.setValue(GeneralStatusContants.SUCCESS);
            return;
        }

        Log.d(TAG, "uploadImage: ");
        String key = ImagesUtilHelper.getGroupImageName(groupModel.getGroupId(), ImageTypeConstants.MAIN);
        final String tnKey = ImagesUtilHelper.getGroupImageName(groupModel.getGroupId(), ImageTypeConstants.THUMBNAIL);
        HashMap<String, Object> userData = new HashMap<>();
        final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(groupModel.getImagePath()), key, GroupActionConstants.UPDATE_GROUP_IMAGE.getAction(), userData));

        manageImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
            @Override
            public void onChanged(@Nullable DomainUpdateResource<String> groupImageUpdateDomainResource) {
                Log.d(TAG, "onChanged: upload status changed");
                if (groupImageUpdateDomainResource != null) {

                    if (groupImageUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
                        manageImage.setValue(GeneralStatusContants.SUCCESS);
                        manageImage.removeSource(uploadImageRespositorySource);

                        //start the upload of the thumbnail size image - this is reverse of the usual behavior since
                        //the whole image is what the user will be seeing
                        uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(tnKey, new File(groupModel.getThumbnailPath()), true));

                    } else if (groupImageUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
                        if (groupImageUpdateDomainResource.data != null) {
                            manageImage.setValue(GeneralStatusContants.LOADING);
                        }
                    } else if (groupImageUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
                        manageImage.setValue(GeneralStatusContants.ERROR);
                        manageImage.removeSource(uploadImageRespositorySource);
                    }
                } else {
                    manageImage.removeSource(uploadImageRespositorySource);
                }
            }
        });
**///
//        final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(groupModel.getImagePath()), key, GroupActionConstants.UPDATE_GROUP_IMAGE.getAction()));
//
//        uploadingGroupImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//            @Override
//            public void onChanged(@Nullable DomainUpdateResource<String> groupUpdateDomainResource) {
//                Log.d(TAG, "onChanged: upload status changed");
//                if (groupUpdateDomainResource != null) {
//
//
//                    if (groupUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//
//                        //image upload was successful, now update the database image timestamps accordingly
//                        //by calling the updateUser
//                        timestamp = new Date().getTime(); //using this time of success as the timestamp for now
//                        //later should find a way to get the correct timestamp from s3
//                        groupModel.setImageUpdateTimestamp(timestamp);
//                        updateGroup(groupModel, GroupActionConstants.UPDATE_GROUP_IMAGE.getAction());
//
//                        //update the signature - so that glide uploads the new image
////                        AppConfigHelper.increaseUserImageSignature();
//
//                        if (groupUpdateDomainResource.data != null) {
//                            //update the local timestamp as well
//                            //this local timestamp is needed in other methods
//                            //like the user creating a new request etc.
//                            uploadingGroupImage.setValue(new GroupEditDetails(GroupActionConstants.UPDATE_GROUP_IMAGE, GroupEditDetails.Status.SUCCESS, timestamp));
//                        }
//                        uploadingGroupImage.removeSource(uploadImageRespositorySource);
//                    } else if (groupUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                        if (groupUpdateDomainResource.data != null) {
////                            showToast.setValue(stringDomainUpdateResource.data +" updating");
//                            uploadingGroupImage.setValue(new GroupEditDetails(GroupActionConstants.UPDATE_GROUP_IMAGE, GroupEditDetails.Status.UPDATING, timestamp));
//                        }
//                    } else if (groupUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
////                        showToast.setValue(stringDomainUpdateResource.data +" errored");
//                        uploadingGroupImage.setValue(new GroupEditDetails(GroupActionConstants.UPDATE_GROUP_IMAGE, GroupEditDetails.Status.ERROR, timestamp));
//                        uploadingGroupImage.removeSource(uploadImageRespositorySource);
////                        showNoNetworkErrorToast.setValue(true);
//                    }
//                } else {
//                    uploadingGroupImage.removeSource(uploadImageRespositorySource);
//                }
//            }
//        });

    }

    public void uploadGroupTNImage(final GroupModel groupModel) {

        //here you should call the worker to upload the group image
        Data inputData = new Data.Builder()
                .putString("groupId", groupModel.getGroupId())
                .putString("groupName", groupModel.getGroupName())
                .putString("imageType", ImageTypeConstants.THUMBNAIL.getType())
                .putString("action", MediaActionConstants.UPLOAD_GROUP_PIC.getAction())
                .putString("attachment_path", groupModel.getThumbnailPath())
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(LoadMediaWorker.class)
                .addTag("upload_group_pic")
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
                        Log.d(TAG, "Group TN Pic Uploaded successfully");
                        resultOfOperation.getResult().get();

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //if the update API gave error, it gets wrapped in ExecutionException
                        Log.d(TAG, "Group TN Pic upload failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Group TN Pic upload failed due to "+e.getMessage());
                    }
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Group TN Pic upload failed due to - "+e.getMessage());
        }

/***
 if(!groupModel.isImageChanged()){
 manageImage.setValue(GeneralStatusContants.SUCCESS);
 return;
 }

 Log.d(TAG, "uploadImage: ");
 String key = ImagesUtilHelper.getGroupImageName(groupModel.getGroupId(), ImageTypeConstants.MAIN);
 final String tnKey = ImagesUtilHelper.getGroupImageName(groupModel.getGroupId(), ImageTypeConstants.THUMBNAIL);
 HashMap<String, Object> userData = new HashMap<>();
 final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(groupModel.getImagePath()), key, GroupActionConstants.UPDATE_GROUP_IMAGE.getAction(), userData));

 manageImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
@Override
public void onChanged(@Nullable DomainUpdateResource<String> groupImageUpdateDomainResource) {
Log.d(TAG, "onChanged: upload status changed");
if (groupImageUpdateDomainResource != null) {

if (groupImageUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
manageImage.setValue(GeneralStatusContants.SUCCESS);
manageImage.removeSource(uploadImageRespositorySource);

//start the upload of the thumbnail size image - this is reverse of the usual behavior since
//the whole image is what the user will be seeing
uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(tnKey, new File(groupModel.getThumbnailPath()), true));

} else if (groupImageUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
if (groupImageUpdateDomainResource.data != null) {
manageImage.setValue(GeneralStatusContants.LOADING);
}
} else if (groupImageUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
manageImage.setValue(GeneralStatusContants.ERROR);
manageImage.removeSource(uploadImageRespositorySource);
}
} else {
manageImage.removeSource(uploadImageRespositorySource);
}
}
});
 **///
//        final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(groupModel.getImagePath()), key, GroupActionConstants.UPDATE_GROUP_IMAGE.getAction()));
//
//        uploadingGroupImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//            @Override
//            public void onChanged(@Nullable DomainUpdateResource<String> groupUpdateDomainResource) {
//                Log.d(TAG, "onChanged: upload status changed");
//                if (groupUpdateDomainResource != null) {
//
//
//                    if (groupUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//
//                        //image upload was successful, now update the database image timestamps accordingly
//                        //by calling the updateUser
//                        timestamp = new Date().getTime(); //using this time of success as the timestamp for now
//                        //later should find a way to get the correct timestamp from s3
//                        groupModel.setImageUpdateTimestamp(timestamp);
//                        updateGroup(groupModel, GroupActionConstants.UPDATE_GROUP_IMAGE.getAction());
//
//                        //update the signature - so that glide uploads the new image
////                        AppConfigHelper.increaseUserImageSignature();
//
//                        if (groupUpdateDomainResource.data != null) {
//                            //update the local timestamp as well
//                            //this local timestamp is needed in other methods
//                            //like the user creating a new request etc.
//                            uploadingGroupImage.setValue(new GroupEditDetails(GroupActionConstants.UPDATE_GROUP_IMAGE, GroupEditDetails.Status.SUCCESS, timestamp));
//                        }
//                        uploadingGroupImage.removeSource(uploadImageRespositorySource);
//                    } else if (groupUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                        if (groupUpdateDomainResource.data != null) {
////                            showToast.setValue(stringDomainUpdateResource.data +" updating");
//                            uploadingGroupImage.setValue(new GroupEditDetails(GroupActionConstants.UPDATE_GROUP_IMAGE, GroupEditDetails.Status.UPDATING, timestamp));
//                        }
//                    } else if (groupUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
////                        showToast.setValue(stringDomainUpdateResource.data +" errored");
//                        uploadingGroupImage.setValue(new GroupEditDetails(GroupActionConstants.UPDATE_GROUP_IMAGE, GroupEditDetails.Status.ERROR, timestamp));
//                        uploadingGroupImage.removeSource(uploadImageRespositorySource);
////                        showNoNetworkErrorToast.setValue(true);
//                    }
//                } else {
//                    uploadingGroupImage.removeSource(uploadImageRespositorySource);
//                }
//            }
//        });

    }



    public void uploadGroupUserImage(final GroupUserModel groupUserModel, final ImageTypeConstants imageType){
        Data inputData = new Data.Builder()
                .putString("groupId", groupUserModel.getGroupId())
                .putString("userId", groupUserModel.getUserId())
                .putString("groupUserName", groupUserModel.getAlias())
                .putString("groupUserRole", groupUserModel.getRole())
                .putString("imageType", imageType.getType())
                .putString("action", MediaActionConstants.UPLOAD_GROUP_USER_PIC.getAction())
                .putString("attachment_path", groupUserModel.getImagePath())
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(LoadMediaWorker.class)
                .addTag("upload_group_user_pic")
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
                        Log.d(TAG, "Group User Pic "+imageType.getType()+" Uploaded successfully");
                        resultOfOperation.getResult().get();

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //if the update API gave error, it gets wrapped in ExecutionException
                        Log.d(TAG, "Group User Pic "+imageType.getType()+"upload failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Group User Pic "+imageType.getType()+"upload failed due to "+e.getMessage());
                    }
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Group User Pic "+imageType.getType()+"upload failed due to - "+e.getMessage());
        }
    }


    public void uploadGroupUserImage(final GroupUserModel groupUserModel) {

//        if(!imageChanged){
//            //user did not use any new image, so the regular profile image is the group user image
//            //it should be copied to s3
//            String userProfilePicKey = ImagesUtilHelper.getUserProfilePicName(groupUserModel.getUserId());
//            String newKey = ImagesUtilHelper.getGroupUserImageName(groupUserModel.getGroupId(), groupUserModel.getUserId());
//            final LiveData<DomainUpdateResource<String>> copyImageRespositorySource = copyImageUseCase.execute(CopyImageUseCase.Params.imageToBeCopied(userProfilePicKey, newKey, GroupActionConstants.UPDATE_GROUP_USER_IMAGE.getAction()));
//            manageImage.addSource(copyImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//                @Override
//                public void onChanged(DomainUpdateResource<String> stringDomainUpdateResource) {
//                    Log.d(TAG, "onChanged: upload status changed");
//                    if (stringDomainUpdateResource != null) {
//
//                        if (stringDomainUpdateResource.status == DomainUpdateResource.Status.SUCCESS) {
//                            manageImage.setValue(GeneralStatusContants.SUCCESS);
//                            manageImage.removeSource(copyImageRespositorySource);
//                        } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.UPDATING) {
//                            if (stringDomainUpdateResource.data != null) {
//                                manageImage.setValue(GeneralStatusContants.LOADING);
//                            }
//                        } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.ERROR) {
//                            manageImage.setValue(GeneralStatusContants.ERROR);
//                            manageImage.removeSource(copyImageRespositorySource);
//                        }
//                    } else {
//                        manageImage.removeSource(copyImageRespositorySource);
//                    }
//                }
//            });
//
//        }
//        else{
        if(!groupUserModel.isImageChanged()){
            manageImage.setValue(GeneralStatusContants.SUCCESS);
            return;
        }
            Log.d(TAG, "uploadGroupUserImage: ");
            String tnKey = ImagesUtilHelper.getGroupUserImageName(groupUserModel.getGroupId(), groupUserModel.getUserId(),ImageTypeConstants.THUMBNAIL );
            final String key = ImagesUtilHelper.getGroupUserImageName(groupUserModel.getGroupId(), groupUserModel.getUserId(),ImageTypeConstants.MAIN );

            HashMap<String, Object> userData = new HashMap<>();
            final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(groupUserModel.getThumbnailPath()), tnKey, GroupActionConstants.UPDATE_GROUP_USER_IMAGE.getAction(), userData));

            manageImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
                @Override
                public void onChanged(@Nullable DomainUpdateResource<String> groupUserImageUpdateDomainResource) {
                    Log.d(TAG, "onChanged: upload status changed");
                    if (groupUserImageUpdateDomainResource != null) {

                        if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
                            manageImage.setValue(GeneralStatusContants.SUCCESS);
                            manageImage.removeSource(uploadImageRespositorySource);

                            //start the upload of the normal size image
                            uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(key, new File(groupUserModel.getImagePath()), true));

                        } else if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
                            if (groupUserImageUpdateDomainResource.data != null) {
                                manageImage.setValue(GeneralStatusContants.LOADING);
                            }
                        } else if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
                            manageImage.setValue(GeneralStatusContants.ERROR);
                            manageImage.removeSource(uploadImageRespositorySource);
                        }
                    } else {
                        manageImage.removeSource(uploadImageRespositorySource);
                    }
                }
            });
//        }



    }

    @Override
    protected void onCleared() {
        super.onCleared();
//        group.removeSource(repositorySource);
//        groupUpdate.removeSource(updateRepositorySource);
//        groupUserUpdate.removeSource(updateGroupUserRepositorySource);
        groupDelete.removeSource(deleteGroupRepositorySource);
    }



}
















