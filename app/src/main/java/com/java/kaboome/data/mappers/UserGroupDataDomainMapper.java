package com.java.kaboome.data.mappers;

import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.domain.entities.DomainUserGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class gets a data specific UserGroup Entity and converts it
 * to a domain specific UserGroup Entity
 * So basically UserGroup --> DomainUserGroup
 */
public class UserGroupDataDomainMapper {

    public UserGroupDataDomainMapper() {
    }

    public static DomainUserGroup transform(UserGroup userGroup) {
        if (userGroup == null) {
            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
        }
        DomainUserGroup domainUserGroup = new DomainUserGroup();
        domainUserGroup.setUserId(userGroup.getUserId());
        domainUserGroup.setGroupId(userGroup.getGroupId());
        domainUserGroup.setGroupName(userGroup.getGroupName());
        domainUserGroup.setGroupAdminRole(userGroup.getGroupAdminRole());
        domainUserGroup.setPrivateGroup(userGroup.isPrivateGroup());
        domainUserGroup.setUnicastGroup(userGroup.getUnicastGroup());
        domainUserGroup.setAlias(userGroup.getAlias());
        domainUserGroup.setIsAdmin(userGroup.getIsAdmin());
        domainUserGroup.setIsCreator(userGroup.getIsCreator());
        domainUserGroup.setDateJoined(userGroup.getDateJoined());
        domainUserGroup.setExpiry(userGroup.getExpiry());
        domainUserGroup.setNotify(userGroup.getNotify());
        domainUserGroup.setDeleted(userGroup.getDeleted());
        domainUserGroup.setDeviceId(userGroup.getDeviceId());
        domainUserGroup.setLastAccessed(userGroup.getLastAccessed());
        domainUserGroup.setLastHigh(userGroup.getLastHigh());
        domainUserGroup.setLastRegular(userGroup.getLastRegular());
        domainUserGroup.setImageUpdateTimestamp(userGroup.getImageUpdateTimestamp());
        domainUserGroup.setGroupPicUploaded(userGroup.getGroupPicUploaded());
        domainUserGroup.setGroupPicLoadingGoingOn(userGroup.getGroupPicLoadingGoingOn());
        domainUserGroup.setUserImageUpdateTimestamp(userGroup.getUserImageUpdateTimestamp());
//        domainUserGroup.setLastMessageCacheTS(userGroup.getLastMessageCacheTS());
        domainUserGroup.setCacheClearTS(userGroup.getCacheClearTS());
//        domainUserGroup.setNumberOfRequests(userGroup.getNumberOfRequests());
        domainUserGroup.setAdminsLastAccessed(userGroup.getAdminsLastAccessed());
        domainUserGroup.setAdminsCacheClearTS(userGroup.getAdminsCacheClearTS());

        return domainUserGroup;
    }

    public static UserGroup transformFromDomain(DomainUserGroup domainUserGroup) {
        if (domainUserGroup == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }
        UserGroup userGroup = new UserGroup();
        userGroup.setUserId(domainUserGroup.getUserId());
        userGroup.setGroupId(domainUserGroup.getGroupId());
        userGroup.setGroupName(domainUserGroup.getGroupName());
        userGroup.setGroupAdminRole(domainUserGroup.getGroupAdminRole());
        userGroup.setPrivateGroup(domainUserGroup.getPrivateGroup());
        userGroup.setUnicastGroup(domainUserGroup.getUnicastGroup());
        userGroup.setAlias(domainUserGroup.getAlias());
        userGroup.setIsAdmin(domainUserGroup.getIsAdmin());
        userGroup.setIsCreator(domainUserGroup.getIsCreator());
        userGroup.setDateJoined(domainUserGroup.getDateJoined());
        userGroup.setExpiry(domainUserGroup.getExpiry());
        userGroup.setNotify(domainUserGroup.getNotify());
        userGroup.setDeleted(domainUserGroup.getDeleted());
        userGroup.setDeviceId(domainUserGroup.getDeviceId());
        userGroup.setLastAccessed(domainUserGroup.getLastAccessed());
        userGroup.setLastHigh(domainUserGroup.getLastHigh());
        userGroup.setLastRegular(domainUserGroup.getLastRegular());
        userGroup.setImageUpdateTimestamp(domainUserGroup.getImageUpdateTimestamp());
        userGroup.setGroupPicUploaded(domainUserGroup.getGroupPicUploaded());
        userGroup.setGroupPicLoadingGoingOn(domainUserGroup.getGroupPicLoadingGoingOn());
        userGroup.setUserImageUpdateTimestamp(domainUserGroup.getUserImageUpdateTimestamp());
        userGroup.setCacheClearTS(domainUserGroup.getCacheClearTS());
//        userGroup.setLastMessageCacheTS(domainUserGroup.getLastMessageCacheTS());
//        userGroup.setNumberOfRequests(domainUserGroup.getNumberOfRequests());
        userGroup.setAdminsLastAccessed(domainUserGroup.getAdminsLastAccessed());
        userGroup.setAdminsCacheClearTS(domainUserGroup.getAdminsCacheClearTS());


        return userGroup;
    }

    public static List<DomainUserGroup> transform(List<UserGroup> userGroupCollection) {
        List<DomainUserGroup> domainUserGroupCollection;

        if (userGroupCollection != null && !userGroupCollection.isEmpty()) {
            domainUserGroupCollection = new ArrayList<>();
            for (UserGroup userGroup : userGroupCollection) {
                domainUserGroupCollection.add(transform(userGroup));
            }
        } else {
            domainUserGroupCollection = Collections.emptyList();
        }

        return domainUserGroupCollection;
    }
}
