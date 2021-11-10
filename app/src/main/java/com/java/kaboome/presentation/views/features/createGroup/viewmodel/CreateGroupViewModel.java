package com.java.kaboome.presentation.views.features.createGroup.viewmodel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.java.kaboome.R;
import com.java.kaboome.constants.CreateGroupStatusContants;
import com.java.kaboome.constants.GeneralStatusContants;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.constants.GroupStatusConstants;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataGroupRepository;
import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.domain.usecases.AddNewGroupToCacheUseCase;
import com.java.kaboome.domain.usecases.AddNewMessageUseCase;
import com.java.kaboome.domain.usecases.CopyImageSingleUseCase;
import com.java.kaboome.domain.usecases.CopyImageUseCase;
import com.java.kaboome.domain.usecases.CreateNewGroupUseCase;
import com.java.kaboome.domain.usecases.UploadImageSingleUseCase;
import com.java.kaboome.domain.usecases.UploadImageUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.helpers.MessageGroupsHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.mappers.GroupModelMapper;
import com.java.kaboome.presentation.mappers.UserGroupModelMapper;

import java.io.File;
import java.util.UUID;

public class CreateGroupViewModel extends ViewModel {

    private static final String TAG = "KMGrpViewModel";

    private GroupModel groupModel;
    private DomainUserGroup domainUserGroup;
    private String imagePath;
    private String userImagePath;
    private String userImageTNPath;
    private String groupImageTNPath;
    private boolean userImageChanged; //true if user selected some image other than the default profile image
    private CreateNewGroupUseCase createNewGroupUseCase;
    private AddNewGroupToCacheUseCase addNewGroupToCacheUseCase;
    private UploadImageUseCase uploadImageUseCase;
    private UploadImageSingleUseCase uploadImageSingleUseCase;
    private CopyImageUseCase copyImageUseCase;
    private CopyImageSingleUseCase copyImageSingleUseCase;
    private AddNewMessageUseCase addNewMessageUseCase;
    private GroupRepository groupRepository;
    private UserGroupsListRepository userGroupsListRepository;
    private ImageUploadRepository imageUploadRepository;
    private MediatorLiveData<CreateGroupStatusContants> createGroup = new MediatorLiveData<>();
    private MediatorLiveData<CreateGroupStatusContants> uploadImage = new MediatorLiveData<>();
    private MediatorLiveData<CreateGroupStatusContants> manageImage = new MediatorLiveData<>();

    private LiveData<DomainResource<DomainGroupUser>> repositorySource;

    public CreateGroupViewModel() {
        groupModel = new GroupModel();
        groupRepository = DataGroupRepository.getInstance();
        createNewGroupUseCase = new CreateNewGroupUseCase(groupRepository);
        userGroupsListRepository = DataUserGroupsListRepository.getInstance();
        addNewGroupToCacheUseCase = new AddNewGroupToCacheUseCase(userGroupsListRepository);
        imageUploadRepository = DataImageUploadRepository.getInstance();
        uploadImageUseCase = new UploadImageUseCase(imageUploadRepository);
        uploadImageSingleUseCase = new UploadImageSingleUseCase(imageUploadRepository);
        copyImageUseCase = new CopyImageUseCase(imageUploadRepository);
        copyImageSingleUseCase = new CopyImageSingleUseCase(imageUploadRepository);
        addNewMessageUseCase = new AddNewMessageUseCase(DataGroupMessagesRepository.getInstance());
    }

    public MediatorLiveData<CreateGroupStatusContants> getCreateGroup() {
        return createGroup;
    }

    public void addGroupNameAndDesc(String name, String desc, Boolean unicast){
        groupModel.setGroupName(name);
        groupModel.setGroupDescription(desc);
        groupModel.setUnicastGroup(unicast);
    }

    public void addGroupAliasAndRole(String alias, String role, String userImagePath, boolean userImageChanged, String userThumbnailPath){
        groupModel.setCreatorRole(role);
        groupModel.setCreatedByAlias(alias);
        this.userImagePath = userImagePath;
        this.userImageTNPath = userThumbnailPath;
        this.userImageChanged = userImageChanged;
    }

    public void addGroupPrivacyExpiryAndPicturePath(Long expiry, boolean privacy, String imagePath, String thumbnailPicturePath){
        groupModel.setExpiryDate(expiry);
        groupModel.setGroupPrivate(privacy);
        this.imagePath = imagePath;
        this.groupImageTNPath = thumbnailPicturePath;
    }

    public UserGroupModel getDomainUserGroup() {
        return UserGroupModelMapper.getUserModelFromDomain(domainUserGroup);
    }

    public void setDomainUserGroup(DomainUserGroup domainUserGroup) {
        this.domainUserGroup = domainUserGroup;
    }

