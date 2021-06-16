package com.java.kaboome.presentation.entities;

public class GroupRequestModel {

    private String groupId;

    private String userId;

    private String userAlias;

    private String userRole;

    private Long dateRequestMade;

    private String requestMessage;

    private Long imageUpdateTimestamp; //users's image's time stamp

    public GroupRequestModel() {
    }

    public GroupRequestModel(String groupId, String userId, String userAlias, String userRole, Long dateRequestMade, String requestMessage, Long imageUpdateTimestamp) {
        this.groupId = groupId;
        this.userId = userId;
        this.userAlias = userAlias;
        this.userRole = userRole;
        this.dateRequestMade = dateRequestMade;
        this.requestMessage = requestMessage;
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

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

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Long getDateRequestMade() {
        return dateRequestMade;
    }

    public void setDateRequestMade(Long dateRequestMade) {
        this.dateRequestMade = dateRequestMade;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }

    public Long getImageUpdateTimestamp() {
        return imageUpdateTimestamp;
    }

    public void setImageUpdateTimestamp(Long imageUpdateTimestamp) {
        this.imageUpdateTimestamp = imageUpdateTimestamp;
    }

    @Override
    public String toString() {
        return "GroupRequestModel{" +
                "groupId='" + groupId + '\'' +
                ", userId='" + userId + '\'' +
                ", userAlias='" + userAlias + '\'' +
                ", userRole='" + userRole + '\'' +
                ", dateRequestMade=" + dateRequestMade +
                ", requestMessage='" + requestMessage + '\'' +
                ", imageUpdateTimestamp=" + imageUpdateTimestamp +
                '}';
    }
}
