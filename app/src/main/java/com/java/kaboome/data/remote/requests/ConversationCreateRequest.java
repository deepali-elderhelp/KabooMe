package com.java.kaboome.data.remote.requests;

import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.helpers.AppConfigHelper;

import java.io.Serializable;

public class ConversationCreateRequest implements Serializable {

        @SerializedName("groupId")
        private String groupId;

        @SerializedName("userId")
        private String userId;

        @SerializedName("otherUserId")
        private String otherUserId;

        @SerializedName("otherUserName")
        private String otherUserName;

        @SerializedName("otherUserRole")
        private String otherUserRole;

        @SerializedName("isOtherUserAdmin")
        private Boolean isOtherUserAdmin;

        @SerializedName("imageUpdateTimestamp")
        private Long imageUpdateTimestamp;

        @SerializedName("thisUserName")
        private String thisUserName;

        @SerializedName("thisUserRole")
        private String thisUserRole;

        @SerializedName("isThisUserAdmin")
        private Boolean isThisUserAdmin;

        @SerializedName("thisUserImageUpdateTimestamp")
        private Long thisUserImageUpdateTimestamp;


    public ConversationCreateRequest(String groupId, String userId, String otherUserId, String otherUserName, String otherUserRole, Boolean isOtherUserAdmin, Long imageUpdateTimestamp, String thisUserName, String thisUserRole, Boolean isThisUserAdmin, Long thisUserImageUpdateTimestamp) {
        this.groupId = groupId;
        this.userId = userId;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserRole = otherUserRole;
        this.isOtherUserAdmin = isOtherUserAdmin;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
        this.thisUserName = thisUserName;
        this.thisUserRole = thisUserRole;
        this.isThisUserAdmin = isThisUserAdmin;
        this.thisUserImageUpdateTimestamp = thisUserImageUpdateTimestamp;
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

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getOtherUserRole() {
        return otherUserRole;
    }

    public void setOtherUserRole(String otherUserRole) {
        this.otherUserRole = otherUserRole;
    }

    public Boolean getOtherUserAdmin() {
        return isOtherUserAdmin;
    }

    public void setOtherUserAdmin(Boolean otherUserAdmin) {
        isOtherUserAdmin = otherUserAdmin;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public String getThisUserName() {
        return thisUserName;
    }

    public void setThisUserName(String thisUserName) {
        this.thisUserName = thisUserName;
    }

    public String getThisUserRole() {
        return thisUserRole;
    }

    public void setThisUserRole(String thisUserRole) {
        this.thisUserRole = thisUserRole;
    }

    public Boolean getThisUserAdmin() {
        return isThisUserAdmin;
    }

    public void setThisUserAdmin(Boolean thisUserAdmin) {
        isThisUserAdmin = thisUserAdmin;
    }

    public Long getThisUserImageUpdateTimestamp() {
        return thisUserImageUpdateTimestamp;
    }

    public void setThisUserImageUpdateTimestamp(Long thisUserImageUpdateTimestamp) {
        this.thisUserImageUpdateTimestamp = thisUserImageUpdateTimestamp;
    }

    @Override
    public String toString() {
        return "ConversationCreateRequest{" +
                "groupId='" + groupId + '\'' +
                ", userId='" + userId + '\'' +
                ", otherUserId='" + otherUserId + '\'' +
                ", otherUserName='" + otherUserName + '\'' +
                ", otherUserRole='" + otherUserRole + '\'' +
                ", isOtherUserAdmin=" + isOtherUserAdmin +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                ", thisUserName='" + thisUserName + '\'' +
                ", thisUserRole='" + thisUserRole + '\'' +
                ", isThisUserAdmin=" + isThisUserAdmin +
                ", thisUserImageUpdateTimestamp=" + thisUserImageUpdateTimestamp +
                '}';
    }
}
