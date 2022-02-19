package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.java.kaboome.constants.AWSConstants;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UpdateResourceDomainResourceMapper;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.CredentialsHandler;
import com.java.kaboome.presentation.images.ImageHelper;

import java.io.File;
import java.util.HashMap;

public class DataImageUploadRepository implements ImageUploadRepository {

    private static final String TAG = "KMDataImageUploadRepo";

    private AppExecutors2 appExecutors;
    private static DataImageUploadRepository instance;

    private static AmazonS3Client s3Client = null;

    private MediatorLiveData<UpdateResource<String>> results = new MediatorLiveData<>();
    private MediatorLiveData<Resource<HashMap<String,Object>>> observeUpload = new MediatorLiveData<>();
    private MediatorLiveData<Resource<HashMap<String,Object>>> observeDownload = new MediatorLiveData<>();

    private DataImageUploadRepository(){
        appExecutors = AppExecutors2.getInstance();
    }

    public static DataImageUploadRepository getInstance(){
        if(instance == null){
            instance = new DataImageUploadRepository();
        }
        return instance;
    }


    @Override
    public LiveData<DomainUpdateResource<String>> uploadImage(String key, File fileToUpload, String action, HashMap<String,Object> userData){
        //TODO: get this timestamp back from server
//        this.timestamp = (new Date()).getTime();
        return Transformations.map(uploadImageToServer(key, fileToUpload, action, userData), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }


    @Override
    public LiveData<DomainUpdateResource<String>> downloadFile(String key, File downloadFile, String action, HashMap<String,Object> userData) {
        return Transformations.map(downloadFileFromServer(key, downloadFile, action, userData), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }



    @Override
    public LiveData<DomainUpdateResource<String>> copyImage(final String toBeCopiedKey, final String newKey, final String action) {
        return Transformations.map(copyImagePrivate(toBeCopiedKey, newKey, action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });

    }

    @Override
    public void startBackgroundCopyImage(final String toBeCopiedKey, final String newKey) {
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
                            try {
                                CopyObjectRequest copyObjRequest = new CopyObjectRequest(AWSConstants.S3_BUCKET_NAME.toString(), toBeCopiedKey, AWSConstants.S3_BUCKET_NAME.toString(), newKey);
                                s3Client.copyObject(copyObjRequest);
                            } catch (AmazonClientException e) {
                                e.printStackTrace();
                                Log.d(TAG, "Exception in startBackgroundCopyImage "+e.getMessage());
                            }
                        }
                    });


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

    @Override
    public LiveData<DomainResource<HashMap<String,Object>>> observeUpload() {
        return Transformations.map(observeUpload, new Function<Resource<HashMap<String,Object>>, DomainResource<HashMap<String,Object>>>() {
            @Override
            public DomainResource<HashMap<String,Object>> apply(Resource<HashMap<String,Object>> input) {
                return ResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }

    @Override
    public LiveData<DomainResource<HashMap<String,Object>>> observeDownload() {
        return Transformations.map(observeDownload, new Function<Resource<HashMap<String,Object>>, DomainResource<HashMap<String,Object>>>() {
            @Override
            public DomainResource<HashMap<String,Object>> apply(Resource<HashMap<String,Object>> input) {
                return ResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }


    private LiveData<UpdateResource<String>> copyImagePrivate(final String toBeCopiedKey, final String newKey, final String action){
        // update LiveData for loading status
        results.setValue(UpdateResource.updating(action));
        copyFile(toBeCopiedKey, newKey, action);

//        //copy the image now in a background thread
//        appExecutors.diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                copyFile(toBeCopiedKey, newKey, action);
//            }
//        });


        return results;
    }

    private void copyFile(final String toBeCopiedKey, final String newKey, final String action) {
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
                            CopyObjectRequest copyObjRequest = new CopyObjectRequest(AWSConstants.S3_BUCKET_NAME.toString(), toBeCopiedKey, AWSConstants.S3_BUCKET_NAME.toString(), newKey);
                            s3Client.copyObject(copyObjRequest);
                        }
                    });

                    results.setValue(UpdateResource.success(action));

                }

                @Override
                public void onFailure(Exception exception) {
                    Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);
                    results.setValue(UpdateResource.error(exception.getMessage(), action));

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "No callback passed to getCredentials()");
            results.setValue(UpdateResource.error(e.getMessage(), action));
            e.printStackTrace();
        }
    }


