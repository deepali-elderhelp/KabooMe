package com.java.kaboome.presentation.views.features.groupInfo;

import com.java.kaboome.constants.GroupActionConstants;

/**
 * A POJO for carrying the info about what is getting updated and what is the status of that
 */
public class GroupDeleteDetails {

    private GroupActionConstants action;
    private Status status;

    public GroupDeleteDetails(GroupActionConstants action, Status status) {
        this.action = action;
        this.status = status;
    }

    public GroupActionConstants getAction() {
        return action;
    }

    public void setAction(GroupActionConstants action) {
        this.action = action;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status { SUCCESS, ERROR, DELETING}
}
