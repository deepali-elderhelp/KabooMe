package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.GroupDataDomainMapper;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UpdateResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UserGroupDataDomainMapper;
import com.java.kaboome.data.persistence.UserGroupDao;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.GroupResponse;
import com.java.kaboome.data.remote.responses.UserGroupResponse;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.helpers.AppConfigHelper;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class DataUserGroupRepository implements UserGroupRepository {

    private static final String TAG = "KMDataGroupRepository";

    private static DataUserGroupRepository instance;
    private UserGroupDao userGroupDao;

    private DataUserGroupRepository(){
        userGroupDao = AppConfigHelper.getKabooMeDatabaseInstance().getUserGroupDao();
    }

    public static DataUserGroupRepository getInstance(){
        if(instance == null){
            instance = new DataUserGroupRepository();
        }
        return instance;
    }


    @Override
    public LiveData<DomainUpdateResource<String>> addUserToTheGroup(DomainUserGroup userGroup, String action) {
        return Transformations.map(addUserToGroupOnServer(UserGroupDataDomainMapper.transformFromDomain(userGroup), action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }

    @Override
    public Void updateUserGroupCache(DomainUserGroup userGroup, String action) {
        updateUserGroupCachePrivate(UserGroupDataDomainMapper.transformFromDomain(userGroup), action);
        return null;
    }

    /**
     * Make sure this method is called on a background thread
     * Otherwise it throws an error - NetworkOnMainThreadException
     * @param groupId
     * @return
     */
    @Override
    public Long getUserGroupLastAccessed(String groupId) {
        return userGroupDao.getUserGroupLastAccessed(AppConfigHelper.getUserId(), groupId);
    }

    @Override
    public LiveData<DomainUserGroup> getUserGroupFromCache(final String groupId) {
        final String userId = AppConfigHelper.getUserId();
        return Transformations.map(userGroupDao.getUserGroup(userId, groupId), new Function<UserGroup, DomainUserGroup>() {
            @Override
            public DomainUserGroup apply(UserGroup input) {
                return UserGroupDataDomainMapper.transform(input);
            }
        });
    }



    private void updateUserGroupCachePrivate(final UserGroup userGroup, final String action){
        if(action == null || action.isEmpty()){
            return;
        }
        if(action.equals(GroupActionConstants.UPDATE_GROUP_IMAGE.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    //update the local UserGroup cache for User Name and Privacy (either could be changed)
                    try {
                        userGroupDao.updateUserGroupImageTS(userGroup.getImageUpdateTimestamp(), userGroup.getUserId(), userGroup.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateUserGroupCachePrivate "+exception.getMessage());
                    }
                }
            });

        }
        if(action.equals(GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local UserGroup cache for User Name and Privacy (either could be changed)
                        userGroupDao.updateUserGroupName(userGroup.getGroupName(), userGroup.getUserId(), userGroup.getGroupId());
                        userGroupDao.updateUserGroupImageLoadingGoingOn(userGroup.getGroupPicLoadingGoingOn(), userGroup.getUserId(), userGroup.getGroupId());
                        userGroupDao.updateUserGroupImageUploaded(userGroup.getGroupPicUploaded(), userGroup.getUserId(), userGroup.getGroupId());
//                    userGroupDao.updateUserGroupPrivacy(userGroup.isPrivateGroup(), userGroup.getUserId(), userGroup.getGroupId());
//                    userGroupDao.updateUserGroupImageTS(userGroup.getImageUpdateTimestamp(), userGroup.getUserId(), userGroup.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateUserGroupCachePrivate "+exception.getMessage());
                    }
                }
            });

        }
        if(action.equals(GroupActionConstants.UPDATE_GROUP_NAME.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local UserGroup cache for User Name and Privacy (either could be changed)
                        userGroupDao.updateUserGroupName(userGroup.getGroupName(), userGroup.getUserId(), userGroup.getGroupId());
                        userGroupDao.updateUserGroupPrivacy(userGroup.isPrivateGroup(), userGroup.getUserId(), userGroup.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateUserGroupCachePrivate "+exception.getMessage());
                    }
                }
            });

        }
        if(action.equals(GroupActionConstants.UPDATE_GROUP_EXPIRY.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         //update the local UserGroup cache for User Name and Privacy (either could be changed)
                         userGroupDao.updateUserGroupExpiry(userGroup.getExpiry(), userGroup.getUserId(), userGroup.getGroupId());
                     } catch (Exception exception) {
                         exception.printStackTrace();
                         Log.d(TAG, "Exception in updateUserGroupCachePrivate "+exception.getMessage());
                     }
                 }
             });

        }
        if(action.equals(GroupActionConstants.UPDATE_GROUP_IMAGE.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local UserGroup cache for group image
                        userGroupDao.updateUserGroupImageTS(userGroup.getImageUpdateTimestamp(), userGroup.getUserId(), userGroup.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateUserGroupCachePrivate "+exception.getMessage());
                    }
                }
            });

        }
        if(action.equals(GroupActionConstants.UPDATE_GROUP_USER_NOTIFICATION.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local UserGroup cache for notification level
                        userGroupDao.updateUserGroupNotify(userGroup.getNotify(), userGroup.getUserId(), userGroup.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateUserGroupCachePrivate "+exception.getMessage());
                    }
                }
            });

        }
        if(action.equals(GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local UserGroup cache for User Role and Alias and Image timestamp (either could be changed)
                        //here only alias and role need to be updated
                        //the image ts is only for group user, right now, there is no field for it in the UserGroup
                        //also, there does not seem to be any need either
                        //because the image TS it has is only for the group, not the user's group image
                        userGroupDao.updateUserGroupRoleAndAlias(userGroup.getGroupAdminRole(), userGroup.getAlias(), userGroup.getUserId(), userGroup.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateUserGroupCachePrivate "+exception.getMessage());
                    }
                }
            });

        }
