package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainContact;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;

import java.util.List;

public interface ContactsRepository {

    List<DomainContact> fetchContacts();

    Void inviteSelectedContacts(List<DomainContact> domainContacts, DomainInvitation invitation);
}