    private LiveData<UpdateResource<String>> uploadImageToServer(final String key, final File fileToUpload, final String action, HashMap<String, Object> userData){
        // update LiveData for loading status
        results.setValue(UpdateResource.updating(action));

//        //upload the image now in a background thread
//        appExecutors.diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                uploadFile(key, fileToUpload, action);
//            }
//        });

        //no need to start another thread because the TransferUtility just submits the task to a background service
        uploadFile(key, fileToUpload, action, userData);

        return results;
    }

    private LiveData<UpdateResource<String>> downloadFileFromServer(final String key, final File fileToUpload, final String action, final HashMap<String, Object> userData){
        // update LiveData for loading status
        results.setValue(UpdateResource.updating("loading"));

//        //upload the image now in a background thread
//        appExecutors.diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                downloadFileFromS3(key, fileToUpload, action);
//            }
//        });

        //no need to start another thread because the TransferUtility just submits the task to a background service
        downloadFileFromS3(key, fileToUpload, action, userData);

        return results;
    }

    private void uploadFile(final String key, final File fileToUpload, final String action, final HashMap<String, Object> userData) {
        //check S3LoadingHelper line - 86-100 for explanation
        //applies here as well
//        if (s3Client == null) {
            try {
                CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                    @Override
                    public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
                        Log.d(TAG, "Successful retrieval of CredentialsProvider");

                        s3Client = new AmazonS3Client(credentialsProviderReturned);
                        s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

                        startUploadFile(key, fileToUpload, action, userData);

                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);
//                        results.postValue(UpdateResource.error(exception.getMessage(), action));
                        results.setValue(UpdateResource.error(exception.getMessage(), action));

                    }
                });
            } catch (Exception e) {
                Log.d(TAG, "No callback passed to getCredentials()");
//                results.postValue(UpdateResource.error(e.getMessage(), action));
                results.setValue(UpdateResource.error(e.getMessage(), action));
                e.printStackTrace();
            }

//        }
//        else{ //s3 is not null, already initialized and ready to use
//            startUploadFile(key, fileToUpload);
//
//        }


    }

    private void downloadFileFromS3(final String key, final File fileToUpload, final String action, final HashMap<String, Object> userData) {
        //check S3LoadingHelper line - 86-100 for explanation
        //applies here as well
//        if (s3Client == null) {
            try {
                CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                    @Override
                    public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
                        Log.d(TAG, "Successful retrieval of CredentialsProvider");

                        s3Client = new AmazonS3Client(credentialsProviderReturned);
                        s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

                        startDownloadFile(key, fileToUpload, action, userData);

                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);
//                        results.postValue(UpdateResource.error(exception.getMessage(), action));
                        results.setValue(UpdateResource.error(exception.getMessage(), action));

                    }
                });
            } catch (Exception e) {
                Log.d(TAG, "No callback passed to getCredentials()");
//                results.postValue(UpdateResource.error(e.getMessage(), action));
                results.setValue(UpdateResource.error(e.getMessage(), action));
                e.printStackTrace();
            }

//        }
//        else{ //s3 is not null, already initialized and ready to use
//            startDownloadFile(key, fileToUpload);
//
//        }


    }

    private void startUploadFile(final String key, final File fileToUpload, final String action, final HashMap<String, Object> userData){

        if(fileToUpload == null || key == null){
            String msg = "File or key passed is null, returning immediately";
            //return immediately
            Log.d(TAG, msg);
//            results.postValue(UpdateResource.error(msg, action));
            results.setValue(UpdateResource.error(msg, action));
            return;

        }

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(AppConfigHelper.getContext()).build();
        TransferObserver uploadObserver = transferUtility.upload(AWSConstants.S3_BUCKET_NAME.toString(), key, fileToUpload);

            uploadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        //all callbacks are called on main thread
//                    results.postValue(UpdateResource.success(action));
                        Log.d(TAG, "onStateChanged: success - finished upload");
                        results.setValue(UpdateResource.success(action));
                        observeUpload.setValue(Resource.success(userData));
                        //fileToUpload.delete();
                    } else if (TransferState.FAILED == state) {
                        //file.delete();
                        Log.d(TAG, "state failed ");
//                    results.postValue(UpdateResource.error("Failed to upload", action));
                        results.setValue(UpdateResource.error("Failed to upload", action));
                        observeUpload.setValue(Resource.error("Failed to upload", userData));
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    Log.d(TAG, "onProgressChanged: bytes current - "+bytesCurrent);
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int) percentDonef;
                    Log.d(TAG, "Percent done "+percentDone);
