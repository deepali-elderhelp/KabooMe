package com.java.kaboome.data.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.User;
import com.java.kaboome.data.mappers.UserDataDomainMapper;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataUserRepository;
import com.java.kaboome.helpers.AppConfigHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

/**
 * Whenever an uploads fails, may be due to big size, or network connection or
 * something, this worker updates the backend to save uploadGoingOn to false
 * and updated the local cache to do the same
 */
public class UpdateUploadFailedWorker extends Worker {


    public static final String MESSAGE_ID = "messageId";
    public static final String GROUP_ID = "groupId";

    public UpdateUploadFailedWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //first get the device token passed by the caller
        String messageId = getInputData().getString(MESSAGE_ID);
        String groupId = getInputData().getString(GROUP_ID);
        String userId = AppConfigHelper.getUserId();
        String action = "attachmentUploadFailed";

        Call<ResponseBody> call = AppConfigHelper.getBackendApiServiceProvider().updateMessage(userId, groupId,messageId,  action);
        try {
            ResponseBody responseBody = call.execute().body();
            if(responseBody == null){
                throw new Exception("Response is null");
            }

            DataGroupMessagesRepository dataGroupMessagesRepository = DataGroupMessagesRepository.getInstance();
            dataGroupMessagesRepository.updateMessageAttachmentUploadFailed(messageId, false);

            Data outPut = new Data.Builder()
                    .putString(WORK_RESULT,WORK_SUCCESS)
//                    .putString(WORK_RESPONSE, String.valueOf(response))
                    .putString(WORK_RESPONSE, "success")
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
