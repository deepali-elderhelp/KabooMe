package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

public class GetConversationLastMessageCache extends BaseSingleUseCase<DomainMessage, GetConversationLastMessageCache.Params> {

    private static final String TAG = "KMConvLstMsgUseCase";
    private MessagesListRepository messagesListRepository;


    public GetConversationLastMessageCache(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }



    @Override
    protected DomainMessage executeUseCase(Params params) {
//        return messagesListRepository.getLatestMessageInCache(params.groupId, MessageGroupsConstants.USER_ADMIN_MESSAGES, params.userId);
        return messagesListRepository.getLastMessageForConvFromCacheSingle(params.groupId, params.userId, params.includeDeleted);
    }

    public static final class Params {
        private final String groupId;
        private final String userId;
        private final boolean includeDeleted;

        private Params(String groupId, String userId, boolean includeDeleted) {
            this.groupId = groupId;
            this.userId = userId;
            this.includeDeleted = includeDeleted;
        }

        public static Params forGroupConversation(String groupId, String userId, boolean includeDeleted){
            return new Params(groupId, userId, includeDeleted);
        }
    }
}
