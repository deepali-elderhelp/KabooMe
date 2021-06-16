package com.java.kaboome.presentation.views.features.groupList.adapter;


import android.view.View;

import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.presentation.entities.UserGroupModel;

public interface UserGroupImageClickListener {
    void onGroupImageClick(UserGroupModel group, View transitionView);
}
