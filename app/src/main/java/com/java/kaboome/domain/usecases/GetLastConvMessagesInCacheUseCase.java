package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

public class GetLastConvMessagesInCacheUseCase extends BaseUseCase<DomainMessage, GetLastConvMessagesInCacheUseCase.Params> {

    private static final String TAG = "KMGtConvMsgLstAsLVUCase";

    private MessagesListRepository messagesListRepository;

    public GetLastConvMessagesInCacheUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected LiveData<DomainMessage> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getLatestConvMessageInCacheAsLiveData(params.groupId, params.userId);
        return messagesListRepository.getLastMessageForConvFromCacheLiveData(params.groupId, params.userId);
    }

    public static final class Params {

        private final String groupId;
        private final String userId;


        private Params(String groupId, String userId) {
            this.groupId = groupId;
            this.userId = userId;
        }

        public static GetLastConvMessagesInCacheUseCase.Params forConversation(String groupId, String userId) {
            return new GetLastConvMessagesInCacheUseCase.Params(groupId, userId);
        }
    }

}
