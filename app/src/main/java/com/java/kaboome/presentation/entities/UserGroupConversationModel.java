package com.java.kaboome.presentation.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.java.kaboome.constants.ReceivedGroupDataTypeConstants;
import com.java.kaboome.data.entities.UserGroupConversation;

import java.io.Serializable;

public class UserGroupConversationModel implements Serializable, Parcelable {

    private static final String TAG = "KMUGConvModel";

    private String groupId;

    private String userId;

    private String otherUserId;

    private String otherUserName;

    private String otherUserRole;

    private String isOtherUserAdmin;

    private Boolean isDeleted = false;

    private Long imageUpdateTimestamp; //the other users's image's time stamp

    private Long lastAccessed;

    private Long cacheClearTS;

    //needed only for the model

    private String lastMessageText;

    private String lastMessageSentBy;

    private Long lastMessageSentAt;

    private int unreadCount;


    private ReceivedGroupDataTypeConstants receivedGroupDataType = ReceivedGroupDataTypeConstants.ALL_DATA;

    public UserGroupConversationModel() {
    }

    protected UserGroupConversationModel(Parcel in) {
        groupId = in.readString();
        userId = in.readString();
        otherUserId = in.readString();
        otherUserName = in.readString();
        otherUserRole = in.readString();
        isOtherUserAdmin = in.readString();
        byte tmpIsDeleted = in.readByte();
        isDeleted = tmpIsDeleted == 0 ? null : tmpIsDeleted == 1;
        if (in.readByte() == 0) {
            imageUpdateTimestamp = null;
        } else {
            imageUpdateTimestamp = in.readLong();
        }
        if (in.readByte() == 0) {
            lastAccessed = null;
        } else {
            lastAccessed = in.readLong();
        }
        if (in.readByte() == 0) {
            cacheClearTS = null;
        } else {
            cacheClearTS = in.readLong();
        }
        lastMessageText = in.readString();
        lastMessageSentBy = in.readString();
        if (in.readByte() == 0) {
            lastMessageSentAt = null;
        } else {
            lastMessageSentAt = in.readLong();
        }
        unreadCount = in.readInt();
    }

    public static final Creator<UserGroupConversationModel> CREATOR = new Creator<UserGroupConversationModel>() {
        @Override
        public UserGroupConversationModel createFromParcel(Parcel in) {
            return new UserGroupConversationModel(in);
        }

        @Override
        public UserGroupConversationModel[] newArray(int size) {
            return new UserGroupConversationModel[size];
        }
    };

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getOtherUserRole() {
        return otherUserRole;
    }

    public void setOtherUserRole(String otherUserRole) {
        this.otherUserRole = otherUserRole;
    }

    public String getIsOtherUserAdmin() {
        return isOtherUserAdmin;
    }

    public void setIsOtherUserAdmin(String isOtherUserAdmin) {
        this.isOtherUserAdmin = isOtherUserAdmin;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    public Long getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(Long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Long getCacheClearTS() {
        return cacheClearTS;
    }

    public void setCacheClearTS(Long cacheClearTS) {
        this.cacheClearTS = cacheClearTS;
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

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Long getLastMessageSentAt() {
        return lastMessageSentAt;
    }

    public void setLastMessageSentAt(Long lastMessageSentAt) {
        this.lastMessageSentAt = lastMessageSentAt;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupId);
        parcel.writeString(userId);
        parcel.writeString(otherUserId);
        parcel.writeString(otherUserName);
        parcel.writeString(otherUserRole);
        parcel.writeString(isOtherUserAdmin);
        parcel.writeByte((byte) (isDeleted == null ? 0 : isDeleted ? 1 : 2));
        if (imageUpdateTimestamp == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(imageUpdateTimestamp);
        }
        if (lastAccessed == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lastAccessed);
        }
        if (cacheClearTS == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(cacheClearTS);
        }
        parcel.writeString(lastMessageText);
        parcel.writeString(lastMessageSentBy);
        if (lastMessageSentAt == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(lastMessageSentAt);
        }
        parcel.writeInt(unreadCount);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserGroupConversationModel) {
            UserGroupConversationModel other = (UserGroupConversationModel) o;
            return groupId.equals(other.groupId) && otherUserId.equals(other.getOtherUserId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (groupId.hashCode() * otherUserId.hashCode());
    }
}
