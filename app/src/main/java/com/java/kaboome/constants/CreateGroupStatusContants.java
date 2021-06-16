package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum CreateGroupStatusContants {


    LOADING_GROUP("LoadingGroup"),
    LOADING_GROUP_IMAGE("LoadingGroupImage"),
    LOADING_GROUP_USER_IMAGE("LoadingGroupUserImage"),
    SUCCESS_GROUP("SuccessGroup"),
    SUCCESS_GROUP_IMAGE("SuccessGroupImage"),
    SUCCESS_GROUP_USER_IMAGE("SuccessGroupUserImage"),
    ERROR_GROUP("ErrorGroup"),
    ERROR_GROUP_IMAGE("ErrorGroupImage"),
    ERROR_GROUP_USER_IMAGE("ErrorGroupUserImage");




    private String status;

    private static final Map<String, CreateGroupStatusContants> CREATE_GROUP_STATUS_CONTANTS_MAP;

    CreateGroupStatusContants(String value) {
        this.status = value;
    }

    public String getStatus() {
        return this.status;
    }


    @Override
    public String toString() {
        return this.status;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String, CreateGroupStatusContants> map = new ConcurrentHashMap<String, CreateGroupStatusContants>();
        for (CreateGroupStatusContants instance : CreateGroupStatusContants.values()) {
            map.put(instance.getStatus(),instance);
        }
        CREATE_GROUP_STATUS_CONTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static CreateGroupStatusContants get (String name) {
        return CREATE_GROUP_STATUS_CONTANTS_MAP.get(name);
    }











}
