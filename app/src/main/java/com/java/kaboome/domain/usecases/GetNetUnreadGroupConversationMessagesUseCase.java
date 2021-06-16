package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;

public class GetNetUnreadGroupConversationMessagesUseCase extends BaseUseCase<List<DomainMessage>, GetNetUnreadGroupConversationMessagesUseCase.Params> {

    private static final String TAG = "KMGetUGrCvMsgsUseCase";

    private MessagesListRepository messagesListRepository;

    public GetNetUnreadGroupConversationMessagesUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected LiveData<List<DomainMessage>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getNetUnreadGroupConversationMessages(params.groupId, params.sentTo);
        return messagesListRepository.getUnreadMessagesForConvFromCacheLiveData(params.groupId, params.sentTo);
    }

    public static final class Params {

        private final String groupId;
        private final String sentTo;

        private Params(String groupId, String sentTo) {
            this.groupId = groupId;
            this.sentTo = sentTo;
        }

        public static GetNetUnreadGroupConversationMessagesUseCase.Params getNetUnreadMessagesForGroup(String groupId, String sentTo) {
            return new GetNetUnreadGroupConversationMessagesUseCase.Params(groupId, sentTo);
        }
    }

}
