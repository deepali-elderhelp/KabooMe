package com.java.kaboome.domain.usecases;

import com.java.kaboome.constants.MessageGroupsConstants;
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
        return messagesListRepository.getLastMessageForConvFromCacheSingle(params.groupId, params.userId);
    }

    public static final class Params {
        private final String groupId;
        private final String userId;

        private Params(String groupId, String userId) {
            this.groupId = groupId;
            this.userId = userId;
        }

        public static Params forGroupConversation(String groupId, String userId){
            return new Params(groupId, userId);
        }
    }
}
