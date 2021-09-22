/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.views.features.createGroup;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.java.kaboome.R;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.helpers.GeneralHelper;
import com.java.kaboome.presentation.views.features.createGroup.adapter.HandleNextListener;
import com.java.kaboome.presentation.views.features.createGroup.viewmodel.CreateGroupViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewGroupInfo1Fragment extends Fragment implements TextWatcher {


    private static final String TAG = "KMNewGroupInfo1Fragment";

    View rootView;
    TextView groupName;
    TextView groupDescription;
    TextView unicastTextView;
    SwitchCompat unicastSwitch;
    CreateGroupViewModel createGroupViewModel;
    HandleNextListener handleNextListener;
    boolean unicast = false;




    public NewGroupInfo1Fragment() {
        // Required empty public constructor
    }

    public NewGroupInfo1Fragment(HandleNextListener handleNextListener){
        this.handleNextListener = handleNextListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createGroupViewModel = ViewModelProviders.of(getParentFragment()).get(CreateGroupViewModel.class);
    }

    @Override
    public String toString() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_new_group_info1, container, false);
        groupName = rootView.findViewById(R.id.fr_cr_gr_name);
        groupName.addTextChangedListener(this);
        groupDescription = rootView.findViewById(R.id.fr_cr_gr_info);
        unicastSwitch = rootView.findViewById(R.id.fr_cr_gr_1_uni_switch);
        unicastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    DialogHelper.showDialogMessage(getActivity(), "Announcements Only", getResources().getString(R.string.announcement_only_alert));
                    unicast = true;
                } else {
                    DialogHelper.showDialogMessage(getActivity(), "Every One Posts", getResources().getString(R.string.everyone_posts_alert));
                    unicast = false;
                }
            }
        });
        unicastTextView = rootView.findViewById(R.id.fr_cr_gr_1_uni);
        unicastTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show alert box showing details
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage(R.string.announcements_only_help);
                alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        groupDescription.addTextChangedListener(this);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        handleNext();

    }

    public String getGroupName(){
        return groupName.getText().toString().trim();
    }

    public String getGroupDescription() {
        return groupDescription.getText().toString().trim();
    }

    public boolean isFormValid(){
        String groupNameSet = getGroupName();
        String groupDescriptionSet = getGroupDescription();

        if(!GeneralHelper.validateString(groupNameSet)){
            return false;
        }

        if(groupNameSet != null && !(groupNameSet.isEmpty())){
            if(groupDescriptionSet != null && !(groupDescriptionSet.isEmpty())){
                return true;
            }
        }

        return false;
    }

    public String getFormErrorMessage(){
        String groupNameSet = getGroupName();
        String groupDescriptionSet = getGroupDescription();
        if(groupNameSet == null || (groupNameSet.isEmpty())){
            return "Group Name is required";
        }

        if(!GeneralHelper.validateString(groupNameSet)){
            return "Group Name contains invalid characters";
        }

        if(groupDescriptionSet == null || (groupDescriptionSet.isEmpty())){
            return "Please add few lines about the group";
        }

        return "Some information is missing, please fill it up";

    }

    public void fillUpGroupObject(){
//        groupCreated.setGroupName(getGroupName());
//        groupCreated.setGroupDescription(getGroupDescription());
        createGroupViewModel.addGroupNameAndDesc(getGroupName(), getGroupDescription(), unicast);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        handleNext();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void handleNext(){
        String groupName = getGroupName();
        String groupDescription = getGroupDescription();
        handleNextListener.handleNext(!groupName.isEmpty() && !groupDescription.isEmpty());
    }
}
