/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.entities;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static androidx.room.ForeignKey.CASCADE;

/**
 * This class represents a Request in the UserGroup table.
 * Every UserGroup has a GroupRequest list
 * It contains info about requests made to the user group, made by whom, on what
 * date, what request message etc.
 *
 * EDIT:- Not adding any foreign key relation ship - not sure about Room DB
 * foreign key constraints with multiple columns standing as a unique key
 *
 * What is means is that if the user deletes a Group, we will have to make sure
 * that the "Request" cache for that group is deleted as part of cleanup by us.
 */

@Entity(tableName = "group_requests",
        primaryKeys = {"groupId", "userId"})
public class GroupRequest implements Serializable {

    private static final String TAG = "KMGroupRequests";

    @NonNull
    @SerializedName("groupId")
    private String groupId;

    @NonNull
    @SerializedName("userId")
    private String userId;

    @SerializedName("userAlias")
    private String userAlias;

    @SerializedName("userRole")
    private String userRole;

    @SerializedName("dateRequestMade")
    private Long dateRequestMade;

    @SerializedName("requestMessage")
    private String requestMessage;

    @SerializedName("imageUpdateTimestamp")
    private Long imageUpdateTimestamp; //users's image's time stamp

    public GroupRequest() {
    }

    public GroupRequest(@NonNull String groupId, @NonNull String userId, String userAlias, String userRole, Long dateRequestMade, String requestMessage, Long imageUpdateTimestamp) {
        this.groupId = groupId;
        this.userId = userId;
        this.userAlias = userAlias;
        this.userRole = userRole;
        this.dateRequestMade = dateRequestMade;
        this.requestMessage = requestMessage;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }


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

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Long getDateRequestMade() {
        return dateRequestMade;
    }

    public void setDateRequestMade(Long dateRequestMade) {
        this.dateRequestMade = dateRequestMade;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof GroupRequest)) {
            return false;
        }
        GroupRequest groupUser = (GroupRequest) o;
        if(groupId.equals(groupUser.getGroupId()) && userId.equals(groupUser.getUserId())){
            return true;
        }
        else{
            return false;
        }

    }

    @Override
    public int hashCode() {
        Log.d(TAG, "Group Id - "+groupId+" User Id - "+userId+" and hashCode - "+(groupId.hashCode() * userId.hashCode()));
        return (groupId.hashCode() * userId.hashCode());
    }

    @Override
    public String toString() {
        return "GroupRequest{" +
                "groupId='" + groupId + '\'' +
                ", userId='" + userId + '\'' +
                ", userAlias='" + userAlias + '\'' +
                ", userRole='" + userRole + '\'' +
                ", dateRequestMade=" + dateRequestMade +
                ", requestMessage='" + requestMessage + '\'' +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                '}';
    }
}

