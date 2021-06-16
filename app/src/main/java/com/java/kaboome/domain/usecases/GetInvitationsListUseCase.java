package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;

import java.util.List;

public class GetInvitationsListUseCase extends BaseUseCase<DomainResource<List<DomainInvitation>>, GetInvitationsListUseCase.Params> {

    private static final String TAG = "KMGetInviListUseCase";

    private InvitationsListRepository invitationsListRepository;

    public GetInvitationsListUseCase(InvitationsListRepository invitationsListRepository) {
        this.invitationsListRepository = invitationsListRepository;
    }

    @Override
    protected LiveData<DomainResource<List<DomainInvitation>>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting invitations");
        return invitationsListRepository.getInvitationsList(params.shouldFetchFromServer);
    }

    public static final class Params {

        private final boolean shouldFetchFromServer;

        private Params(boolean shouldFetchFromServer) {
            this.shouldFetchFromServer = shouldFetchFromServer;
        }

        public static GetInvitationsListUseCase.Params getInvitationsFromServer(boolean shouldFetchFromServer) {
            return new GetInvitationsListUseCase.Params(shouldFetchFromServer);
        }
    }

}
