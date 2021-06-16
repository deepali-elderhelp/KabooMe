package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Group;

public class GroupResponse {
    @SerializedName("group")
    @Expose()
    private Group group;

    public Group getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "GroupResponse{" +
                "group=" + group +
                '}';
    }
}
