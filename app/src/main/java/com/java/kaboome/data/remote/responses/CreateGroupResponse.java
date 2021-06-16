package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * CreateGroupResponse does not need to return anything other than the status that group was created successfully.
 * On next load of GroupList, a server request is made which gets the newly added group from the server and adds it to
 * the cache
 */
public class CreateGroupResponse {
    @SerializedName("groupId")
    @Expose()
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    @Override
    public String toString() {
        return "CreateGroupResponse{" +
                "groupId='" + groupId + '\'' +
                '}';
    }
}
