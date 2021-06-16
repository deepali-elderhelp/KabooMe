//package com.java.kaboome.data.entities;
//
//import androidx.room.Entity;
//import androidx.room.Ignore;
//import androidx.room.PrimaryKey;
//import androidx.annotation.NonNull;
//
//import com.google.gson.annotations.SerializedName;
//
//import java.io.Serializable;
//
//@Entity(tableName = "group_unread_count")
//public class GroupUnreadCount implements Serializable {
//
//    @NonNull
//    @PrimaryKey
//    @SerializedName("groupId")
//    private String groupId;
//
//    @SerializedName("countOfUnreadMessages")
//    private int countOfUnreadMessages;
//
//    @SerializedName("lastMessageText")
//    private String lastMessageText;
//
//    @SerializedName("lastMessageSentBy")
//    private String lastMessageSentBy;
//
//    @Ignore
//    public GroupUnreadCount() {
//    }
//
//    public GroupUnreadCount(String groupId, int countOfUnreadMessages, String lastMessageText, String lastMessageSentBy) {
//        this.groupId = groupId;
//        this.countOfUnreadMessages = countOfUnreadMessages;
//        this.lastMessageText = lastMessageText;
//        this.lastMessageSentBy = lastMessageSentBy;
//    }
//
//    public String getGroupId() {
//        return groupId;
//    }
//
//    public void setGroupId(String groupId) {
//        this.groupId = groupId;
//    }
//
//    public int getCountOfUnreadMessages() {
//        return countOfUnreadMessages;
//    }
//
//    public void setCountOfUnreadMessages(int countOfUnreadMessages) {
//        this.countOfUnreadMessages = countOfUnreadMessages;
//    }
//
//    public String getLastMessageText() {
//        return lastMessageText;
//    }
//
//    public void setLastMessageText(String lastMessageText) {
//        this.lastMessageText = lastMessageText;
//    }
//
//    public String getLastMessageSentBy() {
//        return lastMessageSentBy;
//    }
//
//    public void setLastMessageSentBy(String lastMessageSentBy) {
//        this.lastMessageSentBy = lastMessageSentBy;
//    }
//}
