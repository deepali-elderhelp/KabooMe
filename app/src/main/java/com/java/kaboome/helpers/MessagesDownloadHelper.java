//package com.java.kaboome.helpers;
//
//import android.util.Log;
//
//import androidx.lifecycle.Observer;
//import androidx.work.Configuration;
//import androidx.work.Constraints;
//import androidx.work.Data;
//import androidx.work.NetworkType;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.Operation;
//import androidx.work.WorkInfo;
//import androidx.work.WorkManager;
//
//import com.java.kaboome.data.constants.WorkerConstants;
//import com.java.kaboome.data.executors.AppExecutors2;
//import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
//import com.java.kaboome.data.workers.UpdateGroupMessagesWorker;
//import com.java.kaboome.domain.entities.DomainMessage;
//import com.java.kaboome.domain.entities.DomainUserGroup;
//import com.java.kaboome.domain.usecases.GetUserGroupLastMessageCache;
//import com.java.kaboome.presentation.entities.UserGroupModel;
//
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
///**
// * Idea behind this class -
// * In the background, download all the undownloaded messages for the user groups
// * So, the idea is, look into each User Group messages cache, find the sentAt for the last message
// * Send this data to the server and get the messages since the last message and upload them in the cache
// * So now, the messages are there in the cache and all the unread counts/number of messages shows up nice
// *
// * For implementation - this class calls the worker of a work manager for each group and get the last
// * messages
// */
//public class MessagesDownloadHelper {
//    private static final String TAG = "KMMsgsDownloadHelper";
//
//    public static void updateMessagesForUserGroups(List<DomainUserGroup> groups, AppExecutors2 appExecutors){
//
////        WorkManager.initialize(
////                AppConfigHelper.getContext(),
////                new Configuration.Builder()
////                        .setExecutor(Executors.newFixedThreadPool(8))
////                        .build());
//
//        for(DomainUserGroup domainUserGroup: groups){
//            if(!domainUserGroup.getDeleted()){
//                updateMessagesForGroup(domainUserGroup, appExecutors);
//            }
////        if(domainUserGroup.getGroupId().equalsIgnoreCase("bf9abd6e-5073-442e-9e75-fe7f711a4c39"))
////            updateMessagesForGroup(domainUserGroup, appExecutors);
//        }
//
//    }
//
//    public static void updateMessagesForGroup(final DomainUserGroup userGroup, final AppExecutors2 appExecutors){
//
//        if(appExecutors == null){
//            return; //need to be passed an executor
//        }
//
//        appExecutors.diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                //first get the sentAt for last message in the cache for this group
//                GetUserGroupLastMessageCache getUserGroupLastMessageCache = new GetUserGroupLastMessageCache(DataGroupMessagesRepository.getInstance());
//                final DomainMessage lastMessage = getUserGroupLastMessageCache.execute(GetUserGroupLastMessageCache.Params.forGroup(userGroup.getGroupId()));
//
////                if(lastMessage == null) {//if there are no messages in the group
////                    lastMessage.setSentAt();
////                }
//
//                appExecutors.mainThread().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Data inputData = new Data.Builder()
//                                .putString("groupId", userGroup.getGroupId())
//                                .putLong("lastAccessed", lastMessage != null ? lastMessage.getSentAt(): (new Date()).getTime())
//                                .putLong("cacheClearTS", userGroup.getCacheClearTS())
//                                .putInt("limit", 20)
//                                .putString("scanDirection","forward")
//                                .build();
//
//                        Constraints constraints = new Constraints.Builder()
//                                .setRequiredNetworkType(NetworkType.CONNECTED)
//                                .build();
//
//                        //now start a worker to do the same in the backend
//                        final OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest
//                                .Builder(UpdateGroupMessagesWorker.class)
//                                .addTag(userGroup.getGroupId())
//                                .setInputData(inputData)
//                                .setConstraints(constraints)
//                                .build();
//
//                        WorkManager.getInstance().enqueue(simpleRequest).getResult();
//
//                        WorkManager.getInstance().getWorkInfoByIdLiveData(simpleRequest.getId()).observeForever(new Observer<WorkInfo>() {
//                            @Override
//                            public void onChanged(WorkInfo workInfo) {
//                                if(workInfo.getState().isFinished()){
//                                    Data outputData = workInfo.getOutputData();
//                                    Log.d(TAG, "onChanged: "+outputData.getString(WorkerConstants.WORK_RESPONSE));
//                                }
//                            }
//                        });
//                    }
//                });
//
//
//
////                try {
////                    Future<WorkInfo> workInfoFuture= WorkManager.getInstance().getWorkInfoById(simpleRequest.getId());
////
////                    WorkInfo workInfo = workInfoFuture.get();
////
////                    if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
////                        Log.d(TAG, "returned messages - "+workInfo.getOutputData().getString(WorkerConstants.WORK_RESPONSE));
////                    }
////                    else{
////                        Log.d(TAG, "State did not match - it is "+workInfo.getState());
////                    }
////                }
////                catch (Exception e) {
////                    Log.d(TAG, "run: exception - "+e.getMessage());
////                }
//
//
//
////                final Operation resultOfOperation = WorkManager.getInstance().enqueue(simpleRequest);
//
//
//
////                try {
////                    resultOfOperation.getResult().addListener(new Runnable() {
////                        @Override
////                        public void run() {
////                            try {
////                                Log.d(TAG, "run: came back with this - "+resultOfOperation);
////                                resultOfOperation.getResult().get();
////
////                                WorkInfo workInfo = WorkManager.getInstance().getWorkInfoById(simpleRequest.getId()).get();
////                                if(workInfo.getState() == WorkInfo.State.SUCCEEDED){
////                                    Log.d(TAG, "returned messages - "+workInfo.getOutputData().getString(WorkerConstants.WORK_RESPONSE));
////                                }
////                                else{
////                                    Log.d(TAG, "State did not match - it is "+workInfo.getState()+" and "+workInfo.getOutputData().getString(WorkerConstants.WORK_RESPONSE));
////                                }
////                                //check the result and update to local cache and based on the size of the result, make another workmanager work request
////                                //for getting the next set of messages
////
////
////                            } catch (ExecutionException e) {
////                                e.printStackTrace();
////                                //if the update API gave error, it gets wrapped in ExecutionException
////                                Log.d(TAG, "Getting messages for the group "+userGroup.getGroupId()+" failed due to - "+e.getCause().getMessage());
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                                Log.d(TAG, "Getting messages for the group "+userGroup.getGroupId()+" failed due to - "+e.getMessage());
////                            }
////
////                            Log.d(TAG, "Successful Getting messages for the group "+userGroup.getGroupId());
////
////                        }
////                    }, AppExecutors2.getInstance().diskIO()); //listening on io thread
////                } catch (Exception e) {
////                    Log.d(TAG, "Getting messages for the group "+userGroup.getGroupId()+e.getMessage());
////                }
//            }
//        });
//
//
//
//
//    }
//}
