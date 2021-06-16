//package com.java.kaboome.domain.usecases;
//
//import com.java.kaboome.domain.repositories.MessagesListRepository;
//
//public class UpdateGroupLastSeenMessageUseCase extends BaseSingleUseCase<Void, UpdateGroupLastSeenMessageUseCase.Params> {
//
//    private static final String TAG = "KMUpdGrpLastSeenMsgUseCase";
//    private MessagesListRepository messagesListRepository;
//
//    public UpdateGroupLastSeenMessageUseCase(MessagesListRepository messagesListRepository) {
//        this.messagesListRepository = messagesListRepository;
//    }
//
//
//    @Override
//    protected Void executeUseCase(Params params) {
//        this.messagesListRepository.updateGroupsLastMessageSeen(params.groupId);
//        return null;
//    }
//
//    public static final class Params {
//
//
//        private final String groupId;
//
//        private Params(String groupId) {
//            this.groupId = groupId;
//        }
//
//        public static Params forGroup(String groupId) {
//            return new Params(groupId);
//        }
//    }
//}
