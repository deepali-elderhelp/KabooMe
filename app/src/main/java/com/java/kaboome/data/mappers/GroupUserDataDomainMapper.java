package com.java.kaboome.data.mappers;

import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupUserDataDomainMapper {

    public static DomainGroupUser transformFromGroup(GroupUser groupUser){

        if (groupUser == null) {
//            throw new IllegalArgumentException("Cannot transformFromGroup a null value");
            return null;
        }

        DomainGroupUser domainGroupUser = new DomainGroupUser();


        domainGroupUser.setGroupId(groupUser.getGroupId());
        domainGroupUser.setUserId(groupUser.getUserId());
        domainGroupUser.setUserName(groupUser.getUserName());
        domainGroupUser.setRole(groupUser.getRole());
        domainGroupUser.setDateJoined(groupUser.getDateJoined());
        domainGroupUser.setNotify(groupUser.getNotify());
        domainGroupUser.setIsAdmin(groupUser.getIsAdmin());
        domainGroupUser.setIsCreator(groupUser.getIsCreator());
        domainGroupUser.setDeviceId(groupUser.getDeviceId());
        domainGroupUser.setImageUpdateTimestamp(groupUser.getImageUpdateTimestamp());
        domainGroupUser.setGroupUserPicLoadingGoingOn(groupUser.getGroupUserPicLoadingGoingOn());
        domainGroupUser.setGroupUserPicUploaded(groupUser.getGroupUserPicUploaded());

        return domainGroupUser;
    }


    public static GroupUser transformFromDomain(DomainGroupUser domainGroupUser){

        if (domainGroupUser == null) {
            throw new IllegalArgumentException("Cannot transformFromDomainGroup a null value");
        }

        GroupUser groupUser = new GroupUser();


        groupUser.setGroupId(domainGroupUser.getGroupId());
        groupUser.setUserId(domainGroupUser.getUserId());
        groupUser.setUserName(domainGroupUser.getUserName());
        groupUser.setRole(domainGroupUser.getRole());
        groupUser.setDateJoined(domainGroupUser.getDateJoined());
        groupUser.setNotify(domainGroupUser.getNotify());
        groupUser.setIsAdmin(domainGroupUser.getIsAdmin());
        groupUser.setIsCreator(domainGroupUser.getIsCreator());
        groupUser.setDeviceId(domainGroupUser.getDeviceId());
        groupUser.setImageUpdateTimestamp(domainGroupUser.getImageUpdateTimestamp());
        groupUser.setGroupUserPicLoadingGoingOn(domainGroupUser.getGroupUserPicLoadingGoingOn());
        groupUser.setGroupUserPicUploaded(domainGroupUser.getGroupUserPicUploaded());

        return groupUser;
    }

    public static List<DomainGroupUser> transform(List<GroupUser> groupUsersCollection) {
        List<DomainGroupUser> domainGroupUsersCollection;

        if (groupUsersCollection != null && !groupUsersCollection.isEmpty()) {
            domainGroupUsersCollection = new ArrayList<>();
            for (GroupUser groupUser : groupUsersCollection) {
                domainGroupUsersCollection.add(transformFromGroup(groupUser));
            }
        } else {
            domainGroupUsersCollection = Collections.emptyList();
        }

        return domainGroupUsersCollection;
    }

    public static GroupUser transformFromDomain(DomainGroupUser domainGroupUser, String action){
        if (domainGroupUser == null || action == null) {
            throw new IllegalArgumentException("Cannot transformFromDomainGroupUser or action a null value");
        }

        GroupUser groupUser = new GroupUser();

        groupUser.setGroupId(domainGroupUser.getGroupId());
        groupUser.setUserId(domainGroupUser.getUserId());

        if("updateGroupUserNotification".equals(action)){
            groupUser.setNotify(domainGroupUser.getNotify());
            groupUser.setDeviceId(domainGroupUser.getDeviceId());
        }
        if("updateGroupUserRoleAndAlias".equals(action)){
            groupUser.setUserName(domainGroupUser.getUserName());
            groupUser.setRole(domainGroupUser.getRole());
            groupUser.setImageUpdateTimestamp(domainGroupUser.getImageUpdateTimestamp());
            groupUser.setGroupUserPicLoadingGoingOn(domainGroupUser.getGroupUserPicLoadingGoingOn());
            groupUser.setGroupUserPicUploaded(domainGroupUser.getGroupUserPicUploaded());
        }
        //this action has been moved to Group level
        //because we do it for all the users together
//        if("updateGroupUsersToAdmin".equals(action)){
//            groupUser.setIsAdmin("true");
//        }

        return groupUser;

    }
}
