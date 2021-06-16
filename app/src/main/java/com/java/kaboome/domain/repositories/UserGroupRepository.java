package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainDeleteResource;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;

public interface UserGroupRepository {

    LiveData<DomainUpdateResource<String>> addUserToTheGroup(final DomainUserGroup userGroup, String action);

    Void updateUserGroupCache(final DomainUserGroup userGroup, String action);

    Long getUserGroupLastAccessed(String groupId);

    LiveData<DomainUserGroup> getUserGroupFromCache(String groupId);

    void removeUserGroup(String groupId);


}