    public void createGroup(){

        createGroup.removeSource(repositorySource); //if there is any old one hanging
        createGroup.removeSource(uploadImage); //if there is any old one hanging
        createGroup.removeSource(manageImage); //if there is any old one hanging

        createGroup.setValue(CreateGroupStatusContants.LOADING_GROUP);
        repositorySource = createNewGroupUseCase.execute(CreateNewGroupUseCase.Params.groupToBeCreated(GroupModelMapper.getDomainFromGroupModel(groupModel)));

//        createGroup.setValue(CreateGroupStatusContants.LOADING_GROUP);
        Log.d(TAG, "createGroup: starting group creation - "+System.currentTimeMillis());
        //upload group image
        createGroup.addSource(uploadImage, new Observer<CreateGroupStatusContants>() {
            @Override
            public void onChanged(CreateGroupStatusContants createGroupStatusContants) {
                if(createGroupStatusContants != null && createGroupStatusContants == CreateGroupStatusContants.SUCCESS_GROUP_IMAGE){
                    createGroup.removeSource(uploadImage);
                    createGroup.setValue(CreateGroupStatusContants.SUCCESS_GROUP_IMAGE);

                    //now upload group user image
                    uploadGroupUserImage();
                }
                if(createGroupStatusContants != null && createGroupStatusContants == CreateGroupStatusContants.ERROR_GROUP_IMAGE){
                    createGroup.removeSource(uploadImage);
                    createGroup.setValue(CreateGroupStatusContants.ERROR_GROUP_IMAGE);

                    //now upload group user image
                    uploadGroupUserImage();
                }
            }
        });

        //upload group user image
        createGroup.addSource(manageImage, new Observer<CreateGroupStatusContants>() {
            @Override
            public void onChanged(CreateGroupStatusContants createGroupStatusContants) {
                if(createGroupStatusContants != null && createGroupStatusContants == CreateGroupStatusContants.SUCCESS_GROUP_USER_IMAGE){
                    createGroup.removeSource(manageImage);
                    createGroup.setValue(CreateGroupStatusContants.SUCCESS_GROUP_USER_IMAGE);

                    Log.d(TAG, "createGroup: Everything done - "+System.currentTimeMillis());
                }
                if(createGroupStatusContants != null && createGroupStatusContants == CreateGroupStatusContants.ERROR_GROUP_USER_IMAGE){
                    createGroup.removeSource(manageImage);
                    createGroup.setValue(CreateGroupStatusContants.ERROR_GROUP_USER_IMAGE);

                }
            }
        });

        createGroup.addSource(repositorySource, new Observer<DomainResource<DomainGroupUser>>() {
            @Override
            public void onChanged(DomainResource<DomainGroupUser> domainGroupUserDomainResource) {
                if (domainGroupUserDomainResource != null) {


                    if (domainGroupUserDomainResource.status == DomainResource.Status.SUCCESS) {
                        createGroup.removeSource(repositorySource);
                        //Three more things need to be done
                        //1. Add the group to the UserGroups Local DAO
                        //2. Upload the Image
                        //3. Add a new welcome cache message
                        DomainGroupUser domainGroupUser = domainGroupUserDomainResource.data;
                        groupModel.setGroupId(domainGroupUser.getGroupId());
                        DomainUserGroup domainUserGroupNew = new DomainUserGroup();
                        domainUserGroupNew.setGroupId(domainGroupUser.getGroupId());
                        domainUserGroupNew.setGroupName(groupModel.getGroupName());
                        domainUserGroupNew.setPrivateGroup(groupModel.getGroupPrivate());
                        domainUserGroupNew.setAlias(groupModel.getCreatedByAlias());
                        domainUserGroupNew.setGroupAdminRole(groupModel.getCreatorRole());
                        domainUserGroupNew.setIsAdmin("true");
                        domainUserGroupNew.setDateJoined(domainGroupUser.getDateJoined());
                        domainUserGroupNew.setExpiry(groupModel.getExpiryDate());
                        domainUserGroupNew.setUserId(AppConfigHelper.getUserId());
                        domainUserGroupNew.setIsCreator("true");
                        domainUserGroupNew.setLastAccessed(domainGroupUser.getDateJoined()); //this is what we do at server, last accessed is the time joined
                        domainUserGroupNew.setImageUpdateTimestamp(domainGroupUser.getDateJoined()); //group img time stamp created when user joined the group
                        domainUserGroupNew.setUserImageUpdateTimestamp(domainGroupUser.getImageUpdateTimestamp());

                        addNewGroupToCacheUseCase.execute(AddNewGroupToCacheUseCase.Params.newGroup(domainUserGroupNew));
                        domainUserGroup = domainUserGroupNew;
                        createGroup.setValue(CreateGroupStatusContants.SUCCESS_GROUP);


                        DomainMessage newDomainMessage = new DomainMessage();
                        newDomainMessage.setMessageId(UUID.randomUUID().toString());
                        newDomainMessage.setSentBy(GroupStatusConstants.CREATED_GROUP.getStatus());
                        newDomainMessage.setSentTo(MessageGroupsConstants.GROUP_MESSAGES.toString());
                        newDomainMessage.setAlias("");
                        newDomainMessage.setGroupId(domainGroupUser.getGroupId());
                        newDomainMessage.setSentAt(domainGroupUser.getDateJoined());
                        newDomainMessage.setUploadedToServer(true);
                        newDomainMessage.setHasAttachment(false);
                        newDomainMessage.setDeleted(false);
                        newDomainMessage.setMessageText(AppConfigHelper.getContext().getString(R.string.new_group_welcome_message_1a));
                        addNewMessageUseCase.execute(AddNewMessageUseCase.Params.newMessage(newDomainMessage));


                        Log.d(TAG, "createGroup: Retrofit and cache done - "+System.currentTimeMillis());



                        //Now number 2
                        uploadGroupImage();

                    }

                     else if (domainGroupUserDomainResource.status == DomainResource.Status.LOADING) {
                         createGroup.setValue(CreateGroupStatusContants.LOADING_GROUP);

                    } else if (domainGroupUserDomainResource.status == DomainResource.Status.ERROR) {
                        createGroup.removeSource(repositorySource);
                        createGroup.setValue(CreateGroupStatusContants.ERROR_GROUP);

                    }
                } else {
                    createGroup.removeSource(repositorySource);
                }
            }
        });
    }

