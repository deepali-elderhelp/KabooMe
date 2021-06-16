package com.java.kaboome.presentation.views.features.groupInfo;

import com.java.kaboome.constants.GroupActionConstants;

/**
 * A POJO for carrying the info about what is getting updated and what is the status of that
 */
public class GroupEditDetails {

    private GroupActionConstants action;
    private Status status;
    private Long imageUpdatedTimestamp;

    public GroupEditDetails(GroupActionConstants action, Status status, Long imageUpdatedTimestamp) {
        this.action = action;
        this.status = status;
        this.imageUpdatedTimestamp = imageUpdatedTimestamp;
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

    public Long getImageUpdatedTimestamp() {
        return imageUpdatedTimestamp;
    }

    public void setImageUpdatedTimestamp(Long imageUpdatedTimestamp) {
        this.imageUpdatedTimestamp = imageUpdatedTimestamp;
    }

    public enum Status { SUCCESS, ERROR, UPDATING}
}
