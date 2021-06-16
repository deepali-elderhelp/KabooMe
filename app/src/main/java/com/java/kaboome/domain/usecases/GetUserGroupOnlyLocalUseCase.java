package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;

import java.util.List;

public class GetUserGroupOnlyLocalUseCase extends BaseUseCase<DomainUserGroup, GetUserGroupOnlyLocalUseCase.Params> {

    private static final String TAG = "KMGetUGLocalUseCase";

    private UserGroupRepository userGroupRepository;

    public GetUserGroupOnlyLocalUseCase(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    protected LiveData<DomainUserGroup> executeUseCase(GetUserGroupOnlyLocalUseCase.Params params) {
        Log.d(TAG, "executeUseCase: getting groups list");
        return userGroupRepository.getUserGroupFromCache(params.groupId);
    }

    public static final class Params {
        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetUserGroupOnlyLocalUseCase.Params forGroup(String groupId){
            return new GetUserGroupOnlyLocalUseCase.Params(groupId);
        }
    }

}
