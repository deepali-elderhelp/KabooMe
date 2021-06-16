package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

/**
 * This class returns the last User Group messages sent to "Group"
 */
public class GetLastOnlyGroupMessageInCacheSingleUseCase extends BaseSingleUseCase<DomainMessage, GetLastOnlyGroupMessageInCacheSingleUseCase.Params> {

    private static final String TAG = "KMUpdGrpLstMsgUseCase";
    private MessagesListRepository messagesListRepository;


    public GetLastOnlyGroupMessageInCacheSingleUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }



    @Override
    protected DomainMessage executeUseCase(Params params) {
//        return messagesListRepository.getLatestGroupMessageInCache(params.groupId, params.sentTo);
        return messagesListRepository.getLastMessageForOnlyGroupFromCacheSingle(params.groupId);
    }

    public static final class Params {
        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static Params forGroup(String groupId){
            return new Params(groupId);
        }
    }
}
