package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainUserGroupConversation;
import com.java.kaboome.domain.repositories.ConversationsRepository;


public class AddNewConversationUseCase extends BaseSingleUseCase<Void, AddNewConversationUseCase.Params> {

    private static final String TAG = "KMAddNewConvUseCase";
    private ConversationsRepository conversationsRepository;

    public AddNewConversationUseCase(ConversationsRepository conversationsRepository) {
        this.conversationsRepository = conversationsRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.conversationsRepository.addNewConversation(params.domainUserGroupConversation);
        return null;
    }

    public static final class Params {

        private final DomainUserGroupConversation domainUserGroupConversation;

        private Params(DomainUserGroupConversation domainUserGroupConversation) {
            this.domainUserGroupConversation = domainUserGroupConversation;
        }

        public static Params newConversation(DomainUserGroupConversation domainUserGroupConversation) {
            return new Params(domainUserGroupConversation);
        }
    }
}
