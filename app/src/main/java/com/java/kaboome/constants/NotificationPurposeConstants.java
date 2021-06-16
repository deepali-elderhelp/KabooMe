package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum NotificationPurposeConstants {

    NEW_MESSAGE("NEW_MESSAGE"),
    NEW_REQUEST("NEW_REQUEST"),
    NEW_INVITATION("NEW_INVITATION"),
    NEW_GROUP("NEW_GROUP"),
    REQUEST_CANCEL("REQUEST_CANCEL"),
    GROUP_DELETED("GROUP_DELETED"),
    GROUP_UPDATED("GROUP_UPDATED"),
    USER_DELETED("USER_DELETED"),
    UPDATE_CONVERSATION_ALIAS_ROLE_TS("UPDATE_CONVERSATION_ALIAS_ROLE_TS"),
    UPDATE_CONVERSATION_DELETE("UPDATE_CONVERSATION_DELETE");


    private String purpose;

    private static final Map<String, NotificationPurposeConstants> NOTIFICATION_PURPOSE_CONSTANTS_MAP;

    NotificationPurposeConstants(String value) {
        this.purpose = value;
    }

    public String getPurpose() {
        return this.purpose;
    }


    @Override
    public String toString() {
        return this.purpose;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String, NotificationPurposeConstants> map = new ConcurrentHashMap<String, NotificationPurposeConstants>();
        for (NotificationPurposeConstants instance : NotificationPurposeConstants.values()) {
            map.put(instance.getPurpose(),instance);
        }
        NOTIFICATION_PURPOSE_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static NotificationPurposeConstants get (String name) {
        return NOTIFICATION_PURPOSE_CONSTANTS_MAP.get(name);
    }



}


