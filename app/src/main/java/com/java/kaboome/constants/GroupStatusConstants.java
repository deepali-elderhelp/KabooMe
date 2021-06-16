package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum GroupStatusConstants {


    CREATED_GROUP("CreatedGroup"),
    JOINED_GROUP("JoinedGroup");




    private String status;

    private static final Map<String, GroupStatusConstants> GROUP_STATUS_CONSTANTS_MAP;

    GroupStatusConstants(String value) {
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
        Map<String, GroupStatusConstants> map = new ConcurrentHashMap<String, GroupStatusConstants>();
        for (GroupStatusConstants instance : GroupStatusConstants.values()) {
            map.put(instance.getStatus(),instance);
        }
        GROUP_STATUS_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static GroupStatusConstants get (String name) {
        return GROUP_STATUS_CONSTANTS_MAP.get(name);
    }











}
