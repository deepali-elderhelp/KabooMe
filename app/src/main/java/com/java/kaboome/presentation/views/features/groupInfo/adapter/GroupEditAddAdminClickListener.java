package com.java.kaboome.presentation.views.features.groupInfo.adapter;


import com.java.kaboome.presentation.entities.GroupUserModel;

public interface GroupEditAddAdminClickListener {
    void onGroupEditAddAdminChecked(GroupUserModel groupUserModel);

    void onGroupEditAddAdminUnchecked(GroupUserModel groupUserModel);
}
