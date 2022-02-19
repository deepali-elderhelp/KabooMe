package com.java.kaboome.data.repositories;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UserGroupDataDomainMapper;
import com.java.kaboome.data.persistence.GroupRequestsDao;
import com.java.kaboome.data.persistence.MessageDao;
import com.java.kaboome.data.persistence.UserGroupConversationDao;
import com.java.kaboome.data.persistence.UserGroupDao;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.UserGroupsResponse;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.util.List;

public class DataUserGroupsListRepository implements UserGroupsListRepository {

    private static final String TAG = "KMDataUsrGroupsListRepo";
    private static DataUserGroupsListRepository instance;
    private UserGroupDao userGroupDao;
    private GroupRequestsDao groupRequestsDao;
    private UserGroupConversationDao userGroupConversationDao;


    private DataUserGroupsListRepository() {
        userGroupDao = AppConfigHelper.getKabooMeDatabaseInstance().getUserGroupDao();
        groupRequestsDao = AppConfigHelper.getKabooMeDatabaseInstance().getGroupRequestsDao();
        userGroupConversationDao = AppConfigHelper.getKabooMeDatabaseInstance().getUserGroupConversationsDao();

    }

    public static DataUserGroupsListRepository getInstance(){
        if(instance == null){
            instance = new DataUserGroupsListRepository();
        }
        return instance;
    }

    @Override
    public LiveData<DomainResource<List<DomainUserGroup>>> getGroupsList() {
        Log.d(TAG, "getGroupsList: comes here");
        final String userId = AppConfigHelper.getUserId();

        return Transformations.map(getUserGroups(userId), new Function<Resource<List<UserGroup>>, DomainResource<List<DomainUserGroup>>>() {

            @Override
            public DomainResource<List<DomainUserGroup>> apply(Resource<List<UserGroup>> input) {

//                updateLastMessageTSForUserGroup(input.data);
                //here map all the UserGroups to DomainUserGroups, then wrap it up in DomainResource and return it
                return ResourceDomainResourceMapper.transform(input.status, UserGroupDataDomainMapper.transform(input.data), input.message);
            }
        });

    }

    @Override
    public LiveData<List<DomainUserGroup>> getGroupsListOnlyFromCache() {
        final String userId = AppConfigHelper.getUserId();

        return Transformations.map(userGroupDao.getUserGroups(userId), new Function<List<UserGroup>, List<DomainUserGroup>>() {
            @Override
            public List<DomainUserGroup> apply(List<UserGroup> input) {
                return UserGroupDataDomainMapper.transform(input);
            }
        });


    }

    @Override
    public List<DomainUserGroup> getGroupsListOnlyFromCacheNonLive() {
        return UserGroupDataDomainMapper.transform(userGroupDao.getUserGroupsNonLive(AppConfigHelper.getUserId()));
    }

    @Override
    public void addNewGroupToCache(DomainUserGroup domainUserGroup) {
        final UserGroup userGroup = UserGroupDataDomainMapper.transformFromDomain(domainUserGroup);
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupDao.insertUserGroup(userGroup);
            }
        });
    }

    @Override
    public void updateUserGroupLastAccessed(final String groupId, final Long newLastAccessed) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupDao.updateUserGroupLastAccessed(newLastAccessed, AppConfigHelper.getUserId(), groupId);
            }
        });
    }

    @Override
    public void updateUserGroupCacheClearTS(final String groupId, final Long newCacheClearTS) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
//                userGroupDao.updateUserGroupCacheClearTS(newCacheClearTS, AppConfigHelper.getUserId(), groupId);
                userGroupDao.updateUserGroupCacheClearTSAndLastAccess(newCacheClearTS, AppConfigHelper.getUserId(), groupId);
            }
        });
    }

    @Override
    public void updateUserGroupLastAdminAccessed(final String groupId, final Long newLastAccessed) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupDao.updateUserGroupAdminLastAccessed(newLastAccessed, AppConfigHelper.getUserId(), groupId);
            }
        });
    }

    @Override
    public void updateUserGroupAdminCacheClearTS(final String groupId, final Long newCacheClearTS) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
