package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.GroupDataDomainMapper;
import com.java.kaboome.data.mappers.GroupUserDataDomainMapper;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UpdateResourceDomainResourceMapper;
import com.java.kaboome.data.persistence.GroupDao;
import com.java.kaboome.data.persistence.GroupUserDao;
import com.java.kaboome.data.remote.requests.GroupCreateRequest;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.CreateGroupResponse;
import com.java.kaboome.data.remote.responses.GroupResponse;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.mappers.GroupActionContantsMapper;

import java.util.List;

public class DataGroupRepository implements GroupRepository {

    private static final String TAG = "KMGroupRepository";

    private static DataGroupRepository instance;
    private GroupDao groupDao;
    private GroupUserDao groupUserDao;

    private DataGroupRepository(){
        groupDao = AppConfigHelper.getKabooMeDatabaseInstance().getGroupDao();
        groupUserDao = AppConfigHelper.getKabooMeDatabaseInstance().getGroupUserDao();
    }

    public static DataGroupRepository getInstance(){
        if(instance == null){
            instance = new DataGroupRepository();
        }
        return instance;
    }

    private LiveData<Resource<Group>> getGroupFromRemote(final String userId, final String groupId){
        return new NetworkBoundResource<Group, GroupResponse>(AppExecutors2.getInstance()){

            @Override
            protected void saveCallResult(@NonNull GroupResponse item) {
                Log.d(TAG, "saveCallResult");
                if(item.getGroup() != null){
                    Group group = item.getGroup();
                    groupDao.insertGroup(item.getGroup());

                    //insert group users in another table
                    List<GroupUser> groupUsers = group.getUsersJoined();
                    if(groupUsers != null && groupUsers.size() > 0){
                        GroupUser[] newGroupUsers = new GroupUser[groupUsers.size()];

                        int index = 0;
                        for(long rowid: groupUserDao.insertGroupUsers((GroupUser []) (groupUsers.toArray(newGroupUsers)))){
                            Log.d(TAG, "rowid - "+rowid);
                            index++;
                        }
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable Group data) {
                Log.d(TAG, "shouldFetch");

                if(NetworkHelper.isOnline())
                    return true;
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Group> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                return groupDao.getGroup(groupId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupResponse>> createCall() {
                Log.d(TAG, "createCall");
                return AppConfigHelper.getBackendApiServiceProvider().getGroup(
                        userId, groupId
                );
            }
        }.getAsLiveData();
    }

    @Override
    public LiveData<DomainResource<DomainGroup>> getGroup(final String groupId) {
        final String userId = AppConfigHelper.getUserId();
        return Transformations.map(getGroupFromRemote(userId, groupId), new Function<Resource<Group>, DomainResource<DomainGroup>>() {
            @Override
            public DomainResource<DomainGroup> apply(Resource<Group> input) {
//                if(input.data == null && input.status == Resource.Status.LOADING){
                if(input.data == null){
                    //this could happen when the group is being loaded from server, but there is nothing in the cache yet
                    return ResourceDomainResourceMapper.transform(input.status, new DomainGroup(groupId), input.message);
                }

                //here map all the UserGroups to DomainUserGroups, then wrap it up in DomainResource and return it
                return ResourceDomainResourceMapper.transform(input.status, GroupDataDomainMapper.transformFromGroup(input.data), input.message);
            }
        });
    }

    @Override
    public LiveData<DomainResource<DomainGroupUser>> createNewGroup(DomainGroup domainGroup) {

        return Transformations.map(createNewGroupPrivate(GroupDataDomainMapper.transformFromDomain(domainGroup)), new Function<Resource<GroupUser>, DomainResource<DomainGroupUser>>() {
            @Override
            public DomainResource<DomainGroupUser> apply(Resource<GroupUser> input) {
                if(input.data == null){
                    //this could happen when the group creation is in the loading state
                    return ResourceDomainResourceMapper.transform(input.status, null, input.message);
                }
                return ResourceDomainResourceMapper.transform(input.status, GroupUserDataDomainMapper.transformFromGroup(input.data), input.message);
            }
        });
    }

    private LiveData<Resource<GroupUser>> createNewGroupPrivate(final Group group){
        return new NewNetworkBoundCreateRes<GroupUser, GroupUser>(AppExecutors2.getInstance()){

            @Override
            protected GroupUser processResult(GroupUser groupUser) {
                return groupUser;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupUser>> createCall() {
                Log.d(TAG, "createCall: unicast setting - "+group.getUnicastGroup());
                return AppConfigHelper.getBackendApiServiceProvider().createGroup(AppConfigHelper.getUserId(), new GroupCreateRequest(group));
            }

            @Override
            protected void reflectDataInDB(GroupUser groupUser) {
                //add new group in cache
                String groupId = groupUser.getGroupId();
                group.setGroupId(groupId);
                group.setCurrentUserStatusForGroup(UserGroupStatusConstants.ADMIN_MEMBER);
                group.setImageUpdateTimestamp(groupUser.getImageUpdateTimestamp()); //group and group creator user image created same time
                groupDao.insertGroup(group);
            }
        }.getAsLiveData();
    }

    private LiveData<UpdateResource<String>> updateGroupEverywhere(final Group group, final String action){

        return new NewNetworkBoundUpdateRes<String,Void>(AppExecutors2.getInstance()){


            @Override
            protected String processResult(Void aVoid) {
                return action;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return AppConfigHelper.getBackendApiServiceProvider().updateGroup(AppConfigHelper.getUserId(), group.getGroupId(), group, action);
            }

            @Override
            protected void reflectDataInDB(Void aVoid) {
                //remote call was successful, now update DB
                if(action != null && "updateGroupName".equals(action)){
                    groupDao.updateGroupName(group.getGroupName(), group.getGroupId());
                    groupDao.updateGroupPrivacy(group.isPrivateGroup(), group.getGroupId());
                }
                else if(action != null && "updateGroupDesc".equals(action)){
                    groupDao.updateGroupDescription(group.getGroupDescription(), group.getGroupId());
                }
                else if(action != null && "updateGroupExpiry".equals(action)){
                    groupDao.updateGroupExpiry(group.getExpiry(), group.getGroupId());
                }
                else if(action != null && "updateGroupImage".equals(action)){
                    groupDao.updateGroupImageTS(group.getImageUpdateTimestamp(), group.getGroupId());
                }
                else if(action != null && "updateGroupNamePrivacyImage".equals(action)){
//                    groupDao.updateGroupName(group.getGroupName(), group.getGroupId());
//                    groupDao.updateGroupImageLoadingGoingOn(group.getGroupPicLoadingGoingOn(), group.getGroupId());
//                    groupDao.updateGroupImageUploaded(group.getGroupPicUploaded(), group.getGroupId());
                    groupDao.updateGroupINameAndmageUploadingData(group.getGroupName(), group.getGroupPicUploaded(), group.getGroupPicLoadingGoingOn(), group.getGroupId());
////                    groupDao.updateGroupPrivacy(group.isPrivateGroup(), group.getGroupId());
//                    groupDao.updateGroupImageTS(group.getImageUpdateTimestamp(), group.getGroupId());
                }
                else if(action != null && "updateGroupRequestsSetting".equals(action)){
                    groupDao.updateGroupOpenToRequests(group.getOpenToRequests(), group.getGroupId());
                }
                else if(action != null && "updateGroupUsersToAdmin".equals(action)){
                    List<GroupUser> usersToMakeAdmin = group.getUsersJoined();
                    if(usersToMakeAdmin != null && usersToMakeAdmin.size() > 0){
                        for(GroupUser groupUser: usersToMakeAdmin){
                            groupUserDao.updateGroupUserIsAdmin(groupUser.getUserId(), groupUser.getGroupId(), "true");
                        }
                    }
                }

            }
        }.getAsLiveData();
    }


//    private LiveData<UpdateResource<String>>  updateGroupToServerAndLocal(final Group group, final String action){
//
//        //TODO: get and save the old group data to be used in rollback later
//        Log.d(TAG, "updateGroupToServerAndLocal: Group Id here is "+group.getGroupId());
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
//                return AppConfigHelper.getBackendApiServiceProvider().updateGroup(AppConfigHelper.getUserId(), group.getGroupId(), group, action);
//            }
//
//            @Override
//            protected void uploadToDB() {
//                if(action != null && "updateGroupName".equals(action)){
//                    groupDao.updateGroupName(group.getGroupName(), group.getGroupId());
//                    groupDao.updateGroupPrivacy(group.isPrivateGroup(), group.getGroupId());
//                }
//                else if(action != null && "updateGroupDesc".equals(action)){
//                    groupDao.updateGroupDescription(group.getGroupDescription(), group.getGroupId());
//                }
//                else if(action != null && "updateGroupExpiry".equals(action)){
//                    groupDao.updateGroupExpiry(group.getExpiry(), group.getGroupId());
//                }
//                else if(action != null && "updateGroupImage".equals(action)){
//                    groupDao.updateGroupImageTS(group.getImageUpdateTimestamp(), group.getGroupId());
//                }
//                else if(action != null && "updateGroupNamePrivacyImage".equals(action)){
//                    groupDao.updateGroupName(group.getGroupName(), group.getGroupId());
//                    groupDao.updateGroupPrivacy(group.isPrivateGroup(), group.getGroupId());
//                    groupDao.updateGroupImageTS(group.getImageUpdateTimestamp(), group.getGroupId());
//                }
////                else if(action != null && "groupRemoveForAll".equals(action)){
////                    groupDao.deleteGroup(group.getGroupId());
////                }
//
//            }
//
//            @Override
//            protected void rollbackDatabase() {
//                //TODO: rollback
//            }
//
//        }.getAsLiveData();
//
//
//    };




//    @Override
//    public LiveData<DomainUpdateResource<String>> updateGroup(DomainGroup group, String action) {
//
//        return Transformations.map(updateGroupToServerAndLocal(GroupDataDomainMapper.transformFromDomain(group, action), action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
//            @Override
//            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
//                Log.d(TAG, "apply: response is here");
//                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
//            }
//        });
//    }

    @Override
    public LiveData<DomainUpdateResource<String>> updateGroup(DomainGroup group, String action) {

        return Transformations.map(updateGroupEverywhere(GroupDataDomainMapper.transformFromDomain(group, action), action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }



    @Override
    public LiveData<DomainUpdateResource<String>> deleteGroup(String groupId, String action) {
        return Transformations.map(deleteGroupOrUserToServerAndLocal(groupId, AppConfigHelper.getUserId(), null, action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }

    @Override
    public LiveData<DomainUpdateResource<String>> deleteGroupUser(final String groupId, final String userId, final String groupUserId, String action) {
        return Transformations.map(deleteGroupOrUserToServerAndLocal(groupId, userId, groupUserId, action), new Function<UpdateResource<String>, DomainUpdateResource<String>>() {
            @Override
            public DomainUpdateResource<String> apply(UpdateResource<String> input) {
                Log.d(TAG, "apply: response is here");
                return UpdateResourceDomainResourceMapper.transform(input.status, input.data, input.message);
            }
        });
    }

    @Override
    public void deleteGroupFromCache(final String groupId) {

        if(groupId == null || groupId.isEmpty()){
            return;
        }
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    groupDao.setGroupToDeleted(true, groupId);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in deleteGroupFromCache "+exception.getMessage());
                }
            }
        });
    }

    @Override
    public void removeGroupFromCache(final String groupId) {
        if(groupId == null || groupId.isEmpty()){
            return;
        }
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    groupDao.deleteGroup(groupId);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in removeGroupFromCache "+exception.getMessage());
                }
            }
        });
    }

    //gets called from notification, can't delete it, what if the user is on that page
    @Override
    public void deleteGroupUserFromCache(final String groupId, final String groupUserId) {
        if(groupId == null || groupId.isEmpty()){
            return;
        }
        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    groupUserDao.updateGroupUserIsDeleted(true, groupUserId, groupId);
//                groupDao.deleteGroup(groupId);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "Exception in deleteGroupUserFromCache "+exception.getMessage());
                }
            }
        });
    }


