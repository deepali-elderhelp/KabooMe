package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.remote.responses.GroupMessagesResponse;
import com.java.kaboome.helpers.AppConfigHelper;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

public class UpdateGroupMessagesWorker extends ListenableWorker {

    private static final String TAG = "KMMsgUpdtGrpMsgssWork";


    public static final String GROUP_ID = "groupId";
    public static final String SCAN_DIRECTION="scanDirection";
    public static final String LIMIT = "limit";
    public static final String LAST_ACCESSED_TIME = "lastAccessedTime";
    public static final String CACHE_CLEAR_TS = "cacheClearTS";

    public UpdateGroupMessagesWorker( Context context, WorkerParameters workerParams) {
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

    private void doWork(final WorkerCallback workerCallback) {

        //start a background thread

        AppExecutors2.getInstance().getServiceDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                //get the messages from the backend
                Log.d(TAG, "doWork: Thread is - "+Thread.currentThread().getName());

                //first get the device token passed by the caller
                String groupId = getInputData().getString(GROUP_ID);
                String scanDirection = getInputData().getString(SCAN_DIRECTION);
                int limit = getInputData().getInt(LIMIT, 30);
                Long lastAccessedTime = getInputData().getLong(LAST_ACCESSED_TIME, (new Date()).getTime());
                Long cacheClearTS = getInputData().getLong(CACHE_CLEAR_TS, 0L);
                String userId = AppConfigHelper.getUserId();

                Call<GroupMessagesResponse> call = AppConfigHelper.getBackendApiServiceProvider().getMessagesForGroupInBackground(userId, groupId, lastAccessedTime, cacheClearTS, limit, scanDirection, "Group");
                try {
                    GroupMessagesResponse responseBody = call.execute().body();
                    if(responseBody == null){
                        throw new Exception("Response in null");
                    }
                    Data outPut = new Data.Builder()
                            .putString(WORK_RESULT,WORK_SUCCESS)
                            .putString(WORK_RESPONSE, String.valueOf(responseBody))
                            .build();
                    workerCallback.onSuccess(outPut);

                } catch (Exception e) { //retrofit failure also comes here
                    e.printStackTrace();
                    Data outPut = new Data.Builder()
                            .putString(WORK_RESULT,WORK_FAILURE)
                            .putString(WORK_RESPONSE, e.getMessage())
                            .build();
                    workerCallback.onError(outPut);
                }
            }
        });


    }
}
