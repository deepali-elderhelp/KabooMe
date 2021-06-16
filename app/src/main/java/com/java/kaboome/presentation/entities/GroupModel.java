package com.java.kaboome.presentation.entities;

import com.java.kaboome.constants.UserGroupStatusConstants;

import java.io.Serializable;
import java.util.List;

//Why is this A Serializable?
public class GroupModel implements Serializable {

    String groupId;

    String groupName;

    int numberOfMembers;

    String groupDescription;

    String createdByAlias;

    String creatorRole;

    Long expiryDate;

    String notifications;

    Boolean openToRequests;

//    Boolean isCurrentUserAdmin;

    Boolean isGroupPrivate;

    UserGroupStatusConstants currentUserGroupStatus;

    Long imageUpdateTimestamp; //group's image's time stamp

    List<GroupUserModel> admins;

    List<GroupUserModel> regularMembers;

    //Following fields is needed if the user changes group profile pic

    String imagePath;
    String thumbnailPath;
    boolean imageChanged;

    public GroupModel() {
    }


//    public GroupModel(String groupId, String groupName, int numberOfMembers, String groupDescription, String createdByAlias, String creatorRole, Long expiryDate, String notifications, Boolean openToRequests, Boolean isCurrentUserAdmin, Boolean isGroupPrivate, UserGroupStatusConstants currentUserGroupStatus, Long imageUpdateTimestamp, List<GroupUserModel> admins, List<GroupUserModel> regularMembers) {
    public GroupModel(String groupId, String groupName, int numberOfMembers, String groupDescription, String createdByAlias, String creatorRole, Long expiryDate, String notifications, Boolean openToRequests,  Boolean isGroupPrivate, UserGroupStatusConstants currentUserGroupStatus, Long imageUpdateTimestamp, List<GroupUserModel> admins, List<GroupUserModel> regularMembers) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.numberOfMembers = numberOfMembers;
        this.groupDescription = groupDescription;
        this.createdByAlias = createdByAlias;
        this.creatorRole = creatorRole;
        this.expiryDate = expiryDate;
        this.notifications = notifications;
        this.openToRequests = openToRequests;
        this.isGroupPrivate = isGroupPrivate;
        this.currentUserGroupStatus = currentUserGroupStatus;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
        this.admins = admins;
        this.regularMembers = regularMembers;
    }

    public GroupModel(String groupId) {
        this.groupId = groupId;
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

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getCreatedByAlias() {
        return createdByAlias;
    }

    public void setCreatedByAlias(String createdByAlias) {
        this.createdByAlias = createdByAlias;
    }

    public String getCreatorRole() {
        return creatorRole;
    }

    public void setCreatorRole(String creatorRole) {
        this.creatorRole = creatorRole;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    public Boolean getOpenToRequests() {
        return openToRequests;
    }

    public void setOpenToRequests(Boolean openToRequests) {
        this.openToRequests = openToRequests;
    }

//    public Boolean getCurrentUserAdmin() {
//        return isCurrentUserAdmin;
//    }
//
//    public void setCurrentUserAdmin(Boolean currentUserAdmin) {
//        isCurrentUserAdmin = currentUserAdmin;
//    }

    public int getNumberOfAdmins() {
        return admins != null ? admins.size() : 0;
    }


    public List<GroupUserModel> getAdmins() {
        return admins;
    }

    public void setAdmins(List<GroupUserModel> admins) {
        this.admins = admins;
    }

    public int getNumberOfRegularMembers() {
        return regularMembers != null ? regularMembers.size() : 0;
    }


    public List<GroupUserModel> getRegularMembers() {
        return regularMembers;
    }

    public void setRegularMembers(List<GroupUserModel> regularMembers) {
        this.regularMembers = regularMembers;
    }

    public UserGroupStatusConstants getCurrentUserGroupStatus() {
        return currentUserGroupStatus;
    }

    public void setCurrentUserGroupStatus(UserGroupStatusConstants currentUserGroupStatus) {
        this.currentUserGroupStatus = currentUserGroupStatus;
    }

    public Boolean getGroupPrivate() {
        return isGroupPrivate;
    }

    public void setGroupPrivate(Boolean groupPrivate) {
        isGroupPrivate = groupPrivate;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public GroupUserModel getGroupUserById(String groupUserId){
        if(admins != null){
            for(GroupUserModel admin: admins){
                if(admin.getUserId().equals(groupUserId)){
                    return admin;
                }
            }
        }
        if(regularMembers != null){
            for(GroupUserModel regularMember: regularMembers){
                if(regularMember.getUserId().equals(groupUserId)){
                    return regularMember;
                }
            }
        }
        return null;
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

    public boolean isImageChanged() {
        return imageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        this.imageChanged = imageChanged;
    }

    @Override
    public String toString() {
        return "GroupModel{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", numberOfMembers=" + numberOfMembers +
                ", groupDescription='" + groupDescription + '\'' +
                ", createdByAlias='" + createdByAlias + '\'' +
                ", creatorRole='" + creatorRole + '\'' +
                ", expiryDate=" + expiryDate +
                ", notifications='" + notifications + '\'' +
                ", openToRequests=" + openToRequests +
//                ", isCurrentUserAdmin=" + isCurrentUserAdmin +
                ", isGroupPrivate=" + isGroupPrivate +
                ", currentUserGroupStatus=" + currentUserGroupStatus +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                ", admins=" + admins +
                ", regularMembers=" + regularMembers +
                '}';
    }
}
