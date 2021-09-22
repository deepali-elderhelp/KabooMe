package com.java.kaboome.domain.entities;


import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.data.entities.GroupUser;

import java.util.ArrayList;

public class DomainGroup {


    private String groupId;

    private String groupName;

    private String groupDescription;

    private String createdBy;

    private String createdByAlias;

    private String groupCreatorRole;

    private String createdOn;

    private Long expiry;

    private Boolean privateGroup;

    private Boolean unicastGroup;

    private Boolean openToRequests;

    private Boolean isDeleted;

    private UserGroupStatusConstants currentUserStatusForGroup;

    private Long imageUpdateTimestamp; //group's image's time stamp

    private ArrayList<DomainGroupUser> usersJoined;

    public DomainGroup() {
    }

    public DomainGroup(String groupId, String groupName, String groupDescription, String createdBy, String createdByAlias, String groupCreatorRole, String createdOn, Long expiry, Boolean privateGroup, Boolean isDeleted, UserGroupStatusConstants currentUserStatusForGroup, Long imageUpdateTimestamp) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdBy = createdBy;
        this.createdByAlias = createdByAlias;
        this.groupCreatorRole = groupCreatorRole;
        this.createdOn = createdOn;
        this.expiry = expiry;
        this.privateGroup = privateGroup;
        this.isDeleted = isDeleted;
        this.currentUserStatusForGroup = currentUserStatusForGroup;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public DomainGroup(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByAlias() {
        return createdByAlias;
    }

    public void setCreatedByAlias(String createdByAlias) {
        this.createdByAlias = createdByAlias;
    }

    public String getGroupCreatorRole() {
        return groupCreatorRole;
    }

    public void setGroupCreatorRole(String groupCreatorRole) {
        this.groupCreatorRole = groupCreatorRole;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }


    public Boolean isPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(Boolean privateGroup) {
        this.privateGroup = privateGroup;
    }

    public Boolean getUnicastGroup() {
        return unicastGroup;
    }

    public void setUnicastGroup(Boolean unicastGroup) {
        this.unicastGroup = unicastGroup;
    }

    public Boolean getOpenToRequests() {
        return openToRequests;
    }

    public void setOpenToRequests(Boolean openToRequests) {
        this.openToRequests = openToRequests;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }


    public UserGroupStatusConstants getCurrentUserStatusForGroup() {
        return currentUserStatusForGroup;
    }

    public void setCurrentUserStatusForGroup(UserGroupStatusConstants currentUserStatusForGroup) {
        this.currentUserStatusForGroup = currentUserStatusForGroup;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public ArrayList<DomainGroupUser> getUsersJoined() {
        return usersJoined;
    }

    public void setUsersJoined(ArrayList<DomainGroupUser> usersJoined) {
        this.usersJoined = usersJoined;
    }

}
