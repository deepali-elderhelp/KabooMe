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
//public class GetOnlyGroupUnreadMessagesUseCase extends BaseUseCase<List<DomainMessage>, GetOnlyGroupUnreadMessagesUseCase.Params> {
//
//    private static final String TAG = "KMGetUGrCvMsgsUseCase";
//
//    private MessagesListRepository messagesListRepository;
//
//    public GetOnlyGroupUnreadMessagesUseCase(MessagesListRepository messagesListRepository) {
//        this.messagesListRepository = messagesListRepository;
//    }
//
//    @Override
//    protected LiveData<List<DomainMessage>> executeUseCase(Params params) {
//        Log.d(TAG, "executeUseCase: getting requests");
////        return messagesListRepository.getOnlyGroupUnreadMessages(params.groupId);
//        return messagesListRepository.getUnreadMessagesForOnlyGroupFromCacheLiveData(params.groupId);
//    }
//
//    public static final class Params {
//
//        private final String groupId;
//
//        private Params(String groupId) {
//            this.groupId = groupId;
//        }
//
//        public static GetOnlyGroupUnreadMessagesUseCase.Params getOnlyGroupUnreadMessagesForGroup(String groupId) {
//            return new GetOnlyGroupUnreadMessagesUseCase.Params(groupId);
//        }
//    }
//
//}
