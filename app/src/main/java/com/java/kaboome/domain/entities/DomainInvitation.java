/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.domain.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
This class represents the invitations for the user,
 */


public class DomainInvitation implements Serializable {

    private String groupId;

    private long dateInvited;

    private String groupName;

    private Boolean privateGroup;

    private String invitedBy;

    private String invitedByAlias;

    private String messageByInvitee;

    private String invitationStatus;


    public DomainInvitation() {
    }

    public DomainInvitation(String groupId, long dateInvited, String groupName, String invitedBy, String invitedByAlias) {
        this.groupId = groupId;
        this.dateInvited = dateInvited;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
        this.invitedByAlias = invitedByAlias;
    }

    public DomainInvitation(String groupId, long dateInvited, String groupName, String invitedBy, String invitedByAlias, String contactsInvitedPhoneNumbers) {
        this.groupId = groupId;
        this.dateInvited = dateInvited;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
        this.invitedByAlias = invitedByAlias;
    }


    public DomainInvitation(@NonNull String groupId, long dateInvited, String groupName, String invitedBy, String invitedByAlias, String messageByInvitee, String contactsInvitedPhoneNumbers) {
        this.groupId = groupId;
        this.dateInvited = dateInvited;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
        this.invitedByAlias = invitedByAlias;
        this.messageByInvitee = messageByInvitee;
    }

    public DomainInvitation(String groupId, long dateInvited, String groupName, Boolean privateGroup, String invitedBy, String invitedByAlias, String messageByInvitee, String contactsInvitedPhoneNumbers, String invitationStatus) {
        this.groupId = groupId;
        this.dateInvited = dateInvited;
        this.groupName = groupName;
        this.privateGroup = privateGroup;
        this.invitedBy = invitedBy;
        this.invitedByAlias = invitedByAlias;
        this.messageByInvitee = messageByInvitee;
        this.invitationStatus = invitationStatus;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getDateInvited() {
        return dateInvited;
    }

    public void setDateInvited(long dateInvited) {
        this.dateInvited = dateInvited;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getMessageByInvitee() {
        return messageByInvitee;
    }

    public void setMessageByInvitee(String messageByInvitee) {
        this.messageByInvitee = messageByInvitee;
    }

    public void setInvitedByAlias(String invitedByAlias) {
        this.invitedByAlias = invitedByAlias;
    }

    public Boolean getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(Boolean privateGroup) {
        this.privateGroup = privateGroup;
    }

    public String getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(String invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    @Override
    public String toString() {
        return "DomainInvitation{" +
                "groupId='" + groupId + '\'' +
                ", dateInvited=" + dateInvited +
                ", groupName='" + groupName + '\'' +
                ", privateGroup=" + privateGroup +
                ", invitedBy='" + invitedBy + '\'' +
                ", invitedByAlias='" + invitedByAlias + '\'' +
                ", messageByInvitee='" + messageByInvitee + '\'' +
                ", invitationStatus='" + invitationStatus + '\'' +
                '}';
    }
}


