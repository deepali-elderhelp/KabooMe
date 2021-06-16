package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupRepository;


public class DeleteGroupUseCase extends BaseUseCase<DomainUpdateResource<String>, DeleteGroupUseCase.Params> {

    private static final String TAG = "KMDeleteGroupUseCase";
    private GroupRepository groupRepository;

    public DeleteGroupUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.groupRepository.deleteGroup(params.groupId, params.action);
    }

    public static final class Params {


        private final String groupId;

        private final String action;

        private Params(String groupId, String action) {
            this.groupId = groupId;
            this.action = action;
        }

        public static Params groupToBeDeleted(String groupId, String action) {
            return new Params(groupId, action);
        }
    }
}
