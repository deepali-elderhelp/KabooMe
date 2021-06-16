package com.java.kaboome.data.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class DeleteResource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public DeleteResource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> DeleteResource<T> success(@NonNull T data) {
        return new DeleteResource<>(Status.SUCCESS, data, null);
    }

    public static <T> DeleteResource<T> error(@NonNull String msg, @Nullable T data) {
        return new DeleteResource<>(Status.ERROR, data, msg);
    }

    public static <T> DeleteResource<T> deleting(@Nullable T data) {
        return new DeleteResource<>(Status.DELETING, data, null);
    }


    public enum Status { SUCCESS, ERROR, DELETING}
}