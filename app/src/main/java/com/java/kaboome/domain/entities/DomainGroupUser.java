package com.java.kaboome.domain.entities;

import com.google.gson.annotations.SerializedName;

public class DomainGroupUser {

    private String groupId;

    private String userId;

    private String userName;

    private String role;

    private Long dateJoined;

    private String notify;

    private String isAdmin;

    private String isCreator;

    private String deviceId;

    private Boolean isCheckedToBeAdmin;

    private Long imageUpdateTimestamp; //users's image's time stamp

    private Boolean groupUserPicUploaded;

    private Boolean groupUserPicLoadingGoingOn = false;

    public DomainGroupUser() {
    }

    public DomainGroupUser(String groupId, String userId, String userName, String role, Long dateJoined, String notify, String isAdmin, String isCreator, String deviceId) {
        this.groupId = groupId;
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.dateJoined = dateJoined;
        this.notify = notify;
        this.isAdmin = isAdmin;
        this.isCreator = isCreator;
        this.deviceId = deviceId;
    }

    public DomainGroupUser(String groupId, String userId, String userName, String role, Long dateJoined, String notify, String isAdmin, String isCreator, String deviceId, Long imageUpdateTimestamp) {
        this.groupId = groupId;
        this.userId = userId;
        this.userName = userName;
        this.role = role;
        this.dateJoined = dateJoined;
        this.notify = notify;
        this.isAdmin = isAdmin;
        this.isCreator = isCreator;
        this.deviceId = deviceId;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Long dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getIsCreator() {
        return isCreator;
    }

    public void setIsCreator(String isCreator) {
        this.isCreator = isCreator;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getCheckedToBeAdmin() {
        return isCheckedToBeAdmin;
    }

    public void setCheckedToBeAdmin(Boolean checkedToBeAdmin) {
        isCheckedToBeAdmin = checkedToBeAdmin;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public Boolean getGroupUserPicUploaded() {
        return groupUserPicUploaded;
    }

    public void setGroupUserPicUploaded(Boolean groupUserPicUploaded) {
        this.groupUserPicUploaded = groupUserPicUploaded;
    }

    public Boolean getGroupUserPicLoadingGoingOn() {
        return groupUserPicLoadingGoingOn;
    }

    public void setGroupUserPicLoadingGoingOn(Boolean groupUserPicLoadingGoingOn) {
        this.groupUserPicLoadingGoingOn = groupUserPicLoadingGoingOn;
    }
}
