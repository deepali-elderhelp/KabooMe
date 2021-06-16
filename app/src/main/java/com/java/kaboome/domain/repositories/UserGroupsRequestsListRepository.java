package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;

import java.util.List;

public interface UserGroupsRequestsListRepository {

    LiveData<DomainResource<List<DomainGroupRequest>>> getUserGroupsRequestsLists();

}
