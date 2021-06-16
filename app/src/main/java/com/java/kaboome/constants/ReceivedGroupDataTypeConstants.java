package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ReceivedGroupDataTypeConstants {

    UNREAD_COUNT("unreadCount"),
    LAST_MESSAGE("lastMessage"),
    BOTH_UNREAD_AND_LAST("bothUnreadAndLast"),
    REQUESTS_DATA("requestsData"),
    ALL_DATA("allData");


    private String dataType;

    private static final Map<String, ReceivedGroupDataTypeConstants> RECEIVED_GROUP_DATA_TYPE_CONSTANTS_MAP;

    ReceivedGroupDataTypeConstants(String value) {
        this.dataType = value;
    }

    public String getDataType() {
        return this.dataType;
    }


    @Override
    public String toString() {
        return this.dataType;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String, ReceivedGroupDataTypeConstants> map = new ConcurrentHashMap<String, ReceivedGroupDataTypeConstants>();
        for (ReceivedGroupDataTypeConstants instance : ReceivedGroupDataTypeConstants.values()) {
            map.put(instance.getDataType(),instance);
        }
        RECEIVED_GROUP_DATA_TYPE_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static ReceivedGroupDataTypeConstants get (String name) {
        return RECEIVED_GROUP_DATA_TYPE_CONSTANTS_MAP.get(name);
    }



}


