package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

public class GetLastOnlyGroupMessagesInCacheLiveDataUseCase extends BaseUseCase<DomainMessage, GetLastOnlyGroupMessagesInCacheLiveDataUseCase.Params> {

    private static final String TAG = "KMGtGrpMsgsLstAsLVUCase";

    private MessagesListRepository messagesListRepository;

    public GetLastOnlyGroupMessagesInCacheLiveDataUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected LiveData<DomainMessage> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getLatestWholeGroupMessageInCacheAsLiveData(params.groupId);
        return messagesListRepository.getLastMessageForOnlyGroupFromCacheLiveData(params.groupId);
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetLastOnlyGroupMessagesInCacheLiveDataUseCase.Params forGroup(String groupId) {
            return new GetLastOnlyGroupMessagesInCacheLiveDataUseCase.Params(groupId);
        }
    }

}
