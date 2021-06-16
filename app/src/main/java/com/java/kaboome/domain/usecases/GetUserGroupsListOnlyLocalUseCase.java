package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;

import java.util.List;

public class GetUserGroupsListOnlyLocalUseCase extends BaseUseCase<List<DomainUserGroup>, Void> {

    private static final String TAG = "KMGetGroupsListUseCase";

    private UserGroupsListRepository userGroupsListRepository;

    public GetUserGroupsListOnlyLocalUseCase(UserGroupsListRepository userGroupsListRepository) {
        this.userGroupsListRepository = userGroupsListRepository;
    }

    @Override
    protected LiveData<List<DomainUserGroup>> executeUseCase(Void unused) {
        Log.d(TAG, "executeUseCase: getting groups list");
        return userGroupsListRepository.getGroupsListOnlyFromCache();
    }

}
