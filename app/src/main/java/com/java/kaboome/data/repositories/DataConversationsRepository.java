package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.entities.UserGroupConversation;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UserGroupConversationDataDomainMapper;
import com.java.kaboome.data.mappers.UserGroupDataDomainMapper;
import com.java.kaboome.data.persistence.GroupRequestsDao;
import com.java.kaboome.data.persistence.UserGroupConversationDao;
import com.java.kaboome.data.persistence.UserGroupDao;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.UserGroupConversationsResponse;
import com.java.kaboome.data.remote.responses.UserGroupsResponse;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.util.List;
import java.util.prefs.AbstractPreferences;

public class DataConversationsRepository implements ConversationsRepository {

    private static final String TAG = "KMDataConvsListRepo";
    private static DataConversationsRepository instance;

    private UserGroupConversationDao userGroupConversationDao;


    private DataConversationsRepository() {
        userGroupConversationDao = AppConfigHelper.getKabooMeDatabaseInstance().getUserGroupConversationsDao();

    }

    public static DataConversationsRepository getInstance(){
        if(instance == null){
            instance = new DataConversationsRepository();
        }
        return instance;
    }

    @Override
    public LiveData<DomainResource<List<DomainUserGroupConversation>>> getConversationsForUserGroups(String groupId) {
        Log.d(TAG, "getConversationsForUserGroups: comes here");
        final String userId = AppConfigHelper.getUserId();
        return Transformations.map(getUserGroupConversations(groupId), new Function<Resource<List<UserGroupConversation>>, DomainResource<List<DomainUserGroupConversation>>>() {

            @Override
            public DomainResource<List<DomainUserGroupConversation>> apply(Resource<List<UserGroupConversation>> input) {

//                updateLastMessageTSForUserGroup(input.data);
                //here map all the UserGroups to DomainUserGroups, then wrap it up in DomainResource and return it
                return ResourceDomainResourceMapper.transform(input.status, UserGroupConversationDataDomainMapper.transform(input.data), input.message);
            }
        });

    }

    @Override
    public void updateUserGroupConversationLastAccessed(final String groupId, final String conversationId, final Long newLastAccessed) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupConversationDao.updateUserGroupConversationLastAccessed(newLastAccessed, AppConfigHelper.getUserId(), groupId, conversationId);
            }
        });
    }

    @Override
    public void updateUserGroupConversationCacheClearTS(final String groupId, final String conversationId, final Long newCacheClearTS) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupConversationDao.updateUserGroupConversationCacheClearTS(newCacheClearTS, AppConfigHelper.getUserId(), conversationId, groupId);
            }
        });
    }

    @Override
    public void deleteUserGroupConversation(final String groupId, final String conversationId) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupConversationDao.deleteUserGroupConversation(Boolean.TRUE, AppConfigHelper.getUserId(), conversationId, groupId);
            }
        });
    }

    @Override
    public void addNewConversation(final DomainUserGroupConversation domainUserGroupConversation) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupConversationDao.insertUserGroupConversation(UserGroupConversationDataDomainMapper.transformFromDomain(domainUserGroupConversation));
            }
        });
    }

    @Override
    public void removeUserGroupConversation(final String conversationId, final String groupId) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupConversationDao.removeUserGroupConversation(AppConfigHelper.getUserId(), conversationId, groupId);
            }
        });
    }

    /**
     * This method needs to be called from a background thread otherwise an error is thrown
     * @param groupId
     * @return
     */
    @Override
    public List<DomainUserGroupConversation> getConversationsForUserGroupsFromCache(final String groupId) {
        return UserGroupConversationDataDomainMapper.transform(userGroupConversationDao.getNonLiveUserGroupConversations(groupId, AppConfigHelper.getUserId()));
    }

    @Override
    public void updateUserGroupConversationDetails(final String groupId, final String conversationId, final String otherUserName, final String otherUserRole, final Long userImageTS) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userGroupConversationDao.updateUserGroupConversationDetails(otherUserName, otherUserRole, userImageTS, AppConfigHelper.getUserId(), conversationId, groupId);
            }
        });
    }

    //    @Override
//    public LiveData<List<DomainUserGroup>> getGroupsListOnlyFromCache() {
//        final String userId = AppConfigHelper.getUserId();
//
//        return Transformations.map(userGroupDao.getUserGroups(userId), new Function<List<UserGroup>, List<DomainUserGroup>>() {
//            @Override
//            public List<DomainUserGroup> apply(List<UserGroup> input) {
//                return UserGroupDataDomainMapper.transform(input);
//            }
//        });
//
//
//    }

//    @Override
//    public void addNewGroupToCache(DomainUserGroup domainUserGroup) {
//        final UserGroup userGroup = UserGroupDataDomainMapper.transformFromDomain(domainUserGroup);
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                userGroupDao.insertUserGroup(userGroup);
//            }
//        });
//    }

//    @Override
//    public void updateUserGroupLastAccessed(final String groupId, final Long newLastAccessed) {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                userGroupDao.updateUserGroupLastAccessed(newLastAccessed, AppConfigHelper.getUserId(), groupId);
//            }
//        });
//    }
//
//    @Override
//    public void updateUserGroupCacheClearTS(final String groupId, final Long newCacheClearTS) {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                userGroupDao.updateUserGroupCacheClearTS(newCacheClearTS, AppConfigHelper.getUserId(), groupId);
//            }
//        });
//    }

//    @Override
//    public void updateUserGroupLastAdminAccessed(final String groupId, final Long newLastAccessed) {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                userGroupDao.updateUserGroupAdminLastAccessed(newLastAccessed, AppConfigHelper.getUserId(), groupId);
//            }
//        });
//    }
//
//    @Override
//    public void updateUserGroupAdminCacheClearTS(final String groupId, final Long newCacheClearTS) {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                userGroupDao.updateUserGroupAdminCacheClearTS(newCacheClearTS, AppConfigHelper.getUserId(), groupId);
//            }
//        });
//    }

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

    private LiveData<Resource<List<UserGroupConversation>>> getUserGroupConversations(final String groupId) {
        return new NetworkBoundResource<List<UserGroupConversation>, UserGroupConversationsResponse>(AppExecutors2.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull UserGroupConversationsResponse item) {
                Log.d(TAG, "saveCallResult");
                if (item.getUserGroupConversations() != null) {

                    UserGroupConversation[] userGroupConversations = new UserGroupConversation[item.getUserGroupConversations().size()];

                    int index = 0;
                    for (long rowid : userGroupConversationDao.insertUserGroupConversations((UserGroupConversation []) (item.getUserGroupConversations().toArray(userGroupConversations)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<UserGroupConversation> data) {

                if (NetworkHelper.isOnline()) {
                    Log.d(TAG, "shouldFetch - true");
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<UserGroupConversation>> loadFromDb() {
                Log.d(TAG, "loadFromDB");
//                AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<UserGroupConversation> list = userGroupConversationDao.getNonLiveUserGroupConversations(groupId, AppConfigHelper.getUserId());
//                        Log.d(TAG, "Number of conversations - "+list.size());
//                    }
//                });

                return userGroupConversationDao.getUserGroupConversations(groupId, AppConfigHelper.getUserId());
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserGroupConversationsResponse>> createCall() {
                Log.d(TAG, "createCall");
                return AppConfigHelper.getBackendApiServiceProvider().getConversationsForUserGroup(AppConfigHelper.getUserId(), groupId);
            }
        }.getAsLiveData();
    }

}

