package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum MediaActionConstants {

    UPLOAD_ATTACHMENT("uploadAttachment"),
    DOWNLOAD_ATTACHMENT("downloadAttachment"),
    UPLOAD_GROUP_PIC("uploadGroupPic"),
    UPLOAD_GROUP_USER_PIC("uploadGroupUserPic"),
    COPY_GROUP_USER_PIC("copyGroupUserPic"),
    UPLOAD_USER_PROFILE_PIC("uploadUserProfilePic");


    private String action;

    private static final Map<String, MediaActionConstants> MESSAGE_ACTION_CONSTANTS_MAP;

    MediaActionConstants(String value) {
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
        Map<String, MediaActionConstants> map = new ConcurrentHashMap<String, MediaActionConstants>();
        for (MediaActionConstants instance : MediaActionConstants.values()) {
            map.put(instance.getAction(),instance);
        }
        MESSAGE_ACTION_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static MediaActionConstants get (String name) {
        return MESSAGE_ACTION_CONSTANTS_MAP.get(name);
    }



}


