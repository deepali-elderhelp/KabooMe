package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.GroupUser;
import com.java.kaboome.data.entities.UserGroup;

import java.util.List;

public class GroupUsersResponse {


    @SerializedName("groupUsers")
    @Expose()
    private List<GroupUser> groupUsers;

    public List<GroupUser> getGroupUsers() {
        return groupUsers;
    }

    @Override
    public String toString() {
        return "GroupsListResponse{" +
                "groupUsers=" + groupUsers +
                '}';
    }
}
