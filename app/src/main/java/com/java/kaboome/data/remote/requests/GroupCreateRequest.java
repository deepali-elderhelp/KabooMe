package com.java.kaboome.data.remote.requests;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.helpers.AppConfigHelper;

import java.io.Serializable;

public class GroupCreateRequest implements Serializable {

        @SerializedName("groupId")
        private String groupId;

        @SerializedName("groupName")
        private String groupName;

        @SerializedName("groupDescription")
        private String groupDescription;

        @SerializedName("createdBy")
        private String createdBy;

        @SerializedName("groupCreatorAlias")
        private String groupCreatorAlias;

        @SerializedName("groupCreatorRole")
        private String groupCreatorRole;

        @SerializedName("createdOn")
        private String createdOn;

        @SerializedName("expiry")
        private Long expiry;

        @SerializedName("notify")
        private String notify;

        @SerializedName("privateGroup")
        private Boolean privateGroup;

        @SerializedName("deviceId")
        private String deviceId;

        @SerializedName("unicastGroup")
        private Boolean unicastGroup;


    public GroupCreateRequest(Group group) {
        this.groupId = group.getGroupId();
        this.groupName = group.getGroupName();
        this.groupDescription = group.getGroupDescription();
        this.createdBy = group.getCreatedBy();
        this.groupCreatorAlias = group.getCreatedByAlias();
        this.groupCreatorRole = group.getGroupCreatorRole();
        this.createdOn = group.getCreatedOn();
        this.expiry = group.getExpiry();
        this.notify = group.getNotify();
        this.privateGroup = group.isPrivateGroup();
        this.unicastGroup = group.getUnicastGroup();
        this.deviceId = AppConfigHelper.getDeviceId();
    }

//    public GroupCreateRequest(String groupId, String groupName, String groupDescription, String createdBy, String groupCreatorAlias, String groupCreatorRole, String createdOn, Long expiry, String notify, Boolean privateGroup) {
//        this.groupId = groupId;
//        this.groupName = groupName;
//        this.groupDescription = groupDescription;
//        this.createdBy = createdBy;
//        this.groupCreatorAlias = groupCreatorAlias;
//        this.groupCreatorRole = groupCreatorRole;
//        this.createdOn = createdOn;
//        this.expiry = expiry;
//        this.notify = notify;
//        this.privateGroup = privateGroup;
//        this.deviceId = AppConfigHelper.getDeviceId();
//    }
//
//    public GroupCreateRequest(String groupId, String groupName, String groupDescription, String createdBy, String groupCreatorAlias, String groupCreatorRole, String createdOn, Long expiry, String notify, Boolean privateGroup, String deviceId) {
//        this.groupId = groupId;
//        this.groupName = groupName;
//        this.groupDescription = groupDescription;
//        this.createdBy = createdBy;
//        this.groupCreatorAlias = groupCreatorAlias;
//        this.groupCreatorRole = groupCreatorRole;
//        this.createdOn = createdOn;
//        this.expiry = expiry;
//        this.notify = notify;
//        this.privateGroup = privateGroup;
//        this.deviceId = deviceId;
//        this.deviceId = AppConfigHelper.getDeviceId();
//    }

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

    public String getGroupCreatorAlias() {
        return groupCreatorAlias;
    }

    public void setGroupCreatorAlias(String groupCreatorAlias) {
        this.groupCreatorAlias = groupCreatorAlias;
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

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public Boolean getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(Boolean privateGroup) {
        this.privateGroup = privateGroup;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getUnicastGroup() {
        return unicastGroup;
    }

    public void setUnicastGroup(Boolean unicastGroup) {
        this.unicastGroup = unicastGroup;
    }

    @Override
    public String toString() {
        return "GroupCreateRequest{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupDescription='" + groupDescription + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", groupCreatorAlias='" + groupCreatorAlias + '\'' +
                ", groupCreatorRole='" + groupCreatorRole + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", expiry=" + expiry +
                ", notify='" + notify + '\'' +
                ", privateGroup=" + privateGroup +
                ", deviceId='" + deviceId + '\'' +
                '}';
    }
}
