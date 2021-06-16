package com.java.kaboome.presentation.views.features.groupInfo;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.java.kaboome.R;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditGroupUserNotificationFragment extends DialogFragment {

    View view;
    GroupModel groupModel;
//    GroupViewModel groupViewModel;

    RadioButton radio_only_high;
    RadioButton radio_all;
    RadioButton radio_none;

    Button saveButton;
    private RadioGroup notifyGroup;
    ImageView closeButton;

    public EditGroupUserNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupModel = (GroupModel) getArguments().getSerializable("group");

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        groupViewModel = ViewModelProviders.of(getActivity()).get(GroupViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_group_user_notification, container, false);
        saveButton = view.findViewById(R.id.save_group_notification);
        notifyGroup = view.findViewById(R.id.edit_group_notifications_group);
        radio_only_high = view.findViewById(R.id.edit_group_notifications_group_urgent);
        radio_all = view.findViewById(R.id.edit_group_notifications_group_all);
        radio_none = view.findViewById(R.id.edit_group_notifications_group_none);

        notifyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                saveButton.setEnabled(true);
                if(checkedId == R.id.edit_group_notifications_group_urgent){
                    //set the active urgent icon and others non-active
                    radio_only_high.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_urgent_active), null);
                    radio_all.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_all), null);
                    radio_none.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_off), null);
                }
                if(checkedId == R.id.edit_group_notifications_group_all){
                    //set the active all icon and others non-active
                    radio_only_high.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_urgent), null);
                    radio_all.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_all_active), null);
                    radio_none.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_off), null);
                }
                if(checkedId == R.id.edit_group_notifications_group_none){
                    //set the active none icon and others non-active
                    radio_only_high.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_urgent), null);
                    radio_all.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_all), null);
                    radio_none.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_off_active), null);
                }
            }
        });
        

        //display existing notification level
        selectExistingNotificationLevel();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate
                GroupUserModel groupModelTemp = new GroupUserModel();
                groupModelTemp.setUserId(AppConfigHelper.getUserId()); //notification level of current user
                groupModelTemp.setGroupId(groupModel.getGroupId());
                groupModelTemp.setNotify(getNewSelectedNotification());
                groupModelTemp.setDeviceId(AppConfigHelper.getDeviceId());
////                groupViewModel.updateGroupUser(groupModelTemp, "updateGroupUserNotification");
//                groupViewModel.updateGroupUser(groupModelTemp, GroupActionConstants.UPDATE_GROUP_USER_NOTIFICATION.getAction());
//                dismiss();

                NavController navController = NavHostFragment.findNavController(EditGroupUserNotificationFragment.this);

                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupUserNotification", groupModelTemp);
                navController.popBackStack();
                dismiss();
            }
        });



        closeButton = view.findViewById(R.id.group_edit_notification_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

//    public void setGroupModel(GroupModel groupModel) {
//        this.groupModel = groupModel;
//    }

    private String getNewSelectedNotification(){


        
        int checkedRadioButtonId = notifyGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) notifyGroup.findViewById(checkedRadioButtonId);
        String notify = "1";

        if(radioButton.getId() == radio_only_high.getId()){
            notify = "1";
        }
        else if(radioButton.getId() == radio_all.getId()){
            notify = "2";
        }
        else if(radioButton.getId() == radio_none.getId()){
            notify = "0";
        }
        return notify;
    }

    private void selectExistingNotificationLevel(){
        if(groupModel.getNotifications() != null){
            if(groupModel.getNotifications().equals(NotificationLevels.ONLY_URGENT)){
                //select urgent only
                radio_only_high.setChecked(true);
                radio_only_high.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_urgent_active), null);
                radio_all.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_all), null);
                radio_none.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_off), null);
                return;
            }
            if(groupModel.getNotifications().equals(NotificationLevels.ALL_MESSAGES)){
                //select all messages
                radio_all.setChecked(true);
                radio_only_high.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_urgent), null);
                radio_all.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_all_active), null);
                radio_none.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_off), null);
                return;
            }
            if(groupModel.getNotifications().equals(NotificationLevels.NONE)){
                //select none
                radio_none.setChecked(true);
                radio_only_high.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_urgent), null);
                radio_all.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_all), null);
                radio_none.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.notification_off_active), null);
                return;
            }
            //default is all
            //select all messages
            radio_all.setChecked(true);
        }
    }

}
