package com.java.kaboome.data.entities;

/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "user")
public class User implements Serializable {

    @PrimaryKey
    @NonNull
    @SerializedName("userId")
    private String userId;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("dateUserCreated")
    private String dateUserCreated;

    @SerializedName("userName")
    private String userName;

    @SerializedName("deviceId")
    private String deviceId;

    @SerializedName("email")
    private String email;

    @SerializedName("imageUpdateTimestamp")
    private Long imageUpdateTimestamp;


    public User() {

    }

    public User(@NonNull String userId, String phoneNumber, String dateUserCreated, String userName, String deviceId, String email) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.dateUserCreated = dateUserCreated;
        this.userName = userName;
        this.deviceId = deviceId;
        this.email = email;
    }

    public User(@NonNull String userId, String phoneNumber, String dateUserCreated, String userName, String deviceId, String email, Long imageUpdateTimestamp) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.dateUserCreated = dateUserCreated;
        this.userName = userName;
        this.deviceId = deviceId;
        this.email = email;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public User(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateUserCreated() {
        return dateUserCreated;
    }

    public void setDateUserCreated(String dateUserCreated) {
        this.dateUserCreated = dateUserCreated;
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
        return "User{" +
                "userId='" + userId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateUserCreated='" + dateUserCreated + '\'' +
                ", userName='" + userName + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", email='" + email + '\'' +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                '}';
    }
}

