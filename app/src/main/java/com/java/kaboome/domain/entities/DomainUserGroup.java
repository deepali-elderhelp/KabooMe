package com.java.kaboome.domain.entities;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * Domain User Group POJO
 */

public class DomainUserGroup implements Serializable {

    private String userId;

    private String groupId;

    private String groupName;

    private Long dateJoined;

    private String isAdmin;

    private String isCreator;

    private String alias;

    private String groupAdminRole;

    private Long expiry;

    private Long lastAccessed;

    private Long adminsLastAccessed;

    private long lastHigh;

    private long lastRegular;

    private String notify;

    private Boolean privateGroup;

    private Boolean unicastGroup;

    private Boolean isDeleted = false;

    private String deviceId;

    private Long imageUpdateTimestamp;

    private Long userImageUpdateTimestamp;

//    private Long lastMessageCacheTS;

//    private Long cacheClearTS;
    private Long cacheClearTS;

    private Long adminsCacheClearTS;

//    private int numberOfRequests;

    public DomainUserGroup() {
    }

    public DomainUserGroup(String userId, String groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public DomainUserGroup(String userId, String groupId, String groupName, Long dateJoined, String isAdmin, String isCreator, String alias, String groupAdminRole, Long expiry, long lastAccessed, long lastHigh, long lastRegular, String notify, Boolean privateGroup, Boolean isDeleted, String deviceId) {
        this.userId = userId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.dateJoined = dateJoined;
        this.isAdmin = isAdmin;
        this.isCreator = isCreator;
        this.alias = alias;
        this.groupAdminRole = groupAdminRole;
        this.expiry = expiry;
        this.lastAccessed = lastAccessed;
        this.lastHigh = lastHigh;
        this.lastRegular = lastRegular;
        this.notify = notify;
        this.privateGroup = privateGroup;
        this.isDeleted = isDeleted;
        this.deviceId = deviceId;
    }

//    public DomainUserGroup(String userId, String groupId, String groupName, Long dateJoined, String isAdmin, String isCreator, String alias, String groupAdminRole, Long expiry, long lastAccessed, long lastHigh, long lastRegular, String notify, Boolean privateGroup, Boolean isDeleted, String deviceId, Long imageUpdateTimestamp, int numberOfRequests) {
        public DomainUserGroup(String userId, String groupId, String groupName, Long dateJoined, String isAdmin, String isCreator, String alias, String groupAdminRole, Long expiry, long lastAccessed, long lastHigh, long lastRegular, String notify, Boolean privateGroup, Boolean isDeleted, String deviceId, Long imageUpdateTimestamp) {
        this.userId = userId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.dateJoined = dateJoined;
        this.isAdmin = isAdmin;
        this.isCreator = isCreator;
        this.alias = alias;
        this.groupAdminRole = groupAdminRole;
        this.expiry = expiry;
        this.lastAccessed = lastAccessed;
        this.lastHigh = lastHigh;
        this.lastRegular = lastRegular;
        this.notify = notify;
        this.privateGroup = privateGroup;
        this.isDeleted = isDeleted;
        this.deviceId = deviceId;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
//        this.numberOfRequests = numberOfRequests;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(Long dateJoined) {
        this.dateJoined = dateJoined;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getGroupAdminRole() {
        return groupAdminRole;
    }

    public void setGroupAdminRole(String groupAdminRole) {
        this.groupAdminRole = groupAdminRole;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public Long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public long getLastHigh() {
        return lastHigh;
    }

    public void setLastHigh(long lastHigh) {
        this.lastHigh = lastHigh;
    }

    public long getLastRegular() {
        return lastRegular;
    }

    public void setLastRegular(long lastRegular) {
        this.lastRegular = lastRegular;
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

    public Boolean getUnicastGroup() {
        return unicastGroup;
    }

    public void setUnicastGroup(Boolean unicastGroup) {
        this.unicastGroup = unicastGroup;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getAdminsLastAccessed() {
        return adminsLastAccessed;
    }

    public void setAdminsLastAccessed(Long adminsLastAccessed) {
        this.adminsLastAccessed = adminsLastAccessed;
    }

    public Long getAdminsCacheClearTS() {
        return adminsCacheClearTS;
    }

    public void setAdminsCacheClearTS(Long adminsCacheClearTS) {
        this.adminsCacheClearTS = adminsCacheClearTS;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public Long getUserImageUpdateTimestamp() {
        return userImageUpdateTimestamp;
    }

    public void setUserImageUpdateTimestamp(Long userImageUpdateTimestamp) {
        this.userImageUpdateTimestamp = userImageUpdateTimestamp;
    }

    public Long getCacheClearTS() {
        return cacheClearTS;
    }

    public void setCacheClearTS(Long cacheClearTS) {
        this.cacheClearTS = cacheClearTS;
    }


//    public int getNumberOfRequests() {
//        return numberOfRequests;
//    }
//
//    public void setNumberOfRequests(int numberOfRequests) {
//        this.numberOfRequests = numberOfRequests;
//    }


    @Override
    public String toString() {
        return "DomainUserGroup{" +
                "userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", dateJoined=" + dateJoined +
                ", isAdmin='" + isAdmin + '\'' +
                ", isCreator='" + isCreator + '\'' +
                ", alias='" + alias + '\'' +
                ", groupAdminRole='" + groupAdminRole + '\'' +
                ", expiry=" + expiry +
                ", lastAccessed=" + lastAccessed +
                ", lastHigh=" + lastHigh +
                ", lastRegular=" + lastRegular +
                ", notify='" + notify + '\'' +
                ", privateGroup=" + privateGroup +
                ", isDeleted=" + isDeleted +
                ", deviceId='" + deviceId + '\'' +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                ", userImageUpdateTimestamp=" + userImageUpdateTimestamp +
                ", cacheClearTS=" + cacheClearTS +
                '}';
    }

    @Override
    public int hashCode() {
        return (userId.hashCode() * groupId.hashCode());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainUserGroup userGroup = (DomainUserGroup) o;
        return userId.equals(userGroup.userId) &&
                groupId.equals(userGroup.groupId);
    }
}
