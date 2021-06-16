package com.java.kaboome.data.remote.requests;

import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Contact;

import java.io.Serializable;
import java.util.List;

public class InviteContactsRequest implements Serializable {

        @SerializedName("invitedBy")
        private String invitedBy;

        @SerializedName("invitedByAlias")
        private String invitedByAlias;

        @SerializedName("groupId")
        private String groupId;

        @SerializedName("groupName")
        private String groupName;

        @SerializedName("privateGroup")
        private String privateGroup;

        @SerializedName("messageByInvitee")
        private String messageByInvitee;

        @SerializedName("contactList")
        private String contactList;

    public InviteContactsRequest() {
    }

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public String getInvitedByAlias() {
        return invitedByAlias;
    }

    public void setInvitedByAlias(String invitedByAlias) {
        this.invitedByAlias = invitedByAlias;
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

    public String getMessageByInvitee() {
        return messageByInvitee;
    }

    public void setMessageByInvitee(String messageByInvitee) {
        this.messageByInvitee = messageByInvitee;
    }

    public String getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(String privateGroup) {
        this.privateGroup = privateGroup;
    }

    public String getContactList() {
        return contactList;
    }

    public void setContactList(String contactList) {
        this.contactList = contactList;
    }

    @Override
    public String toString() {
        return "InviteContactsRequest{" +
                "invitedBy='" + invitedBy + '\'' +
                ", invitedByAlias='" + invitedByAlias + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", privateGroup='" + privateGroup + '\'' +
                ", messageByInvitee='" + messageByInvitee + '\'' +
                ", contactList='" + contactList + '\'' +
                '}';
    }

    public String getContactListFromContacts(List<Contact> contacts){

        String contactList = "";
        for(Contact contact: contacts){
            contactList = contactList+contact.getPhone();
        }

        return contactList;

    }

    public void setContactList(List<Contact> contacts) {
        setContactList(getContactListFromContacts(contacts));
    }
}
