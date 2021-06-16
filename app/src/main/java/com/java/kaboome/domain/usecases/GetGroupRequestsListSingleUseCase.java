package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupRequestRepository;

import java.util.List;

/**
 * This class returns the requests for a group from the cache as a single non-live data
 */
public class GetGroupRequestsListSingleUseCase extends BaseSingleUseCase<List<DomainGroupRequest>, GetGroupRequestsListSingleUseCase.Params> {

    private static final String TAG = "KMGetGrpReqListSingleUC";

    private GroupRequestRepository groupRequestRepository;

    public GetGroupRequestsListSingleUseCase(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }

    @Override
    protected List<DomainGroupRequest> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
        return groupRequestRepository.getRequestsForGroupSingle(params.groupId);
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetGroupRequestsListSingleUseCase.Params getRequestsForGroup(String groupId) {
            return new GetGroupRequestsListSingleUseCase.Params(groupId);
        }
    }

}