//        if(action.equals(GroupActionConstants.UPDATE_GROUP_ADMINS_LAST_ACCESS.getAction())){
//            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//                @Override
//                public void run() {
//                    //update the local UserGroup cache for User AdminsLastAccessed timestamp
//                    userGroupDao.updateUserGroupAdminLastAccessed(userGroup.getAdminsLastAccessed(), userGroup.getUserId(), userGroup.getGroupId());
//                }
//            });
//
//        }
//        if(action.equals(GroupActionConstants.UPDATE_GROUP_ADMINS_CACHE_CLEAR.getAction())){
//            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//                @Override
//                public void run() {
//                    //update the local UserGroup cache for User AdminsLastAccessed timestamp
//                    userGroupDao.update(userGroup.getAdminsLastAccessed(), userGroup.getUserId(), userGroup.getGroupId());
//                }
//            });
//
//        }
        if(action.equals(GroupActionConstants.REMOVE_GROUP_FOR_USER.getAction()) ||
        action.equals(GroupActionConstants.REMOVE_GROUP_FOR_ALL.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local UserGroup cache for Group deleted
                        userGroupDao.updateUserGroupIsDeleted(Boolean.TRUE, userGroup.getUserId(), userGroup.getGroupId());
//                    userGroupDao.deleteUserGroup(userGroup.getUserId(), userGroup.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateUserGroupCachePrivate "+exception.getMessage());
                    }
                }
            });

        }
    }

    private LiveData<UpdateResource<String>> addUserToGroupOnServer(final UserGroup userGroup, final String action) {

        return new NewNetworkBoundUpdateRes<String, UserGroupResponse>(AppExecutors2.getInstance()){

            @Override
            protected String processResult(UserGroupResponse userGroupResponse) {
                return action;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserGroupResponse>> createCall() {
                return AppConfigHelper.getBackendApiServiceProvider().joinGroup(AppConfigHelper.getUserId(), userGroup.getGroupId(), userGroup);
            }

            @Override
            protected void reflectDataInDB(UserGroupResponse userGroupResponse) {
//                userGroupDao.insertUserGroup(userGroup);
                userGroupDao.insertUserGroup(userGroupResponse.getUserGroup());
            }
        }.getAsLiveData();
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
//                return AppConfigHelper.getBackendApiServiceProvider().joinGroup(AppConfigHelper.getUserId(), userGroup.getGroupId(), userGroup);
//            }
//
//            @Override
//            protected void uploadToDB() {
//                //OLD:
//                //not doing anything to local cache
//                //it will get updated when the Groups List is refreshed
//                //may be create a new entry in the UnreadCountDB
//
//                //NEW:
//                //Creating a new group in UserGroup so that
//                //user sees that in the groups list when joined
////                userGroup.setUserId(AppConfigHelper.getUserId());
//                userGroupDao.insertUserGroup(userGroup);
//
//            }
//
//            @Override
//            protected void rollbackDatabase() {
//                //OLD:
//                //no need to rollback since nothing is added to the cache
//
//                //NEW:
//                //TODO: delete group from the cache - set isDeleted true
//
//            }
//
//        }.getAsLiveData();

    }

    @Override
    public void removeUserGroup(final String groupId) {
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //update the local UserGroup cache for Group deleted
                    userGroupDao.deleteUserGroup(AppConfigHelper.getUserId(), groupId);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in removeUserGroup "+exception.getMessage());
                }
            }
        });
    }
}
