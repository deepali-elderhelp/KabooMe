package com.java.kaboome.domain.usecases;

import androidx.lifecycle.LiveData;

import com.java.kaboome.constants.MessageGroupsConstants;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;


public class GetMessagesUseCase extends BaseUseCase<DomainResource<List<DomainMessage>>, GetMessagesUseCase.Params> {

    private static final String TAG = "KMRefreshMesgsUseCase";
    private MessagesListRepository messagesListRepository;

    public GetMessagesUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }


    @Override
    protected LiveData<DomainResource<List<DomainMessage>>> executeUseCase(Params params) {
        return this.messagesListRepository.getGroupMessages(params.groupId, params.lastAccessed, params.cacheClearTS, params.limit, params.scanDirection, params.messageGroupsConstants, params.userId);
    }

    public static final class Params {

        private final String groupId;
        private final Long lastAccessed;
        private final Long cacheClearTS;
        private final int limit;
        private final String scanDirection;
        private final MessageGroupsConstants messageGroupsConstants;
        private final String userId;

        private Params(String groupId, Long lastAccessed, Long cacheClearTS, int limit, String scanDirection, MessageGroupsConstants messageGroupsConstants, String userId) {
            this.groupId = groupId;
            this.lastAccessed = lastAccessed;
            this.limit = limit;
            this.scanDirection = scanDirection;
            this.cacheClearTS = cacheClearTS;
            this.messageGroupsConstants = messageGroupsConstants;
            this.userId = userId;
        }

        public static Params forGroupWithDetails(String groupId, Long lastAccessed, Long cacheClearTS, int limit, String scanDirection,
                                                 MessageGroupsConstants messageGroupsConstants, String userId) {
            return new Params(groupId, lastAccessed, cacheClearTS, limit, scanDirection, messageGroupsConstants, userId);
        }
    }
}
