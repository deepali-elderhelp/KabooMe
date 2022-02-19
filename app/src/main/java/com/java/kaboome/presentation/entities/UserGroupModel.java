package com.java.kaboome.presentation.entities;

/*
Groups List POJO
 */

import android.util.Log;

import androidx.annotation.Nullable;

import com.java.kaboome.constants.ReceivedGroupDataTypeConstants;

import java.io.Serializable;
import java.lang.reflect.Field;

public class UserGroupModel implements Serializable {

    private static final String TAG = "KMUserGroupModel";

    String groupId;

    String groupName;

    String lastMessageText;

    String lastMessageSentBy;

    Long lastMessageSentAt;

    String lastAdminMessageText;

    String lastAdminMessageSentBy;

    Long lastAdminMessageSentAt;

    String groupImageLink;

    int unreadCount;

    int unreadPMCount;

    boolean highPriorityUnread;

    Long groupExpiry;

    private Long imageUpdateTimestamp; //this is to help glide determine if new image needs to be loaded from server
    //or cache is okay

    private Boolean groupPicUploaded;

    private Boolean groupPicLoadingGoingOn = false;

    private Long userImageUpdateTimestamp; //user image timestamp

    private Long cacheClearTS; //to keep track of last time the group messages were cleared - needed when user goes to the group messages

    private Long lastAccessed; //to keep track of last message seen TS : it is different than lastMessageSentAt

    private Long adminsCacheClearTS; //to keep track of last time the group messages were cleared - needed when user goes to the group messages

    private Long adminsLastAccessed; //to keep track of last message seen TS : it is different than lastMessageSentAt

    //following data needed to be transferred to message activity - not needed for the GroupsList page

    String alias;

    String role;

    String isAdmin;

    Boolean isPrivate;

    Boolean unicastGroup;

    int numberOfRequests;

    Long lastRequestSentAt;

    private Boolean isDeleted = false;

    ReceivedGroupDataTypeConstants receivedGroupDataType = ReceivedGroupDataTypeConstants.ALL_DATA;

    public UserGroupModel() {

    }

    public UserGroupModel(String groupId, String groupName, String lastMessageText, String lastMessageSentBy, Long lastMessageSentAt, String groupImageLink, int unreadCount, boolean highPriorityUnread, Long groupExpiry, Long imageUpdateTimestamp, String alias, String role, String isAdmin, Boolean isPrivate, int numberOfRequests, Boolean isDeleted, ReceivedGroupDataTypeConstants receivedGroupDataType) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.lastMessageText = lastMessageText;
        this.lastMessageSentBy = lastMessageSentBy;
        this.lastMessageSentAt = lastMessageSentAt;
        this.groupImageLink = groupImageLink;
        this.unreadCount = unreadCount;
        this.highPriorityUnread = highPriorityUnread;
        this.groupExpiry = groupExpiry;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
        this.alias = alias;
        this.role = role;
        this.isAdmin = isAdmin;
        this.isPrivate = isPrivate;
        this.numberOfRequests = numberOfRequests;
        this.isDeleted = isDeleted;
        this.receivedGroupDataType = receivedGroupDataType;
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

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public String getLastMessageSentBy() {
        return lastMessageSentBy;
    }

    public void setLastMessageSentBy(String lastMessageSentBy) {
        this.lastMessageSentBy = lastMessageSentBy;
    }

    public String getGroupImageLink() {
        return groupImageLink;
    }

