package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;

/**
 * this case is created for deleting the welcome message if the user wants
 * can be used for other cases in the future
 */
public class DeleteLocalMessageUseCase extends BaseSingleUseCase<Void, DeleteLocalMessageUseCase.Params> {

    private static final String TAG = "KMAddNewMsgUseCase";
    private MessagesListRepository messagesListRepository;

    public DeleteLocalMessageUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }


    @Override
    protected Void executeUseCase(DeleteLocalMessageUseCase.Params params) {
        this.messagesListRepository.deleteLocalMessage(params.message);
        return null;
    }

    public static final class Params {

        private final DomainMessage message;

        private Params(DomainMessage message) {
            this.message = message;
        }

        public static DeleteLocalMessageUseCase.Params messageToBeDeleted(DomainMessage message) {
            return new DeleteLocalMessageUseCase.Params(message);
        }
    }
}
