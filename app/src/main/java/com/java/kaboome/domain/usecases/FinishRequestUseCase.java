package com.java.kaboome.domain.usecases;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.domain.repositories.InvitationsListRepository;

import java.util.List;


public class FinishRequestUseCase extends BaseUseCase<DomainResource<List<DomainGroupRequest>>, FinishRequestUseCase.Params> {

    private static final String TAG = "KMFinishReqUseCase";
    private GroupRequestRepository groupRequestRepository;

    public FinishRequestUseCase(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }


    @Override
    protected LiveData<DomainResource<List<DomainGroupRequest>>> executeUseCase(Params params) {
        return this.groupRequestRepository.finishRequestForGroup(params.groupId, params.domainGroupRequest, params.accept, params.groupName, params.privateGroup);
    }




    public static final class Params {

        private final String groupId;

        private final DomainGroupRequest domainGroupRequest;

        private final String groupName;

        private final String privateGroup;

        private final boolean accept;

        private Params(String groupId, DomainGroupRequest domainGroupRequest,String groupName, String privateGroup, boolean accept ) {
            this.groupId = groupId;
            this.domainGroupRequest = domainGroupRequest;
            this.groupName = groupName;
            this.privateGroup = privateGroup;
            this.accept = accept;
        }

        public static FinishRequestUseCase.Params finishForGroupRequest(String groupId, DomainGroupRequest domainGroupRequest,String groupName, String privateGroup, boolean accept) {
            return new FinishRequestUseCase.Params(groupId, domainGroupRequest, groupName, privateGroup, accept);
        }
    }
}


//public class RejectInvitationUseCase extends BaseUseCase<DomainDeleteResource<String>, RejectInvitationUseCase.Params> {
//
//    private static final String TAG = "KMRejectInviUseCase";
//    private InvitationsListRepository invitationsListRepository;
//
//    public RejectInvitationUseCase(InvitationsListRepository invitationsListRepository) {
//        this.invitationsListRepository = invitationsListRepository;
//    }
//
//
//    @Override
//    protected LiveData<DomainDeleteResource<String>> executeUseCase(RejectInvitationUseCase.Params params) {
//        return this.invitationsListRepository.rejectInvitation(params.groupId);
//    }
//
//
//
//
//    public static final class Params {
//
//        private final String groupId;
//
//        private Params(String groupId) {
//            this.groupId = groupId;
//        }
//
//        public static RejectInvitationUseCase.Params forGroup(String groupId) {
//            return new RejectInvitationUseCase.Params(groupId);
//        }
//    }
//}

