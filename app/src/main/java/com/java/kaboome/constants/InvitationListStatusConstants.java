package com.java.kaboome.constants;

public enum InvitationListStatusConstants {



    NO_INVITATIONS("No Invitations"),
        LOADING("Loading"),
        ERROR("Error");

    private final String invitationListStatus;

    InvitationListStatusConstants(String value) {
        this.invitationListStatus = value;
    }


    @Override
    public String toString() {
        return this.invitationListStatus;
    }





}
