//package com.java.kaboome.domain.usecases;
//
//import android.util.Log;
//
//import androidx.lifecycle.LiveData;
//
//import com.java.kaboome.domain.entities.DomainMessage;
//import com.java.kaboome.domain.repositories.MessagesListRepository;
//
//import java.util.List;
//
//public class GetGroupMessagesAfterLastAccessSingleUseCase extends BaseSingleUseCase<List<DomainMessage>, GetGroupMessagesAfterLastAccessSingleUseCase.Params> {
//
//    private static final String TAG = "KMGetGrpMsgsLstSingleUC";
//
//    private MessagesListRepository messagesListRepository;
//
//    public GetGroupMessagesAfterLastAccessSingleUseCase(MessagesListRepository messagesListRepository) {
//        this.messagesListRepository = messagesListRepository;
//    }
//
//    @Override
//    protected List<DomainMessage> executeUseCase(Params params) {
//        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getGroupMessagesSentAfterLastAccessCache(params.groupId, params.lastAccessTime);
//    }
//
//    public static final class Params {
//
//        private final String groupId;
//
//        private final Long lastAccessTime;
//
//        private Params(String groupId, Long lastAccessTime) {
//            this.groupId = groupId;
//            this.lastAccessTime = lastAccessTime;
//        }
//
//        public static GetGroupMessagesAfterLastAccessSingleUseCase.Params getMessagesAfterLastAccessForGroupCache(String groupId, Long lastAccessTime) {
//            return new GetGroupMessagesAfterLastAccessSingleUseCase.Params(groupId, lastAccessTime);
//        }
//    }
//
//}
