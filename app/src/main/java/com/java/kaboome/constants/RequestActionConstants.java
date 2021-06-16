package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum RequestActionConstants {

    REQUEST_TO_JOIN("requestToJoin"),
    REQUEST_TO_CANCEL("requestToCancel");


    private String action;

    private static final Map<String, RequestActionConstants> REQUEST_ACTION_CONSTANTS_MAP;

    RequestActionConstants(String value) {
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
        Map<String, RequestActionConstants> map = new ConcurrentHashMap<String, RequestActionConstants>();
        for (RequestActionConstants instance : RequestActionConstants.values()) {
            map.put(instance.getAction(),instance);
        }
        REQUEST_ACTION_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static RequestActionConstants get (String name) {
        return REQUEST_ACTION_CONSTANTS_MAP.get(name);
    }



}


