package com.java.kaboome.presentation.views.features.conversations.adapter;


import android.view.View;

import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;

public interface NewConversationClickListener {
    void onNewConvClick(GroupUserModel groupUserModel);
}
