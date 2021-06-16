package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.domain.repositories.UserGroupsRequestsListRepository;

import java.util.List;

public class GetUserGroupsRequestsListUseCase extends BaseUseCase<List<DomainGroupRequest>, GetUserGroupsRequestsListUseCase.Params> {

    private static final String TAG = "KMGetGrpsReqsLstUseCase";

    private GroupRequestRepository groupRequestRepository;

    public GetUserGroupsRequestsListUseCase(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }

    @Override
    protected LiveData<List<DomainGroupRequest>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting all groups requests list");
        return groupRequestRepository.getRequestsForUserGroups(params.refreshFromServer);
    }


    public static final class Params {

        private final boolean refreshFromServer;


        private Params(boolean refreshFromServer) {
            this.refreshFromServer = refreshFromServer;
        }

        public static GetUserGroupsRequestsListUseCase.Params refreshDataFromServer(boolean refreshFromServer) {
            return new GetUserGroupsRequestsListUseCase.Params(refreshFromServer);
        }
    }

}
