package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainContact;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.repositories.ContactsRepository;

import java.util.List;


public class InviteContactsUseCase extends BaseSingleUseCase<Void, InviteContactsUseCase.Params> {

    private static final String TAG = "KMInviteContactsUseCase";
    private ContactsRepository contactsRepository;

    public InviteContactsUseCase(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.contactsRepository.inviteSelectedContacts(params.contacts, params.domainInvitation);
        return null;
    }

    public static final class Params {

        private final List<DomainContact> contacts;
        private final DomainInvitation domainInvitation;

        private Params(List<DomainContact> contacts, DomainInvitation domainInvitation, String action) {
            this.contacts = contacts;
            this.domainInvitation = domainInvitation;
        }

        public static Params inviteContacts(List<DomainContact> contacts, DomainInvitation domainInvitation, String action) {
            return new Params(contacts, domainInvitation, action);
        }
    }


}


