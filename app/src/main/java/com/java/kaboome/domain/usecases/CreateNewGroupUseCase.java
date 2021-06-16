package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupRepository;


public class CreateNewGroupUseCase extends BaseUseCase<DomainResource<DomainGroupUser>, CreateNewGroupUseCase.Params> {

    private static final String TAG = "KMCreateNewGrpUseCase";
    private GroupRepository groupRepository;

    public CreateNewGroupUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    protected LiveData<DomainResource<DomainGroupUser>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.groupRepository.createNewGroup(params.domainGroup);
    }

    public static final class Params {


        private final DomainGroup domainGroup;

        private Params(DomainGroup domainGroup) {
           this.domainGroup = domainGroup;
        }

        public static Params groupToBeCreated(DomainGroup domainGroup) {
            return new Params(domainGroup);
        }
    }
}
