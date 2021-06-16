package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;

public class GetNetUnreadOnlyGroupMessagesUseCase extends BaseUseCase<List<DomainMessage>, GetNetUnreadOnlyGroupMessagesUseCase.Params> {

    private static final String TAG = "KMGetUnrdGrpMsgsUseCase";

    private MessagesListRepository messagesListRepository;

    public GetNetUnreadOnlyGroupMessagesUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }

    @Override
    protected LiveData<List<DomainMessage>> executeUseCase(Params params) {
        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getNetUnreadGroupMessages(params.groupId);
        return messagesListRepository.getUnreadMessagesForOnlyGroupFromCacheLiveData(params.groupId);
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static GetNetUnreadOnlyGroupMessagesUseCase.Params getNetUnreadMessagesForGroup(String groupId) {
            return new GetNetUnreadOnlyGroupMessagesUseCase.Params(groupId);
        }
    }

}
