/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/*
This class represents the Groups joined by the user,
 */

@Entity(tableName = "user_groups", primaryKeys = {"groupId", "userId" })
public class UserGroup implements Serializable {


    @NonNull
    @SerializedName("userId")
    private String userId;


    @NonNull
    @SerializedName("groupId")
    private String groupId;


    @ColumnInfo(name = "groupName")
    @SerializedName("groupName")
    private String groupName;


    @ColumnInfo(name = "dateJoined")
    @SerializedName("dateJoined")
    private Long dateJoined;

    @ColumnInfo(name = "isAdmin")
    @SerializedName("isAdmin")
    private String isAdmin;

    @ColumnInfo(name = "isCreator")
    @SerializedName("isCreator")
    private String isCreator;

    @ColumnInfo(name = "alias")
    @SerializedName("alias")
    private String alias;

    @ColumnInfo(name = "groupAdminRole")
    @SerializedName("groupAdminRole")
    private String groupAdminRole;

    @ColumnInfo(name = "expiry")
    @SerializedName("expiry")
    private Long expiry;

    @ColumnInfo(name = "lastAccessed")
    @SerializedName("lastAccessed")
    private Long lastAccessed;

    @ColumnInfo(name = "adminsLastAccessed")
    @SerializedName("adminsLastAccessed")
    private Long adminsLastAccessed;


    @ColumnInfo(name = "lastHigh")
    @SerializedName("lastHigh")
    private long lastHigh;

    @ColumnInfo(name = "lastRegular")
    @SerializedName("lastRegular")
    private long lastRegular;

    @ColumnInfo(name = "notify")
    @SerializedName("notify")
    private String notify;

    @ColumnInfo(name = "privateGroup")
    @SerializedName("privateGroup")
    private Boolean privateGroup;

    @ColumnInfo(name = "unicastGroup")
    @SerializedName("unicastGroup")
    private Boolean unicastGroup;

    @ColumnInfo(name = "isDeleted")
    @SerializedName("isDeleted")
    private Boolean isDeleted = false;

    @ColumnInfo(name = "deviceId")
    @SerializedName("deviceId")
    private String deviceId;

    @ColumnInfo(name = "imageUpdateTimestamp")
    @SerializedName("imageUpdateTimestamp")
    private Long imageUpdateTimestamp;

    @ColumnInfo(name = "userImageUpdateTimestamp")
    @SerializedName("userImageUpdateTimestamp")
    private Long userImageUpdateTimestamp;

    @ColumnInfo(name = "cacheClearTS")
    @SerializedName("cacheClearTS")
    private Long cacheClearTS;

    @ColumnInfo(name = "adminsCacheClearTS")
    @SerializedName("adminsCacheClearTS")
    private Long adminsCacheClearTS;

    @SerializedName("groupPicUploaded")
    private Boolean groupPicUploaded;

    @SerializedName("groupPicLoadingGoingOn")
    private Boolean groupPicLoadingGoingOn = false;


//    @ColumnInfo(name = "numberOfRequests")
//    @SerializedName("numberOfRequests")
//    private int numberOfRequests;


    //removed the following - handling it different way
    //in UGLVM, call a background thread to get this data
    //directly from Message DAO cache
//    //this is added so that the groups list when gets the cached groups list
//    //(status = Loading or Success)
//    //it also adds the last time stamp with it for sorting order
//    //so by default the first list look on the GLF is sorted
//    //then the live data listeners for new messges etc. is added
//    @ColumnInfo(name = "lastMessageCacheTS")
//    private transient Long lastMessageCacheTS;

    @Ignore
    @SerializedName("requests")
    private ArrayList<GroupRequest> requests;

    @Ignore
    @SerializedName("conversations")
    private ArrayList<UserGroupConversation> conversations;


    public UserGroup(){ }

    @Ignore
    public UserGroup(@NonNull String userId, @NonNull String groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public UserGroup(@NonNull String userId, @NonNull String groupId, String groupName, Long dateJoined, String isAdmin, String isCreator, String alias, String groupAdminRole, Long expiry, long lastAccessed, long lastHigh, long lastRegular, String notify, Boolean privateGroup, Boolean isDeleted, String deviceId, Long imageUpdateTimestamp, ArrayList<GroupRequest> requests) {
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
        this.requests = requests;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public String getIsCreator() { return isCreator; }

    public void setIsCreator(String isCreator) { this.isCreator = isCreator; }

    public Long getExpiry() { return expiry; }

    public void setExpiry(Long expiry) { this.expiry = expiry; }

    public String getGroupAdminRole() {
        return groupAdminRole;
    }

    public void setGroupAdminRole(String groupAdminRole) {
        this.groupAdminRole = groupAdminRole;
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

    public Boolean getGroupPicUploaded() {
        return groupPicUploaded;
    }

    public void setGroupPicUploaded(Boolean groupPicUploaded) {
        this.groupPicUploaded = groupPicUploaded;
    }

    public Boolean getGroupPicLoadingGoingOn() {
        return groupPicLoadingGoingOn;
    }

    public void setGroupPicLoadingGoingOn(Boolean groupPicLoadingGoingOn) {
        this.groupPicLoadingGoingOn = groupPicLoadingGoingOn;
    }

    //    public int getNumberOfRequests() {
//        return numberOfRequests;
//    }
//
//    public void setNumberOfRequests(int numberOfRequests) {
//        this.numberOfRequests = numberOfRequests;
//    }


//    public Long getLastMessageCacheTS() {
//        return lastMessageCacheTS;
//    }
//
//    public void setLastMessageCacheTS(Long lastMessageCacheTS) {
//        this.lastMessageCacheTS = lastMessageCacheTS;
//    }

    public ArrayList<GroupRequest> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<GroupRequest> requests) {
        this.requests = requests;
    }

    public ArrayList<UserGroupConversation> getConversations() {
        return conversations;
    }

    public void setConversations(ArrayList<UserGroupConversation> conversations) {
        this.conversations = conversations;
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

    @Override
    public String toString() {
        return "UserGroup{" +
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
                ", requests=" + requests +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserGroup userGroup = (UserGroup) o;
        return userId.equals(userGroup.userId) &&
                groupId.equals(userGroup.groupId);
    }

    @Override
    public int hashCode() {
        return (userId.hashCode() * groupId.hashCode());
    }
}


