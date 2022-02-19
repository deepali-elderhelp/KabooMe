package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainDeleteResource;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUser;

public interface UserRepository {

    LiveData<DomainResource<DomainUser>> getUser(String userId);

    DomainUser getUserFromCache(String userId);

    LiveData<DomainUpdateResource<String>> updateUser(DomainUser user, String action);

    void updateUserInCache(DomainUser user);

    void updateUserInCache(DomainUser user, String action);
}
