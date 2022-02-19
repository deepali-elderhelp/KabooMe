package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.GroupUserDataDomainMapper;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UpdateResourceDomainResourceMapper;
import com.java.kaboome.data.persistence.GroupDao;
import com.java.kaboome.data.persistence.GroupUserDao;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.GroupResponse;
import com.java.kaboome.data.remote.responses.GroupUsersResponse;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupsUsersRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.util.List;

public class DataGroupsUsersRepository implements GroupsUsersRepository {

    private static final String TAG = "KMDataGrpUsersRepo";

    private static DataGroupsUsersRepository instance;
    private GroupUserDao groupUserDao;
    private GroupDao groupDao;

    public DataGroupsUsersRepository() {
        groupUserDao = AppConfigHelper.getKabooMeDatabaseInstance().getGroupUserDao();
        groupDao = AppConfigHelper.getKabooMeDatabaseInstance().getGroupDao();
    }

    public static DataGroupsUsersRepository getInstance(){
        if(instance == null){
            instance = new DataGroupsUsersRepository();
        }
        return instance;
    }


    @Override
    public LiveData<DomainResource<List<DomainGroupUser>>> getGroupsUsers(String groupId, boolean fetchFromServer) {
        if(!fetchFromServer) {
            return Transformations.map(groupUserDao.getGroupUsers(groupId), new Function<List<GroupUser>, DomainResource<List<DomainGroupUser>>>() {
                @Override
                public DomainResource<List<DomainGroupUser>> apply(List<GroupUser> input) {
                    return ResourceDomainResourceMapper.transform(Resource.success(GroupUserDataDomainMapper.transform(input)));
                }
            });
        }
        else {
            return Transformations.map(getGroupUsersFromServer(groupId), new Function<Resource<List<GroupUser>>, DomainResource<List<DomainGroupUser>>>() {
                @Override
                public DomainResource<List<DomainGroupUser>> apply(Resource<List<GroupUser>> input) {
                    return ResourceDomainResourceMapper.transform(input.status, GroupUserDataDomainMapper.transform(input.data), input.message);
                }
            });
        }
    }



    @Override
    public LiveData<DomainUpdateResource<String>> updateGroupUser(DomainGroupUser domainGroupUser, String action) {
        return Transformations.map(updateGroupUserToServerAndLocal(GroupUserDataDomainMapper.transformFromDomain(domainGroupUser, action), action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });

    }

    @Override
    public void updateGroupUserCache(final DomainGroupUser groupUser, String action) {
        if(action == null || action.isEmpty()){
            return;
        }
        //only implementing this one action right now, will add more as needed
        //only image upload finish comes back later from a worker thread
        //hence calling it separate
        if(action.equals(GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local Group cache for Group Name and Loading status (either could be changed)
                        groupUserDao.updateGroupUserAliasRoleAndmageUploadingData(groupUser.getUserId(), groupUser.getGroupId(), groupUser.getUserName(), groupUser.getRole(), groupUser.getGroupUserPicUploaded(), groupUser.getGroupUserPicLoadingGoingOn(), groupUser.getImageUpdateTimestamp());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateGroupUserCache "+exception.getMessage());
                    }
                }
            });

        }
    }

    @Override
    public DomainGroupUser getGroupUserFromCache(String groupId, String userId) {
        return GroupUserDataDomainMapper.transformFromGroup(groupUserDao.getGroupUserFromCache(userId, groupId));
    }

    private LiveData<UpdateResource<String>>  updateGroupUserToServerAndLocal(final GroupUser groupUser, final String action) {

        return new NewNetworkBoundUpdateRes<String, Void>(AppExecutors2.getInstance()) {

            @Override
            protected String processResult(Void aVoid) {
                return action;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return AppConfigHelper.getBackendApiServiceProvider().updateGroupUser(AppConfigHelper.getUserId(), groupUser.getGroupId(), groupUser.getUserId(), groupUser, action);
            }

            @Override
            protected void reflectDataInDB(Void aVoid) {
                if (action != null && "updateGroupUserNotification".equals(action)) {
                    groupUserDao.updateGroupUserNotification(groupUser.getUserId(), groupUser.getGroupId(), groupUser.getNotify());
                }
                if (action != null && "updateGroupUserRoleAndAlias".equals(action)) {
//                    groupUserDao.updateGroupUserAliasAndRole(groupUser.getUserId(), groupUser.getGroupId(), groupUser.getUserName(), groupUser.getRole(), groupUser.getImageUpdateTimestamp());
                    groupUserDao.updateGroupUserAliasRoleAndmageUploadingDataNoTS(groupUser.getUserId(), groupUser.getGroupId(), groupUser.getUserName(), groupUser.getRole(), groupUser.getGroupUserPicUploaded(), groupUser.getGroupUserPicLoadingGoingOn());
                }
                //this action has been moved to the Group level
                //because we do it for all users made admins together
//                if(action != null && "updateGroupUsersToAdmin".equals(action)) {
//                    groupUserDao.updateGroupUserIsAdmin(groupUser.getUserId(), groupUser.getGroupId(), "true"); //for now only can make admin
//                }
            }
        }.getAsLiveData();
    }

    private LiveData<Resource<List<GroupUser>>> getGroupUsersFromServer(final String groupId) {
        return new NetworkBoundResource<List<GroupUser>, GroupUsersResponse>(AppExecutors2.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GroupUsersResponse item) {
                Log.d(TAG, "saveCallResult");
                if (item.getGroupUsers() != null) {

                    GroupUser[] groupUsers = new GroupUser[item.getGroupUsers().size()];

                    int index = 0;
                    for (long rowid : groupUserDao.insertGroupUsers((GroupUser[]) (item.getGroupUsers().toArray(groupUsers)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }
                }

            }
            @Override
            protected boolean shouldFetch(@Nullable List<GroupUser> data) {

                if (NetworkHelper.isOnline()) {
                    Log.d(TAG, "shouldFetch - true");
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<GroupUser>> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                return groupUserDao.getGroupUsers(groupId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupUsersResponse>> createCall() {
                Log.d(TAG, "createCall");
                return AppConfigHelper.getBackendApiServiceProvider().getGroupUsers(
                        AppConfigHelper.getUserId(), groupId);
            }
        }.getAsLiveData();
        }

        }

