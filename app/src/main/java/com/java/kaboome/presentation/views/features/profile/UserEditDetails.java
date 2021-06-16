package com.java.kaboome.presentation.views.features.profile;

import com.java.kaboome.constants.UserActionConstants;

/**
 * A POJO for carrying the info about what is getting updated and what is the status of that
 */
public class UserEditDetails {

    private UserActionConstants action;
    private Status status;
    private Long imageUpdatedTimestamp;

    public UserEditDetails(UserActionConstants action, Status status, Long imageUpdatedTimestamp) {
        this.action = action;
        this.status = status;
        this.imageUpdatedTimestamp = imageUpdatedTimestamp;
    }

    public UserActionConstants getAction() {
        return action;
    }

    public void setAction(UserActionConstants action) {
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
