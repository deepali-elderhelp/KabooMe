package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;

import java.util.List;

public interface GroupRequestRepository {

    LiveData<DomainUpdateResource<String>> createRequest(DomainGroupRequest groupRequest, String groupName, String privateGroup, String action);

    LiveData<List<DomainGroupRequest>> getRequestsForUserGroups(boolean refreshFromServer); //for all user groups

    LiveData<List<DomainGroupRequest>> getRequestsForUserGroup(String groupId, boolean refreshFromServer); //for one user group

    LiveData<DomainResource<List<DomainGroupRequest>>> getRequestsForGroup(String groupId);

    List<DomainGroupRequest> getRequestsForGroupSingle(String groupId);


    LiveData<DomainResource<List<DomainGroupRequest>>> finishRequestForGroup(String groupId, DomainGroupRequest domainGroupRequest,
                                                                             boolean accept,
                                                                             String groupName,
                                                                             String privateGroup);

    void deleteRequestOnlyLocal(String userId, String groupId);

    void deleteAllRequestsForGroupOnlyLocal(String groupId);

    void addRequestOnlyLocal(DomainGroupRequest domainGroupRequest);
}
