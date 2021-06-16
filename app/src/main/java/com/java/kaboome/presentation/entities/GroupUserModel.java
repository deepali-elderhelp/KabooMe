package com.java.kaboome.presentation.entities;

import java.io.Serializable;

public class GroupUserModel implements Serializable {

    String userId;

    String groupId;

    String alias;

    String role;

    String isAdmin;

    String isCreator;

    String notify;

    String deviceId;

    Long imageUpdateTimestamp; //users's image's time stamp

    Boolean isCheckedToBeAdmin = false; //needed for add admin screen checkbox

    //Following fields is needed if the user changes profile pic

    String imagePath;
    String thumbnailPath;
    boolean imageChanged;


    public GroupUserModel() {
    }

    public GroupUserModel(String userId, String groupId, String alias, String role, String isAdmin, String isCreator, String notify, Long imageUpdateTimestamp) {
        this.userId = userId;
        this.groupId = groupId;
        this.alias = alias;
        this.role = role;
        this.isAdmin = isAdmin;
        this.isCreator = isCreator;
        this.notify = notify;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
        this.isCheckedToBeAdmin = false;
    }

    public GroupUserModel(String userId, String groupId, String alias, String role, String isAdmin, String isCreator, String notify, String deviceId, Long imageUpdateTimestamp) {
        this.userId = userId;
        this.groupId = groupId;
        this.alias = alias;
        this.role = role;
        this.isAdmin = isAdmin;
        this.isCreator = isCreator;
        this.notify = notify;
        this.deviceId = deviceId;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
        this.isCheckedToBeAdmin = false;
    }

    public Boolean getCheckedToBeAdmin() {
        return isCheckedToBeAdmin;
    }

    public void setCheckedToBeAdmin(Boolean checkedToBeAdmin) {
        isCheckedToBeAdmin = checkedToBeAdmin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isImageChanged() {
        return imageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        this.imageChanged = imageChanged;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    @Override
    public String toString() {
        return "GroupUserModel{" +
                "userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", alias='" + alias + '\'' +
                ", role='" + role + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                ", isCreator='" + isCreator + '\'' +
                ", notify='" + notify + '\'' +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                ", isCheckedToBeAdmin=" + isCheckedToBeAdmin +
                '}';
    }
}
