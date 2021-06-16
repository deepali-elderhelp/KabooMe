package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.repositories.GroupRequestRepository;

import java.util.List;

public class GetUserGroupRequestsListUseCase extends BaseUseCase<List<DomainGroupRequest>, GetUserGroupRequestsListUseCase.Params> {

    private static final String TAG = "KMGetGrpReqsLstUseCase";

    private GroupRequestRepository groupRequestRepository;

    public GetUserGroupRequestsListUseCase(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }

    @Override
    protected LiveData<List<DomainGroupRequest>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting all groups requests list");
        return groupRequestRepository.getRequestsForUserGroup(params.groupId, params.refreshFromServer);
    }


    public static final class Params {

        private final String groupId;
        private final boolean refreshFromServer;


        private Params(String groupId, boolean refreshFromServer ) {
            this.groupId = groupId;
            this.refreshFromServer = refreshFromServer;
        }

        public static GetUserGroupRequestsListUseCase.Params getUserGroupRequests(String groupId, boolean refreshFromServer) {
            return new GetUserGroupRequestsListUseCase.Params(groupId, refreshFromServer);
        }
    }

}
