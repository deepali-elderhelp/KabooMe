package com.java.kaboome.data.remote.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequestCreateRequest implements Serializable {

    @SerializedName("userId")
    private String userId;

    @SerializedName("groupId")
    private String groupId;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("userRole")
    private String userRole;

    @SerializedName("userAlias")
    private String userAlias;

    @SerializedName("privateGroup")
    private String privateGroup;

    @SerializedName("requestMessage")
    private String requestMessage;

    @SerializedName("userImageTimestamp")
    private Long userImageTimestamp;

    public RequestCreateRequest(String userId, String groupId, String groupName, String userRole, String userAlias, String privateGroup, String requestMessage, Long userImageTimestamp) {
        this.userId = userId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.userRole = userRole;
        this.userAlias = userAlias;
        this.privateGroup = privateGroup;
        this.requestMessage = requestMessage;
        this.userImageTimestamp = userImageTimestamp;
    }

    public RequestCreateRequest() {

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

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(String privateGroup) {
        this.privateGroup = privateGroup;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public Long getUserImageTimestamp() {
        return userImageTimestamp;
    }

    public void setUserImageTimestamp(Long userImageTimestamp) {
        this.userImageTimestamp = userImageTimestamp;
    }

    @Override
    public String toString() {
        return "RequestCreateRequest{" +
                "userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", userRole='" + userRole + '\'' +
                ", userAlias='" + userAlias + '\'' +
                ", privateGroup='" + privateGroup + '\'' +
                ", requestMessage='" + requestMessage + '\'' +
                ", userImageTimestamp=" + userImageTimestamp +
                '}';
    }
}
