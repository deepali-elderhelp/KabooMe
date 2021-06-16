package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupRequestRepository;

import java.util.List;

public class GetGroupConvsListUseCase extends BaseUseCase<DomainResource<List<DomainGroupRequest>>, GetGroupConvsListUseCase.Params> {

    private static final String TAG = "KMGetGrpConvsListUC";

    private GroupRequestRepository groupRequestRepository;

    public GetGroupConvsListUseCase(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }

    @Override
    protected LiveData<DomainResource<List<DomainGroupRequest>>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
        return groupRequestRepository.getRequestsForGroup(params.groupId);
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetGroupConvsListUseCase.Params getRequestsForGroup(String groupId) {
            return new GetGroupConvsListUseCase.Params(groupId);
        }
    }

}
