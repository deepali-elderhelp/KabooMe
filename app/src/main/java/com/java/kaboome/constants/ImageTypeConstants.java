package com.java.kaboome.constants;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ImageTypeConstants {

    MAIN("_mn"),
    THUMBNAIL("_tn");


    private String type;

    private static final Map<String, ImageTypeConstants> IMAGE_TYPE_CONSTANTS_MAP;

    ImageTypeConstants(String value) {
        this.type = value;
    }

    public String getType() {
        return this.type;
    }


    @Override
    public String toString() {
        return this.type;
    }

    // Build an immutable map of String name to enum pairs.
    // Any Map impl can be used.

    static {
        Map<String, ImageTypeConstants> map = new ConcurrentHashMap<String, ImageTypeConstants>();
        for (ImageTypeConstants instance : ImageTypeConstants.values()) {
            map.put(instance.getType(),instance);
        }
        IMAGE_TYPE_CONSTANTS_MAP = Collections.unmodifiableMap(map);
    }

    public static ImageTypeConstants get (String name) {
        return IMAGE_TYPE_CONSTANTS_MAP.get(name);
    }



}


