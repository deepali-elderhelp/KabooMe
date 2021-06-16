package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupRepository;


public class UpdateGroupUseCase extends BaseUseCase<DomainUpdateResource<String>, UpdateGroupUseCase.Params> {

    private static final String TAG = "KMUpdateGroupUseCase";
    private GroupRepository groupRepository;

    public UpdateGroupUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.groupRepository.updateGroup(params.group, params.action);
    }

    public static final class Params {


        private final DomainGroup group;

        private final String action;

        private Params(DomainGroup group, String action) {
            this.group = group;
            this.action = action;
        }

        public static Params groupUpdated(DomainGroup group, String action) {
            return new Params(group, action);
        }
    }
}
