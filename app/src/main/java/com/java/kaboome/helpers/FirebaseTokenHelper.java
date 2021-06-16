package com.java.kaboome.helpers;

import android.util.Log;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.workers.UpdateDeviceTokenWorker;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FirebaseTokenHelper {

    private static final String TAG = "KMFirebaseTokenHelper";

    public static boolean matchTokens(String newDeviceToken){

        String deviceToken = AppConfigHelper.getDeviceId(); //this device id is from before

        if(deviceToken == null || deviceToken.isEmpty()) //still not assigned
            return false;
        else
            return deviceToken.equals(newDeviceToken);


    }

//    /**
//     * This method is called when a new token is received and the current session is valid
//     * so a backend call to update the token is made
//     * @param newDeviceToken
//     */
//    public static void updateDeviceToken(String newDeviceToken) {
//
//        Log.d(TAG, "updating Device Token");
//
//
//        if(newDeviceToken == null || newDeviceToken.isEmpty()) //something wrong, let's not set it
//            return;
//
//        //call worked thread and update it to the server - would also work when the oldToken does not exist
//    }


    /**
     * This method can be called whenever a new token is received and and the current session is valid
     * so a backend call to update the token is made. Make sure this method is called after checking that the
     * session is valid
     */
    public static void updateDeviceToken() {

        Log.d(TAG, "updating Device Token");

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                String oldToken = AppConfigHelper.getDeviceId();
                if(newToken != null && !newToken.isEmpty() && !newToken.equals(oldToken)){

                    //call worker thread and update it to the server - would also work when the oldToken does not exist
                    callUpdateDeviceTokenWorkerThread(newToken);
                    Log.d(TAG, "new token and old token do not match, hence need to be updated");
                }
            }
        });

    }

    private static void callUpdateDeviceTokenWorkerThread(final String newDeviceToken){
        Data inputData = new Data.Builder()
                .putString("token", newDeviceToken)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(UpdateDeviceTokenWorker.class)
                .addTag("token_update")
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
                        resultOfOperation.getResult().get();

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        //if the update API gave error, it gets wrapped in ExecutionException
                        Log.d(TAG, "Update to device token failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Update to device token failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful update of the device token");
                    //now update the local device token
                    AppConfigHelper.setDeviceId(newDeviceToken);
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Device token update failed due to - "+e.getMessage());
        }
    }
}
