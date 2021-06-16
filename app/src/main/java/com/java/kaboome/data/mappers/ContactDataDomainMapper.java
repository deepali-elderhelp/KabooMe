package com.java.kaboome.data.mappers;


import com.java.kaboome.data.entities.Contact;
import com.java.kaboome.domain.entities.DomainContact;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactDataDomainMapper {

    public static DomainContact transformFromContact(Contact contact) {
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

    public static Contact transformFromDomain(DomainContact domainContact) {
        if (domainContact == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }

        Contact contact = new Contact();

        contact.setName(domainContact.getName());
        contact.setPhone(domainContact.getPhone());
        contact.setPhotoURI(domainContact.getPhotoURI());
        contact.setLookupKey(domainContact.getLookupKey());

        return contact;

    }



    public static List<DomainContact> transformAllFromContactsToDomain(List<Contact> contactList) {
        List<DomainContact> domainContacts;

        if (contactList != null && !contactList.isEmpty()) {
            domainContacts = new ArrayList<>();
            for (Contact contact : contactList) {
                domainContacts.add(transformFromContact(contact));
            }
        } else {
            domainContacts = Collections.emptyList();
        }

        return domainContacts;
    }

    public static List<Contact> transformAllFromDomainToContacts(List<DomainContact> domainContactList) {
        List<Contact> contacts;

        if (domainContactList != null && !domainContactList.isEmpty()) {
            contacts = new ArrayList<>();
            for (DomainContact domainContact : domainContactList) {
                contacts.add(transformFromDomain(domainContact));
            }
        } else {
            contacts = Collections.emptyList();
        }

        return contacts;
    }


}
