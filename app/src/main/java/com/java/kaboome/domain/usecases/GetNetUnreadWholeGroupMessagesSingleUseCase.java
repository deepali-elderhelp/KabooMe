package com.java.kaboome.domain.usecases;

import android.util.Log;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;

public class GetNetUnreadWholeGroupMessagesSingleUseCase extends BaseSingleUseCase<List<DomainMessage>, GetNetUnreadWholeGroupMessagesSingleUseCase.Params> {

    private static final String TAG = "KMGetNetUnrdMsgsSngleUC";

    private MessagesListRepository messagesListRepository;

    public GetNetUnreadWholeGroupMessagesSingleUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected List<DomainMessage> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getNetUnreadGroupMessagesCache(params.groupId);
        return messagesListRepository.getUnreadMessagesForWholeGroupFromCacheSingle(params.groupId);
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetNetUnreadWholeGroupMessagesSingleUseCase.Params getNetUnreadMessagesCacheForGroup(String groupId) {
            return new GetNetUnreadWholeGroupMessagesSingleUseCase.Params(groupId);
        }
    }

}
