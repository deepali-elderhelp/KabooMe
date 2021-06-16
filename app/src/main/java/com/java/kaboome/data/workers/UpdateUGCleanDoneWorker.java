package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.helpers.AppConfigHelper;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

/**
 * This worker class is called to set the new Cache clear TimeStamp for a
 * particular group. This class uploads that time to the server
 */
public class UpdateUGCleanDoneWorker extends Worker {

    private static final String TAG = "KMUpdtUGCleanDoneWrkr";


    public static final String GROUP_ID = "groupId";
    public static final String ACTION = "action";

    public UpdateUGCleanDoneWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

            String groupId = getInputData().getString(GROUP_ID);
            String userId = AppConfigHelper.getUserId();

            //now update the same info on to the server
            UserGroup userGroup = new UserGroup();
            userGroup.setUserId(userId);
            userGroup.setGroupId(groupId);


            Call<ResponseBody> call = AppConfigHelper.getBackendApiServiceProvider().updateUserGroup(userId, userGroup, "updateUserGroupCleanUpDone");
            try {
                ResponseBody responseBody = call.execute().body();
                if(responseBody == null){
                    throw new Exception("Response is null");
                }
//            User response = call.execute().body();
                Data outPut = new Data.Builder()
                        .putString(WORK_RESULT,WORK_SUCCESS)
//                    .putString(WORK_RESPONSE, String.valueOf(response))
                        .putString(WORK_RESPONSE, String.valueOf(responseBody))
                        .build();
                return Result.success(outPut);

            } catch (Exception e) { //retrofit failure also comes here
                Log.d(TAG, "doWork: failed - exception - "+e.getMessage());
                e.printStackTrace();
                Data outPut = new Data.Builder()
                        .putString(WORK_RESULT,WORK_FAILURE)
                        .putString(WORK_RESPONSE, e.getMessage())
                        .build();
                return Result.failure(outPut);
            }

    }
}
