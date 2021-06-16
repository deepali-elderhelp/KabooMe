package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum GeneralStatusContants {


    ERROR("Error"),
    LOADING("Loading"),
    SUCCESS("Success");



    private String status;

    private static final Map<String, GeneralStatusContants> GENERAL_STATUS_CONTANTS_MAP;

    GeneralStatusContants(String value) {
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
        Map<String, GeneralStatusContants> map = new ConcurrentHashMap<String, GeneralStatusContants>();
        for (GeneralStatusContants instance : GeneralStatusContants.values()) {
            map.put(instance.getStatus(),instance);
        }
        GENERAL_STATUS_CONTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static GeneralStatusContants get (String name) {
        return GENERAL_STATUS_CONTANTS_MAP.get(name);
    }











}
