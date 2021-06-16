package com.java.kaboome.domain.usecases;

import android.util.Log;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;

public class GetGroupConversationFromCacheSingleUseCase extends BaseSingleUseCase<List<DomainUserGroupConversation>, GetGroupConversationFromCacheSingleUseCase.Params> {

    private static final String TAG = "KMGGCFCSUC";

    private ConversationsRepository conversationsRepository;

    public GetGroupConversationFromCacheSingleUseCase(ConversationsRepository conversationsRepository) {
        this.conversationsRepository = conversationsRepository;
    }

    @Override
    protected List<DomainUserGroupConversation> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
        return conversationsRepository.getConversationsForUserGroupsFromCache(params.groupId);
    }

    public static final class Params {

        private final String groupId;


        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetGroupConversationFromCacheSingleUseCase.Params getConversationsForGroup(String groupId) {
            return new GetGroupConversationFromCacheSingleUseCase.Params(groupId);
        }
    }

}
