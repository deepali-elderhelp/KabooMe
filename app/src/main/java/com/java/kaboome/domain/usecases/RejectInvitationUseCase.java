package com.java.kaboome.domain.usecases;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.InvitationsListRepository;

import java.util.List;


public class RejectInvitationUseCase extends BaseUseCase<DomainResource<List<DomainInvitation>>, RejectInvitationUseCase.Params> {

    private static final String TAG = "KMRejectInviUseCase";
    private InvitationsListRepository invitationsListRepository;

    public RejectInvitationUseCase(InvitationsListRepository invitationsListRepository) {
        this.invitationsListRepository = invitationsListRepository;
    }


    @Override
    protected LiveData<DomainResource<List<DomainInvitation>>> executeUseCase(Params params) {
        return this.invitationsListRepository.rejectInvitation(params.groupId);
    }




    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static RejectInvitationUseCase.Params forGroup(String groupId) {
            return new RejectInvitationUseCase.Params(groupId);
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

