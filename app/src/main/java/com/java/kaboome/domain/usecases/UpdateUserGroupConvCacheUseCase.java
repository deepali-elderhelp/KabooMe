package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;

public class UpdateUserGroupConvCacheUseCase extends BaseSingleUseCase<Void, UpdateUserGroupConvCacheUseCase.Params> {

    private static final String TAG = "KMUpdUsrGrpConvCacheUseCase";
    private ConversationsRepository conversationsRepository;


    public UpdateUserGroupConvCacheUseCase(ConversationsRepository conversationsRepository) {
        this.conversationsRepository = conversationsRepository;
    }



    @Override
    protected Void executeUseCase(Params params) {
        conversationsRepository.updateUserGroupConversationDetails(params.groupId, params.otherUserId, params.otherUserName, params.otherUserRole, params.userImageTS);
        return null;
    }

    public static final class Params {
        private final String groupId;
        private final String otherUserId;
        private final String otherUserName;
        private final String otherUserRole;
        private final Long userImageTS;

        private Params(String groupId, String otherUserId, String otherUserName, String otherUserRole, Long userImageTS) {
            this.groupId = groupId;
            this.otherUserId = otherUserId;
            this.otherUserName = otherUserName;
            this.otherUserRole = otherUserRole;
            this.userImageTS = userImageTS;
        }

        public static Params forUserGroupConv(String groupId, String otherUserId, String otherUserName, String otherUserRole, Long userImageTS){
            return new Params(groupId, otherUserId, otherUserName, otherUserRole, userImageTS);
        }
    }
}
