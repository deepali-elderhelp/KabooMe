package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;

/**
 * This class is needed to create the Pending Invitation in local DB after the user has
 * made a request to join a private group.
 * The whole purpose of calling this usecase is to update the local invitation db so that
 * the notification badge shows appropriate number
 * Once the user goes to Invitation list, the whole data is anyways dumped and put again
 * so that the user sees the latest ones
 */
public class AddNewInvitationUseCase extends BaseSingleUseCase<Void, AddNewInvitationUseCase.Params> {

    private static final String TAG = "KMAddNewMsgUseCase";
    private InvitationsListRepository invitationsListRepository;

    public AddNewInvitationUseCase(InvitationsListRepository invitationsListRepository) {
        this.invitationsListRepository = invitationsListRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.invitationsListRepository.addNewInvitation(params.invitation);
        return null;
    }

    public static final class Params {

        private final DomainInvitation invitation;

        private Params(DomainInvitation invitation) {
            this.invitation = invitation;
        }

        public static Params newInvitation(DomainInvitation invitation) {
            return new Params(invitation);
        }
    }
}
