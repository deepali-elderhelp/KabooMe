package com.java.kaboome.constants;

public enum GroupRequestsListStatusConstants {



    NO_REQUESTS("No Requests"),
        LOADING("Loading"),
        ERROR("Error");

    private final String groupRequestStatus;

    GroupRequestsListStatusConstants(String value) {
        this.groupRequestStatus = value;
    }


    @Override
    public String toString() {
        return this.groupRequestStatus;
    }





}
