/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.entities;

import androidx.annotation.NonNull;

import com.java.kaboome.constants.InvitationStatusConstants;

import java.io.Serializable;

/*
This class represents the invitations for the user,
 */


public class InvitationModel implements Serializable {

    private String groupId;

    private long dateInvited;

    private String groupName;

    private Boolean privateGroup;

    private String invitedBy;

    private String invitedByAlias;

    private String messageByInvitee;

    private InvitationStatusConstants invitationStatus;


    public InvitationModel() {
    }

    public InvitationModel(String groupId, long dateInvited, String groupName, String invitedBy, String invitedByAlias) {
        this.groupId = groupId;
        this.dateInvited = dateInvited;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
        this.invitedByAlias = invitedByAlias;
    }

    public InvitationModel(String groupId, long dateInvited, String groupName, String invitedBy, String invitedByAlias, String contactsInvitedPhoneNumbers) {
        this.groupId = groupId;
        this.dateInvited = dateInvited;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
        this.invitedByAlias = invitedByAlias;
    }


    public InvitationModel(@NonNull String groupId, long dateInvited, String groupName, String invitedBy, String invitedByAlias, String messageByInvitee, String contactsInvitedPhoneNumbers) {
        this.groupId = groupId;
        this.dateInvited = dateInvited;
        this.groupName = groupName;
        this.invitedBy = invitedBy;
        this.invitedByAlias = invitedByAlias;
        this.messageByInvitee = messageByInvitee;
    }

    public InvitationModel(String groupId, long dateInvited, String groupName, Boolean privateGroup, String invitedBy, String invitedByAlias, String messageByInvitee, String contactsInvitedPhoneNumbers, InvitationStatusConstants invitationStatus) {
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

    public InvitationStatusConstants getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(InvitationStatusConstants invitationStatus) {
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


