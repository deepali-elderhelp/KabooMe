//package com.java.kaboome.domain.usecases;
//
//import com.java.kaboome.domain.entities.DomainMessage;
//import com.java.kaboome.domain.repositories.MessagesListRepository;
//
//public class DeleteMessageUseCase extends BaseSingleUseCase<Void, DeleteMessageUseCase.Params> {
//
//    private static final String TAG = "KMAddNewMsgUseCase";
//    private MessagesListRepository messagesListRepository;
//
//    public DeleteMessageUseCase(MessagesListRepository messagesListRepository) {
//        this.messagesListRepository = messagesListRepository;
//    }
//
//
//    @Override
//    protected Void executeUseCase(DeleteMessageUseCase.Params params) {
//        this.messagesListRepository.deleteMessage(params.message);
//        return null;
//    }
//
//    public static final class Params {
//
//        private final DomainMessage message;
//
//        private Params(DomainMessage message) {
//            this.message = message;
//        }
//
//        public static DeleteMessageUseCase.Params newMessage(DomainMessage message) {
//            return new DeleteMessageUseCase.Params(message);
//        }
//    }
//}
