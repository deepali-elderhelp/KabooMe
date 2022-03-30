package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;

/**
 * This class is needed when we need to copy the image from S3 bucket key to same bucket another key
 * Needed when user uses the same profile pic for a group
 * This runs in the background, does not notify when done
 */
public class GetMessageSingleUseCase extends BaseSingleUseCase<DomainMessage, GetMessageSingleUseCase.Params> {

    private static final String TAG = "KMUploadImageSUC";
    private MessagesListRepository messagesListRepository;

    public GetMessageSingleUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }


    @Override
    protected DomainMessage executeUseCase(Params params) {
        return this.messagesListRepository.getMessage(params.messageId);
    }

    public static final class Params {

        private final String messageId;

        private Params(String messageId) {
            this.messageId = messageId;
        }

        public static Params messageToGet(String messageId) {
            return new Params(messageId);
        }
    }
}
