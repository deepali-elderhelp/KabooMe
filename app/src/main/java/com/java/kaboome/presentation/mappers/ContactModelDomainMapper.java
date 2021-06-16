package com.java.kaboome.presentation.mappers;


import com.java.kaboome.domain.entities.DomainContact;
import com.java.kaboome.presentation.entities.ContactModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactModelDomainMapper {

    public static DomainContact transformFromContact(ContactModel contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Cannot transformFromContact a null value");
        }

        DomainContact domainContact = new DomainContact();

        domainContact.setName(contact.getName());
        domainContact.setPhone(contact.getPhone());
        domainContact.setPhotoURI(contact.getPhotoURI());
        domainContact.setLookupKey(contact.getLookupKey());

        return domainContact;
    }

    public static ContactModel transformFromDomain(DomainContact domainContact) {
        if (domainContact == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }

        ContactModel contact = new ContactModel();

        contact.setName(domainContact.getName());
        contact.setPhone(domainContact.getPhone());
        contact.setPhotoURI(domainContact.getPhotoURI());
        contact.setLookupKey(domainContact.getLookupKey());

        return contact;

    }



    public static List<DomainContact> transformAllFromContactModelToDomain(List<ContactModel> contactList) {
        List<DomainContact> domainContacts;

        if (contactList != null && !contactList.isEmpty()) {
            domainContacts = new ArrayList<>();
            for (ContactModel contact : contactList) {
                domainContacts.add(transformFromContact(contact));
            }
        } else {
            domainContacts = Collections.emptyList();
        }

        return domainContacts;
    }

    public static List<ContactModel> transformAllFromDomainToModel(List<DomainContact> domainContacts) {
        List<ContactModel> contactModels;

        if (domainContacts != null && !domainContacts.isEmpty()) {
            contactModels = new ArrayList<>();
            for (DomainContact domainContact : domainContacts) {
                contactModels.add(transformFromDomain(domainContact));
            }
        } else {
            contactModels = Collections.emptyList();
        }

        return contactModels;
    }


}
