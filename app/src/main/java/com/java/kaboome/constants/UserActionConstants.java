package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum UserActionConstants {

    UPDATE_USER_NAME("updateUserName"),
    UPDATE_USER_EMAIL("updateUserEmail"),
    UPDATE_USER_PROFILE_IMAGE_TS("updateUserImage"),
    UPDATE_USER_PROFILE_IMAGE_NO_TS("updateUserImageNoTS");


    private String action;

    private static final Map<String, UserActionConstants> USER_ACTION_CONSTANTS_MAP;

    UserActionConstants(String value) {
        this.action = value;
    }

    public String getAction() {
        return this.action;
    }


    @Override
    public String toString() {
        return this.action;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String, UserActionConstants> map = new ConcurrentHashMap<String, UserActionConstants>();
        for (UserActionConstants instance : UserActionConstants.values()) {
            map.put(instance.getAction(),instance);
        }
        USER_ACTION_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static UserActionConstants get (String name) {
        return USER_ACTION_CONSTANTS_MAP.get(name);
    }



}


