package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.data.repositories.DataConversationsRepository;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.repositories.ConversationsRepository;
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
public class UpdateConvCacheClearTSWorker extends Worker {

    private static final String TAG = "KMUpdConvCachClearTSWor";


    public static final String GROUP_ID = "groupId";
    public static final String ACTION = "action";
    public static final String CACHE_CLEAR_TS = "cacheClearTS";
    public static final String OTHER_USER_ID = "otherUserId";

    public UpdateConvCacheClearTSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String groupId = getInputData().getString(GROUP_ID);
        Long cacheCLearTS = getInputData().getLong(CACHE_CLEAR_TS, (new Date()).getTime());
        String otherUserId = getInputData().getString(OTHER_USER_ID);

        //first delete the messages from the local cache
        MessagesListRepository messagesListRepository = DataGroupMessagesRepository.getInstance();
        messagesListRepository.clearMessagesOfConversation(groupId, otherUserId);

        //now update the Conversation DAO
            ConversationsRepository conversationsRepository = DataConversationsRepository.getInstance();
            conversationsRepository.updateUserGroupConversationCacheClearTS(groupId, otherUserId, cacheCLearTS);

            //now update the same info on to the server
        UserGroupConversation userGroupConversation = new UserGroupConversation();
        userGroupConversation.setUserId(AppConfigHelper.getUserId());
        userGroupConversation.setOtherUserId(otherUserId);
        userGroupConversation.setCacheClearTS(cacheCLearTS);


            Call<ResponseBody> call = AppConfigHelper.getBackendApiServiceProvider().updateUserGroupConversation(AppConfigHelper.getUserId(), groupId, otherUserId, userGroupConversation, "updateConversationCacheClearTS");
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
