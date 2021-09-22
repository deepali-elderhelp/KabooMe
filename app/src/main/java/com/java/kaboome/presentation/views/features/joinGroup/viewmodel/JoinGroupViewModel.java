package com.java.kaboome.presentation.views.features.joinGroup.viewmodel;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.R;
import com.java.kaboome.constants.GeneralStatusContants;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.constants.GroupStatusConstants;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataInvitationsListRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.data.repositories.UpdateResource;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.domain.usecases.AddNewMessageUseCase;
import com.java.kaboome.domain.usecases.CopyImageSingleUseCase;
import com.java.kaboome.domain.usecases.CopyImageUseCase;
import com.java.kaboome.domain.usecases.JoinGroupUseCase;
import com.java.kaboome.domain.usecases.RejectInvitationCacheSingleUseCase;
import com.java.kaboome.domain.usecases.RejectInvitationUseCase;
import com.java.kaboome.domain.usecases.UploadImageSingleUseCase;
import com.java.kaboome.domain.usecases.UploadImageUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.entities.UpdateResourceModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.mappers.UserGroupModelMapper;

import java.io.File;
import java.util.Date;
import java.util.UUID;


public class JoinGroupViewModel extends ViewModel {

    private static final String TAG = "KMJoinGroupViewModel";
    private UserGroupModel userGroupModel;
    private JoinGroupUseCase joinGroupUseCase;
    private UserGroupRepository userGroupRepository;
    private UploadImageUseCase uploadImageUseCase;
    private UploadImageSingleUseCase uploadImageSingleUseCase;
    private CopyImageUseCase copyImageUseCase;
    private CopyImageSingleUseCase copyImageSingleUseCase;
    private RejectInvitationCacheSingleUseCase rejectInvitationCacheSingleUseCase;
    private AddNewMessageUseCase addNewMessageUseCase;
    private ImageUploadRepository imageUploadRepository;
    private MediatorLiveData<GeneralStatusContants> manageImage = new MediatorLiveData<>();

    public enum Status { SUCCESS, ERROR, UPDATING}

    private MediatorLiveData<UpdateResourceModel<String>> groupJoinUpdate = new MediatorLiveData<>();


    public JoinGroupViewModel() {

        userGroupRepository = DataUserGroupRepository.getInstance();
        imageUploadRepository = DataImageUploadRepository.getInstance();
        joinGroupUseCase = new JoinGroupUseCase(userGroupRepository);
        uploadImageUseCase = new UploadImageUseCase(imageUploadRepository);
        copyImageUseCase = new CopyImageUseCase(imageUploadRepository);
        uploadImageSingleUseCase = new UploadImageSingleUseCase(imageUploadRepository);
        copyImageSingleUseCase = new CopyImageSingleUseCase(imageUploadRepository);
        rejectInvitationCacheSingleUseCase = new RejectInvitationCacheSingleUseCase(DataInvitationsListRepository.getInstance());
        addNewMessageUseCase = new AddNewMessageUseCase(DataGroupMessagesRepository.getInstance());

    }



    public LiveData<UpdateResourceModel<String>> getGroupJoinUpdate() {
        return groupJoinUpdate;
    }


    public UserGroupModel getUserGroupModel() {
        return userGroupModel;
    }


