package com.java.kaboome.domain.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DomainResource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    public DomainResource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> DomainResource<T> success(@NonNull T data) {
        return new DomainResource<>(Status.SUCCESS, data, null);
    }

    public static <T> DomainResource<T> error(@NonNull String msg, @Nullable T data) {
        return new DomainResource<>(Status.ERROR, data, msg);
    }

    public static <T> DomainResource<T> loading(@Nullable T data) {
        return new DomainResource<>(Status.LOADING, data, null);
    }


    public enum Status { SUCCESS, ERROR, LOADING}
}
