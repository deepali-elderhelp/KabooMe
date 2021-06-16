package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;

public class GetNetUnreadGroupAllConversationMessagesUseCase extends BaseUseCase<List<DomainMessage>, GetNetUnreadGroupAllConversationMessagesUseCase.Params> {

    private static final String TAG = "KMGetAllUnrdConvUseCase";

    private MessagesListRepository messagesListRepository;

    public GetNetUnreadGroupAllConversationMessagesUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected LiveData<List<DomainMessage>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
        return messagesListRepository.getUnreadMessagesForAllConvFromCacheLiveData(params.groupId);
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetNetUnreadGroupAllConversationMessagesUseCase.Params getNetUnreadAllConvMessagesForGroup(String groupId) {
            return new GetNetUnreadGroupAllConversationMessagesUseCase.Params(groupId);
        }
    }

}
