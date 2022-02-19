package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;

import java.util.List;

public interface GroupsUsersRepository {

    LiveData<DomainResource<List<DomainGroupUser>>> getGroupsUsers(String groupId, boolean fetchFromServer);

    LiveData<DomainUpdateResource<String>> updateGroupUser(final DomainGroupUser groupUser, final String action);

//    void updateGroupUserAliasAndRole(String groupId, String userId, String alias, String role);

//    void updateGroupUserToAdmin(String groupId, String userId);
//
//    void removeGroupUser(String groupId, String userId);

    void updateGroupUserCache(DomainGroupUser groupUser, String action);

    DomainGroupUser getGroupUserFromCache(String groupId, String userId);

}
