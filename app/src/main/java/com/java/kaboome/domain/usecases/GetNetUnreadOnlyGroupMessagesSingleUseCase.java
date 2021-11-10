package com.java.kaboome.domain.usecases;

import android.util.Log;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;

public class GetNetUnreadOnlyGroupMessagesSingleUseCase extends BaseSingleUseCase<List<DomainMessage>, GetNetUnreadOnlyGroupMessagesSingleUseCase.Params> {

    private static final String TAG = "KMGetNetUnrdMsgsSngleUC";

    private MessagesListRepository messagesListRepository;

    public GetNetUnreadOnlyGroupMessagesSingleUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected List<DomainMessage> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getNetUnreadGroupMessagesCache(params.groupId);
        return messagesListRepository.getUnreadMessagesForOnlyGroupFromCacheSingle(params.groupId);
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetNetUnreadOnlyGroupMessagesSingleUseCase.Params getNetUnreadMessagesCacheForGroup(String groupId) {
            return new GetNetUnreadOnlyGroupMessagesSingleUseCase.Params(groupId);
        }
    }

}
