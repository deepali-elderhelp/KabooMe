package com.java.kaboome.data.remote.requests;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequestDeleteRequest implements Serializable {

    @SerializedName("requestUserId")
    private String requestUserId;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("userRole")
    private String userRole;

    @SerializedName("userAlias")
    private String userAlias;

    @SerializedName("privateGroup")
    private String privateGroup;

    @SerializedName("acceptUser")
    private boolean acceptUser;

    @SerializedName("imageUpdateTimestamp")
    private Long imageUpdateTimestamp; //users's image's time stamp


//    @SerializedName("userImageTimestamp")
//    private Long userImageTimestamp;

    //deviceId


    public RequestDeleteRequest() {

    }

    public RequestDeleteRequest(String requestUserId, String groupName, String userRole, String userAlias, String privateGroup, boolean acceptUser, Long imageUpdateTimestamp) {
        this.requestUserId = requestUserId;
        this.groupName = groupName;
        this.userRole = userRole;
        this.userAlias = userAlias;
        this.privateGroup = privateGroup;
        this.acceptUser = acceptUser;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public String getRequestUserId() {
        return requestUserId;
    }

    public void setRequestUserId(String requestUserId) {
        this.requestUserId = requestUserId;
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

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public boolean isAcceptUser() {
        return acceptUser;
    }

    public void setAcceptUser(boolean acceptUser) {
        this.acceptUser = acceptUser;
    }

    @Override
    public String toString() {
        return "RequestDeleteRequest{" +
                "requestUserId='" + requestUserId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", userRole='" + userRole + '\'' +
                ", userAlias='" + userAlias + '\'' +
                ", privateGroup='" + privateGroup + '\'' +
                ", acceptUser=" + acceptUser +
                '}';
    }
}