    public void uploadGroupImage() {

        Log.d(TAG, "createGroup: starting group image upload - "+System.currentTimeMillis());

        if(groupImageTNPath == null || groupImageTNPath.isEmpty()){
            //user selected no image for the group, just return
            uploadImage.setValue(CreateGroupStatusContants.SUCCESS_GROUP_IMAGE);
            return;
        }
        final String key = ImagesUtilHelper.getGroupImageName(groupModel.getGroupId(), ImageTypeConstants.MAIN);
        final String tnKey = ImagesUtilHelper.getGroupImageName(groupModel.getGroupId(), ImageTypeConstants.THUMBNAIL);

        uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(tnKey, new File(groupImageTNPath), true));
        uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(key, new File(imagePath), true));

        uploadImage.setValue(CreateGroupStatusContants.SUCCESS_GROUP_IMAGE);


//        Log.d(TAG, "uploadGroupImage: starting before upload group TN image - "+System.currentTimeMillis());
//        final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(groupImageTNPath), tnKey, GroupActionConstants.UPDATE_GROUP_IMAGE.getAction()));
//
//        uploadImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//            @Override
//            public void onChanged(@Nullable DomainUpdateResource<String> groupUpdateDomainResource) {
//                Log.d(TAG, "onChanged: upload status changed - "+groupUpdateDomainResource.status);
//                if (groupUpdateDomainResource != null) {
//
//                    if (groupUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//                        uploadImage.removeSource(uploadImageRespositorySource);
//                        //start downloading as well, so that the image is there in the cache when the user goes to the Group List Page
//                        ImageHelper.getInstance().downloadImage(tnKey);
//
//                        Log.d(TAG, "uploadGroupImage: group TN image uploaded- "+System.currentTimeMillis());
//
//                        Log.d(TAG, "uploadGroupImage: starting to upload normal image - "+System.currentTimeMillis());
//                        //start the upload of the normal size images in the background thread
//                        uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(key, new File(imagePath), true));
//                        Log.d(TAG, "uploadGroupImage: continuing after normal image call - "+System.currentTimeMillis());
//                        uploadImage.setValue(CreateGroupStatusContants.SUCCESS_GROUP_IMAGE);
//
//                    } else if (groupUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                        if (groupUpdateDomainResource.data != null) {
//                            uploadImage.setValue(CreateGroupStatusContants.LOADING_GROUP_IMAGE);
//                        }
//                    } else if (groupUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
//                        uploadImage.removeSource(uploadImageRespositorySource);
//                        uploadImage.setValue(CreateGroupStatusContants.ERROR_GROUP_IMAGE);
//                    }
//                } else {
//                    uploadImage.removeSource(uploadImageRespositorySource);
//                }
//            }
//        });

    }

    //this new uploadGroupUserImage does all the group user image either copy or upload in the background
    //the API call does not wait for the return and goes ahead

    public void uploadGroupUserImage() {


            Log.d(TAG, "createGroup: starting group user image upload/copy - "+System.currentTimeMillis());
            String newTNKey = ImagesUtilHelper.getGroupUserImageName(groupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
            String newKey = ImagesUtilHelper.getGroupUserImageName(groupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);

        if(!userImageChanged){
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

            //start the upload of the normal size and thumbnail images - this will kick off in the background
            uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(newTNKey, new File(userImageTNPath), true));
            uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(newKey, new File(userImagePath), true));

        }

        manageImage.setValue(CreateGroupStatusContants.SUCCESS_GROUP_USER_IMAGE);

    }


