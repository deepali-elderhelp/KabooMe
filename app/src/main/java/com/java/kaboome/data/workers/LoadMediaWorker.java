package com.java.kaboome.data.workers;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.google.common.util.concurrent.ListenableFuture;
import com.java.kaboome.constants.AWSConstants;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.MediaActionConstants;
import com.java.kaboome.constants.UserActionConstants;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataGroupRepository;
import com.java.kaboome.data.repositories.DataGroupsUsersRepository;
import com.java.kaboome.data.repositories.DataUserGroupRepository;
import com.java.kaboome.data.repositories.DataUserRepository;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.usecases.UpdateMessageAttachmentDetailsUseCase;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.CredentialsHandler;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

/**
 * This worker class is called to upload and download the message attachment or for that matter any image to the server
 * and then do the needful, like updating the local cache accordingly
 * Message attachment upload to S3 takes care of publishing to iot again on upload complete
 */
public class LoadMediaWorker extends ListenableWorker {



    private static AmazonS3Client s3Client = null;

    private static final String TAG = "KMUpdMediaWorker";


    public static final String GROUP_ID = "groupId";
    private static final String USER_ID = "userId";
    public static final String GROUP_NAME = "groupName";
    public static final String GROUP_USER_ALIAS = "groupUserName";
    public static final String GROUP_USER_ROLE = "groupUserRole";
    public static final String MESSAGE_ID = "messageId";
    public static final String ACTION = "action";
    private static final String ATTACHMENT_PATH = "attachment_path";
    private static final String ATTACHMENT_URI = "attachment_uri";
    private static final String IMAGE_TYPE = "imageType";
    public static final String KEY_TO_COPY = "keyToCopy";
    public static final String NEW_KEY = "newKey";

