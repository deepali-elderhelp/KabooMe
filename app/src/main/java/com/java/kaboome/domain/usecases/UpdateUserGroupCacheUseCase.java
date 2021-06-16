package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupRepository;

public class UpdateUserGroupCacheUseCase extends BaseSingleUseCase<Void, UpdateUserGroupCacheUseCase.Params> {

    private static final String TAG = "KMUpdUsrGrpCacheUseCase";
    private UserGroupRepository userGroupRepository;


    public UpdateUserGroupCacheUseCase(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }



    @Override
    protected Void executeUseCase(Params params) {
        userGroupRepository.updateUserGroupCache(params.userGroup, params.action);
        return null;
    }

    public static final class Params {
        private final DomainUserGroup userGroup;
        private final String action;

        private Params(DomainUserGroup userGroup, String action) {
            this.userGroup = userGroup;
            this.action = action;
        }

        public static Params forUserGroup(DomainUserGroup userGroup, String action){
            return new Params(userGroup, action);
        }
    }
}
