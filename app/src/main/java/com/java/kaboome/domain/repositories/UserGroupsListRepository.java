package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;

import java.util.List;

public interface UserGroupsListRepository {

    LiveData<DomainResource<List<DomainUserGroup>>> getGroupsList();

    LiveData<List<DomainUserGroup>> getGroupsListOnlyFromCache();

    List<DomainUserGroup> getGroupsListOnlyFromCacheNonLive();

    void addNewGroupToCache(DomainUserGroup domainUserGroup);

    void updateUserGroupLastAccessed(String groupId, Long newLastAccessed);

    void updateUserGroupCacheClearTS(String groupId, Long newCacheClearTS);

    void updateUserGroupLastAdminAccessed(String groupId, Long newLastAccessed);

    void updateUserGroupAdminCacheClearTS(String groupId, Long newCacheClearTS);

    void removeDeletedUserGroupsFromCache(String userId);

//    void updateUserGroupLastMessageTS(String groupId, Long newLastMessageTS);


}
