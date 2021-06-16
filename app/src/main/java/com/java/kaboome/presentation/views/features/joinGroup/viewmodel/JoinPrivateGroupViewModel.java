package com.java.kaboome.presentation.views.features.joinGroup.viewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.constants.GeneralStatusContants;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.InvitationStatusConstants;
import com.java.kaboome.data.repositories.DataGroupRequestRepository;
import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataInvitationsListRepository;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.usecases.AddNewInvitationUseCase;
import com.java.kaboome.domain.usecases.CopyImageSingleUseCase;
import com.java.kaboome.domain.usecases.CopyImageUseCase;
import com.java.kaboome.domain.usecases.CreateRequestUseCase;
import com.java.kaboome.domain.usecases.UploadImageSingleUseCase;
import com.java.kaboome.domain.usecases.UploadImageUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.entities.UpdateResourceModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.mappers.GroupRequestModelMapper;
import com.java.kaboome.presentation.mappers.InvitationModelMapper;

import java.io.File;

public class JoinPrivateGroupViewModel extends ViewModel{

    private static final String TAG = "KMJoinPrivateGroupVM";

    public enum Status { SUCCESS, ERROR, UPDATING}

    private CreateRequestUseCase createRequestUseCase;
    private AddNewInvitationUseCase addNewInvitationUseCase;
    private UploadImageSingleUseCase uploadImageSingleUseCase;
    private GroupRequestRepository groupRequestRepository;
    private InvitationsListRepository invitationsListRepository;
    private ImageUploadRepository imageUploadRepository;
    private UploadImageUseCase uploadImageUseCase;
    private CopyImageUseCase copyImageUseCase;
    private CopyImageSingleUseCase copyImageSingleUseCase;
    private  MediatorLiveData<UpdateResourceModel<String>> request = new MediatorLiveData<>();
    private MediatorLiveData<GeneralStatusContants> manageImage = new MediatorLiveData<>();

    public JoinPrivateGroupViewModel()  {
        groupRequestRepository = DataGroupRequestRepository.getInstance();
        createRequestUseCase = new CreateRequestUseCase(groupRequestRepository);

        invitationsListRepository = DataInvitationsListRepository.getInstance();
        addNewInvitationUseCase = new AddNewInvitationUseCase(invitationsListRepository);

        imageUploadRepository = DataImageUploadRepository.getInstance();
        uploadImageUseCase = new UploadImageUseCase(imageUploadRepository);
        copyImageUseCase = new CopyImageUseCase(imageUploadRepository);
        uploadImageSingleUseCase = new UploadImageSingleUseCase(imageUploadRepository);
        copyImageSingleUseCase = new CopyImageSingleUseCase(imageUploadRepository);
    }

    public MediatorLiveData<UpdateResourceModel<String>> getRequest() {
        return request;
    }

    public void createRequest(final GroupRequestModel groupRequestModel, final String groupName, final String privateGroup,
                              String action, final String imagePath, final String imageTNPath, final boolean imageChanged
                              ) {

        final LiveData<DomainUpdateResource<String>> createRequestRepSource = createRequestUseCase.execute(CreateRequestUseCase.Params.groupRequestedToJoin(GroupRequestModelMapper.getDomainFromGroupRequestModel(groupRequestModel), groupName, privateGroup, action));


        request.addSource(manageImage, new Observer<GeneralStatusContants>() {
            @Override
            public void onChanged(GeneralStatusContants generalStatusContants) {
                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.SUCCESS){
                    request.setValue(new UpdateResourceModel(UpdateResourceModel.Status.SUCCESS, "Success", ""));
//                    request.setValue(Status.SUCCESS);
                    request.removeSource(createRequestRepSource);
                    request.removeSource(manageImage);
                }
                if(generalStatusContants != null && generalStatusContants == GeneralStatusContants.ERROR){
                    request.setValue(new UpdateResourceModel(UpdateResourceModel.Status.ERROR, "Error", ""));
//                    request.setValue(Status.ERROR);
                    request.removeSource(createRequestRepSource);
                    request.removeSource(manageImage);
                }
            }
        });


