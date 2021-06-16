package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;

public class GetNetUnreadWholeGroupMessagesUseCase extends BaseUseCase<List<DomainMessage>, GetNetUnreadWholeGroupMessagesUseCase.Params> {

    private static final String TAG = "KMGNUWGMUseCase";

    private MessagesListRepository messagesListRepository;

    public GetNetUnreadWholeGroupMessagesUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected LiveData<List<DomainMessage>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getNetUnreadGroupMessages(params.groupId);
        return messagesListRepository.getUnreadMessagesForWholeGroupFromCacheLiveData(params.groupId);
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetNetUnreadWholeGroupMessagesUseCase.Params getNetUnreadMessagesForGroup(String groupId) {
            return new GetNetUnreadWholeGroupMessagesUseCase.Params(groupId);
        }
    }

}
