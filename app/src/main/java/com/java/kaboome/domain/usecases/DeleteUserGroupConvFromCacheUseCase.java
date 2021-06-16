package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.GroupRepository;

/**
 * This class is needed when the notification for a group deleted is received.
 * This group is already deleted in the server, it only needs to be set deleted true in the cache.
 */

public class DeleteUserGroupConvFromCacheUseCase extends BaseSingleUseCase<Void, DeleteUserGroupConvFromCacheUseCase.Params> {

    private static final String TAG = "KMDelLocalUGConvUseCase";
    private ConversationsRepository conversationsRepository;

    public DeleteUserGroupConvFromCacheUseCase(ConversationsRepository conversationsRepository) {
        this.conversationsRepository = conversationsRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.conversationsRepository.deleteUserGroupConversation(params.groupId, params.groupUserId);
        return null;
    }

    public static final class Params {

        private final String groupId;
        private final String groupUserId;


        private Params(String groupId, String groupUserId) {
            this.groupId = groupId;
            this.groupUserId = groupUserId;
        }

        public static Params deleteUserGroupConversation(String groupId, String groupUserId) {
            return new Params(groupId, groupUserId);
        }
    }
}