    public void joinUserToGroup(final UserGroupModel userGroupModel, final String imagePath, final String imageTNPath, final boolean imageChanged) {

        this.userGroupModel = userGroupModel;
        Log.d(TAG, "joinUserToGroup: starting "+System.currentTimeMillis());
        DomainUserGroup domainUserGroup = UserGroupModelMapper.getDomainFromUserModel(userGroupModel);
        final LiveData<DomainUpdateResource<String>> joinGroupRepositorySource = joinGroupUseCase.execute(JoinGroupUseCase.Params.groupUpdated(domainUserGroup, "joinUserToGroup"));

        groupJoinUpdate.addSource(manageImage, new Observer<GeneralStatusContants>() {
            @Override
            public void onChanged(GeneralStatusContants generalStatusContants) {
                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.SUCCESS){
                    Log.d(TAG, "joinUserToGroup: everything done "+System.currentTimeMillis());
                    groupJoinUpdate.setValue(new UpdateResourceModel<String>(UpdateResourceModel.Status.SUCCESS, "Success", ""));
//                    groupJoinUpdate.setValue(JoinGroupViewModel.Status.SUCCESS);
                    groupJoinUpdate.removeSource(joinGroupRepositorySource);
                    groupJoinUpdate.removeSource(manageImage);
                }
                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.ERROR){
                    groupJoinUpdate.setValue(new UpdateResourceModel(UpdateResourceModel.Status.ERROR, "Error", ""));
//                    groupJoinUpdate.setValue(JoinGroupViewModel.Status.ERROR);
                    groupJoinUpdate.removeSource(joinGroupRepositorySource);
                    groupJoinUpdate.removeSource(manageImage);
                }
            }
        });

        groupJoinUpdate.addSource(joinGroupRepositorySource, new Observer<DomainUpdateResource<String>>() {
            @Override
            public void onChanged(@Nullable DomainUpdateResource<String> groupDomainResource) {
                if (groupDomainResource != null) {


                    if (groupDomainResource.status == DomainUpdateResource.Status.SUCCESS) {

                        if (groupDomainResource.data != null) {

                            Log.d(TAG, "joinUserToGroup: retrofit done "+System.currentTimeMillis());
                            //update the cache to remove the invitation if the group has been joined by invitation
                            rejectInvitationCacheSingleUseCase.execute(RejectInvitationCacheSingleUseCase.Params.rejectInviForGroup(userGroupModel.getGroupId()));

                            DomainMessage newDomainMessage = new DomainMessage();
                            newDomainMessage.setMessageId(UUID.randomUUID().toString());
                            newDomainMessage.setSentBy(GroupStatusConstants.JOINED_GROUP.getStatus());
                            newDomainMessage.setSentTo(MessageGroupsConstants.GROUP_MESSAGES.toString());
                            newDomainMessage.setDeleted(false);
                            newDomainMessage.setAlias("");
                            newDomainMessage.setGroupId(userGroupModel.getGroupId());
                            newDomainMessage.setSentAt(userGroupModel.getLastAccessed());
                            newDomainMessage.setUploadedToServer(true);
                            newDomainMessage.setHasAttachment(false);
                            newDomainMessage.setMessageText(AppConfigHelper.getContext().getString(R.string.new_group_welcome_message_1aa));
                            addNewMessageUseCase.execute(AddNewMessageUseCase.Params.newMessage(newDomainMessage));

//                            groupJoinUpdate.setValue(Status.SUCCESS);
                            uploadGroupUserImage(userGroupModel, imagePath, imageTNPath, imageChanged);



                        }
//                        groupJoinUpdate.removeSource(joinGroupRepositorySource);
                    } else if (groupDomainResource.status == DomainUpdateResource.Status.UPDATING) {
                        if (groupDomainResource.data != null) {
//                            groupJoinUpdate.setValue(Status.UPDATING);
                            groupJoinUpdate.setValue(new UpdateResourceModel(UpdateResourceModel.Status.UPDATING, "Updating", ""));
                        }
                    } else if (groupDomainResource.status == DomainUpdateResource.Status.ERROR) {
                        groupJoinUpdate.setValue(new UpdateResourceModel(UpdateResourceModel.Status.ERROR, groupDomainResource.data, groupDomainResource.message));
//                        groupJoinUpdate.setValue(Status.ERROR);
                        groupJoinUpdate.removeSource(joinGroupRepositorySource);
                    }
                } else {
                    groupJoinUpdate.removeSource(joinGroupRepositorySource);
                }
            }
        });



    }


