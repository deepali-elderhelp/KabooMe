package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.MessagesListRepository;


public class UpdateMessageLoadingProgressUseCase extends BaseSingleUseCase<Void, UpdateMessageLoadingProgressUseCase.Params> {

    private static final String TAG = "KMUpdateMsgLoadingProg";
    private MessagesListRepository messagesListRepository;

    public UpdateMessageLoadingProgressUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.messagesListRepository.updateMessageLoadingProgress(params.messageId, params.loadingProgress);
        return null;
    }

    public static final class Params {

        private final String messageId;

        private final int loadingProgress;


        public Params(String messageId, int loadingProgress) {
            this.messageId = messageId;
            this.loadingProgress = loadingProgress;
        }


        public static Params messageLoadingProgToBeUpdated(String messageId, int loadingProgress) {
            return new Params(messageId, loadingProgress);
        }
    }
}
