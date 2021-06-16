package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainDeleteResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupRepository;


public class DeleteGroupUserUseCase extends BaseUseCase<DomainUpdateResource<String>, DeleteGroupUserUseCase.Params> {

    private static final String TAG = "KMDeleteGroupUseCase";
    private GroupRepository groupRepository;

    public DeleteGroupUserUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.groupRepository.deleteGroupUser(params.groupId, params.userId, params.groupUserId, params.action);
    }

    public static final class Params {


        private final String groupId;
        private final String userId;
        private final String groupUserId;

        private final String action;

        private Params(String groupId, String userId, String groupUserId, String action) {
            this.groupId = groupId;
            this.userId = userId;
            this.groupUserId = groupUserId;
            this.action = action;
        }

        public static Params groupToBeDeleted(String groupId, String userId, String groupUserId, String action) {
            return new Params(groupId, userId, groupUserId, action);
        }
    }
}
