package com.java.kaboome.data.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class UpdateResource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public UpdateResource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> UpdateResource<T> success(@NonNull T data) {
        return new UpdateResource<>(Status.SUCCESS, data, null);
    }

    public static <T> UpdateResource<T> error(@NonNull String msg, @Nullable T data) {
        return new UpdateResource<>(Status.ERROR, data, msg);
    }

    public static <T> UpdateResource<T> updating(@Nullable T data) {
        return new UpdateResource<>(Status.UPDATING, data, null);
    }


    public enum Status { SUCCESS, ERROR, UPDATING}
}