package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;
import com.java.kaboome.domain.repositories.UserGroupRepository;

public class GetUserGroupConversationOnlyLocalUseCase extends BaseUseCase<DomainUserGroupConversation, GetUserGroupConversationOnlyLocalUseCase.Params> {

    private static final String TAG = "KMGetUGCLocalUseCase";

    private ConversationsRepository conversationsRepository;

    public GetUserGroupConversationOnlyLocalUseCase(ConversationsRepository conversationsRepository) {
        this.conversationsRepository = conversationsRepository;
    }

    @Override
    protected LiveData<DomainUserGroupConversation> executeUseCase(GetUserGroupConversationOnlyLocalUseCase.Params params) {
        Log.d(TAG, "executeUseCase: getting groups list");
        return conversationsRepository.getUserGroupConversationFromCache(params.groupId, params.otherUserId);
    }

    public static final class Params {
        private final String groupId;
        public String otherUserId;

        private Params(String groupId, String otherUserId) {
            this.groupId = groupId;
            this.otherUserId = otherUserId;
        }

        public static GetUserGroupConversationOnlyLocalUseCase.Params forGroup(String groupId, String otherUserId){
            return new GetUserGroupConversationOnlyLocalUseCase.Params(groupId, otherUserId);
        }
    }

}
