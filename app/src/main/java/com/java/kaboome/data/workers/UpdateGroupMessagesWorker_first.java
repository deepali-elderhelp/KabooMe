package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.remote.responses.GroupMessagesResponse;
import com.java.kaboome.helpers.AppConfigHelper;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

public class UpdateGroupMessagesWorker_first extends Worker {

    private static final String TAG = "KMUpdtGrpMsgssWork";


    public static final String GROUP_ID = "groupId";
    public static final String SCAN_DIRECTION="scanDirection";
    public static final String LIMIT = "limit";
    public static final String LAST_ACCESSED_TIME = "lastAccessedTime";
    public static final String CACHE_CLEAR_TS = "cacheClearTS";

    public UpdateGroupMessagesWorker_first(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //get the messages from the backend
        Log.d(TAG, "doWork: Thread is - "+Thread.currentThread().getName());
        //first get the device token passed by the caller
        String groupId = getInputData().getString(GROUP_ID);
        String scanDirection = getInputData().getString(SCAN_DIRECTION);
        int limit = getInputData().getInt(LIMIT, 30);
        Long lastAccessedTime = getInputData().getLong(LAST_ACCESSED_TIME, (new Date()).getTime());
        Long cacheClearTS = getInputData().getLong(CACHE_CLEAR_TS, 0L);
        String userId = AppConfigHelper.getUserId();

        Call<GroupMessagesResponse> call = AppConfigHelper.getBackendApiServiceProvider().getMessagesForGroupInBackground(userId, groupId, lastAccessedTime, cacheClearTS,limit, scanDirection, "Group");
        try {
            GroupMessagesResponse responseBody = call.execute().body();
            if(responseBody == null){
                throw new Exception("Response in null");
            }
//            User response = call.execute().body();
            Data outPut = new Data.Builder()
                    .putString(WORK_RESULT,WORK_SUCCESS)
//                    .putString(WORK_RESPONSE, String.valueOf(response))
                    .putString(WORK_RESPONSE, String.valueOf(responseBody))
                    .build();
            return Result.success(outPut);

        } catch (Exception e) { //retrofit failure also comes here
            e.printStackTrace();
            Data outPut = new Data.Builder()
                    .putString(WORK_RESULT,WORK_FAILURE)
                    .putString(WORK_RESPONSE, e.getMessage())
                    .build();
            return Result.failure(outPut);
        }
    }
}
