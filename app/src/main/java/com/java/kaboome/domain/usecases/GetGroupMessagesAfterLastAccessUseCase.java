//package com.java.kaboome.domain.usecases;
//
//import android.util.Log;
//
//import androidx.lifecycle.LiveData;
//
//import com.java.kaboome.domain.entities.DomainGroupRequest;
//import com.java.kaboome.domain.entities.DomainMessage;
//import com.java.kaboome.domain.entities.DomainResource;
//import com.java.kaboome.domain.repositories.GroupRequestRepository;
//import com.java.kaboome.domain.repositories.MessagesListRepository;
//
//import java.util.List;
//
//public class GetGroupMessagesAfterLastAccessUseCase extends BaseUseCase<List<DomainMessage>, GetGroupMessagesAfterLastAccessUseCase.Params> {
//
//    private static final String TAG = "KMGetGrpMsgsLastUseCase";
//
//    private MessagesListRepository messagesListRepository;
//
//    public GetGroupMessagesAfterLastAccessUseCase(MessagesListRepository messagesListRepository) {
//        this.messagesListRepository = messagesListRepository;
//    }
//
//    @Override
//    protected LiveData<List<DomainMessage>> executeUseCase(Params params) {
//        Log.d(TAG, "executeUseCase: getting requests");
//        return messagesListRepository.getGroupMessagesSentAfterLastAccess(params.groupId, params.lastAccessTime);
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
//        public static GetGroupMessagesAfterLastAccessUseCase.Params getMessagesAfterLastAccessForGroup(String groupId, Long lastAccessTime) {
//            return new GetGroupMessagesAfterLastAccessUseCase.Params(groupId, lastAccessTime);
//        }
//    }
//
//}