        request.addSource(createRequestRepSource, new Observer<DomainUpdateResource<String>>() {
            @Override
            public void onChanged(DomainUpdateResource<String> stringDomainUpdateResource) {

                Log.d(TAG, "onChanged: update changed - coming here");
                if (stringDomainUpdateResource != null) {


                    if (stringDomainUpdateResource.status == DomainUpdateResource.Status.SUCCESS) {

                        if (stringDomainUpdateResource.data != null) {
                            //here update the local invitations cache with the data
                            addNewInvitationUseCase.execute(AddNewInvitationUseCase.Params.newInvitation(
                                    InvitationModelMapper.transformFromInvitation(createPendingInvitation(
                                            groupRequestModel, groupName, privateGroup))));

//                            request.setValue(Status.SUCCESS);
                            uploadGroupUserImage(groupRequestModel, imagePath, imageTNPath, imageChanged);
                        }
//                        request.removeSource(createRequestRepSource);
                    } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.UPDATING) {
                        if (stringDomainUpdateResource.data != null) {
//                            request.setValue(Status.UPDATING);
                            request.setValue(new UpdateResourceModel(UpdateResourceModel.Status.UPDATING, "Updating", ""));
                        }
                    } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.ERROR) {
//                        request.setValue(Status.ERROR);
                        request.setValue(new UpdateResourceModel(UpdateResourceModel.Status.ERROR, stringDomainUpdateResource.data, stringDomainUpdateResource.message));
                        request.removeSource(createRequestRepSource);
                    }
                } else {
                    request.removeSource(createRequestRepSource);
                }
            }
        });
    }

    private InvitationModel createPendingInvitation(GroupRequestModel groupRequestModel, String groupName, String privateGroup){
        boolean isGroupPrivate = "true".equals(privateGroup)? true:false;
        InvitationModel invitationModel = new InvitationModel();
        invitationModel.setGroupId(groupRequestModel.getGroupId());
        invitationModel.setGroupName(groupName);
        invitationModel.setPrivateGroup(isGroupPrivate);
        invitationModel.setInvitationStatus(InvitationStatusConstants.PENDING);
        invitationModel.setInvitedBy(groupRequestModel.getUserId());
        invitationModel.setInvitedByAlias(groupRequestModel.getUserAlias());
        invitationModel.setDateInvited(groupRequestModel.getDateRequestMade());
        invitationModel.setMessageByInvitee("Self Invited");

        return  invitationModel;
    }

//    public void uploadGroupUserImage(GroupRequestModel groupRequestModel, final String imagePath, String imageTNPath, boolean imageChanged) {
//
//        if(!imageChanged){
//            //user did not use any new image, so the regular profile image is the group user image
//            //it should be copied to s3
//            String userProfileTNPicKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
//            final String userProfilePicKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
//            String newTNKey = ImagesUtilHelper.getGroupUserImageName(groupRequestModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
//            final String newKey = ImagesUtilHelper.getGroupUserImageName(groupRequestModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
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
//            Log.d(TAG, "uploadGroupUserImage: ");
//            final String key = ImagesUtilHelper.getGroupUserImageName(groupRequestModel.getGroupId(), groupRequestModel.getUserId(), ImageTypeConstants.MAIN);
//            final String tnKey = ImagesUtilHelper.getGroupUserImageName(groupRequestModel.getGroupId(), groupRequestModel.getUserId(), ImageTypeConstants.THUMBNAIL);
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

    public void uploadGroupUserImage(GroupRequestModel groupRequestModel, String imagePath, String imageTNPath, boolean imageChanged) {

//        Log.d(TAG, "joinUserToGroup: starting image "+System.currentTimeMillis());
        String newTNKey = ImagesUtilHelper.getGroupUserImageName(groupRequestModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
        String newKey = ImagesUtilHelper.getGroupUserImageName(groupRequestModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);

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
}
