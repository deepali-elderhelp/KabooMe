package com.java.kaboome.presentation.views.features.inviteContacts.adapter;


import com.java.kaboome.presentation.entities.ContactModel;

public interface InvitationContactListener {
    void contactChecked(ContactModel contactModel);
    void contactCheckedRemoved(ContactModel contactModel);
    boolean isContactAlreadySelected(ContactModel contactToBeChecked);
}
