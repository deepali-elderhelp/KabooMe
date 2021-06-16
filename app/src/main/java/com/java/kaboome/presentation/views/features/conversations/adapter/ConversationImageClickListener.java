package com.java.kaboome.presentation.views.features.conversations.adapter;


import android.view.View;

import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;

public interface ConversationImageClickListener {
    void onConvImageClick(UserGroupConversationModel conversationModel, View transitionView);
}