    public void setGroupImageLink(String groupImageLink) {
        this.groupImageLink = groupImageLink;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public boolean isHighPriorityUnread() {
        return highPriorityUnread;
    }

    public void setHighPriorityUnread(boolean highPriorityUnread) {
        this.highPriorityUnread = highPriorityUnread;
    }

    public Long getGroupExpiry() {
        return groupExpiry;
    }

    public void setGroupExpiry(Long groupExpiry) {
        this.groupExpiry = groupExpiry;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Boolean getUnicastGroup() {
        return unicastGroup;
    }

    public void setUnicastGroup(Boolean unicastGroup) {
        this.unicastGroup = unicastGroup;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public Boolean getGroupPicUploaded() {
        return groupPicUploaded;
    }

    public void setGroupPicUploaded(Boolean groupPicUploaded) {
        this.groupPicUploaded = groupPicUploaded;
    }

    public Boolean getGroupPicLoadingGoingOn() {
        return groupPicLoadingGoingOn;
    }

    public void setGroupPicLoadingGoingOn(Boolean groupPicLoadingGoingOn) {
        this.groupPicLoadingGoingOn = groupPicLoadingGoingOn;
    }

    public Long getUserImageUpdateTimestamp() {
        return userImageUpdateTimestamp;
    }

    public void setUserImageUpdateTimestamp(Long userImageUpdateTimestamp) {
        this.userImageUpdateTimestamp = userImageUpdateTimestamp;
    }

    public Long getCacheClearTS() {
        return cacheClearTS;
    }

    public void setCacheClearTS(Long cacheClearTS) {
        this.cacheClearTS = cacheClearTS;
    }

    public Long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Long getAdminsCacheClearTS() {
        return adminsCacheClearTS;
    }

    public void setAdminsCacheClearTS(Long adminsCacheClearTS) {
        this.adminsCacheClearTS = adminsCacheClearTS;
    }

    public Long getAdminsLastAccessed() {
        return adminsLastAccessed;
    }

    public void setAdminsLastAccessed(Long adminsLastAccessed) {
        this.adminsLastAccessed = adminsLastAccessed;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public Long getLastRequestSentAt() {
        return lastRequestSentAt;
    }

    public void setLastRequestSentAt(Long lastRequestSentAt) {
        this.lastRequestSentAt = lastRequestSentAt;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Long getLastMessageSentAt() {
        return lastMessageSentAt;
    }

    public void setLastMessageSentAt(Long lastMessageSentAt) {
        this.lastMessageSentAt = lastMessageSentAt;
    }

    public String getLastAdminMessageText() {
        return lastAdminMessageText;
    }

    public void setLastAdminMessageText(String lastAdminMessageText) {
        this.lastAdminMessageText = lastAdminMessageText;
    }

    public String getLastAdminMessageSentBy() {
        return lastAdminMessageSentBy;
    }

    public void setLastAdminMessageSentBy(String lastAdminMessageSentBy) {
        this.lastAdminMessageSentBy = lastAdminMessageSentBy;
    }

    public Long getLastAdminMessageSentAt() {
        return lastAdminMessageSentAt;
    }

    public void setLastAdminMessageSentAt(Long lastAdminMessageSentAt) {
        this.lastAdminMessageSentAt = lastAdminMessageSentAt;
    }

    public ReceivedGroupDataTypeConstants getReceivedGroupDataType() {
        return receivedGroupDataType;
    }

    public void setReceivedGroupDataType(ReceivedGroupDataTypeConstants receivedGroupDataType) {
        this.receivedGroupDataType = receivedGroupDataType;
    }

    public boolean isSameLastMessageText(String lastMessageTextToCompare){

        //if either one of them is null, changing them to empty string
        //this helps in comparing values
        if(lastMessageText == null){
            lastMessageText = "";
        }
        if(lastMessageTextToCompare == null){
            lastMessageTextToCompare = "";
        }
        if(lastMessageText.equals(lastMessageTextToCompare))
            return true;

        Log.d(TAG, "Not equal "+lastMessageText+" -and- "+lastMessageTextToCompare);
        return false;
    }

    public boolean isSameLastMessageSentBy(String lastMessageSentByToCompare){
        if(lastMessageSentBy == null){
            lastMessageSentBy = "";
        }
        if(lastMessageSentByToCompare == null){
            lastMessageSentByToCompare = "";
        }
        if(lastMessageSentBy.equals(lastMessageSentByToCompare))
            return true;

        return false;
    }

    public int getUnreadPMCount() {
        return unreadPMCount;
    }

    public void setUnreadPMCount(int unreadPMCount) {
        this.unreadPMCount = unreadPMCount;
    }

    //    public boolean isSame(UserGroupModel userGroupModel){
//
//
//        if( groupId.equals(userGroupModel.groupId) &&
//            groupName.equals(userGroupModel.groupName) &&
//            lastMessageText.equals(userGroupModel.lastMessageText) &&
//            lastMessageSentBy.equals(userGroupModel.lastMessageSentBy) &&
//            groupImageLink.equals(userGroupModel.groupImageLink) &&
//            unreadCount == (userGroupModel.unreadCount) &&
//            highPriorityUnread == (userGroupModel.highPriorityUnread) &&
//            showExpiryWarning == (userGroupModel.showExpiryWarning) &&
//            imageUpdateTimestamp == (userGroupModel.imageUpdateTimestamp) &&
//            alias.equals(userGroupModel.alias) &&
//            role.equals(userGroupModel.role) &&
//            isAdmin.equals(userGroupModel.isAdmin) &&
//            isPrivate.equals(userGroupModel.isPrivate) &&
//            numberOfRequests == (userGroupModel.numberOfRequests) &&
//            isDeleted == (userGroupModel.isDeleted)
//            )
//        {
//            return true;
//        }
//        return false;
//    }

    public boolean isSameGroup(UserGroupModel userGroupModel){

        if( groupId.equals(userGroupModel.groupId) &&
                groupName.equals(userGroupModel.groupName) &&
                highPriorityUnread == (userGroupModel.highPriorityUnread) &&
                groupExpiry == (userGroupModel.groupExpiry) &&
                imageUpdateTimestamp == (userGroupModel.imageUpdateTimestamp) &&
                alias.equals(userGroupModel.alias) &&
                role.equals(userGroupModel.role) &&
                isAdmin.equals(userGroupModel.isAdmin) &&
                isPrivate.equals(userGroupModel.isPrivate) &&
                isDeleted == (userGroupModel.isDeleted)
        )
        {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "UserGroupModel{" +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", lastMessageText='" + lastMessageText + '\'' +
                ", lastMessageSentBy='" + lastMessageSentBy + '\'' +
                ", lastMessageSentAt=" + lastMessageSentAt +
                ", groupImageLink='" + groupImageLink + '\'' +
                ", unreadCount=" + unreadCount +
                ", highPriorityUnread=" + highPriorityUnread +
                ", groupExpiry=" + groupExpiry +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                ", alias='" + alias + '\'' +
                ", role='" + role + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                ", isPrivate=" + isPrivate +
                ", numberOfRequests=" + numberOfRequests +
                ", isDeleted=" + isDeleted +
                ", receivedGroupDataType=" + receivedGroupDataType +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof UserGroupModel) {
            UserGroupModel other = (UserGroupModel) o;
            return groupId.equals(other.groupId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return groupId.hashCode();
    }

}
