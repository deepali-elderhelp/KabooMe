package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;

import java.util.List;

public interface GroupsListRepository {

    LiveData<DomainResource<List<DomainGroup>>> getGroupsList(String groupNameOrId, String searchText, boolean goToServer);
}
