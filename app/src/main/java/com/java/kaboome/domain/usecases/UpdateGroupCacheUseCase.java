package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.repositories.GroupRepository;

public class UpdateGroupCacheUseCase extends BaseSingleUseCase<Void, UpdateGroupCacheUseCase.Params> {

    private static final String TAG = "KMUpdUsrCacheUseCase";
    private GroupRepository groupRepository;


    public UpdateGroupCacheUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }



    @Override
    protected Void executeUseCase(Params params) {
        groupRepository.updateGroupCache(params.domainGroup, params.action);
        return null;
    }

    public static final class Params {
        private final DomainGroup domainGroup;
        private final String action;

        private Params(DomainGroup domainGroup, String action) {
            this.domainGroup = domainGroup;
            this.action = action;
        }

        public static Params forGroup(DomainGroup domainGroup, String action){
            return new Params(domainGroup, action);
        }
    }
}
