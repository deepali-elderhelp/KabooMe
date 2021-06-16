package com.java.kaboome.data.mappers;


import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupDataDomainMapper {

    /**
     * This method also needs to wrap all the GroupUsers into DomainGroupUsers
     * and set it in the DomainGroup and send upwards
     * @param group
     * @return
     */
    public static DomainGroup transformFromGroup(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Cannot transformFromGroup a null value");
        }

        DomainGroup domainGroup = new DomainGroup();

        domainGroup.setGroupId(group.getGroupId());
        domainGroup.setGroupName(group.getGroupName());
        domainGroup.setGroupDescription(group.getGroupDescription());
        domainGroup.setGroupCreatorRole(group.getGroupCreatorRole());
        domainGroup.setCreatedBy(group.getCreatedBy());
        domainGroup.setCreatedOn(group.getCreatedOn());
        domainGroup.setCreatedByAlias(group.getCreatedByAlias());
        domainGroup.setGroupCreatorRole(group.getGroupCreatorRole());
        domainGroup.setExpiry(group.getExpiry());
        domainGroup.setPrivateGroup(group.isPrivateGroup());
        domainGroup.setOpenToRequests(group.getOpenToRequests());
        domainGroup.setDeleted(group.getDeleted());
        domainGroup.setCurrentUserStatusForGroup(group.getCurrentUserStatusForGroup());
        domainGroup.setImageUpdateTimestamp(group.getImageUpdateTimestamp());

//        List<GroupUser> groupUsers = group.getUsersJoined();
//        if(groupUsers != null && groupUsers.size() > 0){
//            for(GroupUser groupUser : groupUsers){
//                DomainGroupUser domainGroupUser = GroupUserDataDomainMapper.transformFromGroup(groupUser);
//
//            }
//        }

        return domainGroup;
    }

    public static Group transformFromDomain(DomainGroup domainGroup) {
        if (domainGroup == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }

        Group group = new Group();

        group.setGroupId(domainGroup.getGroupId());
        group.setGroupName(domainGroup.getGroupName());
        group.setGroupDescription(domainGroup.getGroupDescription());
        group.setGroupCreatorRole(domainGroup.getGroupCreatorRole());
        group.setGroupCreatorRole(domainGroup.getGroupCreatorRole());
        group.setCreatedBy(domainGroup.getCreatedBy());
        group.setCreatedOn(domainGroup.getCreatedOn());
        group.setCreatedByAlias(domainGroup.getCreatedByAlias());
        group.setExpiry(domainGroup.getExpiry());
        group.setPrivateGroup(domainGroup.isPrivateGroup());
        group.setOpenToRequests(domainGroup.getOpenToRequests());
        group.setDeleted(domainGroup.getDeleted());
        group.setCurrentUserStatusForGroup(domainGroup.getCurrentUserStatusForGroup());
        group.setImageUpdateTimestamp(domainGroup.getImageUpdateTimestamp());


        return group;
    }

    public static Group transformFromDomain(DomainGroup domainGroup, String action){
        if (domainGroup == null || action == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain or action a null value");
        }

        Group group = new Group();

        group.setGroupId(domainGroup.getGroupId());

        if("updateGroupName".equals(action)){ //it could be name or privacy
            group.setGroupName(domainGroup.getGroupName());
            group.setPrivateGroup(domainGroup.isPrivateGroup());
        }
        if("updateGroupDesc".equals(action)){
            group.setGroupDescription(domainGroup.getGroupDescription());
        }
        if("updateGroupExpiry".equals(action)){
            group.setExpiry(domainGroup.getExpiry());
        }
        if("updateGroupImage".equals(action)){
            group.setImageUpdateTimestamp(domainGroup.getImageUpdateTimestamp());
        }
        if("updateGroupNamePrivacyImage".equals(action)){
            group.setGroupName(domainGroup.getGroupName());
            group.setPrivateGroup(domainGroup.isPrivateGroup());
            group.setImageUpdateTimestamp(domainGroup.getImageUpdateTimestamp());
        }
        if("updateGroupRequestsSetting".equals(action)){
            group.setOpenToRequests(domainGroup.getOpenToRequests());
        }
        if("updateGroupUsersToAdmin".equals(action)){
            List<GroupUser> groupUsers = new ArrayList<>();
            List<DomainGroupUser> usersJoined = domainGroup.getUsersJoined();
            for(DomainGroupUser domainGroupUser: usersJoined){
                groupUsers.add(GroupUserDataDomainMapper.transformFromDomain(domainGroupUser));
            }
            group.setUsersJoined((ArrayList<GroupUser>) groupUsers);
        }

        return group;

    }

    public static List<DomainGroup> transformAllFromGroup(List<Group> groupCollection) {
        List<DomainGroup> domainGroupCollection;

        if (groupCollection != null && !groupCollection.isEmpty()) {
            domainGroupCollection = new ArrayList<>();
            for (Group group : groupCollection) {
                domainGroupCollection.add(transformFromGroup(group));
            }
        } else {
            domainGroupCollection = Collections.emptyList();
        }

        return domainGroupCollection;
    }


}
