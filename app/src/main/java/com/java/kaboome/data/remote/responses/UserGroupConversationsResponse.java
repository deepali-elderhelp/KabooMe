package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.entities.UserGroupConversation;

import java.util.List;

public class UserGroupConversationsResponse {
    @SerializedName("conversations")
    @Expose()
    private List<UserGroupConversation> conversations;

    public List<UserGroupConversation> getUserGroupConversations() {
        return conversations;
    }

    @Override
    public String toString() {
        return "ConversationsResponse{" +
                "conversations=" + conversations +
                '}';
    }
}
