package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.GroupRequestRepository;


public class CreateRequestUseCase extends BaseUseCase<DomainUpdateResource<String>, CreateRequestUseCase.Params> {

    private static final String TAG = "KMCreateRqstUseCase";
    private GroupRequestRepository groupRequestRepository;

    public CreateRequestUseCase(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.groupRequestRepository.createRequest(params.domainGroupRequest, params.groupName, params.privateGroup, params.action);
    }

    public static final class Params {


        private final DomainGroupRequest domainGroupRequest;
        private final String groupName;
        private final String privateGroup;
        private final String action;

        private Params(DomainGroupRequest domainGroupRequest, String groupName, String privateGroup, String action) {
            this.domainGroupRequest = domainGroupRequest;
            this.groupName = groupName;
            this.privateGroup = privateGroup;
            this.action = action;
        }

        public static Params groupRequestedToJoin(DomainGroupRequest domainGroupRequest, String groupName, String privateGroup, String action) {
            return new Params(domainGroupRequest,groupName,privateGroup, action);
        }
    }
}
