package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.repositories.HelpRepository;


public class SendHelpFeedbackUseCase extends BaseSingleUseCase<Void, SendHelpFeedbackUseCase.Params> {

    private static final String TAG = "SendHelpFeedbackUseCase";
    private HelpRepository helpRepository;

    public SendHelpFeedbackUseCase(HelpRepository helpRepository) {
        this.helpRepository = helpRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.helpRepository.postHelpFeedbackMessage(params.subject, params.messageText, params.contactAllowed);
        return null;
    }

    public static final class Params {

        private final String messageText;
        private final String subject;
        private final boolean contactAllowed;

        private Params(String subject, String messageText, boolean contactAllowed) {
            this.subject = subject;
            this.messageText = messageText;
            this.contactAllowed = contactAllowed;
        }

        public static Params sendHelpFeedbackMessage(String subject, String messageText, boolean contactAllowed) {
            return new Params(subject, messageText, contactAllowed);
        }
    }


}


