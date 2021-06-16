package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.java.kaboome.constants.UserActionConstants;
import com.java.kaboome.data.entities.User;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UpdateResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UserDataDomainMapper;
import com.java.kaboome.data.persistence.UserDao;
import com.java.kaboome.data.remote.requests.UserUpdateRequest;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.UserResponse;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.repositories.UserRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataUserRepository implements UserRepository {

    private static final String TAG = "KMDataUserRepository";

    private static DataUserRepository instance;
    private UserDao userDao;

    private DataUserRepository(){
        userDao = AppConfigHelper.getKabooMeDatabaseInstance().getUserDao();
    }

    public static DataUserRepository getInstance(){
        if(instance == null){
            instance = new DataUserRepository();
        }
        return instance;
    }



    private LiveData<Resource<User>> getUserFromRemote(final String userId){
        return new NetworkBoundResource<User, UserResponse>(AppExecutors2.getInstance()){

            @Override
            protected void saveCallResult(@NonNull UserResponse item) {
                Log.d(TAG, "saveCallResult");
                if(item.getUser() != null){
                    User user = item.getUser();
                    userDao.insertUser(user);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable User user) {
                Log.d(TAG, "shouldFetch");

                if(NetworkHelper.isOnline())
                    return true;
                return false;
            }

            @Override
            protected LiveData<User> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                return userDao.getUser(userId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserResponse>> createCall() {
                Log.d(TAG, "createCall");
                return AppConfigHelper.getBackendApiServiceProvider().getUser(
                        userId
                );
            }
        }.getAsLiveData();
    }


    @Override
    public LiveData<DomainResource<DomainUser>> getUser(final String userId) {

        return Transformations.map(getUserFromRemote(userId), new Function<Resource<User>, DomainResource<DomainUser>>() {
            @Override
            public DomainResource<DomainUser> apply(Resource<User> input) {
                if(input.data == null && input.status == Resource.Status.LOADING){
                    //this could happen when the user is being loaded from server, but there is nothing in the cache yet
                    return ResourceDomainResourceMapper.transform(input.status, new DomainUser(userId), input.message);
                }

                //here map all the UserGroups to DomainUserGroups, then wrap it up in DomainResource and return it
                return ResourceDomainResourceMapper.transform(input.status, UserDataDomainMapper.transformFromUser(input.data), input.message);
            }
        });
    }


    private LiveData<UpdateResource<String>>  updateUserToServerAndLocal(final User user, final String action){
        return new NewNetworkBoundUpdateRes<String, Void>(AppExecutors2.getInstance()){

            @Override
            protected String processResult(Void aVoid) {
                return action;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return AppConfigHelper.getBackendApiServiceProvider().updateUser(AppConfigHelper.getUserId(), user, action);
            }

            @Override
            protected void reflectDataInDB(Void aVoid) {
                if(action != null && UserActionConstants.UPDATE_USER_NAME.getAction().equals(action)){
                    userDao.updateUserName(user.getUserName(), user.getUserId());
                }
                else if(action != null && UserActionConstants.UPDATE_USER_EMAIL.getAction().equals(action)){
                    userDao.updateUserEmail(user.getEmail(), user.getUserId());
                }
                else if(action != null && UserActionConstants.UPDATE_USER_PROFILE_IMAGE_TS.getAction().equals(action)){
                    userDao.updateUserImageTimeStamp(user.getImageUpdateTimestamp(), user.getUserId());
                }
            }
        }.getAsLiveData();

//        //TODO: get and save the old group data to be used in rollback later
//        Log.d(TAG, "updateUserToServerAndLocal: User Id here is "+user.getUserId());
//
//        return new NetworkBoundUpdateResource(AppExecutors2.getInstance(), action){
//
//            @Override
//            protected boolean shouldUpdate() {
//                return true;
//            }
//
//            @NonNull
//            @Override
//            protected Call<ResponseBody> createCall() {
////                if(action != null && "groupRemoveForAll".equals(action)){
////                   return AppConfigHelper.getBackendApiServiceProvider().removeGroup(AppConfigHelper.getRequestUserId(), group.getGroupId());
////                }
////                UserUpdateRequest userUpdateRequest = new UserUpdateRequest(user);
//                return AppConfigHelper.getBackendApiServiceProvider().updateUser(AppConfigHelper.getUserId(), user, action);
//            }
//
//            @Override
//            protected void uploadToDB() {
//                if(action != null && UserActionConstants.UPDATE_USER_NAME.getAction().equals(action)){
//                    userDao.updateUserName(user.getUserName(), user.getUserId());
//                }
//                else if(action != null && UserActionConstants.UPDATE_USER_EMAIL.getAction().equals(action)){
//                    userDao.updateUserEmail(user.getEmail(), user.getUserId());
//                }
//                else if(action != null && UserActionConstants.UPDATE_USER_PROFILE_IMAGE_TS.getAction().equals(action)){
//                    userDao.updateUserImageTimeStamp(user.getImageUpdateTimestamp(), user.getUserId());
//                }
//
//            }
//
//            @Override
//            protected void rollbackDatabase() {
//                //TODO: rollback
//            }
//
//        }.getAsLiveData();


    };


    @Override
    public LiveData<DomainUpdateResource<String>> updateUser(DomainUser user, String action) {

        return Transformations.map(updateUserToServerAndLocal(UserDataDomainMapper.transformFromDomain(user, action), action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }

    @Override
    public void updateUserInCache(final DomainUser user) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userDao.insertUser(UserDataDomainMapper.transformFromDomain(user));
                Log.d(TAG, "User "+user.getUserId()+" updated");
            }
        });
    }


}