//    public void uploadGroupUserImage(UserGroupModel userGroupModel, final String imagePath, String imageTNPath, boolean imageChanged) {
//
//        if(!imageChanged){
//            //user did not use any new image, so the regular profile image is the group user image
//            //it should be copied to s3
//            Log.d(TAG, "uploadGroupUserImage: starting before copy user image - "+System.currentTimeMillis());
//            String userProfileTNPicKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
//            final String userProfilePicKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
//            String newTNKey = ImagesUtilHelper.getGroupUserImageName(userGroupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
//            final String newKey = ImagesUtilHelper.getGroupUserImageName(userGroupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
//
//            final LiveData<DomainUpdateResource<String>> copyImageRespositorySource = copyImageUseCase.execute(CopyImageUseCase.Params.imageToBeCopied(userProfileTNPicKey, newTNKey, GroupActionConstants.UPDATE_GROUP_USER_IMAGE.getAction()));
//            manageImage.addSource(copyImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//                @Override
//                public void onChanged(DomainUpdateResource<String> stringDomainUpdateResource) {
//                    Log.d(TAG, "onChanged: upload status changed");
//                    if (stringDomainUpdateResource != null) {
//
//                        if (stringDomainUpdateResource.status == DomainUpdateResource.Status.SUCCESS) {
//                            manageImage.setValue(GeneralStatusContants.SUCCESS);
//                            manageImage.removeSource(copyImageRespositorySource);
//
//                            //start the copy of the normal size images in the background
//                            copyImageSingleUseCase.execute(CopyImageSingleUseCase.Params.imageToCopy(userProfilePicKey, newKey));
//
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
//            //user used a new image, update that image to S3
//            Log.d(TAG, "uploadGroupUserImage: ");
//            final String key = ImagesUtilHelper.getGroupUserImageName(userGroupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
//            String tnKey = ImagesUtilHelper.getGroupUserImageName(userGroupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
//
//            final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(imageTNPath), tnKey, GroupActionConstants.UPDATE_GROUP_USER_IMAGE.getAction()));
//
//            manageImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//                @Override
//                public void onChanged(@Nullable DomainUpdateResource<String> groupUserImageUpdateDomainResource) {
//                    Log.d(TAG, "onChanged: upload status changed");
//                    if (groupUserImageUpdateDomainResource != null) {
//
//                        if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//                            manageImage.setValue(GeneralStatusContants.SUCCESS);
//                            manageImage.removeSource(uploadImageRespositorySource);
//
//                            //start the upload of the normal size images - this will kick off in the background
//                            uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(key, new File(imagePath)));
//
//                        } else if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                            if (groupUserImageUpdateDomainResource.data != null) {
//                                manageImage.setValue(GeneralStatusContants.LOADING);
//                            }
//                        } else if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
//                            manageImage.setValue(GeneralStatusContants.ERROR);
//                            manageImage.removeSource(uploadImageRespositorySource);
//                        }
//                    } else {
//                        manageImage.removeSource(uploadImageRespositorySource);
//                    }
//                }
//            });
//        }
//
//
//
//    }

    //this new uploadGroupUserImage does all the group user image either copy or upload in the background
    //the API call does not wait for the return and goes ahead

    public void uploadGroupUserImage(UserGroupModel userGroupModel, String imagePath, String imageTNPath, boolean imageChanged) {

//        Log.d(TAG, "joinUserToGroup: starting image "+System.currentTimeMillis());
        String newTNKey = ImagesUtilHelper.getGroupUserImageName(userGroupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
        String newKey = ImagesUtilHelper.getGroupUserImageName(userGroupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);

        if(!imageChanged){
            //user did not use any new image, so the regular profile image is the group user image
            //it should be copied to s3
//            Log.d(TAG, "uploadGroupUserImage: starting before copy user image - "+System.currentTimeMillis());
            String userProfileTNPicKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
            String userProfilePicKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);

            copyImageSingleUseCase.execute(CopyImageSingleUseCase.Params.imageToCopy(userProfileTNPicKey, newTNKey));
            copyImageSingleUseCase.execute(CopyImageSingleUseCase.Params.imageToCopy(userProfilePicKey, newKey));

            //now also download these images so that they are there in the cache when the user needs them
            ImageHelper.getInstance().downloadImage(newTNKey);
            ImageHelper.getInstance().downloadImage(newKey);

        }
        else{
            //user used a new image, update that image to S3
//            Log.d(TAG, "uploadGroupUserImage: ");

            //start the upload of the normal size images - this will kick off in the background
            uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(newTNKey, new File(imageTNPath), true));
            uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(newKey, new File(imagePath), true));

        }

        manageImage.setValue(GeneralStatusContants.SUCCESS);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }



}
