//                results.postValue(UpdateResource.updating(String.valueOf(percentDone)));
                    results.setValue(UpdateResource.updating(String.valueOf(percentDone)));
                    userData.put("percent", String.valueOf(percentDone));
                    observeUpload.setValue(Resource.loading(userData));
                }

                @Override
                public void onError(int id, Exception ex) {
                    ex.printStackTrace();
//                results.postValue(UpdateResource.error(ex.getMessage(), action));
                    results.setValue(UpdateResource.error(ex.getMessage(), action));
                    observeUpload.setValue(Resource.error("Failed to upload", userData));
                }

            });


    }
    @Override
    public void startBackgroundUploadFile(final String key, final File fileToUpload, final boolean downloadImage){

        if(fileToUpload == null || key == null){
            String msg = "File or key passed is null, returning immediately";
            //return immediately
            Log.d(TAG, msg);
            return;

        }

        try {
            CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                @Override
                public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
                    Log.d(TAG, "Successful retrieval of CredentialsProvider");

                    s3Client = new AmazonS3Client(credentialsProviderReturned);
                    s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));

                    TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(AppConfigHelper.getContext()).build();
                    TransferObserver uploadObserver = transferUtility.upload(AWSConstants.S3_BUCKET_NAME.toString(), key, fileToUpload);
                    if(downloadImage){
                        uploadObserver.setTransferListener(new TransferListener() {

                            @Override
                            public void onStateChanged(int id, TransferState state) {
                                if (TransferState.COMPLETED == state) {
                                    //all callbacks are called on main thread
                                    //download the image for further use
                                    Log.d("TestTAG", "uploading of the image is done = "+key);
                                    ImageHelper.getInstance().downloadImage(key);

                                } else if (TransferState.FAILED == state) {
                                    //file.delete();
                                    Log.d(TAG, "state failed ");
                                }
                            }

                            @Override
                            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                            }


                            @Override
                            public void onError(int id, Exception ex) {
                                ex.printStackTrace();
                            }

                        });

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

    private void startDownloadFile(final String key, final File fileToDownload, final String action, final HashMap<String, Object> userData){

        if(fileToDownload == null || key == null){
            String msg = "File or key passed is null, returning immediately";
            //return immediately
            Log.d(TAG, msg);
//            results.postValue(UpdateResource.error(msg, "error"));
            results.setValue(UpdateResource.error(msg, "error"));
            return;

        }

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(AppConfigHelper.getContext()).build();
        TransferObserver downloadObserver = transferUtility.download(AWSConstants.S3_BUCKET_NAME.toString(), key, fileToDownload);


        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
//                    results.postValue(UpdateResource.success(action));
                    results.setValue(UpdateResource.success(action));
                    observeDownload.setValue(Resource.success(userData));
                    //fileToUpload.delete();
                } else if (TransferState.FAILED == state) {
                    //file.delete();
                    Log.d(TAG, "state failed ");
//                    results.postValue(UpdateResource.error("Failed to download", action));
                    results.setValue(UpdateResource.error("Failed to download", action));
                    observeDownload.setValue(Resource.error("Failed to download", userData));
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
                Log.d(TAG, "Percent done "+percentDone);
                results.setValue(UpdateResource.updating(String.valueOf(percentDone)));
                userData.put("percent",String.valueOf(percentDone));
                observeDownload.setValue(Resource.loading(userData));

            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
//                results.postValue(UpdateResource.error(ex.getMessage(), action));
                results.setValue(UpdateResource.error(ex.getMessage(), action));
                observeDownload.setValue(Resource.error("Failed to download", userData));
            }

        });
    }


}
