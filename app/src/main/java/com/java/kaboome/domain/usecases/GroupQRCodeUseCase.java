package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.GroupRepository;

public class GroupQRCodeUseCase extends BaseUseCase<DomainResource<DomainGroup>, GroupQRCodeUseCase.Params> {

    private static final String TAG = "KMGroupQRCodeUseCase";

    private GroupRepository groupRepository;

    @Override
    protected LiveData<DomainResource<DomainGroup>> executeUseCase(GroupQRCodeUseCase.Params params) {
        Log.d(TAG, "executeUseCase: getting group");
        return groupRepository.getGroup(params.groupId);
    }


    public static final class Params {

        private final String groupId;


        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GroupQRCodeUseCase.Params forGroup(String groupId) {
            return new GroupQRCodeUseCase.Params(groupId);
        }
    }
}
