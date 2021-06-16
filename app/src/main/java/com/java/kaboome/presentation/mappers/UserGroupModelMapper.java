package com.java.kaboome.presentation.mappers;

import android.util.Log;

import com.java.kaboome.constants.GroupListStatusConstants;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainGroupUnreadData;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.DateHelper;
import com.java.kaboome.presentation.entities.UserGroupModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class takes a UserGroup Domain object and converts it into a
 * UserGroupModel object and vice versa
 */
public class UserGroupModelMapper {

    private static final String TAG = "KMUserGroupModelMapper";




    //you should order the list here based upon last message etc.
    public static List<UserGroupModel> transformAll(DomainResource<List<DomainUserGroup>> groupsResource, List<DomainGroupUnreadData> unreadData){

        List<DomainUserGroup> groups = groupsResource.data;

        Log.d(TAG, "transformAll: status is "+groupsResource.status);

        List<UserGroupModel> viewGroupsList = new ArrayList<>();

        for(DomainUserGroup domainUserGroup: groups){

            //if group is deleted, do not add it to the list
            if(domainUserGroup.getDeleted() == null ||  domainUserGroup.getDeleted() != true) {

                UserGroupModel userGroupModel = new UserGroupModel();
                userGroupModel.setGroupId(domainUserGroup.getGroupId());
                userGroupModel.setGroupName(domainUserGroup.getGroupName());
                userGroupModel.setAlias(domainUserGroup.getAlias());
                userGroupModel.setIsAdmin(domainUserGroup.getIsAdmin());
                userGroupModel.setRole(domainUserGroup.getGroupAdminRole());
                userGroupModel.setPrivate(domainUserGroup.getPrivateGroup());
                userGroupModel.setImageUpdateTimestamp(domainUserGroup.getImageUpdateTimestamp());
                userGroupModel.setUserImageUpdateTimestamp(domainUserGroup.getUserImageUpdateTimestamp());
                userGroupModel.setCacheClearTS(domainUserGroup.getCacheClearTS());
                userGroupModel.setAdminsLastAccessed(domainUserGroup.getAdminsLastAccessed());
                userGroupModel.setAdminsCacheClearTS(domainUserGroup.getAdminsCacheClearTS());
//                userGroupModel.setNumberOfRequests(domainUserGroup.getNumberOfRequests());

                DomainGroupUnreadData domainGroupUnreadData = getGroupUnreadData(domainUserGroup.getGroupId(), unreadData);
                if (domainGroupUnreadData != null) {
                    String separator = (domainGroupUnreadData.getLastMessageSentBy()!= null && !domainGroupUnreadData.getLastMessageSentBy().isEmpty())? ":":"";
                    userGroupModel.setUnreadCount(domainGroupUnreadData.getCountOfUnreadMessages());
                    userGroupModel.setLastMessageText(domainGroupUnreadData.getLastMessageSentBy() +separator+domainGroupUnreadData.getLastMessageText());
                }
                viewGroupsList.add(userGroupModel);
            }
        }

        if(groupsResource.status == DomainResource.Status.LOADING){
            UserGroupModel userGroupModel = new UserGroupModel();
            userGroupModel.setGroupId(GroupListStatusConstants.LOADING.toString());
            viewGroupsList.add(userGroupModel);
        }
        else if(groupsResource.status == DomainResource.Status.SUCCESS){
            if(groups.isEmpty()){
                UserGroupModel userGroupModel = new UserGroupModel();
                userGroupModel.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
                viewGroupsList.add(userGroupModel);
            }
            //else it is already done
        }
//        else if(groupsResource.status == DomainResource.Status.ERROR){
//            UserGroupModel userGroupModel = new UserGroupModel();
//            userGroupModel.setGroupId(GroupListStatusConstants.ERROR.toString());
//            viewGroupsList.add(userGroupModel);
//        }

        return viewGroupsList;
    }


