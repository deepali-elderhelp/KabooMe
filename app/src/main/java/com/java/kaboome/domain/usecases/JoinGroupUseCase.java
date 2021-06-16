package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;


public class JoinGroupUseCase extends BaseUseCase<DomainUpdateResource<String>, JoinGroupUseCase.Params> {

    private static final String TAG = "KMJoinGroupUseCase";
    private UserGroupRepository userGroupRepository;

    public JoinGroupUseCase(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.userGroupRepository.addUserToTheGroup(params.userGroup, params.action);
    }

    public static final class Params {


        private final DomainUserGroup userGroup;

        private final String action;

        private Params(DomainUserGroup userGroup, String action) {
            this.userGroup = userGroup;
            this.action = action;
        }

        public static Params groupUpdated(DomainUserGroup userGroup, String action) {
            return new Params(userGroup, action);
        }
    }
}
