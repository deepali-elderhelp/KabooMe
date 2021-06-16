package com.java.kaboome.data.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.entities.DomainMessage;
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
//public class UpdateUGLastMessageTSWorker extends Worker {
//
//
//    public static final String GROUP_ID = "groupId";
//
//    public UpdateUGLastMessageTSWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//
//        String groupId = getInputData().getString(GROUP_ID);
//
//        //first get last cache message for the group from the local cache
//        MessagesListRepository messagesListRepository = DataGroupMessagesRepository.getInstance();
//        DomainMessage message = messagesListRepository.getLatestGroupMessageInCache(groupId);
//
//        if(message == null){
//            return Result.success(null);
//        }
//
//        //now update the User Group DAO
//        UserGroupsListRepository userGroupsListRepository = DataUserGroupsListRepository.getInstance();
//        userGroupsListRepository.updateUserGroupLastMessageTS(groupId, message.getSentAt());
//
//        Data outPut = new Data.Builder()
//                .putString(WORK_RESULT,WORK_SUCCESS)
//                .build();
//        return Result.success(outPut);
//
//    }
//}
