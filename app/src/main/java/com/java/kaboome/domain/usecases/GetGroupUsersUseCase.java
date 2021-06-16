package com.java.kaboome.domain.usecases;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupsUsersRepository;

import java.util.List;

public class GetGroupUsersUseCase extends BaseUseCase<DomainResource<List<DomainGroupUser>>, GetGroupUsersUseCase.Params> {

    private GroupsUsersRepository groupsUsersRepository;

    public GetGroupUsersUseCase(GroupsUsersRepository groupsUsersRepository) {
        this.groupsUsersRepository = groupsUsersRepository;
    }

    @Override
    protected LiveData<DomainResource<List<DomainGroupUser>>> executeUseCase(Params params) {
        return groupsUsersRepository.getGroupsUsers(params.groupId, params.fetchFromServer);
    }


    public static final class Params {

        private final String groupId;
        private boolean fetchFromServer;

        private Params(String groupId, boolean fetchFromServer) {
            this.groupId = groupId;
            this.fetchFromServer = fetchFromServer;
        }

        public static GetGroupUsersUseCase.Params forGroup(String groupId, boolean fetchFromServer) {
            return new GetGroupUsersUseCase.Params(groupId, fetchFromServer);
        }
    }


}
