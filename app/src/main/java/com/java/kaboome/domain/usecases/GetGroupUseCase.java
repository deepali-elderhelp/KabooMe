package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupRepository;

public class GetGroupUseCase extends BaseUseCase<DomainResource<DomainGroup>, GetGroupUseCase.Params> {

    private static final String TAG = "KMGetGroupUseCase";

    private GroupRepository groupRepository;

    public GetGroupUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    protected LiveData<DomainResource<DomainGroup>> executeUseCase(GetGroupUseCase.Params params) {
        Log.d(TAG, "executeUseCase: getting group");
        return groupRepository.getGroup(params.groupId);
    }


    public static final class Params {

        private final String groupId;


        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static Params forGroup(String groupId) {
            return new Params(groupId);
        }
    }

}
