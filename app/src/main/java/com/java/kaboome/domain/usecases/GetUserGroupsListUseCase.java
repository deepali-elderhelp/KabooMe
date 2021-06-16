package com.java.kaboome.domain.usecases;

import androidx.lifecycle.LiveData;
import android.util.Log;

import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;

import java.util.List;

public class GetUserGroupsListUseCase extends BaseUseCase<DomainResource<List<DomainUserGroup>>, Void> {

    private static final String TAG = "KMGetGroupsListUseCase";

    private UserGroupsListRepository userGroupsListRepository;

    public GetUserGroupsListUseCase(UserGroupsListRepository userGroupsListRepository) {
        this.userGroupsListRepository = userGroupsListRepository;
    }

    @Override
    protected LiveData<DomainResource<List<DomainUserGroup>>> executeUseCase(Void unused) {
        Log.d(TAG, "executeUseCase: getting groups list");
        return userGroupsListRepository.getGroupsList();
    }

}
