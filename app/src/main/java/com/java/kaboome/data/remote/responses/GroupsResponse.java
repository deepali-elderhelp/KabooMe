package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Group;
import com.java.kaboome.data.entities.UserGroup;

import java.util.List;

public class GroupsResponse {


    @SerializedName("groups")
    @Expose()
    private List<Group> groups;

    public List<Group> getGroups() {
        return groups;
    }

    @Override
    public String toString() {
        return "GroupsListResponse{" +
                "groups=" + groups +
                '}';
    }
}
