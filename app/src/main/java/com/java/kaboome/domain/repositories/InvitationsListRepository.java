package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainDeleteResource;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;

import java.util.List;

public interface InvitationsListRepository {

    LiveData<DomainResource<List<DomainInvitation>>> getInvitationsList(boolean shouldFetchFromServer);

//    LiveData<DomainDeleteResource<String>> rejectInvitation(String groupId);

    LiveData<DomainResource<List<DomainInvitation>>>rejectInvitation(String groupId);

    void addNewInvitation(DomainInvitation invitation);

    void rejectInvitationFromCache(String groupId);
}
