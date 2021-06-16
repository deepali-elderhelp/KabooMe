package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;

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
public class UpdateLastSeenTSWorker extends Worker {

    private static final String TAG = "KMUpdtLastSeenTSWorker";


    public static final String GROUP_ID = "groupId";
    public static final String LAST_ACCESSED_TS = "lastAccessed";
    public static final String ADMIN_LAST_ACCESSED_TS = "adminsLastAccessed";
    public static final String GROUP_NOT_ADMIN = "groupNotAdmin";

    public UpdateLastSeenTSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
//        commented this below because now even if the session is not valid, the BackendAPIImpl should take care of this
//        by trying to create a new session. If the new session still is not created (say the user is network off or something),
//        then, the backend call will fail. Cache will still have the new value, and the db will have old value
//        In that case, the user will get the old db value again when logged in, so will see new messages for messages which are not
//        new for him, but then again, the whole work manager work should not happen if the network is not there.
//        So, what are the chances of this happening.
//        if(CognitoHelper.getCurrSession() == null || !CognitoHelper.getCurrSession().isValid()){
//            Data outPut = new Data.Builder()
//                    .putString(WORK_RESULT,WORK_FAILURE)
//                    .putString(WORK_RESPONSE, "User session not valid")
//                    .build();
//            return Result.failure(outPut);
//        }
        String groupId = getInputData().getString(GROUP_ID);
//        Long lastAccessed = getInputData().getLong(LAST_ACCESSED_TS, 0L);
        String userId = AppConfigHelper.getUserId();
        Long lastAccessed = getInputData().getLong(LAST_ACCESSED_TS, (new Date()).getTime());
        Long adminsLastAccessed = getInputData().getLong(ADMIN_LAST_ACCESSED_TS, (new Date()).getTime());
        Boolean groupNotAdmin = getInputData().getBoolean(GROUP_NOT_ADMIN, true);

        //doing the cache update in the MessagesViewModel
        //only calling worker for the server update
        //get the latest message from cache, get its sentAt
//        DomainMessage lastMessageInCache = DataGroupMessagesRepository.getInstance().getLatestGroupMessageInCache(groupId);
//        if(lastMessageInCache == null){
//            Data outPut = new Data.Builder()
//                    .putString(WORK_RESULT,WORK_SUCCESS)
////                    .putString(WORK_RESPONSE, String.valueOf(response))
//                    .putString(WORK_RESPONSE, "noMessageInCache")
//                    .build();
//            return Result.success(outPut);
//        }
//        Long lastAccessed = lastMessageInCache.getSentAt();
//
        //update the UserGroup DAO with the new lastAccessed for this group
        //commenting because this is already being done before calling this function
        //it was being called second time over here
//        UserGroupsListRepository userGroupsListRepository = DataUserGroupsListRepository.getInstance();
//        userGroupsListRepository.updateUserGroupLastAccessed(groupId, lastAccessed);

        //update the same info on to the server
        UserGroup userGroup = new UserGroup();
        userGroup.setUserId(userId);
        userGroup.setGroupId(groupId);
        String action = "";
        if(groupNotAdmin) {
            userGroup.setLastAccessed(lastAccessed);
            action = "updateUserGroupLastAccessed";
        }
        else{
            userGroup.setAdminsLastAccessed(adminsLastAccessed);
            action = "updateUserGroupAdminLastAccessed";
        }




        Call<ResponseBody> call = AppConfigHelper.getBackendApiServiceProvider().updateUserGroup(userId, userGroup, action);
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