//    private LiveData<DeleteResource<String>> deleteGroupOrUserToServerAndLocal(final String groupId, final String userId, final String groupUserId, final String action) {
//
//        return new NetworkBoundDeleteResource<String>(AppExecutors2.getInstance(),action){
//
//            @Override
//            protected boolean shouldDelete() {
//                if(NetworkHelper.isOnline()){
//                    return true;
//                }
//                return false;
//            }
//
//            @NonNull
//            @Override
//            protected Call<ResponseBody> createCall() {
//                if(action.equals("groupRemoveForAll")){
//                    return AppConfigHelper.getBackendApiServiceProvider().removeGroup(userId, groupId);
//                }
//                if(action.equals("groupRemoveForCurrentUser")){
//                    return AppConfigHelper.getBackendApiServiceProvider().removeGroupUser(userId, groupId, groupUserId);
//                }
//                if(action.equals("groupRemoveForSelectedUser")){
//                    return AppConfigHelper.getBackendApiServiceProvider().removeGroupUser(userId, groupId, groupUserId);
//                }
//                return null;
//
//            }
//
//            @Override
//            protected void deleteInDB() {
//                //forgot why it does nothing - I guess isDeleted is set to true
//                //I should write better comments - can't recollect now
////                if(action.equals("groupRemoveForAll")){
////                    groupDao.setGroupToDeleted(true, groupId);
////                }
////                if(action.equals("groupRemoveForCurrentUser")){
////
////                }
//
//            }
//        }.getAsLiveData();
//    }

    private LiveData<UpdateResource<String>> deleteGroupOrUserToServerAndLocal(final String groupId, final String userId, final String groupUserId, final String action) {
        return new NewNetworkBoundUpdateRes<String, Void>(AppExecutors2.getInstance()){

            @Override
            protected String processResult(Void aVoid) {
                return action;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                if(GroupActionContantsMapper.getConstant(action) == GroupActionConstants.REMOVE_GROUP_FOR_ALL){
                    return AppConfigHelper.getBackendApiServiceProvider().removeGroup(userId, groupId);
                }
                if(GroupActionContantsMapper.getConstant(action) == GroupActionConstants.REMOVE_GROUP_FOR_USER){
                    return AppConfigHelper.getBackendApiServiceProvider().removeGroupUser(userId, groupId, groupUserId);
                }
                if(GroupActionContantsMapper.getConstant(action) == GroupActionConstants.REMOVE_GROUP_FOR_OTHER_USER){
                    return AppConfigHelper.getBackendApiServiceProvider().removeGroupUser(userId, groupId, groupUserId);
                }
                return null;
            }

            @Override
            protected void reflectDataInDB(Void aVoid) {
                if(GroupActionContantsMapper.getConstant(action) == GroupActionConstants.REMOVE_GROUP_FOR_ALL){
                    //the group should be set to isDeleted
                    groupDao.setGroupToDeleted(true, groupId);
//                    groupDao.deleteGroup(groupId);
                    //TODO: right here also delete the group users since the cascade delete relationship has been removed
                }
                if(GroupActionContantsMapper.getConstant(action) == GroupActionConstants.REMOVE_GROUP_FOR_USER){
                    //the group should be set to isDeleted
                    groupDao.setGroupToDeleted(true, groupId);
//                    groupDao.deleteGroup(groupId);
                }
                if(GroupActionContantsMapper.getConstant(action) == GroupActionConstants.REMOVE_GROUP_FOR_OTHER_USER){
                    //the groupUser should be set to isDeleted
                    groupUserDao.updateGroupUserIsDeleted(true, groupUserId, groupId);
//                    groupUserDao.removeGroupUser(groupUserId, groupId);
                }

            }
        }.getAsLiveData();

    }

    @Override
    public void updateGroupCache(final DomainGroup group, final String action){
        if(action == null || action.isEmpty()){
            return;
        }
        //only implementing this one action right now, will add more as needed
        //only image upload finish comes back later from a worker thread
        //hence calling it separate
        if(action.equals(GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local Group cache for Group Name and Loading status (either could be changed)
//                    groupDao.updateGroupName(group.getGroupName(), group.getGroupId());
//                    groupDao.updateGroupImageLoadingGoingOn(group.getGroupPicLoadingGoingOn(), group.getGroupId());
//                    groupDao.updateGroupImageUploaded(group.getGroupPicUploaded(), group.getGroupId());
                        groupDao.updateGroupINameAndmageUploadingData(group.getGroupName(),group.getGroupPicUploaded(), group.getGroupPicLoadingGoingOn(), group.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateGroupCache "+exception.getMessage());
                    }
                }
            });

        }
        else if(action.equals(GroupActionConstants.UPDATE_GROUP_CURRENT_STATUS.getAction())){
            AppExecutors2.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //update the local Group cache for Group Name and Loading status (either could be changed)
//                    groupDao.updateGroupName(group.getGroupName(), group.getGroupId());
//                    groupDao.updateGroupImageLoadingGoingOn(group.getGroupPicLoadingGoingOn(), group.getGroupId());
//                    groupDao.updateGroupImageUploaded(group.getGroupPicUploaded(), group.getGroupId());
                        groupDao.updateCurrentUserStatusForGroup(group.getCurrentUserStatusForGroup(), group.getGroupId());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        Log.d(TAG, "Exception in updateGroupCache "+exception.getMessage());
                    }
                }
            });

        }
    }

//    @Override
//    public void updateGroupPicUploadingFields(final Boolean picUploaded, final Boolean picUploadingGoingOn, final String groupId) {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                //update the local Group cache for Group Name and Loading status (either could be changed)
//                groupDao.updateGroupImageUploadingData(picUploaded, picUploadingGoingOn, groupId);
//            }
//        });
//    }
}
