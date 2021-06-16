package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.entities.UserGroupConversation;
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
public class UpdateLastSeenConversationTSWorker extends Worker {

    private static final String TAG = "KMUpdtLstSnConvTSWrker";


    public static final String GROUP_ID = "groupId";
    public static final String CONVERSATION_ID = "conversationId";
    public static final String LAST_ACCESSED_TS = "lastAccessed";


    public UpdateLastSeenConversationTSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String groupId = getInputData().getString(GROUP_ID);
        String conversationId = getInputData().getString(CONVERSATION_ID);
        String userId = AppConfigHelper.getUserId();
        Long lastAccessed = getInputData().getLong(LAST_ACCESSED_TS, (new Date()).getTime());

        //doing the cache update in the ViewModel
        //only calling worker for the server update

        UserGroupConversation userGroupConversation = new UserGroupConversation();
        userGroupConversation.setGroupId(groupId);
        userGroupConversation.setUserId(userId);
        userGroupConversation.setOtherUserId(conversationId);
        userGroupConversation.setLastAccessed(lastAccessed);
        String action = "updateConversationLastAccessed";




        Call<ResponseBody> call = AppConfigHelper.getBackendApiServiceProvider().updateUserGroupConversation(userId, groupId, conversationId, userGroupConversation, action);
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
            Log.d(TAG, "doWork: exception- "+e.getMessage());
            e.printStackTrace();
            Data outPut = new Data.Builder()
                    .putString(WORK_RESULT,WORK_FAILURE)
                    .putString(WORK_RESPONSE, e.getMessage())
                    .build();
            return Result.failure(outPut);
        }
    }
}
