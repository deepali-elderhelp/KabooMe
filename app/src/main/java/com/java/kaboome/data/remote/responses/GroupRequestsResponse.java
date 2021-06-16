package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.GroupRequest;
import com.java.kaboome.data.entities.Invitation;

import java.util.List;

public class GroupRequestsResponse {


    @SerializedName("requests")
    @Expose()
    private List<GroupRequest> requests;

    public List<GroupRequest> getRequests() {
        return requests;
    }

    @Override
    public String toString() {
        return "GroupRequestsResponse{" +
                "requests=" + requests +
                '}';
    }
}
