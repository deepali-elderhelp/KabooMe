package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import com.java.kaboome.data.entities.Message;

public interface WelcomeMessageClickListener {
    void onInviteMembersClicked();

    void onCloseWelcomeMessageClicked(Message message);
}
