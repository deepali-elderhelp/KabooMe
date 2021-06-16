package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;

public class GetUserGroupLastAccessedCache extends BaseSingleUseCase<Long, GetUserGroupLastAccessedCache.Params> {

    private static final String TAG = "KMUpdGrpLstMsgUseCase";
    private UserGroupRepository userGroupRepository;


    public GetUserGroupLastAccessedCache(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }



    @Override
    protected Long executeUseCase(Params params) {
        return userGroupRepository.getUserGroupLastAccessed(params.groupId);
    }

    public static final class Params {
        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static Params forGroup(String groupId){
            return new Params(groupId);
        }
    }
}
