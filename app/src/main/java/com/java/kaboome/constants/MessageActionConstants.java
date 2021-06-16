package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum MessageActionConstants {

    UPLOAD_ATTACHMENT("uploadAttachment"),
    DOWNLOAD_ATTACHMENT("downloadAttachment");


    private String action;

    private static final Map<String, MessageActionConstants> MESSAGE_ACTION_CONSTANTS_MAP;

    MessageActionConstants(String value) {
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
        Map<String, MessageActionConstants> map = new ConcurrentHashMap<String, MessageActionConstants>();
        for (MessageActionConstants instance : MessageActionConstants.values()) {
            map.put(instance.getAction(),instance);
        }
        MESSAGE_ACTION_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static MessageActionConstants get (String name) {
        return MESSAGE_ACTION_CONSTANTS_MAP.get(name);
    }



}