//    public void uploadGroupUserImage( ) {
//
//        if(!this.userImageChanged){
//
//            //TODO: we will have to copy both the images - main and thumbnail for the user
//            //TODO: better look at the approach of a worker thread doing this all in the background
//            //TODO: or copy thumbnail right away, but also copy main in the background
//            //user did not use any new image, so the regular profile image is the group user image
//            //it should be copied to s3
//            Log.d(TAG, "uploadGroupUserImage: starting before copy user image - "+System.currentTimeMillis());
//            String userProfileTNPicKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
//            final String userProfilePicKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
//            String newTNKey = ImagesUtilHelper.getGroupUserImageName(groupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
//            final String newKey = ImagesUtilHelper.getGroupUserImageName(groupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
//            final LiveData<DomainUpdateResource<String>> copyImageRespositorySource = copyImageUseCase.execute(CopyImageUseCase.Params.imageToBeCopied(userProfileTNPicKey, newTNKey, GroupActionConstants.UPDATE_GROUP_USER_IMAGE.getAction()));
//            manageImage.addSource(copyImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//                @Override
//                public void onChanged(DomainUpdateResource<String> stringDomainUpdateResource) {
//                    Log.d(TAG, "onChanged: upload status changed");
//                    if (stringDomainUpdateResource != null) {
//
//                        if (stringDomainUpdateResource.status == DomainUpdateResource.Status.SUCCESS) {
//                            manageImage.setValue(CreateGroupStatusContants.SUCCESS_GROUP_USER_IMAGE);
//                            manageImage.removeSource(copyImageRespositorySource);
//
//                            //start the copy of the normal size images in the background
//                            copyImageSingleUseCase.execute(CopyImageSingleUseCase.Params.imageToCopy(userProfilePicKey, newKey));
//                            Log.d(TAG, "uploadGroupUserImage: user image copied from one S3 key to another - "+System.currentTimeMillis());
//                        } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.UPDATING) {
//                            if (stringDomainUpdateResource.data != null) {
//                                manageImage.setValue(CreateGroupStatusContants.LOADING_GROUP_USER_IMAGE);
//                            }
//                        } else if (stringDomainUpdateResource.status == DomainUpdateResource.Status.ERROR) {
//                            manageImage.setValue(CreateGroupStatusContants.ERROR_GROUP_USER_IMAGE);
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
//            if(userImageTNPath == null || userImageTNPath.isEmpty()){
//                //user selected no image for the group profile, just return
//                uploadImage.setValue(CreateGroupStatusContants.SUCCESS_GROUP_USER_IMAGE);
//                return;
//            }
//            final String key = ImagesUtilHelper.getGroupUserImageName(groupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
//            final String tnKey = ImagesUtilHelper.getGroupUserImageName(groupModel.getGroupId(), AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
//            Log.d(TAG, "uploadGroupUserImage: starting before upload user image - "+System.currentTimeMillis());
//            final LiveData<DomainUpdateResource<String>> uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(userImageTNPath), tnKey, GroupActionConstants.UPDATE_GROUP_USER_IMAGE.getAction()));
//
//            manageImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//                @Override
//                public void onChanged(@Nullable DomainUpdateResource<String> groupUserImageUpdateDomainResource) {
//                    Log.d(TAG, "onChanged: upload status changed");
//                    if (groupUserImageUpdateDomainResource != null) {
//
//                        if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//                            manageImage.setValue(CreateGroupStatusContants.SUCCESS_GROUP_USER_IMAGE);
//                            manageImage.removeSource(uploadImageRespositorySource);
//                            Log.d(TAG, "uploadGroupUserImage: user image TN copied - "+System.currentTimeMillis());
//                            //start the upload of the normal size images in the background
//                            Log.d(TAG, "uploadGroupUserImage: starting to upload normal image - "+System.currentTimeMillis());
//                            uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(key, new File(userImagePath)));
//                            Log.d(TAG, "uploadGroupUserImage: continuing after normal image call - "+System.currentTimeMillis());
//
//                        } else if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                            if (groupUserImageUpdateDomainResource.data != null) {
//                                manageImage.setValue(CreateGroupStatusContants.LOADING_GROUP_USER_IMAGE);
//                            }
//                        } else if (groupUserImageUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
//                            manageImage.setValue(CreateGroupStatusContants.ERROR_GROUP_USER_IMAGE);
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

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
