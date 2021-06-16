package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.hbb20.CCPCountryGroup;
import com.java.kaboome.constants.AWSConstants;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.data.repositories.UpdateResource;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.CredentialsHandler;

import java.io.File;
import java.util.Date;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

/**
 * This worker class is called to when an image upload needs to happen in the
 * background. The result is updated only after the complete update had either happened
 * or failed for good.
 */
//public class UploadImageWorker extends Worker {

//    private static final String TAG = "KMUploadImageWorker";
//
//
//    public static final String KEY = "key";
//    public static final String FILE_TO_UPLOAD = "filePath";
//    public static final String CREDENTIALS = "credentials";
//
//    public UploadImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//
//        final Map<String, Object> allData = getInputData().getKeyValueMap();
//        final File fileToUpload = (File) allData.get(FILE_TO_UPLOAD);
//        final String key = (String) allData.get(KEY);
//        final CognitoCachingCredentialsProvider credentials = (CognitoCachingCredentialsProvider)allData.get()
//
//
//        try {
//            CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
//                @Override
//                public void onSuccess(CognitoCachingCredentialsProvider credentialsProviderReturned) {
//                    Log.d(TAG, "Successful retrieval of CredentialsProvider");
//
//                    AmazonS3Client s3Client = new AmazonS3Client(credentialsProviderReturned);
//                    s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
//
//                    startUploadFile(s3Client, key, fileToUpload);
//
//                }
//
//                @Override
//                public void onFailure(Exception exception) {
//                    Log.d(TAG, "Failed retrieval of CredentialsProvider due to" + exception);
//                }
//            });
//
//            Data outPut = new Data.Builder()
//                    .putString(WORK_RESULT,WORK_SUCCESS)
////                    .putString(WORK_RESPONSE, String.valueOf(response))
//                    .putString(WORK_RESPONSE, "started")
//                    .build();
//            return Result.success(outPut);
//
//        } catch (Exception e) {
//            Log.d(TAG, "No callback passed to getCredentials()");
//            e.printStackTrace();
//        }
//    }
//
//
//    private void startUploadFile(AmazonS3Client s3Client, final String key, final File fileToUpload){
//
//        if(fileToUpload == null || key == null){
//            String msg = "File or key passed is null, returning immediately";
//            //return immediately
//            Log.d(TAG, msg);
//            return;
//
//        }
//
//        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(AppConfigHelper.getContext()).build();
//        TransferObserver uploadObserver = transferUtility.upload(AWSConstants.S3_BUCKET_NAME.toString(), key, fileToUpload);
//
//        uploadObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if (TransferState.COMPLETED == state) {
//                    //all callbacks are called on main thread
//
//                } else if (TransferState.FAILED == state) {
//                    Log.d(TAG, "state failed ");
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
//                int percentDone = (int) percentDonef;
//                Log.d(TAG, "Percent done "+percentDone);
//
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                ex.printStackTrace();
//            }
//
//        });
//    }
//}
