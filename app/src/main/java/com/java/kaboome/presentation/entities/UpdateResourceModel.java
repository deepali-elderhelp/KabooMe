package com.java.kaboome.presentation.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.java.kaboome.domain.entities.DomainUpdateResource;

public class UpdateResourceModel<T> {

    @NonNull
    public final UpdateResourceModel.Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public UpdateResourceModel(@NonNull UpdateResourceModel.Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> UpdateResourceModel<T> success(@NonNull T data) {
        return new UpdateResourceModel<>(UpdateResourceModel.Status.SUCCESS, data, null);
    }

    public static <T> UpdateResourceModel<T> error(@NonNull String msg, @Nullable T data) {
        return new UpdateResourceModel<>(UpdateResourceModel.Status.ERROR, data, msg);
    }

    public static <T> UpdateResourceModel<T> loading(@Nullable T data) {
        return new UpdateResourceModel<>(UpdateResourceModel.Status.UPDATING, data, null);
    }


    public enum Status { SUCCESS, ERROR, UPDATING}
}
