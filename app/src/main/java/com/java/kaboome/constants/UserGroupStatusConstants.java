package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum UserGroupStatusConstants {

    REGULAR_MEMBER("regular_member"),
    ADMIN_MEMBER("admin_member"),
    INVITED("invited"),
    PENDING("pending"),
    DELETED("deleted"),
    NONE("none");


    private String status;

    private static final Map<String, UserGroupStatusConstants> USER_GROUP_STATUS_CONSTANTS_MAP;

    UserGroupStatusConstants(String value) {
        this.status = value;
    }

    public String getAction() {
        return this.status;
    }


    @Override
    public String toString() {
        return this.status;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String, UserGroupStatusConstants> map = new ConcurrentHashMap<String, UserGroupStatusConstants>();
        for (UserGroupStatusConstants instance : UserGroupStatusConstants.values()) {
            map.put(instance.getAction(),instance);
        }
        USER_GROUP_STATUS_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static UserGroupStatusConstants get (String name) {
        return USER_GROUP_STATUS_CONSTANTS_MAP.get(name);
    }



}


