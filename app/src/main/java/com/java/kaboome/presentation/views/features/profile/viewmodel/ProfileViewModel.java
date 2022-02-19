package com.java.kaboome.presentation.views.features.profile.viewmodel;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.MediaActionConstants;
import com.java.kaboome.constants.UserActionConstants;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.repositories.DataImageUploadRepository;
import com.java.kaboome.data.repositories.DataUserRepository;
import com.java.kaboome.data.workers.LoadMediaWorker;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.UserRepository;
import com.java.kaboome.domain.usecases.GetUserUseCase;
import com.java.kaboome.domain.usecases.UpdateUserCacheUseCase;
import com.java.kaboome.domain.usecases.UpdateUserUseCase;
import com.java.kaboome.domain.usecases.UploadImageSingleUseCase;
import com.java.kaboome.domain.usecases.UploadImageUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.UserModel;
import com.java.kaboome.presentation.mappers.UserActionConstantsMapper;
import com.java.kaboome.presentation.mappers.UserModelMapper;
import com.java.kaboome.presentation.views.features.profile.UserEditDetails;

import java.util.concurrent.ExecutionException;


public class ProfileViewModel extends ViewModel {

    private static final String TAG = "KMProfileViewModel";
    private GetUserUseCase getUserUseCase;
    private UpdateUserUseCase updateUserUseCase;
    private UpdateUserCacheUseCase updateUserCacheUseCase;
    private UploadImageUseCase uploadImageUseCase;
    private UploadImageSingleUseCase uploadImageSingleUseCase;
    private ImageUploadRepository imageUploadRepository;
    private UserRepository userRepository;
    private LiveData<DomainResource<DomainUser>> repositorySource;
    private LiveData<DomainUpdateResource<String>> updateRepositorySource;
    private LiveData<DomainUpdateResource<String>> uploadImageRespositorySource;


    private MediatorLiveData<UserModel> user = new MediatorLiveData<>();
    private MediatorLiveData<UserEditDetails> updatedUser = new MediatorLiveData<>();
    private MediatorLiveData<UserEditDetails> uploadingUserImage = new MediatorLiveData<>();

    private Long timestamp;


    public ProfileViewModel() {

        userRepository = DataUserRepository.getInstance();
        getUserUseCase = new GetUserUseCase(userRepository);
        updateUserUseCase = new UpdateUserUseCase(userRepository);
        updateUserCacheUseCase = new UpdateUserCacheUseCase(userRepository);
        imageUploadRepository = DataImageUploadRepository.getInstance();
        uploadImageUseCase = new UploadImageUseCase(imageUploadRepository);
        uploadImageSingleUseCase = new UploadImageSingleUseCase(imageUploadRepository);

    }



    public MediatorLiveData<UserModel> getUser() {
        return user;
    }
    public MediatorLiveData<UserEditDetails> getUpdatedUser() {
        return updatedUser;
    }
    public MediatorLiveData<UserEditDetails> getUploadingUserImage() {
        return uploadingUserImage;
    }



    public void loadUser() {
        Log.d(TAG, "loadUser: ");

        user.removeSource(repositorySource); //if any old hanging there
        repositorySource = getUserUseCase.execute(GetUserUseCase.Params.forUser(AppConfigHelper.getUserId()));

        user.addSource(repositorySource, new Observer<DomainResource<DomainUser>>() {
            @Override
            public void onChanged(@Nullable DomainResource<DomainUser> userDomainResource) {
                if (userDomainResource != null) {

                    if (userDomainResource.status == DomainResource.Status.SUCCESS) {
                        if (userDomainResource.data != null) {
                            user.setValue(UserModelMapper.transformFromDomainToModel(userDomainResource));
                        }
//                        user.removeSource(repositorySource); //so that if cache is updated on update, it gets reflected
                    }
                    else if (userDomainResource.status == DomainResource.Status.LOADING) {
                        if (userDomainResource.data != null) {
                            user.setValue(UserModelMapper.transformFromDomainToModel(userDomainResource));
                        }
                    }
                    else if (userDomainResource.status == DomainResource.Status.ERROR) {
                        if (userDomainResource.data != null) {
                            user.setValue(UserModelMapper.transformFromDomainToModel(userDomainResource));
                        }
                        Log.d(TAG, "Coming here when status is error");
                        user.removeSource(repositorySource);
                    }


                } else {
                    Log.d(TAG, "Coming here when userDomainResource is null");
                    user.removeSource(repositorySource);
                }
            }
        });

    }

