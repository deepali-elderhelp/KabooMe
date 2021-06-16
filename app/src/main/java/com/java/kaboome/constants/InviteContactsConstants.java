package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum InviteContactsConstants {


    INVITE_CONTACTS("inviteContacts");



    private String status;

    private static final Map<String, InviteContactsConstants> INVITE_CONTACTS_CONSTANTS_MAP;

    InviteContactsConstants(String value) {
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
        Map<String, InviteContactsConstants> map = new ConcurrentHashMap<String, InviteContactsConstants>();
        for (InviteContactsConstants instance : InviteContactsConstants.values()) {
            map.put(instance.getStatus(),instance);
        }
        INVITE_CONTACTS_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static InviteContactsConstants get (String name) {
        return INVITE_CONTACTS_CONSTANTS_MAP.get(name);
    }











}
