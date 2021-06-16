package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum InvitationStatusConstants {


    PENDING("Pending"),
    NO_ACTION("No Action"),
    REJECTED("Rejected");



    private String status;

    private static final Map<String,InvitationStatusConstants> INVITATION_STATUS_CONSTANTS_MAP;

    InvitationStatusConstants(String value) {
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
        Map<String,InvitationStatusConstants> map = new ConcurrentHashMap<String, InvitationStatusConstants>();
        for (InvitationStatusConstants instance : InvitationStatusConstants.values()) {
            map.put(instance.getStatus(),instance);
        }
        INVITATION_STATUS_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static InvitationStatusConstants get (String name) {
        return INVITATION_STATUS_CONSTANTS_MAP.get(name);
    }











}
