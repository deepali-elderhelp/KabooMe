package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.InvitationsListRepository;


public class RejectInvitationCacheSingleUseCase extends BaseSingleUseCase<Void, RejectInvitationCacheSingleUseCase.Params> {

    private static final String TAG = "KMRejInvCacSingleUC";
    private InvitationsListRepository invitationsListRepository;

    public RejectInvitationCacheSingleUseCase(InvitationsListRepository invitationsListRepository) {
        this.invitationsListRepository = invitationsListRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.invitationsListRepository.rejectInvitationFromCache(params.groupId);
        return null;
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static Params rejectInviForGroup(String groupId) {
            return new Params(groupId);
        }
    }
}
