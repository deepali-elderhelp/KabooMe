package com.java.kaboome.data.repositories;

import androidx.lifecycle.LiveData;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;


import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.persistence.KabooMeDatabase;
import com.java.kaboome.data.persistence.UserGroupDao;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.UserGroupsResponse;

import java.util.List;

public class UserGroupsRepository {

    private static final String TAG = "KMUserGroupsRepository";
    private static UserGroupsRepository instance;
    private UserGroupDao userGroupDao;

    public static UserGroupsRepository getInstance(Context context){
        if(instance == null){
            instance = new UserGroupsRepository(context);
        }
        return instance;
    }


    private UserGroupsRepository(Context context) {
        userGroupDao = KabooMeDatabase.getInstance(context).getUserGroupDao();
    }


    public LiveData<Resource<List<UserGroup>>> getUserGroups(final String userId){
        return new NetworkBoundResource<List<UserGroup>, UserGroupsResponse>(AppExecutors2.getInstance()){

            @Override
            protected void saveCallResult(@NonNull UserGroupsResponse item) {
                Log.d(TAG, "saveCallResult");
                if(item.getGroups() != null){

                    UserGroup[] userGroups = new UserGroup[item.getGroups().size()];


                    int index = 0;
                    for(long rowid: userGroupDao.insertUserGroups((UserGroup[]) (item.getGroups().toArray(userGroups)))){
                        Log.d(TAG, "saveCallResult: row id is - "+rowid);
                        index++;
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<UserGroup> data) {
                Log.d(TAG, "shouldFetch");
                if(NetworkHelper.isOnline()){
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<UserGroup>> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                return userGroupDao.getUserGroups(userId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserGroupsResponse>> createCall() {
                Log.d(TAG, "createCall");
                return AppConfigHelper.getBackendApiServiceProvider().getUserGroups(
                        userId);
            }
        }.getAsLiveData();
    }




}
