package com.java.kaboome.data.remote.requests;

import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.User;

import java.io.Serializable;

public class UserUpdateRequest implements Serializable {

    @SerializedName("userId")
    private String userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("email")
    private String email;

    @SerializedName("imageUpdateTimestamp")
    private Long imageUpdateTimestamp;


    public UserUpdateRequest(User user,Long imageUpdateTimestamp ) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.deviceId = user.getDeviceId();
        this.email = user.getEmail();
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", email='" + email + '\'' +
                ", imageUpdateTimestamp='" + imageUpdateTimestamp + '\'' +
                '}';
    }
}