    public void updateUser(UserModel userModel,String action) {
        Log.d(TAG, "updateUser: ");

        updatedUser.removeSource(updateRepositorySource); //if any old hanging there
        updateRepositorySource = updateUserUseCase.execute(UpdateUserUseCase.Params.userUpdated(UserModelMapper.transformFromModelToDomain(userModel), action));

        updatedUser.addSource(updateRepositorySource, new Observer<DomainUpdateResource<String>>() {
            @Override
            public void onChanged(@Nullable DomainUpdateResource<String> userUpdateDomainResource) {
                Log.d(TAG, "onChanged: update changed - coming here");
                if (userUpdateDomainResource != null) {


                    if (userUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {

                        if (userUpdateDomainResource.data != null) {
                            updatedUser.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.SUCCESS, timestamp));
                        }
                        updatedUser.removeSource(updateRepositorySource);
                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
                        if (userUpdateDomainResource.data != null) {
//                            showToast.setValue(stringDomainUpdateResource.data +" updating");
                            updatedUser.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.UPDATING, timestamp));
                        }
                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
//                        showToast.setValue(stringDomainUpdateResource.data +" errored");
                        updatedUser.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.ERROR, timestamp));
                        updatedUser.removeSource(updateRepositorySource);
//                        showNoNetworkErrorToast.setValue(true);
                    }
                } else {
                    updatedUser.removeSource(updateRepositorySource);
                }
            }
        });

        //If the image was changed, start uploading the image as well
//        if(userModel.isImageChanged()){
//            if(userModel.getImagePath() != null && !userModel.getImagePath().isEmpty()){
//                uploadUserImage(userModel.getImagePath());
//            }
//        }

    }

