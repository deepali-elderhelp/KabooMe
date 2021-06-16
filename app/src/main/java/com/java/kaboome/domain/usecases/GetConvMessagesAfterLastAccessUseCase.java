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
//public class GetConvMessagesAfterLastAccessUseCase extends BaseUseCase<List<DomainMessage>, GetConvMessagesAfterLastAccessUseCase.Params> {
//
//    private static final String TAG = "KMGetConvMsgLastUseCase";
//
//    private MessagesListRepository messagesListRepository;
//
//    public GetConvMessagesAfterLastAccessUseCase(MessagesListRepository messagesListRepository) {
//        this.messagesListRepository = messagesListRepository;
//    }
//
//    @Override
//    protected LiveData<List<DomainMessage>> executeUseCase(Params params) {
//        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getConversationMessagesSentAfterLastAccess(params.groupId, params.userId, params.lastAccessTime);
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
//        public static GetConvMessagesAfterLastAccessUseCase.Params getMessagesAfterLastAccessForConv(String groupId, String userId, Long lastAccessTime) {
//            return new GetConvMessagesAfterLastAccessUseCase.Params(groupId, userId, lastAccessTime);
//        }
//    }
//
//}
