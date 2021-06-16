package com.java.kaboome.constants;

public enum GroupListStatusConstants {



        NO_GROUPS("No Groups"),
        LOADING("Loading"),
        ERROR("Error");

    private final String groupListStatus;

    GroupListStatusConstants(String value) {
        this.groupListStatus = value;
    }


    @Override
    public String toString() {
        return this.groupListStatus;
    }





}
