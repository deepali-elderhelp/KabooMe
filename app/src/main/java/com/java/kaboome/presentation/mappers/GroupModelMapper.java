package com.java.kaboome.presentation.mappers;

import com.java.kaboome.constants.GroupListStatusConstants;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.views.features.groupInfo.NotificationLevels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupModelMapper {
    public static GroupModel transformAll(DomainResource<DomainGroup> input, List<DomainGroupUser> groupUsers) {

        DomainGroup domainGroup = input.data;

        GroupModel group = new GroupModel();

        group.setGroupId(domainGroup.getGroupId());
        group.setGroupName(domainGroup.getGroupName());
        group.setGroupDescription(domainGroup.getGroupDescription());

        if(groupUsers != null) {
            group.setNumberOfMembers(groupUsers.size());
        }

        group.setExpiryDate(domainGroup.getExpiry());
        group.setNotifications(getCurrentUserNotificationLevel(groupUsers));

        group.setOpenToRequests(domainGroup.getOpenToRequests());

//        group.setCurrentUserAdmin(getCurrentUserIsAdmin(groupUsers));

        group.setCurrentUserGroupStatus(domainGroup.getCurrentUserStatusForGroup());

        group.setGroupPrivate(domainGroup.isPrivateGroup());

        group.setUnicastGroup(domainGroup.getUnicastGroup());

        group.setAdmins(getAdmins(groupUsers));

        group.setRegularMembers(getNonAdminMembers(groupUsers));

        group.setImageUpdateTimestamp(domainGroup.getImageUpdateTimestamp());

        group.setGroupPicUploaded(domainGroup.getGroupPicUploaded());

        group.setGroupPicLoadingGoingOn(domainGroup.getGroupPicLoadingGoingOn());

        return group;
    }

    private static List<GroupUserModel> getAdmins(List<DomainGroupUser> listOfDomainUsers){

        List<GroupUserModel> listOfGroupUsers = new ArrayList<>();

        if(listOfDomainUsers == null)
            return listOfGroupUsers;

        for(DomainGroupUser groupUser: listOfDomainUsers){
            if(groupUser.getIsAdmin().equals("true")){
                GroupUserModel groupUserModel = new GroupUserModel();
                groupUserModel.setGroupId(groupUser.getGroupId());
                groupUserModel.setUserId(groupUser.getUserId());
                groupUserModel.setAlias(groupUser.getUserName());
                groupUserModel.setNotify(groupUser.getNotify());
                groupUserModel.setIsAdmin(groupUser.getIsAdmin());
                groupUserModel.setIsCreator(groupUser.getIsCreator());
                groupUserModel.setRole(groupUser.getRole());
                groupUserModel.setGroupUserPicUploaded(groupUser.getGroupUserPicUploaded());
                groupUserModel.setGroupUserPicLoadingGoingOn(groupUser.getGroupUserPicLoadingGoingOn());
                groupUserModel.setImageUpdateTimestamp(groupUser.getImageUpdateTimestamp());
                groupUserModel.setDeviceId(groupUser.getDeviceId());
                listOfGroupUsers.add(groupUserModel);
            }
        }

        return listOfGroupUsers;
    }


    private static List<GroupUserModel> getNonAdminMembers(List<DomainGroupUser> listOfDomainUsers){

        List<GroupUserModel> listOfGroupUsers = new ArrayList<>();

        if(listOfDomainUsers == null)
            return listOfGroupUsers;

        for(DomainGroupUser groupUser: listOfDomainUsers){
            if(groupUser.getIsAdmin().equals("false")){
                GroupUserModel groupUserModel = new GroupUserModel();
                groupUserModel.setGroupId(groupUser.getGroupId());
                groupUserModel.setUserId(groupUser.getUserId());
                groupUserModel.setAlias(groupUser.getUserName());
                groupUserModel.setNotify(groupUser.getNotify());
                groupUserModel.setIsAdmin(groupUser.getIsAdmin());
                groupUserModel.setIsCreator(groupUser.getIsCreator());
                groupUserModel.setRole(groupUser.getRole());
                groupUserModel.setGroupUserPicUploaded(groupUser.getGroupUserPicUploaded());
                groupUserModel.setGroupUserPicLoadingGoingOn(groupUser.getGroupUserPicLoadingGoingOn());
                groupUserModel.setImageUpdateTimestamp(groupUser.getImageUpdateTimestamp());
                groupUserModel.setDeviceId(groupUser.getDeviceId());
                listOfGroupUsers.add(groupUserModel);
            }
        }

        return listOfGroupUsers;
    }

//    private static Boolean getCurrentUserIsAdmin(List<DomainGroupUser> listOfDomainUsers){
//        String currentUserId = AppConfigHelper.getUserId();
//
//        if(listOfDomainUsers == null){
//            return false;
//        }
//
//        for(DomainGroupUser domainGroupUser: listOfDomainUsers){
//            if(domainGroupUser.getUserId().equals(currentUserId)){
//                return domainGroupUser.getIsAdmin().equals("true");
//            }
//        }
//
//        return false; //default
//    }

    private static String getCurrentUserNotificationLevel(List<DomainGroupUser> listOfDomainUsers){
        String currentUserId = AppConfigHelper.getUserId();

        if(listOfDomainUsers == null){
            return "All messages"; //default
        }

        for(DomainGroupUser domainGroupUser: listOfDomainUsers){
            if(domainGroupUser.getUserId().equals(currentUserId)){
                return getNotificationString(domainGroupUser.getNotify());
            }
        }

        return "All messages"; //default
    }

    private static String getNotificationString(String notify) {

        if(notify == null){
            return NotificationLevels.ALL_MESSAGES;
        }
        if("1".equals(notify)){
            return NotificationLevels.ONLY_URGENT;
        }
        if("2".equals(notify)){
            return NotificationLevels.ALL_MESSAGES;
        }
        if("0".equals(notify)){
            return NotificationLevels.NONE;
        }

        return NotificationLevels.ALL_MESSAGES;

    }

    public static DomainGroup getDomainFromGroupModel(GroupModel groupModel){

        DomainGroup domainGroup = new DomainGroup();

        domainGroup.setGroupId(groupModel.getGroupId());
        domainGroup.setGroupName(groupModel.getGroupName());
        domainGroup.setGroupDescription(groupModel.getGroupDescription());
        domainGroup.setExpiry(groupModel.getExpiryDate());
        domainGroup.setPrivateGroup(groupModel.getGroupPrivate());
        domainGroup.setUnicastGroup(groupModel.getUnicastGroup());
        domainGroup.setOpenToRequests(groupModel.getOpenToRequests());
        domainGroup.setImageUpdateTimestamp(groupModel.getImageUpdateTimestamp());
        domainGroup.setGroupPicLoadingGoingOn(groupModel.getGroupPicLoadingGoingOn());
        domainGroup.setGroupPicUploaded(groupModel.getGroupPicUploaded());
        domainGroup.setGroupCreatorRole(groupModel.getCreatorRole());
        domainGroup.setCreatedByAlias(groupModel.getCreatedByAlias());

        return domainGroup;
    }

    public static DomainGroup getDomainFromGroupModelWithUsers(GroupModel groupModel){

        DomainGroup domainGroup = new DomainGroup();

        domainGroup.setGroupId(groupModel.getGroupId());
        domainGroup.setGroupName(groupModel.getGroupName());
        domainGroup.setGroupDescription(groupModel.getGroupDescription());
        domainGroup.setExpiry(groupModel.getExpiryDate());
        domainGroup.setPrivateGroup(groupModel.getGroupPrivate());
        domainGroup.setUnicastGroup(groupModel.getUnicastGroup());
        domainGroup.setOpenToRequests(groupModel.getOpenToRequests());
        domainGroup.setImageUpdateTimestamp(groupModel.getImageUpdateTimestamp());
        domainGroup.setGroupPicUploaded(groupModel.getGroupPicUploaded());
        domainGroup.setGroupPicLoadingGoingOn(groupModel.getGroupPicLoadingGoingOn());
        domainGroup.setGroupCreatorRole(groupModel.getCreatorRole());
        domainGroup.setCreatedByAlias(groupModel.getCreatedByAlias());

        List<DomainGroupUser> groupUsers = new ArrayList<>();
        List<GroupUserModel> usersJoined = groupModel.getRegularMembers();
        if(usersJoined != null){
            for(GroupUserModel groupUserModel: usersJoined){
                groupUsers.add(GroupUserModelMapper.getDomainFromGroupUserModel(groupUserModel));
            }

            domainGroup.setUsersJoined((ArrayList<DomainGroupUser>) groupUsers);
        }

        return domainGroup;
    }

    public static GroupModel getModelFromGroupDomain(DomainGroup domainGroup){

        GroupModel groupModel = new GroupModel();

        groupModel.setGroupId(domainGroup.getGroupId());
        groupModel.setGroupName(domainGroup.getGroupName());
        groupModel.setGroupDescription(domainGroup.getGroupDescription());
        groupModel.setCurrentUserGroupStatus(domainGroup.getCurrentUserStatusForGroup());
        groupModel.setCreatedByAlias(domainGroup.getCreatedByAlias());
        groupModel.setGroupPrivate(domainGroup.isPrivateGroup());
        groupModel.setUnicastGroup(domainGroup.getUnicastGroup());
        groupModel.setExpiryDate(domainGroup.getExpiry());
        groupModel.setOpenToRequests(domainGroup.getOpenToRequests());
        groupModel.setImageUpdateTimestamp(domainGroup.getImageUpdateTimestamp());
        groupModel.setGroupPicLoadingGoingOn(domainGroup.getGroupPicLoadingGoingOn());
        groupModel.setGroupPicUploaded(domainGroup.getGroupPicUploaded());
        groupModel.setCreatorRole(domainGroup.getGroupCreatorRole());

        return groupModel;
    }

    public static List<GroupModel> getGroupModelsFromDomain(List<DomainGroup> domainGroups){

        List<GroupModel> groupCollection;

        if (domainGroups != null && !domainGroups.isEmpty()) {
            groupCollection = new ArrayList<>();
            for (DomainGroup group : domainGroups) {
                groupCollection.add(getModelFromGroupDomain(group));
            }
        } else {
            groupCollection = Collections.emptyList();
        }

        return groupCollection;

    }

    public static List<GroupModel> getGroupModelsFromDomainFilterByJoinedGroups(List<DomainGroup> domainGroups, List<DomainUserGroup> userGroups){


        List<GroupModel> groupCollection;

        if (domainGroups != null && !domainGroups.isEmpty()) {

            groupCollection = new ArrayList<>();
            for (DomainGroup group : domainGroups) {

                group.setCurrentUserStatusForGroup(UserGroupStatusConstants.NONE); //default unless proven otherwise

                for(DomainUserGroup userGroup : userGroups){
                    if(userGroup.getGroupId().equals(group.getGroupId())){
                        if(!userGroup.getDeleted()){
                            group.setCurrentUserStatusForGroup(UserGroupStatusConstants.REGULAR_MEMBER);
                        }
                    }
                }

                groupCollection.add(getModelFromGroupDomain(group));
            }
        } else {
            groupCollection = Collections.emptyList();
        }

        return groupCollection;

    }

    public static List<GroupModel> transformAllGroupDomainResourceToModels(DomainResource<List<DomainGroup>> groupsResource, boolean serverFetch){

        List<DomainGroup> groups = groupsResource.data;

        List<GroupModel> viewGroupsList = new ArrayList<>();

        for(DomainGroup domainGroup: groups){

            //if group is deleted, do not add it to the list
            if(domainGroup.getDeleted() == null ||  domainGroup.getDeleted() != true) {

                GroupModel groupModel = new GroupModel();
                groupModel.setGroupId(domainGroup.getGroupId());
                groupModel.setGroupName(domainGroup.getGroupName());
                groupModel.setGroupDescription(domainGroup.getGroupDescription());
                groupModel.setCreatedByAlias(domainGroup.getCreatedByAlias());
                groupModel.setCreatorRole(domainGroup.getGroupCreatorRole());
                groupModel.setCurrentUserGroupStatus(domainGroup.getCurrentUserStatusForGroup());
                groupModel.setGroupPrivate(domainGroup.isPrivateGroup());
                groupModel.setUnicastGroup(domainGroup.getUnicastGroup());
                groupModel.setExpiryDate(domainGroup.getExpiry());
                groupModel.setOpenToRequests(domainGroup.getOpenToRequests());
                groupModel.setImageUpdateTimestamp(domainGroup.getImageUpdateTimestamp());
                groupModel.setGroupPicUploaded(domainGroup.getGroupPicUploaded());
                groupModel.setGroupPicLoadingGoingOn(domainGroup.getGroupPicLoadingGoingOn());
                //TODO: remaining fields as and when needed

                viewGroupsList.add(groupModel);
            }
        }

        if(groupsResource.status == DomainResource.Status.LOADING){
            GroupModel groupModel = new GroupModel();
            groupModel.setGroupId(GroupListStatusConstants.LOADING.toString());
            viewGroupsList.add(groupModel);
        }
        else if(groupsResource.status == DomainResource.Status.SUCCESS){
            if(groups.isEmpty()){
                if(serverFetch) { //?? why is server fetch there, can't remember
                    GroupModel groupModel = new GroupModel();
                    groupModel.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
                    viewGroupsList.add(groupModel);
                }
            }
            //else it is already done
        }
        else if(groupsResource.status == DomainResource.Status.ERROR){
            if(groups.isEmpty()){
                if(serverFetch) { //?? why is server fetch there, can't remember
                    GroupModel groupModel = new GroupModel();
                    groupModel.setGroupId(GroupListStatusConstants.NO_GROUPS.toString());
                    viewGroupsList.add(groupModel);
                }
            }
            //else it is already done
        }
        return viewGroupsList;
    }





}
