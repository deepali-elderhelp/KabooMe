package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Invitation;
import com.java.kaboome.data.entities.UserGroup;

import java.util.List;

public class InvitationsResponse {


    @SerializedName("invitations")
    @Expose()
    private List<Invitation> invitations;

    public List<Invitation> getInvitations() {
        return invitations;
    }

    @Override
    public String toString() {
        return "InvitationsResponse{" +
                "invitations=" + invitations +
                '}';
    }
}
