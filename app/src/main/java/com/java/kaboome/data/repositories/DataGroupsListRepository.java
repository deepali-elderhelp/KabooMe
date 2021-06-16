package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.mappers.GroupDataDomainMapper;
import com.java.kaboome.data.mappers.ResourceDomainResourceMapper;
import com.java.kaboome.data.mappers.UserGroupDataDomainMapper;
import com.java.kaboome.data.persistence.GroupDao;
import com.java.kaboome.data.persistence.UserGroupDao;
import com.java.kaboome.data.remote.responses.ApiResponse;
import com.java.kaboome.data.remote.responses.GroupsResponse;
import com.java.kaboome.data.remote.responses.UserGroupsResponse;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupsListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;

import java.util.List;

public class DataGroupsListRepository implements GroupsListRepository {

    private static final String TAG = "KMDataGroupsListRepo";
    private static DataGroupsListRepository instance;
    private GroupDao groupDao;
//    private List<UserGroup> userGroups; //needed to check current user status

    private DataGroupsListRepository() {
        groupDao = AppConfigHelper.getKabooMeDatabaseInstance().getGroupDao();
//        loadUserGroupsForLater();

    }

//    private void loadUserGroupsForLater() {
//        AppExecutors2.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                userGroups = AppConfigHelper.getKabooMeDatabaseInstance().getUserGroupDao().getUserGroupsOnlyCache(AppConfigHelper.getRequestUserId());
//            }
//        });
//    }

    public static DataGroupsListRepository getInstance(){
        if(instance == null){
            instance = new DataGroupsListRepository();
        }
        return instance;
    }

    @Override
    public LiveData<DomainResource<List<DomainGroup>>> getGroupsList(String groupNameOrId, String searchText, boolean goToServer) {
        final String userId = AppConfigHelper.getUserId();
        if(goToServer) {
            return Transformations.map(getGroupsListFromServer(groupNameOrId, searchText), new Function<Resource<List<Group>>, DomainResource<List<DomainGroup>>>() {

                @Override
                public DomainResource<List<DomainGroup>> apply(Resource<List<Group>> input) {

                    //here map all the UserGroups to DomainUserGroups, then wrap it up in DomainResource and return it
                    return ResourceDomainResourceMapper.transform(input.status, GroupDataDomainMapper.transformAllFromGroup(input.data), input.message);
                }
            });
        }
        else{
            return Transformations.map(groupDao.getGroupsBySearchTextName("%" + searchText + "%"), new Function<List<Group>, DomainResource<List<DomainGroup>>>() {
                @Override
                public DomainResource<List<DomainGroup>> apply(List<Group> input) {
                    return ResourceDomainResourceMapper.transform(Resource.Status.SUCCESS, GroupDataDomainMapper.transformAllFromGroup(input), null);
                }
            });
        }

    }




    public LiveData<Resource<List<Group>>> getGroupsListFromServer(final String groupNameOrId, final String searchText) {
        return new NetworkBoundResource<List<Group>, GroupsResponse>(AppExecutors2.getInstance()) {

            @Override
            protected void saveCallResult(@NonNull GroupsResponse item) {
                Log.d(TAG, "saveCallResult");
                if (item.getGroups() != null) {

//                    updateCurrentUserGroupStatus(item.getGroups());
                    Group[] groups = new Group[item.getGroups().size()];




                    int index = 0;
//                    for (Group group: item.getGroups()){
//                        if(userIsAMemberOfGroup(group.getGroupId())){
//                            if(groupStillActive(group.getGroupId())){
//                                group.setCurrentUserStatusForGroup(UserGroupStatusConstants.REGULAR_MEMBER);
//                            }
//                            else{
//                                group.setCurrentUserStatusForGroup(UserGroupStatusConstants.NONE);
//                            }
//
//                        }
//                        else{
//                            group.setCurrentUserStatusForGroup(UserGroupStatusConstants.NONE);
//                        }
//                        //groupDao.updateFromSearchResults(group.getGroupId(), group.getGroupName(),group.getGroupDescription(), group.getGroupCreatorAlias(), group.isPrivateGroup(), group.getCurrentUserStatusForGroup());
//                        //we are getting all group data in search (not GroupUser, but Group data)
//                        //so, we are just replacing it
//                        groupDao.insertGroup(group);
//
//                    for (long rowid : groupDao.insertGroups((Group[]) (item.getGroups().toArray(groups)))) {
//                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
//                        index++;
//                    }
                    for (long rowid : groupDao.deleteAndInsert((Group[]) (item.getGroups().toArray(groups)))) {
                        Log.d(TAG, "saveCallResult: row id is - " + rowid);
                        index++;
                    }
                }

            }

            @Override
            protected boolean shouldFetch(@Nullable List<Group> data) {
                Log.d(TAG, "shouldFetch");
                if (NetworkHelper.isOnline()) {
                    return true;
                }
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Group>> loadFromDb() {
                Log.d(TAG, "loadFromDB");
                if("GroupName".equals(groupNameOrId))
                    return groupDao.getGroupsBySearchTextName("%"+searchText+"%");
                else{
                    return groupDao.getGroupsBySearchTextId("%"+searchText+"%");
                }
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<GroupsResponse>> createCall() {
                Log.d(TAG, "createCall");
//                return AppConfigHelper.getBackendApiServiceProvider().getGroupsMatchByName(groupNameOrId, searchText);
                return AppConfigHelper.getBackendApiServiceProvider().getGroupsMatchByNameOrId(AppConfigHelper.getUserId(), searchText, groupNameOrId);
            }
        }.getAsLiveData();
    }

//    private boolean groupStillActive(String groupId) {
//        if(userGroups == null || groupId == null){ return false;}
//
//        for(UserGroup userGroup: userGroups){
//            if(userGroup.getGroupId().equals(groupId)){
//                return !userGroup.getDeleted(); //if getDeleted is false, that means the group is still active
//            }
//        }
//
//        return false;
//
//    }


//    private boolean userIsAMemberOfGroup(String groupId){
//        if(userGroups == null || groupId == null){ return false;}
//
//        return userGroups.contains(new UserGroup(AppConfigHelper.getRequestUserId(), groupId));
//    }
}

