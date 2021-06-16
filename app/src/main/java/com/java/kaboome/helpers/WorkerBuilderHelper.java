package com.java.kaboome.helpers;

import android.util.Log;

import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.Operation;
import androidx.work.WorkManager;

import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.workers.DeleteGroupAttachmentsWorker;
import com.java.kaboome.data.workers.DeleteMessageAttachmentWorker;
import com.java.kaboome.data.workers.UpdateAdminCacheClearTSWorker;
import com.java.kaboome.data.workers.UpdateConvCacheClearTSWorker;
import com.java.kaboome.data.workers.UpdateDeviceTokenWorker;
import com.java.kaboome.data.workers.UpdateLastSeenConversationTSWorker;
import com.java.kaboome.data.workers.UpdateLastSeenTSWorker;
import com.java.kaboome.data.workers.UpdateUGCacheClearTSWorker;
import com.java.kaboome.data.workers.UpdateUGCleanDoneWorker;

import java.util.concurrent.ExecutionException;

public class WorkerBuilderHelper {

    private static final String TAG = "KMWorkerBuilderHelper";

    public static void callUpdateLastSeenTSWorker(final String groupId, final Long lastAccessed, final Long adminsLastAccessed, Boolean groupNotAdmin){
        if(groupId == null || groupNotAdmin == null){
            return;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data inputData = null;

        if(groupNotAdmin){
            inputData = new Data.Builder()
                    .putString("groupId", groupId)
                    .putLong("lastAccessed", lastAccessed)
                    .putBoolean("groupNotAdmin", true)
                    .build();
        }
        else{
            inputData = new Data.Builder()
                    .putString("groupId", groupId)
                    .putLong("adminsLastAccessed", adminsLastAccessed)
                    .putBoolean("groupNotAdmin", false)
                    .build();
        }

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(UpdateLastSeenTSWorker.class)
                .addTag("last_seen_update")
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
                        Log.d(TAG, "Update lastAccessed failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Update lastAccessed failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful update of lastAccessed");
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Update to lastAccessed failed due to - "+e.getMessage());
        }
    }


    public static void callUpdateLastSeenConvTSWorker(final String groupId, final String conversationId, final Long lastAccessed){
        if(groupId == null || conversationId == null){
            return;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data inputData =  new Data.Builder()
                    .putString("groupId", groupId)
                    .putString("conversationId", conversationId)
                    .putLong("lastAccessed", lastAccessed)
                    .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(UpdateLastSeenConversationTSWorker.class)
                .addTag("last_seen_conv_update")
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
                        Log.d(TAG, "Update lastAccessed for conversation failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Update lastAccessed for conversation failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful update of lastAccessed for conversation ");
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Update to lastAccessed for conversation failed due to - "+e.getMessage());
        }
    }

    public static void callUpdateCacheClearTSWorker(final String groupId, final Long cacheClearTS, final boolean updateServer,
                                                    final boolean updateUserGroupDAO){
        Data inputData = new Data.Builder()
                .putString("groupId", groupId)
                .putLong("cacheClearTS", cacheClearTS)
                .putBoolean("updateServer", updateServer)
                .putBoolean("updateUserGroupDAO", updateUserGroupDAO)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(UpdateUGCacheClearTSWorker.class)
                .addTag("group_clear_cache_TS_update")
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
                        Log.d(TAG, "Update to User Group's clear cache TS failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Update to User Group's clear cache TS failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful Update to User Group's clear cache TS");
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Update to User Group's clear cache TS failed due to - "+e.getMessage());
        }
    }


    public static void callUpdateUGCLeanDoneWorker(final String groupId){
        Data inputData = new Data.Builder()
                .putString("groupId", groupId)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(UpdateUGCleanDoneWorker.class)
                .addTag("user_group_clean_done_worker")
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
                        Log.d(TAG, "Update to User Group's clean done failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Update to User Group's clean done failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful Update to User Group's clean done");
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Update to User Group's clean done failed due to - "+e.getMessage());
        }
    }

    public static void callUpdateAdminCacheClearTSWorker(final String groupId, final Long cacheClearTS, final boolean updateServer,
                                                    final boolean updateUserGroupDAO){
        Data inputData = new Data.Builder()
                .putString("groupId", groupId)
                .putLong("cacheClearTS", cacheClearTS)
                .putBoolean("updateServer", updateServer)
                .putBoolean("updateUserGroupDAO", updateUserGroupDAO)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(UpdateAdminCacheClearTSWorker.class)
                .addTag("group_clear_cache_TS_update")
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
                        Log.d(TAG, "Update to User Group's admin clear cache TS failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Update to User Group's admin clear cache TS failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful Update to User Group's admin clear cache TS");
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Update to User Group's admin clear cache TS failed due to - "+e.getMessage());
        }
    }

    public static void callUpdateConvCacheClearTSWorker(final String groupId, final String otherUserId, final Long cacheClearTS){
        Data inputData = new Data.Builder()
                .putString("groupId", groupId)
                .putLong("cacheClearTS", cacheClearTS)
                .putString("otherUserId", otherUserId)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(UpdateConvCacheClearTSWorker.class)
                .addTag("group_conv_clear_cache_TS_update")
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
                        Log.d(TAG, "Update to User Group Conversation's clear cache TS failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Update to User Group Conversation's clear cache TS failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful Update to User Group Conversation's clear cache TS");
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Update to User Group Conversation's clear cache TS failed due to - "+e.getMessage());
        }
    }

    public static void callDeleteMessageAttachmentWorker(final Message message){

        Data inputData = new Data.Builder()
                .putString("messageId", message.getMessageId())
                .putString("groupId", message.getGroupId())
                .putString("fileExtension", message.getAttachmentExtension())
                .putString("fileMime", message.getAttachmentMime())
                .putString("sentTo", message.getSentTo())
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(DeleteMessageAttachmentWorker.class)
                .addTag("attahcment_delete")
                .setInputData(inputData)
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
                        Log.d(TAG, "Deleting the message attachment failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Deleting the message attachment failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful deletion of the message attachment");
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Deleting the message attachment failed due to - "+e.getMessage());
        }
    }

    public static void callDeleteGroupAttachmentsWorker(final String groupName, final String sentTo){

        Data inputData = new Data.Builder()
                .putString("groupName", groupName)
                .putString("sentTo", sentTo)
                .build();

        //now start a worker to do the same in the backend
        OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
                .Builder(DeleteGroupAttachmentsWorker.class)
                .addTag("group_attachments_delete")
                .setInputData(inputData)
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
                        Log.d(TAG, "Deleting the group attachments failed due to "+e.getCause().getMessage());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Deleting the group attachments failed due to "+e.getMessage());
                    }
                    Log.d(TAG, "Successful deletion of the group attachments");
                }
            }, AppExecutors2.getInstance().diskIO());
        } catch (Exception e) {
            Log.d(TAG, "Deleting the group attachments failed due to - "+e.getMessage());
        }
    }

}
