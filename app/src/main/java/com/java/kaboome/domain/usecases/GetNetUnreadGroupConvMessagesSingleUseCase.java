package com.java.kaboome.domain.usecases;

import android.util.Log;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;

public class GetNetUnreadGroupConvMessagesSingleUseCase extends BaseSingleUseCase<List<DomainMessage>, GetNetUnreadGroupConvMessagesSingleUseCase.Params> {

    private static final String TAG = "KMGetNetUnrdMsgsSngleUC";

    private MessagesListRepository messagesListRepository;

    public GetNetUnreadGroupConvMessagesSingleUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected List<DomainMessage> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getNetUnreadGroupConversationMessagesCache(params.groupId, params.sentTo);
        return messagesListRepository.getUnreadMessagesForConvFromCacheSingle(params.groupId, params.sentTo);
    }

    public static final class Params {

        private final String groupId;
        private final String sentTo;

        private Params(String groupId, String sentTo) {
            this.groupId = groupId;
            this.sentTo = sentTo;
        }

        public static GetNetUnreadGroupConvMessagesSingleUseCase.Params getNetUnreadMessagesCacheForGroup(String groupId, String sentTo) {
            return new GetNetUnreadGroupConvMessagesSingleUseCase.Params(groupId, sentTo);
        }
    }

}
