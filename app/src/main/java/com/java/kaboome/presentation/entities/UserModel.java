package com.java.kaboome.presentation.entities;


import java.io.Serializable;

public class UserModel implements Serializable {

    String userId;

    String userName;

    String email;

    String phoneNumber;

    String status; //for showing status/state on UI

    Long imageUpdateTimestamp;
    private Boolean userPicUploaded;
    private Boolean userPicLoadingGoingOn = false;

    //Following fields is needed if the user changes profile pic

    String imagePath;
    String thumbnailPath;
    boolean imageChanged;




    public UserModel() {
    }

    public UserModel(String userId, String userName, String email, String phoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public UserModel(String userId, String userName, String email, String phoneNumber, String status, Long imageUpdateTimestamp) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isImageChanged() {
        return imageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        this.imageChanged = imageChanged;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public Boolean getUserPicUploaded() {
        return userPicUploaded;
    }

    public void setUserPicUploaded(Boolean userPicUploaded) {
        this.userPicUploaded = userPicUploaded;
    }

    public Boolean getUserPicLoadingGoingOn() {
        return userPicLoadingGoingOn;
    }

    public void setUserPicLoadingGoingOn(Boolean userPicLoadingGoingOn) {
        this.userPicLoadingGoingOn = userPicLoadingGoingOn;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status='" + status + '\'' +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                ", imagePath='" + imagePath + '\'' +
                ", imageChanged=" + imageChanged +
                '}';
    }
}
