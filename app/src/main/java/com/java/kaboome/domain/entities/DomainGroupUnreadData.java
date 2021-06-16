package com.java.kaboome.domain.entities;

/*
Domain GroupUnreadCount POJO
 */
public class DomainGroupUnreadData {

    private String groupId;
    private int countOfUnreadMessages;
    private String lastMessageText;
    private String lastMessageSentBy;

    public DomainGroupUnreadData() {
    }

    public DomainGroupUnreadData(String groupId, int countOfUnreadMessages, String lastMessageText, String lastMessageSentBy) {
        this.groupId = groupId;
        this.countOfUnreadMessages = countOfUnreadMessages;
        this.lastMessageText = lastMessageText;
        this.lastMessageSentBy = lastMessageSentBy;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getCountOfUnreadMessages() {
        return countOfUnreadMessages;
    }

    public void setCountOfUnreadMessages(int countOfUnreadMessages) {
        this.countOfUnreadMessages = countOfUnreadMessages;
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
}