    public LoadMediaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @Override
    public ListenableFuture<Result> startWork() {
        return CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<Result>() {
            @Override
            public WorkerCallback attachCompleter(final CallbackToFutureAdapter.Completer<Result> completer) throws Exception {

                WorkerCallback workerCallback = new WorkerCallback() {
                    @Override
                    public void onSuccess(Data response) {
                        Log.d(TAG, "onSuccess: calling completer.set(success)");
                        completer.set(Result.success(response));
                    }

                    @Override
                    public void onError(Data failure) {
                        Log.d(TAG, "onSuccess: calling completer.set(failure)");
                        completer.setException(new Exception(failure.getString(WORK_RESPONSE)));
                    }
                };

                doWork(workerCallback);

                return workerCallback;



            }
        });
    }



    public void doWork(final WorkerCallback workerCallback) {


        //start a background thread
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                //1. Call the DataImageUpload thingy
                //2. Do the needful once the media is uploaded completely

                String action = getInputData().getString(ACTION);
                if(action.equals(MediaActionConstants.UPLOAD_ATTACHMENT.getAction())){
                    String messageId = getInputData().getString(MESSAGE_ID);
                    String groupId = getInputData().getString(GROUP_ID);
                    String attachmentPath = getInputData().getString(ATTACHMENT_PATH);
                    File attachment = new File(attachmentPath);
                    String attachmentURI = getInputData().getString(ATTACHMENT_URI);

                    //get the message from the message id
                    MessagesListRepository messagesListRepository = DataGroupMessagesRepository.getInstance();
                    DomainMessage message = null;
                    try {
                        message = messagesListRepository.getMessage(messageId);
                    } catch (IllegalArgumentException exception) {
                        //there is some error, it came here with message not being there in the cache
                        //just log and return
                        Log.d(TAG, "Message loading from the cache for the worker comes null, hence returning");
                        exception.printStackTrace();
                        Data outPut = new Data.Builder()
                                .putString(WORK_RESULT,WORK_FAILURE)
                                .putString(WORK_RESPONSE, exception.getMessage())
                                .build();
                        workerCallback.onError(outPut);
                    }

                    message.setAttachmentUri(attachmentURI);

                    //first update the local database for the message attachmentURI
                    UpdateMessageAttachmentDetailsUseCase updateMessageAttachmentDetailsUseCase = new UpdateMessageAttachmentDetailsUseCase(DataGroupMessagesRepository.getInstance());
                    updateMessageAttachmentDetailsUseCase.execute(UpdateMessageAttachmentDetailsUseCase.Params.messageToBeUpdated(messageId, true, false, true, message.getAttachmentMime(), attachmentURI));

                    String key = ImagesUtilHelper.getMessageAttachmentKeyName(groupId, messageId);
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("action", MediaActionConstants.UPLOAD_ATTACHMENT);
                    userData.put("message", message);
                    userData.put("attachment", attachment);

                    loadFile(key, attachment, MediaActionConstants.UPLOAD_ATTACHMENT.getAction(), userData, new loadCallback() {
                        @Override
                        public void onSuccess(HashMap<String, Object> userData) {
                            onLoadSuccess(userData, new DataUpdateCallback(){

                                @Override
                                public void onSuccess() {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT,WORK_SUCCESS)
                                            .putString(WORK_RESPONSE, "SUCCESS")
                                            .build();
                                    workerCallback.onSuccess(outPut);
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT,WORK_FAILURE)
                                            .putString(WORK_RESPONSE, exception.getMessage())
                                            .build();
                                    workerCallback.onError(outPut);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {
                            //TODO: Pass DATA Result as error
                            //image could not be uploaded, so just return error
                            Data outPut = new Data.Builder()
                                    .putString(WORK_RESULT,WORK_FAILURE)
                                    .putString(WORK_RESPONSE, exception.getMessage())
                                    .build();
                            workerCallback.onError(outPut);
                        }

                        @Override
                        public void onProgressChanged(int progress) {
                            String messageId = getInputData().getString("messageId");
                            DataGroupMessagesRepository.getInstance().updateMessageLoadingProgress(messageId, progress);
                        }
                    });

                }

                else if(action.equals(MediaActionConstants.DOWNLOAD_ATTACHMENT.getAction())){

                    String messageId = getInputData().getString(MESSAGE_ID);
                    String groupId = getInputData().getString(GROUP_ID);
                    String groupName = getInputData().getString(GROUP_NAME);
                    String attachmentPath = getInputData().getString(ATTACHMENT_PATH);

                    //get the message from the message id
                    MessagesListRepository messagesListRepository = DataGroupMessagesRepository.getInstance();
                    DomainMessage message = null;
                    try {
                        message = messagesListRepository.getMessage(messageId);
                    } catch (IllegalArgumentException exception) {
                        //there is some error, it came here with message not being there in the cache
                        //just log and return
                        Log.d(TAG, "Message loading from the cache for the worker comes null, hence returning");
                        exception.printStackTrace();
                        Data outPut = new Data.Builder()
                                .putString(WORK_RESULT,WORK_FAILURE)
                                .putString(WORK_RESPONSE, exception.getMessage())
                                .build();
                        workerCallback.onError(outPut);
                    }



                    String key = ImagesUtilHelper.getMessageAttachmentKeyName(groupId, messageId);
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("action", MediaActionConstants.DOWNLOAD_ATTACHMENT);
                    userData.put("message", message);
                    userData.put("groupName", groupName);
                    userData.put("filePath", attachmentPath);

                    loadFile(key, new File(attachmentPath), MediaActionConstants.DOWNLOAD_ATTACHMENT.getAction(), userData, new loadCallback() {
                        @Override
                        public void onSuccess(HashMap<String, Object> userData) {
                            onLoadSuccess(userData, new DataUpdateCallback(){

                                @Override
                                public void onSuccess() {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT,WORK_SUCCESS)
                                            .putString(WORK_RESPONSE, "SUCCESS")
                                            .build();
                                    workerCallback.onSuccess(outPut);
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT,WORK_FAILURE)
                                            .putString(WORK_RESPONSE, exception.getMessage())
                                            .build();
                                    workerCallback.onError(outPut);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {
                            //TODO: Pass DATA Result as error
                            //image could not be uploaded, so just return error
                            Data outPut = new Data.Builder()
                                    .putString(WORK_RESULT,WORK_FAILURE)
                                    .putString(WORK_RESPONSE, exception.getMessage())
                                    .build();
                            workerCallback.onError(outPut);
                        }

                        @Override
                        public void onProgressChanged(int progress) {
                            String messageId = getInputData().getString("messageId");
                            DataGroupMessagesRepository.getInstance().updateMessageLoadingProgress(messageId, progress);
                        }
                    });
                }
                else if(action.equals(MediaActionConstants.UPLOAD_GROUP_PIC.getAction())){

                    String groupId = getInputData().getString(GROUP_ID);
                    String groupName = getInputData().getString(GROUP_NAME);
                    String imageType = getInputData().getString(IMAGE_TYPE);
                    String filePath = getInputData().getString(ATTACHMENT_PATH);


                    String key = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.get(imageType));
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("action", MediaActionConstants.UPLOAD_GROUP_PIC);
                    userData.put("imageType", imageType);
                    userData.put("groupId", groupId);
                    userData.put("groupName", groupName);

                    loadFile(key, new File(filePath), MediaActionConstants.UPLOAD_GROUP_PIC.getAction(), userData, new loadCallback() {
                        @Override
                        public void onSuccess(HashMap<String, Object> userData) {
                            onLoadSuccess(userData, new DataUpdateCallback(){

                                @Override
                                public void onSuccess() {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT,WORK_SUCCESS)
                                            .putString(WORK_RESPONSE, "SUCCESS")
                                            .build();
                                    workerCallback.onSuccess(outPut);
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT,WORK_FAILURE)
                                            .putString(WORK_RESPONSE, exception.getMessage())
                                            .build();
                                    workerCallback.onError(outPut);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {
                            //TODO: Pass DATA Result as error
                            //image could not be uploaded, so just return error
                            Data outPut = new Data.Builder()
                                    .putString(WORK_RESULT,WORK_FAILURE)
                                    .putString(WORK_RESPONSE, exception.getMessage())
                                    .build();
                            workerCallback.onError(outPut);
                        }

                        @Override
                        public void onProgressChanged(int progress) {
                            //TBD: update loading progress for group pic
//                            String messageId = getInputData().getString("messageId");
//                            DataGroupMessagesRepository.getInstance().updateMessageLoadingProgress(messageId, progress);
                        }
                    });
                }
                else if(action.equals(MediaActionConstants.UPLOAD_GROUP_USER_PIC.getAction())) {

                    String groupId = getInputData().getString(GROUP_ID);
                    String userId = getInputData().getString(USER_ID);
                    String imageType = getInputData().getString(IMAGE_TYPE);
                    String filePath = getInputData().getString(ATTACHMENT_PATH);
                    String groupUserRole = getInputData().getString(GROUP_USER_ROLE);
                    String groupUserAlias = getInputData().getString(GROUP_USER_ALIAS);



                    String key = ImagesUtilHelper.getGroupUserImageName(groupId, userId, ImageTypeConstants.get(imageType));
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("action", MediaActionConstants.UPLOAD_GROUP_USER_PIC);
                    userData.put("imageType", imageType);
                    userData.put("groupId", groupId);
                    userData.put("userId", userId);
                    userData.put("groupUserRole", groupUserRole);
                    userData.put("groupUserAlias", groupUserAlias);


                    loadFile(key, new File(filePath), MediaActionConstants.UPLOAD_GROUP_USER_PIC.getAction(), userData, new loadCallback() {
                        @Override
                        public void onSuccess(HashMap<String, Object> userData) {
                            onLoadSuccess(userData, new DataUpdateCallback() {

                                @Override
                                public void onSuccess() {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT, WORK_SUCCESS)
                                            .putString(WORK_RESPONSE, "SUCCESS")
                                            .build();
                                    workerCallback.onSuccess(outPut);
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT, WORK_FAILURE)
                                            .putString(WORK_RESPONSE, exception.getMessage())
                                            .build();
                                    workerCallback.onError(outPut);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {
                            //TODO: Pass DATA Result as error
                            //image could not be uploaded, so just return error
                            Data outPut = new Data.Builder()
                                    .putString(WORK_RESULT, WORK_FAILURE)
                                    .putString(WORK_RESPONSE, exception.getMessage())
                                    .build();
                            workerCallback.onError(outPut);
                        }

                        @Override
                        public void onProgressChanged(int progress) {
                            //TBD: update loading progress for group pic
//                            String messageId = getInputData().getString("messageId");
//                            DataGroupMessagesRepository.getInstance().updateMessageLoadingProgress(messageId, progress);
                        }
                    });
                }
                else if(action.equals(MediaActionConstants.COPY_GROUP_USER_PIC.getAction())) {

                    String groupId = getInputData().getString(GROUP_ID);
                    String userId = getInputData().getString(USER_ID);
                    String imageType = getInputData().getString(IMAGE_TYPE);
                    String keyToCopy = getInputData().getString(KEY_TO_COPY);
                    String newKey = getInputData().getString(NEW_KEY);


                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("action", MediaActionConstants.COPY_GROUP_USER_PIC);
                    userData.put("imageType", imageType);
                    userData.put("groupId", groupId);
                    userData.put("userId", userId);


                    copyFile(keyToCopy, newKey, userData, new loadCallback() {
                        @Override
                        public void onSuccess(HashMap<String, Object> userData) {
                            onLoadSuccess(userData, new DataUpdateCallback() {

                                @Override
                                public void onSuccess() {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT, WORK_SUCCESS)
                                            .putString(WORK_RESPONSE, "SUCCESS")
                                            .build();
                                    workerCallback.onSuccess(outPut);
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT, WORK_FAILURE)
                                            .putString(WORK_RESPONSE, exception.getMessage())
                                            .build();
                                    workerCallback.onError(outPut);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {
                            Data outPut = new Data.Builder()
                                    .putString(WORK_RESULT, WORK_FAILURE)
                                    .putString(WORK_RESPONSE, exception.getMessage())
                                    .build();
                            workerCallback.onError(outPut);
                        }

                        @Override
                        public void onProgressChanged(int progress) {

                        }
                    });

                }
                else if(action.equals(MediaActionConstants.UPLOAD_USER_PROFILE_PIC.getAction())) {
                    String userId = getInputData().getString(USER_ID);
                    String imageType = getInputData().getString(IMAGE_TYPE);
                    String filePath = getInputData().getString(ATTACHMENT_PATH);

                    String key = ImagesUtilHelper.getUserProfilePicName(userId, ImageTypeConstants.get(imageType));
                    HashMap<String, Object> userData = new HashMap<>();
                    userData.put("action", MediaActionConstants.UPLOAD_USER_PROFILE_PIC);
                    userData.put("imageType", imageType);
                    userData.put("userId", userId);

                    loadFile(key, new File(filePath), MediaActionConstants.UPLOAD_GROUP_USER_PIC.getAction(), userData, new loadCallback() {
                        @Override
                        public void onSuccess(HashMap<String, Object> userData) {
                            onLoadSuccess(userData, new DataUpdateCallback() {

                                @Override
                                public void onSuccess() {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT, WORK_SUCCESS)
                                            .putString(WORK_RESPONSE, "SUCCESS")
                                            .build();
                                    workerCallback.onSuccess(outPut);
                                }

                                @Override
                                public void onError(Exception exception) {
                                    Data outPut = new Data.Builder()
                                            .putString(WORK_RESULT, WORK_FAILURE)
                                            .putString(WORK_RESPONSE, exception.getMessage())
                                            .build();
                                    workerCallback.onError(outPut);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception exception) {
                            //TODO: Pass DATA Result as error
                            //image could not be uploaded, so just return error
                            Data outPut = new Data.Builder()
                                    .putString(WORK_RESULT, WORK_FAILURE)
                                    .putString(WORK_RESPONSE, exception.getMessage())
                                    .build();
                            workerCallback.onError(outPut);
                        }

                        @Override
                        public void onProgressChanged(int progress) {
                            //TBD: update loading progress for group pic
//                            String messageId = getInputData().getString("messageId");
//                            DataGroupMessagesRepository.getInstance().updateMessageLoadingProgress(messageId, progress);
                        }
                    });

                }


            }
        });
            }







    private void loadFile(final String key, final File fileToUpload, final String action, final HashMap<String, Object> userData, final loadCallback loadCallback) {
        try {
            CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                @Override
                public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
                    Log.d(TAG, "Successful retrieval of CredentialsProvider");

                    s3Client = new AmazonS3Client(credentialsProviderReturned);
                    s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

                    if(action.equals(MediaActionConstants.UPLOAD_ATTACHMENT.getAction())) {
                        startUploadFile(key, fileToUpload, userData, loadCallback);
                    }
                    else if(action.equals(MediaActionConstants.DOWNLOAD_ATTACHMENT.getAction())){
                        startDownloadFile(key, fileToUpload, userData, loadCallback);
                    }
                    else if(action.equals(MediaActionConstants.UPLOAD_GROUP_PIC.getAction())){
                        startUploadFile(key, fileToUpload, userData, loadCallback);
                    }
                    else if(action.equals(MediaActionConstants.UPLOAD_GROUP_USER_PIC.getAction())){
                        startUploadFile(key, fileToUpload, userData, loadCallback);
                    }
                    else if(action.equals(MediaActionConstants.UPLOAD_USER_PROFILE_PIC.getAction())){
                        startUploadFile(key, fileToUpload, userData, loadCallback);
                    }

                }

                @Override
                public void onFailure(Exception exception) {
                    Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "No callback passed to getCredentials()");
            e.printStackTrace();
        }


    }

    private void copyFile(final String toBeCopiedKey, final String newKey, final HashMap<String, Object> userData, final loadCallback loadCallback) {
        try {
            CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                @Override
                public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
                    Log.d(TAG, "Successful retrieval of CredentialsProvider");

                    s3Client = new AmazonS3Client(credentialsProviderReturned);
                    s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

                    AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "toBeCopiedKey - "+toBeCopiedKey+" new key "+newKey);
                            CopyObjectRequest copyObjRequest = new CopyObjectRequest(AWSConstants.S3_BUCKET_NAME.toString(), toBeCopiedKey, AWSConstants.S3_BUCKET_NAME.toString(), newKey);
                            s3Client.copyObject(copyObjRequest);
                            loadCallback.onSuccess(userData);
                        }
                    });


                }

                @Override
                public void onFailure(Exception exception) {
                    Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);
                    loadCallback.onError(new Exception("failed"));
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "No callback passed to getCredentials()");
            e.printStackTrace();
            loadCallback.onError(new Exception("failed"));
        }
    }

    private void startUploadFile(final String key, final File fileToUpload, final HashMap<String, Object> userData, final loadCallback loadCallback){

        if(fileToUpload == null || key == null){
            String msg = "File or key passed is null, returning immediately";
            //return immediately
            Log.d(TAG, msg);
            return;

        }

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(AppConfigHelper.getContext()).build();
        TransferObserver uploadObserver = transferUtility.upload(AWSConstants.S3_BUCKET_NAME.toString(), key, fileToUpload);

        uploadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    //all callbacks are called on main thread
                    Log.d(TAG, "onStateChanged: success - finished upload");
                    //fileToUpload.delete();
                    //go back to background thread
                    AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            loadCallback.onSuccess(userData);
                        }
                    });

                } else if (TransferState.FAILED == state) {
                    //file.delete();
                    Log.d(TAG, "state failed ");
                    AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            loadCallback.onError(new Exception("failed"));
                        }
                    });

                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.d(TAG, key+" - onProgressChanged: bytes current - "+bytesCurrent);
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                final int percentDone = (int) percentDonef;
                Log.d(TAG, "Percent done "+percentDone);
                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        loadCallback.onProgressChanged(percentDone);
                    }
                });

            }

            @Override
            public void onError(int id, final Exception ex) {
                ex.printStackTrace();
                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        loadCallback.onError(ex);
                    }
                });

            }

        });


    }

    private void startDownloadFile(final String key, final File fileToDownload, final HashMap<String, Object> userData, final loadCallback loadCallback){

        if(fileToDownload == null || key == null){
            String msg = "File or key passed is null, returning immediately";
            //return immediately
            Log.d(TAG, msg);
            return;

        }

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(AppConfigHelper.getContext()).build();
        TransferObserver downloadObserver = transferUtility.download(AWSConstants.S3_BUCKET_NAME.toString(), key, fileToDownload);


        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    //go back to background thread
                    AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            loadCallback.onSuccess(userData);
                        }
                    });
                    //fileToUpload.delete();
                } else if (TransferState.FAILED == state) {
                    //file.delete();
                    Log.d(TAG, "state failed ");
                    AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            loadCallback.onError(new Exception("failed"));
                        }
                    });
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                final int percentDone = (int) percentDonef;
                Log.d(TAG, "Percent done "+percentDone);
                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        loadCallback.onProgressChanged(percentDone);
                    }
                });

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "state failed ");
                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        loadCallback.onError(new Exception("failed"));
                    }
                });
            }

        });
    }


    private void onLoadSuccess(HashMap<String, Object> userData, final DataUpdateCallback dataUpdateCallback){
        //some upload or download changed, see if it relevant to you

        if(userData != null){
            MediaActionConstants mediaActionConstants = (MediaActionConstants) userData.get("action");
            final DomainMessage messageUpOrDownloaded = (DomainMessage) userData.get("message");
            File attachment = (File) userData.get("attachment");

            if(MediaActionConstants.UPLOAD_ATTACHMENT.equals(mediaActionConstants) && messageUpOrDownloaded != null ) {

                    Log.d(TAG, "Upload successful");

                    //now delete the file from external folder
                    //only for version Q and up because they have a new file created in the directories for them
                    //unlike version P and below which is in the external folder and the same has been used as uri
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.deleteFile(attachment.getPath());
                    }
                    dataUpdateCallback.onSuccess();

            }
            else if(MediaActionConstants.DOWNLOAD_ATTACHMENT.equals(mediaActionConstants) && messageUpOrDownloaded != null ) {


                    String attachmentUri = null;

                    String groupName = (String) userData.get("groupName");
                    String filePath = (String) userData.get("filePath");

                    String newName = messageUpOrDownloaded.getGroupId() + "_Group_" + messageUpOrDownloaded.getMessageId() + messageUpOrDownloaded.getAttachmentExtension();
                    attachmentUri = MediaHelper.saveMediaToGallery(AppConfigHelper.getContext(), AppConfigHelper.getContext().getContentResolver(), filePath, newName, messageUpOrDownloaded.getAttachmentMime(), groupName);

                    //now delete the file from external folder
                    //only deleting for the build Q and up since in those builds, the image is copied to the new directory
                    //for older releases, just the path is attached to the uri, but the file is in the same place
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.deleteFile(filePath);
                    }
//                    }

                    //just a dummy update call, does nothing really, but updates the cache, so a refresh is forced
                    DataGroupMessagesRepository.getInstance().updateMessageAttachmentDetails(messageUpOrDownloaded.getMessageId(), true, true, false, messageUpOrDownloaded.getAttachmentMime(), attachmentUri);
                    dataUpdateCallback.onSuccess();

            }
            else if(MediaActionConstants.UPLOAD_GROUP_PIC.equals(mediaActionConstants) ) {

                //here write code to update the local cache
                //the group image has been uploaded and the relevant server data has been updated
                //now reflect the changes in the cache over here
                //changes would be to update the local cache mainly the Group table and the UserGroup table
                //user data has the group id
                //also locally download the images for Glide or have the attachmentUri - figure out


                //so no need to update the local cache because the user would be on group message page
                //when he comes back to GLF, new data from server would be called and displayed
                //this is true when new group is created - should check for when group image is updated

                //but this is not true for group pic change
                //so, let's update the cache
                Log.d(TAG, "Group pic has been successfully uploaded");
                //now download it in the background for future use
                String groupId = (String) userData.get("groupId");
                String groupName = (String) userData.get("groupName");
                String imageType = (String) userData.get("imageType");
                String key = ImagesUtilHelper.getGroupImageName(groupId, ImageTypeConstants.get(imageType));

                /**
                 * adding a second to the time, here is the explanation
                 * When file is uploaded, S3 triggers a lambda function which updates
                 * the database with the new image update time, that time is little
                 * behind the time of this download image call because this call is
                 * directly being called here on the client as as soon as the loading is over
                 * and as a result, image is downloaded again
                 * That time difference is around 3 seconds sometime, so adding 3000ms as a precaution
                 */
                long timePassed = new Date().getTime() + 3000;
                ImageHelper.getInstance().downloadImage(key, timePassed);

                //if it was main image then update the cache and all
                if(ImageTypeConstants.MAIN.getType().equals(imageType)) {
                    //now update the cache
                    DomainGroup domainGroup = new DomainGroup();
                    domainGroup.setGroupId(groupId);
                    domainGroup.setGroupName(groupName);
                    domainGroup.setGroupPicUploaded(true);
                    domainGroup.setGroupPicLoadingGoingOn(false);
                    DataGroupRepository.getInstance().updateGroupCache(domainGroup, GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction());

                    DomainUserGroup domainUserGroup = new DomainUserGroup();
                    domainUserGroup.setUserId(AppConfigHelper.getUserId());
                    domainUserGroup.setGroupId(groupId);
                    domainUserGroup.setGroupName(groupName);
                    domainUserGroup.setGroupPicUploaded(true);
                    domainUserGroup.setGroupPicLoadingGoingOn(false);
                    DataUserGroupRepository.getInstance().updateUserGroupCache(domainUserGroup, GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction());
                }
                dataUpdateCallback.onSuccess();

            }
            else if(MediaActionConstants.UPLOAD_GROUP_USER_PIC.equals(mediaActionConstants) ) {

                //here write code to update the local cache
                //the group user image has been uploaded and the relevant server data has been updated
                //now reflect the changes in the cache over here
                //changes would be to update the local cache mainly the Group table and the UserGroup table
                //user data has the group id
                //also locally download the images for Glide or have the attachmentUri - figure out
                Log.d(TAG, "Group User pic has been successfully uploaded");
                //now download it in the background for future use
                String groupId = (String) userData.get("groupId");
                String userId = (String) userData.get("userId");
                String role = (String) userData.get("groupUserRole");
                String alias = (String) userData.get("groupUserAlias");
                String imageType = (String) userData.get("imageType");
                String key = ImagesUtilHelper.getGroupUserImageName(groupId, userId, ImageTypeConstants.get(imageType));

                /**
                 * adding a second to the time, here is the explanation
                 * When file is uploaded, S3 triggers a lambda function which updates
                 * the database with the new image update time, that time is little
                 * behind the time of this download image call because this call is
                 * directly being called here on the client as as soon as the loading is over
                 * and as a result, image is downloaded again
                 * That time difference is around 3 seconds sometime, so adding 3000ms as a precaution
                 */
                long timePassed = new Date().getTime() + 3000;
                ImageHelper.getInstance().downloadImage(key, timePassed);

                //if it was main image then update the cache and all
                if(ImageTypeConstants.MAIN.getType().equals(imageType)) {
                    //first get the cached GroupUser -  the reason for this is -
                    //if the GroupUser is not there in the cache yet - this happens when
                    //new Group is created -  in that case we do not need to update the cache
                    DomainGroupUser existingGroupUser = DataGroupsUsersRepository.getInstance().getGroupUserFromCache(groupId, AppConfigHelper.getUserId());
                    if(existingGroupUser != null) {
                        //now update the cache
                        DomainGroupUser domainGroupUser = new DomainGroupUser();
                        domainGroupUser.setUserId(AppConfigHelper.getUserId());
                        domainGroupUser.setGroupId(groupId);
                        domainGroupUser.setRole(role);
                        domainGroupUser.setUserName(alias);
                        domainGroupUser.setGroupUserPicUploaded(true);
                        domainGroupUser.setGroupUserPicLoadingGoingOn(false);
                        domainGroupUser.setImageUpdateTimestamp(timePassed); //so that the image is refreshed
                        DataGroupsUsersRepository.getInstance().updateGroupUserCache(domainGroupUser, GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS.getAction());
                    }
                }
                dataUpdateCallback.onSuccess();


            }
            else if(MediaActionConstants.COPY_GROUP_USER_PIC.equals(mediaActionConstants) ) {


                //locally download the images for Glide or have the attachmentUri - figure out
                Log.d(TAG, "Group User pic has been successfully copied");
                //now download it in the background for future use
                String groupId = (String) userData.get("groupId");
                String userId = (String) userData.get("userId");
                String imageType = (String) userData.get("imageType");
                String key = ImagesUtilHelper.getGroupUserImageName(groupId, userId, ImageTypeConstants.get(imageType));

                /**
                 * adding a second to the time, here is the explanation
                 * When file is uploaded, S3 triggers a lambda function which updates
                 * the database with the new image update time, that time is little
                 * behind the time of this download image call because this call is
                 * directly being called here on the client as as soon as the loading is over
                 * and as a result, image is downloaded again
                 * That time difference is around 3 seconds sometime, so adding 3000ms as a precaution
                 */
                long timePassed = new Date().getTime() + 3000;
                ImageHelper.getInstance().downloadImage(key, timePassed);
                dataUpdateCallback.onSuccess();


            }
            else if(MediaActionConstants.UPLOAD_USER_PROFILE_PIC.equals(mediaActionConstants) ) {

                //here write code to update the local cache
                //the user image has been uploaded and the relevant server data has been updated
                //now reflect the changes in the cache over here
                //also locally download the images for Glide or have the attachmentUri - figure out
                Log.d(TAG, "User pic has been successfully uploaded");
                //now download it in the background for future use
                String userId = (String) userData.get("userId");
                String imageType = (String) userData.get("imageType");
                String key = ImagesUtilHelper.getUserProfilePicName(userId, ImageTypeConstants.get(imageType));

                /**
                 * adding a second to the time, here is the explanation
                 * When file is uploaded, S3 triggers a lambda function which updates
                 * the database with the new image update time, that time is little
                 * behind the time of this download image call because this call is
                 * directly being called here on the client as as soon as the loading is over
                 * and as a result, image is downloaded again
                 * That time difference is around 3 seconds sometime, so adding 3000ms as a precaution
                 */
                long timePassed = new Date().getTime() + 3000;
                ImageHelper.getInstance().downloadImage(key, timePassed);

                //if it was main image then update the cache and all
                if(ImageTypeConstants.MAIN.getType().equals(imageType)) {

                    AppConfigHelper.setProfilePicSelected();
                    AppConfigHelper.setCurrentUserImageTimestamp(timePassed);

                    //update the user cache
                    //first get the cached User -  the reason for this is -
                    //if the User is not there in the cache yet - this happens when
                    //new User is created -  in that case we do not need to update the cache
                    DomainUser existingUser = DataUserRepository.getInstance().getUserFromCache(userId);

                    if(existingUser != null) {
                        //now update the cache
                        DomainUser domainUser = new DomainUser();
                        domainUser.setUserId(AppConfigHelper.getUserId());
                        domainUser.setUserPicUploaded(true);
                        domainUser.setUserPicLoadingGoingOn(false);
                        domainUser.setImageUpdateTimestamp(timePassed); //so that the image is refreshed
                        DataUserRepository.getInstance().updateUserInCache(domainUser, UserActionConstants.UPDATE_USER_PROFILE_IMAGE_TS.getAction());
                    }
                }
                dataUpdateCallback.onSuccess();


            }

        }

    }




}

interface loadCallback {

    void onSuccess(HashMap<String, Object> userData);
    void onError(Exception exception);
    void onProgressChanged(int progress);
}

interface DataUpdateCallback {
    void onSuccess();
    void onError(Exception exception);
}

