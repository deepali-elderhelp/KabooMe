package com.java.kaboome.domain.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DomainDeleteResource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public DomainDeleteResource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> DomainDeleteResource<T> success(@NonNull T data) {
        return new DomainDeleteResource<>(Status.SUCCESS, data, null);
    }

    public static <T> DomainDeleteResource<T> error(@NonNull String msg, @Nullable T data) {
        return new DomainDeleteResource<>(Status.ERROR, data, msg);
    }

    public static <T> DomainDeleteResource<T> loading(@Nullable T data) {
        return new DomainDeleteResource<>(Status.DELETING, data, null);
    }


    public enum Status { SUCCESS, ERROR, DELETING}
}
