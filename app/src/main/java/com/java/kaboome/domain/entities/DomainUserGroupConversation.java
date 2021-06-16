package com.java.kaboome.domain.entities;


import java.io.Serializable;

public class DomainUserGroupConversation implements Serializable {


    private String groupId;

    private String userId;

    private String otherUserId;

    private String otherUserName;

    private String otherUserRole;

    private String isOtherUserAdmin;

    private Boolean isDeleted = false;

    private Long imageUpdateTimestamp; //the other users's image's time stamp

    private Long lastAccessed;

    private Long cacheClearTS;

    public DomainUserGroupConversation() {
    }

    public DomainUserGroupConversation(String groupId, String userId, String otherUserId, String otherUserName, String otherUserRole, String isOtherUserAdmin, Boolean isDeleted, Long imageUpdateTimestamp, Long lastAccessed, Long cacheClearTS) {
        this.groupId = groupId;
        this.userId = userId;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserRole = otherUserRole;
        this.isOtherUserAdmin = isOtherUserAdmin;
        this.isDeleted = isDeleted;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
        this.lastAccessed = lastAccessed;
        this.cacheClearTS = cacheClearTS;
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

    public String getIsOtherUserAdmin() {
        return isOtherUserAdmin;
    }

    public void setIsOtherUserAdmin(String isOtherUserAdmin) {
        this.isOtherUserAdmin = isOtherUserAdmin;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public Long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Long getCacheClearTS() {
        return cacheClearTS;
    }

    public void setCacheClearTS(Long cacheClearTS) {
        this.cacheClearTS = cacheClearTS;
    }
}
