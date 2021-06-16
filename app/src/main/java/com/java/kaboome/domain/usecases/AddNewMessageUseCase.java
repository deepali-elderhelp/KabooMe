package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.MessagesListRepository;

import java.util.List;


public class AddNewMessageUseCase extends BaseSingleUseCase<Void, AddNewMessageUseCase.Params> {

    private static final String TAG = "KMAddNewMsgUseCase";
    private MessagesListRepository messagesListRepository;

    public AddNewMessageUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.messagesListRepository.addNewMessage(params.message);
        return null;
    }

    public static final class Params {

        private final DomainMessage message;

        private Params(DomainMessage message) {
            this.message = message;
        }

        public static Params newMessage(DomainMessage message) {
            return new Params(message);
        }
    }
}
