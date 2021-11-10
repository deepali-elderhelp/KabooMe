package com.java.kaboome.domain.repositories;

public interface HelpRepository {


    Void postHelpFeedbackMessage(String subject, String messageText, boolean contactAllowed);
}
