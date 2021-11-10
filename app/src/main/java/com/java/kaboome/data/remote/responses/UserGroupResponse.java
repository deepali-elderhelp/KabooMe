package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.UserGroup;

import java.util.List;

public class UserGroupResponse {


    @SerializedName("group")
    @Expose()
    private UserGroup group;

    public UserGroup getUserGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "UserGroupsResponse{" +
                "group=" + group +
                '}';
    }
}