//    private void uploadUserImage(String filePath) {
//
//        Log.d(TAG, "uploadImage: ");
//        String key = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getRequestUserId());
//
//        uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(filePath), key, "uploadUserImage"));
//
//        uploadingUserImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
//            @Override
//            public void onChanged(@Nullable DomainUpdateResource<String> userUpdateDomainResource) {
//                Log.d(TAG, "onChanged: upload status changed");
//                if (userUpdateDomainResource != null) {
//
//
//                    if (userUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {
//
//                        //update the signature - so that glide uploads the new image
//                        AppConfigHelper.increaseUserImageSignature();
//
//                        if (userUpdateDomainResource.data != null) {
//                            uploadingUserImage.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.SUCCESS));
//                        }
//                        uploadingUserImage.removeSource(uploadImageRespositorySource);
//                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                        if (userUpdateDomainResource.data != null) {
////                            showToast.setValue(stringDomainUpdateResource.data +" updating");
//                            uploadingUserImage.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.UPDATING));
//                        }
//                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
////                        showToast.setValue(stringDomainUpdateResource.data +" errored");
//                        uploadingUserImage.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.ERROR));
//                        uploadingUserImage.removeSource(uploadImageRespositorySource);
////                        showNoNetworkErrorToast.setValue(true);
//                    }
//                } else {
//                    uploadingUserImage.removeSource(uploadImageRespositorySource);
//                }
//            }
//        });
//
//    }

    public void uploadUserImage(final UserModel userModel) {

        //set cache to uploading to true
        //then call worker to upload the image
        userModel.setUserPicUploaded(false);
        userModel.setUserPicLoadingGoingOn(true);
        updateUserCacheUseCase.execute(UpdateUserCacheUseCase.Params.forUser(UserModelMapper.transformFromModelToDomain(userModel), UserActionConstants.UPDATE_USER_PROFILE_IMAGE_NO_TS.getAction()));

        uploadUserImage(ImageTypeConstants.THUMBNAIL, userModel.getThumbnailPath());
        uploadUserImage(ImageTypeConstants.MAIN, userModel.getImagePath());


   /*     Log.d(TAG, "uploadImage: ");
        String key = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN);
        final String tnKey = ImagesUtilHelper.getUserProfilePicName(AppConfigHelper.getUserId(), ImageTypeConstants.THUMBNAIL);
        HashMap<String, Object> userData = new HashMap<>();
        uploadingUserImage.removeSource(uploadImageRespositorySource);// if any old hanging there
        uploadImageRespositorySource = uploadImageUseCase.execute(UploadImageUseCase.Params.imageUpload(new File(userModel.getImagePath()), key, UserActionConstants.UPDATE_USER_PROFILE_IMAGE_TS.getAction(), userData));

        uploadingUserImage.addSource(uploadImageRespositorySource, new Observer<DomainUpdateResource<String>>() {
            @Override
            public void onChanged(@Nullable DomainUpdateResource<String> userUpdateDomainResource) {
                Log.d(TAG, "onChanged: upload status changed");
                if (userUpdateDomainResource != null) {


                    if (userUpdateDomainResource.status == DomainUpdateResource.Status.SUCCESS) {

                        //upload the thumbnail as well - in the background
                        uploadImageSingleUseCase.execute(UploadImageSingleUseCase.Params.imageToUpload(tnKey, new File(userModel.getThumbnailPath()), true));

                        //image upload was successful, now update the database image timestamps accordingly
                        //by calling the updateUser
                        timestamp = new Date().getTime(); //using this time of success as the timestamp for now
                        //later should find a way to get the correct timestamp from s3
                        userModel.setImageUpdateTimestamp(timestamp);
                        updateUser(userModel, UserActionConstants.UPDATE_USER_PROFILE_IMAGE_TS.getAction());

                        //update the signature - so that glide uploads the new image
//                        AppConfigHelper.increaseUserImageSignature();

                        if (userUpdateDomainResource.data != null) {
                            //update the local timestamp as well
                            //this local timestamp is needed in other methods
                            //like the user creating a new request etc.
                            AppConfigHelper.setProfilePicSelected();
                            AppConfigHelper.setCurrentUserImageTimestamp(timestamp);
                            uploadingUserImage.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.SUCCESS, timestamp));
                        }
                        uploadingUserImage.removeSource(uploadImageRespositorySource);
                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.UPDATING) {
//                        Log.d(TAG, "Rate of upload done - "+userUpdateDomainResource.data);
                        if (userUpdateDomainResource.data != null) {
                            uploadingUserImage.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.UPDATING, timestamp));
                        }
                    } else if (userUpdateDomainResource.status == DomainUpdateResource.Status.ERROR) {
                        uploadingUserImage.setValue(new UserEditDetails(UserActionConstantsMapper.getConstant(userUpdateDomainResource.data), UserEditDetails.Status.ERROR, timestamp));
                        uploadingUserImage.removeSource(uploadImageRespositorySource);
                    }
                } else {
                    uploadingUserImage.removeSource(uploadImageRespositorySource);
                }
            }
        });
        */


    }

    private void uploadUserImage(ImageTypeConstants imageType, String pathOfImage) {
        //here you should call the worker to upload the group image
        Data inputData = new Data.Builder()
                .putString("userId", AppConfigHelper.getUserId())
                .putString("imageType", imageType.getType())
                .putString("action", MediaActionConstants.UPLOAD_USER_PROFILE_PIC.getAction())
                .putString("attachment_path", pathOfImage)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(LoadMediaWorker.class)
                .addTag("upload_User_pic")
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
                        Log.d(TAG, "User Pic Uploaded successfully");
                        resultOfOperation.getResult().get();

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //if the update API gave error, it gets wrapped in ExecutionException
                        Log.d(TAG, "User Pic upload failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "User Pic upload failed due to "+e.getMessage());
                    }
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "User Pic upload failed due to - "+e.getMessage());
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: coming here");
        user.removeSource(repositorySource);
        updatedUser.removeSource(updateRepositorySource);
        uploadingUserImage.removeSource(uploadImageRespositorySource);
    }



}
















