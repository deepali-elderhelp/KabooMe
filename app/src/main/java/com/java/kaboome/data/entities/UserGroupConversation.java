/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;
import com.java.kaboome.presentation.entities.UserGroupModel;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

/**
 * This class represents a Conversation in the Group.
 * Every Group had a Conversations list
 * It contains info about personal conversation between two group members,
 */
//@Entity(tableName = "group_users", primaryKeys = {"groupId", "userId"})
@Entity(tableName = "user_group_conversation", foreignKeys = @ForeignKey(entity = UserGroup.class,
        parentColumns = {"groupId", "userId"},
        childColumns = {"groupId", "userId"},
        onDelete = CASCADE),
        primaryKeys = {"groupId", "otherUserId"})
public class UserGroupConversation implements Serializable {

    private static final String TAG = "KMUserGroupConv";

    @NonNull
    @SerializedName("groupId")
    private String groupId;

    @NonNull
    @SerializedName("userId")
    private String userId;

    @NonNull
    @SerializedName("otherUserId")
    private String otherUserId;

    @SerializedName("otherUserName")
    private String otherUserName;

    @SerializedName("otherUserRole")
    private String otherUserRole;

    @SerializedName("isOtherUserAdmin")
    private String isOtherUserAdmin;

    @SerializedName("isDeleted")
    private Boolean isDeleted = false;

    @SerializedName("imageUpdateTimestamp")
    private Long imageUpdateTimestamp; //the other users's image's time stamp

    @ColumnInfo(name = "lastAccessed")
    @SerializedName("lastAccessed")
    private Long lastAccessed;

    @ColumnInfo(name = "cacheClearTS")
    @SerializedName("cacheClearTS")
    private Long cacheClearTS;


    @NonNull
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(@NonNull String groupId) {
        this.groupId = groupId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }


    @NonNull
    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(@NonNull String otherUserId) {
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

