package com.java.kaboome.data.remote.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.java.kaboome.data.entities.User;

public class UserResponse {

    @SerializedName("user")
    @Expose()
    private User user;

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "user=" + user +
                '}';
    }
}
