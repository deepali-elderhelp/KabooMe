/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.java.kaboome.constants.UserGroupStatusConstants;

import java.io.Serializable;
import java.util.ArrayList;

@Entity(tableName = "groups")
public class Group implements Serializable {

    @NonNull
    @PrimaryKey
    @SerializedName("groupId")
    private String groupId;

    @SerializedName("groupName")
    private String groupName;

    @SerializedName("groupDescription")
    private String groupDescription;

    @SerializedName("createdBy")
    private String createdBy;

    @SerializedName("createdByAlias")
    private String createdByAlias;

    @SerializedName("groupCreatorRole")
    private String groupCreatorRole;

    @SerializedName("createdOn")
    private String createdOn;

    @SerializedName("expiry")
    private Long expiry;

    //this indicates the notofication level of the creator of the group
    //TODO: get rid of this field - right now needed when group is created, this is set
    @SerializedName("notify")
    private String notify;

    @SerializedName("privateGroup")
    private Boolean privateGroup;

    @SerializedName("unicastGroup")
    private Boolean unicastGroup;

    @SerializedName("openToRequests")
    private Boolean openToRequests;

    @SerializedName("isDeleted")
    private Boolean isDeleted;

    @SerializedName("imageUpdateTimestamp")
    private Long imageUpdateTimestamp; //group's image's time stamp

    //only needed for the DAO, does not need to be carried to Domain and View layer
    @SerializedName("lowerCase")
    private String lowerCase; //group's name stripped off spaces

    @Ignore
    @SerializedName("usersJoined")
    private ArrayList<GroupUser> usersJoined;


    //Temporary field does not get updated to the server
    @Ignore
    private String tempGroupProfilePicLink;

    //Temporary field does not get updated to the server
    @Ignore
    private String tempGroupTNPicLink;

//    @ColumnInfo(name = "currentUserStatusForGroup")
    @SerializedName("currentUserStatusForGroup")
    private UserGroupStatusConstants currentUserStatusForGroup;

    public Group(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public Group(String groupId, String groupName, String groupDescription, String createdBy, String createdByAlias) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdBy = createdBy;
        this.createdByAlias = createdByAlias;
    }

    public Group(String groupId, String groupName, String groupDescription, String createdBy, String createdByAlias, String createdOn) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdBy = createdBy;
        this.createdByAlias = createdByAlias;
        this.createdOn = createdOn;

    }

    public Group(String groupId, String groupName, String groupDescription, String createdBy, String createdByAlias, String createdOn, long expiry) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdBy = createdBy;
        this.createdByAlias = createdByAlias;
        this.createdOn = createdOn;
        this.expiry = expiry;
    }

    public Group(String groupId, String groupName, String groupDescription, String createdBy, String createdByAlias, String createdOn, long expiry, String notify) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdBy = createdBy;
        this.createdByAlias = createdByAlias;
        this.createdOn = createdOn;
        this.expiry = expiry;
        this.notify = notify;
    }


    public Group(@NonNull String groupId, String groupName, String groupDescription, String createdBy, String createdByAlias, String groupCreatorRole, String createdOn, Long expiry, String notify, Boolean privateGroup, ArrayList<GroupUser> usersJoined) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdBy = createdBy;
        this.createdByAlias = createdByAlias;
        this.groupCreatorRole = groupCreatorRole;
        this.createdOn = createdOn;
        this.expiry = expiry;
        this.notify = notify;
        this.privateGroup = privateGroup;
        this.usersJoined = usersJoined;
    }

    public Group(@NonNull String groupId, String groupName, String groupDescription, String createdBy, String createdByAlias, String groupCreatorRole, String createdOn, Long expiry, String notify, Boolean privateGroup, Boolean isDeleted, ArrayList<GroupUser> usersJoined) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdBy = createdBy;
        this.createdByAlias = createdByAlias;
        this.groupCreatorRole = groupCreatorRole;
        this.createdOn = createdOn;
        this.expiry = expiry;
        this.notify = notify;
        this.privateGroup = privateGroup;
        this.isDeleted = isDeleted;
        this.usersJoined = usersJoined;
    }

    public Group(@NonNull String groupId, String groupName, String groupDescription, String createdBy, String createdByAlias, String groupCreatorRole, String createdOn, Long expiry, String notify, Boolean privateGroup, Boolean isDeleted, Long imageUpdateTimestamp, ArrayList<GroupUser> usersJoined) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.createdBy = createdBy;
        this.createdByAlias = createdByAlias;
        this.groupCreatorRole = groupCreatorRole;
        this.createdOn = createdOn;
        this.expiry = expiry;
        this.notify = notify;
        this.privateGroup = privateGroup;
        this.isDeleted = isDeleted;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
        this.usersJoined = usersJoined;
    }

    public Group() {
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }


    @Override
    public int hashCode() {

        //return (id+""+authorEmailId+todoString+place).hashCode();
        return groupId.hashCode();
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByAlias() {
        return createdByAlias;
    }

    public void setCreatedByAlias(String createdByAlias) {
        this.createdByAlias = createdByAlias;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Long getExpiry() {
        return expiry;
    }

    public void setExpiry(Long expiry) {
        this.expiry = expiry;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public Boolean isPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(Boolean privateGroup) {
        this.privateGroup = privateGroup;
    }

    public Boolean getUnicastGroup() {
        return unicastGroup;
    }

    public void setUnicastGroup(Boolean unicastGroup) {
        this.unicastGroup = unicastGroup;
    }

    public ArrayList<GroupUser> getUsersJoined() {
        return usersJoined;
    }

    public void setUsersJoined(ArrayList<GroupUser> usersJoined) {
        this.usersJoined = usersJoined;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getTempGroupProfilePicLink() {
        return tempGroupProfilePicLink;
    }

    public void setTempGroupProfilePicLink(String tempGroupProfilePicLink) {
        this.tempGroupProfilePicLink = tempGroupProfilePicLink;
    }

    public String getTempGroupTNPicLink() {
        return tempGroupTNPicLink;
    }

    public void setTempGroupTNPicLink(String tempGroupTNPicLink) {
        this.tempGroupTNPicLink = tempGroupTNPicLink;
    }

    public String getGroupCreatorRole() {
        return groupCreatorRole;
    }

    public void setGroupCreatorRole(String groupCreatorRole) {
        this.groupCreatorRole = groupCreatorRole;
    }

    public UserGroupStatusConstants getCurrentUserStatusForGroup() {
        return currentUserStatusForGroup;
    }

    public void setCurrentUserStatusForGroup(UserGroupStatusConstants currentUserStatusForGroup) {
        this.currentUserStatusForGroup = currentUserStatusForGroup;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public Boolean getOpenToRequests() {
        return openToRequests;
    }

    public void setOpenToRequests(Boolean openToRequests) {
        this.openToRequests = openToRequests;
    }

    public String getLowerCase() {
        return lowerCase;
    }

    public void setLowerCase(String lowerCase) {
        this.lowerCase = lowerCase;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupDescription='" + groupDescription + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdByAlias='" + createdByAlias + '\'' +
                ", groupCreatorRole='" + groupCreatorRole + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", expiry=" + expiry +
                ", notify='" + notify + '\'' +
                ", privateGroup=" + privateGroup +
                ", isDeleted=" + isDeleted +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                ", usersJoined=" + usersJoined +
                ", tempGroupProfilePicLink='" + tempGroupProfilePicLink + '\'' +
                ", tempGroupTNPicLink='" + tempGroupTNPicLink + '\'' +
                ", currentUserStatusForGroup=" + currentUserStatusForGroup +
                '}';
    }
}