//                userGroupDao.updateUserGroupAdminCacheClearTS(newCacheClearTS, AppConfigHelper.getUserId(), groupId);
                userGroupDao.updateUserGroupAdminCacheClearTSAndLastAccess(newCacheClearTS, AppConfigHelper.getUserId(), groupId);
            }
        });
    }

    @Override
    public void removeDeletedUserGroupsFromCache(final String userId) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: deleting user groups from cache");
                userGroupDao.deleteUserGroupsWithIsDeletedTrue(userId, Boolean.TRUE);
            }
        });
    }

//    @Override
//    public void updateUserGroupLastMessageTS(final String groupId, final Long newLastMessageTS) {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                userGroupDao.updateUserGroupLastMessageTS(newLastMessageTS, AppConfigHelper.getUserId(), groupId);
//            }
//        });
//    }

//    private void updateLastMessageTSForUserGroup(final List<UserGroup> userGroups){
//     //following code is to insert last cache time in the list
//     //so that when it goes go fragment, sorting can be done
//    //based on the last cache time
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                if (userGroups != null) {
//                    for (UserGroup userGroup : userGroups) {
//                        Message message = messageDao.getLastMessageForGroup(userGroup.getGroupId());
//                        if(message != null){
//                            userGroup.setLastMessageCacheTS(message.getSentAt());
//                        }
//
//                    }
//                }
//            }
//        });
//
//
//
//    }

    private LiveData<Resource<List<UserGroup>>> getUserGroups(final String userId) {
        return new NetworkBoundResource<List<UserGroup>, UserGroupsResponse>(AppExecutors2.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull UserGroupsResponse item) {
                Log.d(TAG, "saveCallResult");
                if (item.getGroups() != null) {


//                    for(UserGroup userGroup : item.getGroups()){
//                        //get the last message TS from cache and update the userGroup with it
//                        UserGroup userGroupInCache = userGroupDao.getUserGroupData(userId, userGroup.getGroupId());
//                        userGroup.setLastMessageCacheTS(userGroupInCache.getLastMessageCacheTS());
//
//                    }

                    UserGroup[] userGroups = new UserGroup[item.getGroups().size()];

                    int index = 0;
                    for (long rowid : userGroupDao.insertUserGroups((UserGroup[]) (item.getGroups().toArray(userGroups)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }


                    for(int i=0; i< userGroups.length; i++) {

                        //get and fill up/refresh conversations for each user group
                        //insert group users in another table
                        List<UserGroupConversation> userGroupConversations = userGroups[i].getConversations();
                        if(userGroupConversations != null && userGroupConversations.size() > 0){
                            UserGroupConversation[] newUserGroupConversations = new UserGroupConversation[userGroupConversations.size()];

                            int index_conv = 0;
                            for(long rowid: userGroupConversationDao.insertUserGroupConversations((UserGroupConversation []) (userGroupConversations.toArray(newUserGroupConversations)))){
                                Log.d(TAG, "rowid - "+rowid);
                                index_conv++;
                            }
                        }

                        //now first delete all the old requests, because it might be that some other admin
                        //has accepted someone's request and you are left with old ones in the cache
                        //and then insert the requests - they re coming with the User Groups List data
                        //hence the transaction has been used for achieving the same.
                        List<GroupRequest> groupRequests = userGroups[i].getRequests();
                        if (groupRequests != null && groupRequests.size() > 0) {
                            GroupRequest[] newGroupRequests = new GroupRequest[groupRequests.size()];

                            int index_requests = 0;
                            for (long rowid : groupRequestsDao.deleteAndInsert(userGroups[i].getGroupId(), (GroupRequest[]) (groupRequests.toArray(newGroupRequests)))) {
                                Log.d(TAG, "rowid - " + rowid);
                                index_requests++;
                            }
                        }
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<UserGroup> data) {

                if (NetworkHelper.isOnline()) {
                    Log.d(TAG, "shouldFetch - true");
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

