package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum MessageGroupsConstants {


    GROUP_MESSAGES("Group"),
    ADMIN_MESSAGES("InterAdmin"),
    USER_ADMIN_MESSAGES("UserAdmin");




    private String status;

    private static final Map<String, MessageGroupsConstants> MESSAGE_GROUPS_CONSTANTS_MAP;

    MessageGroupsConstants(String value) {
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
        Map<String, MessageGroupsConstants> map = new ConcurrentHashMap<String, MessageGroupsConstants>();
        for (MessageGroupsConstants instance : MessageGroupsConstants.values()) {
            map.put(instance.getStatus(),instance);
        }
        MESSAGE_GROUPS_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static MessageGroupsConstants get (String name) {
        return MESSAGE_GROUPS_CONSTANTS_MAP.get(name);
    }











}
