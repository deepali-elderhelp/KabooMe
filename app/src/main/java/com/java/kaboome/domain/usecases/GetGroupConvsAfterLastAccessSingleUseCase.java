//package com.java.kaboome.domain.usecases;
//
//import android.util.Log;
//
//import com.java.kaboome.domain.entities.DomainMessage;
//import com.java.kaboome.domain.repositories.MessagesListRepository;
//
//import java.util.List;
//
//public class GetGroupConvsAfterLastAccessSingleUseCase extends BaseSingleUseCase<List<DomainMessage>, GetGroupConvsAfterLastAccessSingleUseCase.Params> {
//
//    private static final String TAG = "KMGetConvMsgsLstSngleUC";
//
//    private MessagesListRepository messagesListRepository;
//
//    public GetGroupConvsAfterLastAccessSingleUseCase(MessagesListRepository messagesListRepository) {
//        this.messagesListRepository = messagesListRepository;
//    }
//
//    @Override
//    protected List<DomainMessage> executeUseCase(Params params) {
//        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getConvMessagesSentAfterLastAccessCache(params.groupId, params.userId, params.lastAccessTime);
//    }
//
//    public static final class Params {
//
//        private final String groupId;
//        private final String userId;
//        private final Long lastAccessTime;
//
//        private Params(String groupId, String userId, Long lastAccessTime) {
//            this.groupId = groupId;
//            this.userId = userId;
//            this.lastAccessTime = lastAccessTime;
//        }
//
//        public static GetGroupConvsAfterLastAccessSingleUseCase.Params getMessagesAfterLastAccessForGroupConvCache(String groupId, String userId, Long lastAccessTime) {
//            return new GetGroupConvsAfterLastAccessSingleUseCase.Params(groupId, userId, lastAccessTime);
//        }
//    }
//
//}
