package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.entities.UserGroup;

import java.util.List;

public class DeleteMessageResponse {


    @SerializedName("message")
    @Expose()
    private Message message;

    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DeleteMessageResponse{" +
                "message=" + message +
                '}';
    }
}
