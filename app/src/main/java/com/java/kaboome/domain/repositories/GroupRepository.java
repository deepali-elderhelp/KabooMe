package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;

public interface GroupRepository {

    LiveData<DomainResource<DomainGroup>> getGroup(String groupId);

    LiveData<DomainResource<DomainGroupUser>> createNewGroup(DomainGroup domainGroup);

//    void updateGroup(final String groupId, final String groupName, int privacy, final String action);

    LiveData<DomainUpdateResource<String>> updateGroup(final DomainGroup group, final String action);

    LiveData<DomainUpdateResource<String>> deleteGroup(final String groupId, final String action);

    LiveData<DomainUpdateResource<String>> deleteGroupUser(String groupId, String userId, String groupUserId, String action);

//    void updateGroupNameAndPrivacy(String groupId, String groupName, int privacy);
//
//    void updateGroupDescription(String groupId, String groupDescription);
//
//    void updateGroupExpiry(String groupId, Long groupExpiry);

    void deleteGroupFromCache(String groupId);

    void removeGroupFromCache(String groupId);

    void deleteGroupUserFromCache(String groupId, String groupUserId);
}
