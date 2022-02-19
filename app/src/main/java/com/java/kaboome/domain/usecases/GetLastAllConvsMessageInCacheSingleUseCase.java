package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

/**
 * This class returns the last User Group messages sent to "Group"
 */
public class GetLastAllConvsMessageInCacheSingleUseCase extends BaseSingleUseCase<DomainMessage, GetLastAllConvsMessageInCacheSingleUseCase.Params> {

    private static final String TAG = "KMAllConvsLstMsgUseCase";
    private MessagesListRepository messagesListRepository;


    public GetLastAllConvsMessageInCacheSingleUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }



    @Override
    protected DomainMessage executeUseCase(Params params) {
//        return messagesListRepository.getLatestGroupMessageInCache(params.groupId, params.sentTo);
        return messagesListRepository.getLastMessageForOnlyGroupFromCacheSingle(params.groupId, params.includeDeleted);
    }

    public static final class Params {
        private final String groupId;
        private final boolean includeDeleted;

        private Params(String groupId, boolean includeDeleted) {
            this.groupId = groupId;
            this.includeDeleted = includeDeleted;
        }

        public static Params forGroup(String groupId, boolean includeDeleted){
            return new Params(groupId, includeDeleted);
        }
    }
}
