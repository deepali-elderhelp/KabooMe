package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupsUsersRepository;


public class UpdateGroupUserUseCase extends BaseUseCase<DomainUpdateResource<String>, UpdateGroupUserUseCase.Params> {

    private static final String TAG = "KMUpdateGroupUseCase";
    private GroupsUsersRepository groupsUsersRepository;

    public UpdateGroupUserUseCase(GroupsUsersRepository groupsUsersRepository) {
        this.groupsUsersRepository = groupsUsersRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.groupsUsersRepository.updateGroupUser(params.groupUser, params.action);
    }

    public static final class Params {

//        private final String groupId;
//
//        private final String groupName;
//
//        private final int privacy;
//
//        private final String action;
//
//        private Params(String groupId, String groupName, int privacy, String action) {
//            this.groupId = groupId;
//            this.groupName = groupName;
//            this.privacy = privacy;
//            this.action = action;
//        }
//
//        public static Params groupUpdated(String groupId, String groupName, int privacy, String action) {
//            return new Params(groupId, groupName, privacy, action);
//        }

        private final DomainGroupUser groupUser;

        private final String action;

        private Params(DomainGroupUser groupUser, String action) {
            this.groupUser = groupUser;
            this.action = action;
        }

        public static Params groupUserUpdated(DomainGroupUser groupUser, String action) {
            return new Params(groupUser, action);
        }
    }
}