    public static List<UserGroupModel> transformAll(DomainResource<List<DomainUserGroup>> groupsResource, List<DomainGroupUnreadData> unreadData, List<DomainGroupRequest> groupRequests){

        List<DomainUserGroup> groups = groupsResource.data;

        Log.d(TAG, "transformAll: status is "+groupsResource.status);

        List<UserGroupModel> viewGroupsList = new ArrayList<>();

        for(DomainUserGroup domainUserGroup: groups){

            //if group is deleted, do not add it to the list
            if(domainUserGroup.getDeleted() == null ||  domainUserGroup.getDeleted() != true) {

                UserGroupModel userGroupModel = new UserGroupModel();
                userGroupModel.setGroupId(domainUserGroup.getGroupId());
                userGroupModel.setGroupName(domainUserGroup.getGroupName());
                userGroupModel.setAlias(domainUserGroup.getAlias());
                userGroupModel.setIsAdmin(domainUserGroup.getIsAdmin());
                userGroupModel.setRole(domainUserGroup.getGroupAdminRole());
                userGroupModel.setPrivate(domainUserGroup.getPrivateGroup());
                userGroupModel.setImageUpdateTimestamp(domainUserGroup.getImageUpdateTimestamp());
                userGroupModel.setUserImageUpdateTimestamp(domainUserGroup.getUserImageUpdateTimestamp());
                userGroupModel.setCacheClearTS(domainUserGroup.getCacheClearTS());
                userGroupModel.setAdminsLastAccessed(domainUserGroup.getAdminsLastAccessed());
                userGroupModel.setAdminsCacheClearTS(domainUserGroup.getAdminsCacheClearTS());
                if("true".equalsIgnoreCase(domainUserGroup.getIsAdmin())){
                    userGroupModel.setNumberOfRequests(getNumberOfRequestsForGroup(domainUserGroup.getGroupId(), groupRequests));
                }

                DomainGroupUnreadData domainGroupUnreadData = getGroupUnreadData(domainUserGroup.getGroupId(), unreadData);
                if (domainGroupUnreadData != null) {
                    String separator = (domainGroupUnreadData.getLastMessageSentBy()!= null && !domainGroupUnreadData.getLastMessageSentBy().isEmpty())? ":":"";
                    userGroupModel.setUnreadCount(domainGroupUnreadData.getCountOfUnreadMessages());
                    userGroupModel.setLastMessageText(domainGroupUnreadData.getLastMessageSentBy() +separator+domainGroupUnreadData.getLastMessageText());
                }
                viewGroupsList.add(userGroupModel);
            }
        }

        if(groupsResource.status == DomainResource.Status.LOADING){
            UserGroupModel userGroupModel = new UserGroupModel();
            userGroupModel.setGroupId(GroupListStatusConstants.LOADING.toString());
            viewGroupsList.add(userGroupModel);
        }
        else if(groupsResource.status == DomainResource.Status.SUCCESS){
            if(groups.isEmpty() || allGroupsDeleted(groups)){
                UserGroupModel userGroupModel = new UserGroupModel();
                userGroupModel.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
                viewGroupsList.add(userGroupModel);
            }
            //else it is already done
        }
//        else if(groupsResource.status == DomainResource.Status.ERROR){
//            UserGroupModel userGroupModel = new UserGroupModel();
//            userGroupModel.setGroupId(GroupListStatusConstants.ERROR.toString());
//            viewGroupsList.add(userGroupModel);
//        }

        return viewGroupsList;
    }

    public static List<UserGroupModel> transformAllFromDomain(DomainResource<List<DomainUserGroup>> groupsResource){

            List<DomainUserGroup> groups = groupsResource.data;

            Log.d(TAG, "transformAll: status is "+groupsResource.status);

            List<UserGroupModel> viewGroupsList = new ArrayList<>();

            for(DomainUserGroup domainUserGroup: groups){

                //if group is deleted, do not add it to the list
                if(domainUserGroup.getDeleted() == null ||  domainUserGroup.getDeleted() != true) {

                    UserGroupModel userGroupModel = new UserGroupModel();
                    userGroupModel.setGroupId(domainUserGroup.getGroupId());
                    userGroupModel.setGroupName(domainUserGroup.getGroupName());
                    userGroupModel.setAlias(domainUserGroup.getAlias());
                    userGroupModel.setIsAdmin(domainUserGroup.getIsAdmin());
                    userGroupModel.setRole(domainUserGroup.getGroupAdminRole());
                    userGroupModel.setPrivate(domainUserGroup.getPrivateGroup());
                    userGroupModel.setImageUpdateTimestamp(domainUserGroup.getImageUpdateTimestamp());
                    userGroupModel.setUserImageUpdateTimestamp(domainUserGroup.getUserImageUpdateTimestamp());
                    userGroupModel.setCacheClearTS(domainUserGroup.getCacheClearTS());
                    userGroupModel.setLastAccessed(domainUserGroup.getLastAccessed());
                    userGroupModel.setGroupExpiry(domainUserGroup.getExpiry());
                    userGroupModel.setAdminsLastAccessed(domainUserGroup.getAdminsLastAccessed());
                    userGroupModel.setAdminsCacheClearTS(domainUserGroup.getAdminsCacheClearTS());
//                    userGroupModel.setLastMessageSentAt(domainUserGroup.getLastMessageCacheTS());
//                userGroupModel.setNumberOfRequests(domainUserGroup.getNumberOfRequests());

                    viewGroupsList.add(userGroupModel);
                }
            }

            if(groupsResource.status == DomainResource.Status.LOADING){
                UserGroupModel userGroupModel = new UserGroupModel();
                userGroupModel.setGroupId(GroupListStatusConstants.LOADING.toString());
                userGroupModel.setLastMessageSentAt(Long.MIN_VALUE);
                viewGroupsList.add(userGroupModel);
            }
            else if(groupsResource.status == DomainResource.Status.SUCCESS){
                if(groups.isEmpty() || allGroupsDeleted(groups)){
                    UserGroupModel userGroupModel = new UserGroupModel();
                    userGroupModel.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
                    viewGroupsList.add(userGroupModel);
                }
                //else it is already done
            }
//        else if(groupsResource.status == DomainResource.Status.ERROR){
//            UserGroupModel userGroupModel = new UserGroupModel();
//            userGroupModel.setGroupId(GroupListStatusConstants.ERROR.toString());
//            viewGroupsList.add(userGroupModel);
//        }

            return viewGroupsList;
        }


