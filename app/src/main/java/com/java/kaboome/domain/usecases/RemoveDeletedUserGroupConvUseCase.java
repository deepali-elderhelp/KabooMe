package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.ConversationsRepository;


public class RemoveDeletedUserGroupConvUseCase extends BaseSingleUseCase<Void, RemoveDeletedUserGroupConvUseCase.Params> {

    private static final String TAG = "KMRmDelUGUseCase";
    private ConversationsRepository conversationsRepository;

    public RemoveDeletedUserGroupConvUseCase(ConversationsRepository conversationsRepository) {
        this.conversationsRepository = conversationsRepository;
    }

    @Override
    protected Void executeUseCase(Params params) {
        this.conversationsRepository.deleteUserGroupConversation(params.groupId, params.conversationId);
        return null;
    }

    public static final class Params {

        private final String groupId;
        private final String conversationId;


        private Params(String groupId, String conversationId) {
            this.groupId = groupId;
            this.conversationId = conversationId;
        }

        public static RemoveDeletedUserGroupConvUseCase.Params forUserGroupConv(String groupId, String conversationId) {
            return new RemoveDeletedUserGroupConvUseCase.Params(groupId, conversationId);
        }
    }

}
