package com.java.kaboome.data.workers;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.User;
import com.java.kaboome.data.mappers.UserDataDomainMapper;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.repositories.DataUserRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

/**
 * This updates a new device token if there is to the server
 * Network is needed for this, also there should be a check
 * to see if valid userId is there
 * This method does not check if existing token is same as new token
 * It just updates. Caller needs to verify that on their own and based upon the
 * need, call this worker thread.
 */
public class UpdateDeviceTokenWorker extends Worker {


    public static final String DEVICE_TOKEN = "token";

    public UpdateDeviceTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        //commenting this because now BackendAPIProvider should take care of getting the new session if expired

        //if not valid session, don't do it
//        if(CognitoHelper.getCurrSession() == null || !CognitoHelper.getCurrSession().isValid()){
//            Data outPut = new Data.Builder()
//                    .putString(WORK_RESULT,WORK_FAILURE)
//                    .putString(WORK_RESPONSE, "User session not valid")
//                    .build();
//            return Result.failure(outPut);
//        }
        //first get the device token passed by the caller
        String newDeviceToken = getInputData().getString(DEVICE_TOKEN);
        String userId = AppConfigHelper.getUserId();

        User user = new User();
        user.setUserId(userId);
        user.setDeviceId(newDeviceToken);

        AppConfigHelper.getBackendApiServiceProvider().updateUser(userId, user, "updateUserToken");

        Call<User> call = AppConfigHelper.getBackendApiServiceProvider().updateUserForToken(userId, user, "updateUserToken");
        try {
            User userReturned = call.execute().body();
            if(userReturned == null){
                throw new Exception("Response is null");
            }

            DataUserRepository dataUserRepository = DataUserRepository.getInstance();
            dataUserRepository.updateUserInCache(UserDataDomainMapper.transformFromUser(userReturned));

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