    public static DomainGroupUnreadData getGroupUnreadData(String groupId, List<DomainGroupUnreadData> allUnreadData){

        if(groupId ==null || groupId.isEmpty() || allUnreadData == null || allUnreadData.isEmpty())
            return null;
        for(DomainGroupUnreadData unreadData : allUnreadData){
            if(groupId.equals(unreadData.getGroupId()))
                return  unreadData;
        }
        return null;

    }

    private static int getNumberOfRequestsForGroup(String groupId, List<DomainGroupRequest> allGroupRequests){
        if(groupId ==null || groupId.isEmpty() || allGroupRequests == null || allGroupRequests.isEmpty())
            return 0;
        int count=0;
        for(DomainGroupRequest domainGroupRequest : allGroupRequests){
            if(groupId.equals(domainGroupRequest.getGroupId()))
                count++;
        }
        return count;
    }

    public static DomainUserGroup getDomainFromUserModel(UserGroupModel userGroupModel){
        DomainUserGroup domainUserGroup = new DomainUserGroup();

        domainUserGroup.setUserId(AppConfigHelper.getUserId());
        domainUserGroup.setGroupId(userGroupModel.getGroupId());
        domainUserGroup.setGroupName(userGroupModel.getGroupName());
        domainUserGroup.setAlias(userGroupModel.getAlias());
        domainUserGroup.setIsAdmin(userGroupModel.getIsAdmin());
        domainUserGroup.setGroupAdminRole(userGroupModel.getRole());
        domainUserGroup.setPrivateGroup(userGroupModel.getPrivate());
        domainUserGroup.setImageUpdateTimestamp(userGroupModel.getImageUpdateTimestamp());
        domainUserGroup.setUserImageUpdateTimestamp(userGroupModel.getUserImageUpdateTimestamp());
        domainUserGroup.setCacheClearTS(userGroupModel.getCacheClearTS());
        domainUserGroup.setLastAccessed(userGroupModel.getLastAccessed());
        domainUserGroup.setDeleted(userGroupModel.getDeleted());
        domainUserGroup.setExpiry(userGroupModel.getGroupExpiry());
        domainUserGroup.setAdminsLastAccessed(userGroupModel.getAdminsLastAccessed());
        domainUserGroup.setAdminsCacheClearTS(userGroupModel.getAdminsCacheClearTS());
//        domainUserGroup.setLastMessageCacheTS(userGroupModel.getLastMessageSentAt());
//        domainUserGroup.setNumberOfRequests(userGroupModel.getNumberOfRequests());

        return domainUserGroup;
    }

    public static UserGroupModel getUserModelFromDomain(DomainUserGroup domainUserGroup){
        UserGroupModel userGroupModel = new UserGroupModel();

        userGroupModel.setGroupId(domainUserGroup.getGroupId());
        userGroupModel.setGroupName(domainUserGroup.getGroupName());
        userGroupModel.setAlias(domainUserGroup.getAlias());
        userGroupModel.setIsAdmin(domainUserGroup.getIsAdmin());
        userGroupModel.setRole(domainUserGroup.getGroupAdminRole());
        userGroupModel.setPrivate(domainUserGroup.getPrivateGroup());
        userGroupModel.setImageUpdateTimestamp(domainUserGroup.getImageUpdateTimestamp());
        userGroupModel.setUserImageUpdateTimestamp(domainUserGroup.getUserImageUpdateTimestamp());
        userGroupModel.setCacheClearTS(domainUserGroup.getCacheClearTS());
        userGroupModel.setLastAccessed(domainUserGroup.getLastAccessed());
//        userGroupModel.setLastMessageSentAt(domainUserGroup.getLastMessageCacheTS());
        userGroupModel.setDeleted(domainUserGroup.getDeleted());
        userGroupModel.setGroupExpiry(domainUserGroup.getExpiry());
        userGroupModel.setAdminsLastAccessed(domainUserGroup.getAdminsLastAccessed());
        userGroupModel.setAdminsCacheClearTS(domainUserGroup.getAdminsCacheClearTS());

//        domainUserGroup.setNumberOfRequests(userGroupModel.getNumberOfRequests());

        return userGroupModel;
    }

    private static boolean allGroupsDeleted(List<DomainUserGroup> groups){
        if(groups == null) return true;

        for(DomainUserGroup group : groups){
            if(!group.getDeleted()){
                return false;
            }
        }

        return true;
    }
}
