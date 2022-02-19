/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.entities;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

/**
 * This class represents a User in the Group table.
 * Every Group had a UsersJoined list
 * It contains info about when the user joined the group,
 * what is his notification level, whether he is admin or not etc.
 */
//@Entity(tableName = "group_users", primaryKeys = {"groupId", "userId"})
    //TODO: remove the foreign relationship, fails in the case where you delete the cache
    //TODO: come back, do not go to the Group, but just go to the conv-new-getUsers - it fails the constraint
@Entity(tableName = "group_users", foreignKeys = @ForeignKey(entity = Group.class,
        parentColumns = "groupId",
        childColumns = "groupId",
        onDelete = CASCADE),
        primaryKeys = {"groupId", "userId"})
public class GroupUser implements Serializable {

    private static final String TAG = "KMGroupUser";

    @NonNull
    @SerializedName("groupId")
    private String groupId;

    @NonNull
    @SerializedName("userId")
    private String userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("role")
    private String role;

    @SerializedName("dateJoined")
    private Long dateJoined;

    @SerializedName("notify")
    private String notify;

    @SerializedName("isAdmin")
    private String isAdmin;

    @SerializedName("isCreator")
    private String isCreator;

    @SerializedName("isDeleted")
    private Boolean isDeleted = false;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("imageUpdateTimestamp")
    private Long imageUpdateTimestamp; //group users's image's time stamp

    @SerializedName("groupUserPicUploaded")
    private Boolean groupUserPicUploaded;

    @SerializedName("groupUserPicLoadingGoingOn")
    private Boolean groupUserPicLoadingGoingOn = false;

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

    public Long getDateJoined() { return dateJoined; }

    public void setDateJoined(Long dateJoined) { this.dateJoined = dateJoined; }

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getIsCreator() { return isCreator; }

    public void setIsCreator(String isCreator) { this.isCreator = isCreator; }

    @NonNull
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(@NonNull String groupId) {
        this.groupId = groupId;
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

    public GroupUser() {
    }

    @Ignore
    public GroupUser(@NonNull String userId, @NonNull String groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    public GroupUser(@NonNull String groupId, @NonNull String userId, String userName, String role, Long dateJoined, String notify, String isAdmin, String isCreator, String deviceId, Long imageUpdateTimestamp) {
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

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof GroupUser)) {
            return false;
        }
        GroupUser groupUser = (GroupUser) o;
        if(groupId.equals(groupUser.getGroupId()) && userId.equals(groupUser.getUserId())){
            return true;
        }
        else{
            return false;
        }

    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public int hashCode() {
        Log.d(TAG, "Group Id - "+groupId+" User Id - "+userId+" and hashCode - "+(groupId.hashCode() * userId.hashCode()));
        return (groupId.hashCode() * userId.hashCode());
    }
}

