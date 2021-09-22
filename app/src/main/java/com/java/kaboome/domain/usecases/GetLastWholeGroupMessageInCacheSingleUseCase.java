package com.java.kaboome.domain.usecases;

import android.util.Log;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

public class GetLastWholeGroupMessageInCacheSingleUseCase extends BaseSingleUseCase<DomainMessage, GetLastWholeGroupMessageInCacheSingleUseCase.Params> {

    private static final String TAG = "KMGetNetUnrdMsgsSngleUC";

    private MessagesListRepository messagesListRepository;

    public GetLastWholeGroupMessageInCacheSingleUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected DomainMessage executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getNetUnreadGroupMessagesCache(params.groupId);
        return messagesListRepository.getLastMessageForWholeGroupFromCacheSingle(params.groupId, params.includeDeleted);
    }

    public static final class Params {

        private final String groupId;
        private final boolean includeDeleted;

        private Params(String groupId, boolean includeDeleted) {
            this.groupId = groupId;
            this.includeDeleted = includeDeleted;
        }

        public static GetLastWholeGroupMessageInCacheSingleUseCase.Params forGroup(String groupId, boolean includeDeleted) {
            return new GetLastWholeGroupMessageInCacheSingleUseCase.Params(groupId, includeDeleted);
        }
    }

}
