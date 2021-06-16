package com.java.kaboome.domain.entities;

public class DomainUser {

    private String userId;
    private String phoneNumber;
    private String dateUserCreated;
    private String userName;
    private String deviceId;
    private String email;
    private Long imageUpdateTimestamp;

    public DomainUser() {
    }

    public DomainUser(String userId, String phoneNumber, String dateUserCreated, String userName, String deviceId, String email, String imagePath, Long imageUpdateTimestamp) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.dateUserCreated = dateUserCreated;
        this.userName = userName;
        this.deviceId = deviceId;
        this.email = email;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public DomainUser(String userId) {
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
        return "DomainUser{" +
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
