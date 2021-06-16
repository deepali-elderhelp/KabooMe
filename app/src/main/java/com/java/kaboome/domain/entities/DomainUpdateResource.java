package com.java.kaboome.domain.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DomainUpdateResource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public DomainUpdateResource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> DomainUpdateResource<T> success(@NonNull T data) {
        return new DomainUpdateResource<>(Status.SUCCESS, data, null);
    }

    public static <T> DomainUpdateResource<T> error(@NonNull String msg, @Nullable T data) {
        return new DomainUpdateResource<>(Status.ERROR, data, msg);
    }

    public static <T> DomainUpdateResource<T> loading(@Nullable T data) {
        return new DomainUpdateResource<>(Status.UPDATING, data, null);
    }


    public enum Status { SUCCESS, ERROR, UPDATING}
}
