package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Message;

import java.util.List;

public class GroupMessagesResponse {
    @SerializedName("messages")
    @Expose()
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "GroupMessagesResponse{" +
                "messages=" + messages +
                '}';
    }
}
