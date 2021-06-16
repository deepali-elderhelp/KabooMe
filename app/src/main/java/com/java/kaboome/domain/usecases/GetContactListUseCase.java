package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainContact;
import com.java.kaboome.domain.repositories.ContactsRepository;

import java.util.List;

public class GetContactListUseCase extends BaseSingleUseCase<List<DomainContact>, Void> {

    private static final String TAG = "KMGetContactListUseCase";
    private ContactsRepository contactsRepository;


    public GetContactListUseCase(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
    }

    @Override
    protected List<DomainContact> executeUseCase(Void aVoid) {
        return contactsRepository.fetchContacts();
    }
}
