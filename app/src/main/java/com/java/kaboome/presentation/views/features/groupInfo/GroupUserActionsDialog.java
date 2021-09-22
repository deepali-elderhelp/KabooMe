package com.java.kaboome.presentation.views.features.groupInfo;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.images.ImageHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupUserActionsDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "KMGroupUserActionsDlg";


    private View rootView;
    private CircleImageView userImage;
    private ProgressBar userImageProgressBar;
    private TextView userAlias;
    private TextView userRole;
    private ImageView makeAdminImage, sendAdminMessageImage, dividerForSendMessage;
    private TextView makeAdminLink, sendAdminMessageLink;
    private TextView removeUserLink;
    private GroupUserModel groupUser;
    private UserGroupModel userGroupModel;
    private boolean currentUserIsAdmin;
    Handler handler = new Handler(); //needed for Glide
    private NavController navController;


    public GroupUserActionsDialog() {
        // Required empty public constructor
    }

//    @Override
//    public int getTheme() {
//
//        return R.style.MyCustomDialogTheme;
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupUser = (GroupUserModel) getArguments().getSerializable("groupUser");
        userGroupModel = (UserGroupModel) getArguments().getSerializable("userGroup");
        currentUserIsAdmin = getArguments().getBoolean("currentUserIsAdmin");
        navController = NavHostFragment.findNavController(GroupUserActionsDialog.this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        group = (UserGroupModel) getArguments().getSerializable("group");

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_COLLAPSED);
//                BottomSheetBehavior.from(bottomSheet).setPeekHeight((int)((Resources.getSystem().getDisplayMetrics().heightPixels) * 0.7) );
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group_user_actions_dialog, container, false);

        userImage = rootView.findViewById(R.id.group_user_actions_dialog_image);
        userAlias = rootView.findViewById(R.id.group_user_actions_user_alias);
        userRole = rootView.findViewById(R.id.group_user_actions_user_role);
        userImageProgressBar = rootView.findViewById(R.id.group_user_actions_dialog_image_progress);
        makeAdminImage = rootView.findViewById(R.id.group_user_actions_admin_icon);
        makeAdminLink = rootView.findViewById(R.id.group_user_actions_admin);
        removeUserLink = rootView.findViewById(R.id.group_user_actions_delete);
        sendAdminMessageImage = rootView.findViewById(R.id.group_user_actions_message_icon);
        dividerForSendMessage = rootView.findViewById(R.id.group_user_action_divider_line_2);
        sendAdminMessageLink = rootView.findViewById(R.id.group_user_actions_message);

        makeAdminImage.setOnClickListener(this);
        makeAdminLink.setOnClickListener(this);
        removeUserLink.setOnClickListener(this);
        sendAdminMessageLink.setOnClickListener(this);
        sendAdminMessageImage.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {

        //set field values
        userAlias.setText(groupUser.getAlias());
        userRole.setText(groupUser.getRole());

        Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.bs_profile);
        ImageHelper.getInstance().loadGroupUserImage(groupUser.getGroupId(), ImageTypeConstants.MAIN, groupUser.getUserId(), groupUser.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                handler, userImage, null);

        if(currentUserIsAdmin && "false".equalsIgnoreCase(groupUser.getIsAdmin())){
            makeAdminLink.setVisibility(View.VISIBLE);
            makeAdminImage.setVisibility(View.VISIBLE);
            removeUserLink.setVisibility(View.VISIBLE);

            //for private admin message now
            sendAdminMessageLink.setVisibility(View.VISIBLE);
            sendAdminMessageImage.setVisibility(View.VISIBLE);
            dividerForSendMessage.setVisibility(View.VISIBLE);

        }
        else{
            makeAdminLink.setVisibility(View.INVISIBLE);
            makeAdminImage.setVisibility(View.INVISIBLE);
            removeUserLink.setVisibility(View.INVISIBLE);

            //for private admin message now
            sendAdminMessageLink.setVisibility(View.INVISIBLE);
            sendAdminMessageImage.setVisibility(View.INVISIBLE);
            dividerForSendMessage.setVisibility(View.INVISIBLE);
        }


        super.onResume();
    }




    public void onClick(View v) {

        switch (v.getId()){

            case R.id.group_user_actions_admin_icon:
            case R.id.group_user_actions_admin: {
                List<GroupUserModel> newMembers = new ArrayList<>();
                newMembers.add(groupUser);

                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupNewAdminMembers", newMembers);
                navController.popBackStack();
                dismiss();
                break;
            }

            case R.id.group_user_actions_message_icon:
            case R.id.group_user_actions_message:{
//                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupSendAdminMessage", groupUser);
//                navController.popBackStack();
                UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
                userGroupConversationModel.setGroupId(groupUser.getGroupId());
                userGroupConversationModel.setUserId(AppConfigHelper.getUserId());
                userGroupConversationModel.setOtherUserId(groupUser.getUserId());
                userGroupConversationModel.setOtherUserName(groupUser.getAlias());
                userGroupConversationModel.setOtherUserRole(groupUser.getRole());
                userGroupConversationModel.setLastAccessed((new Date()).getTime());

                Bundle bundle = new Bundle();
                bundle.putSerializable("conversation", userGroupConversationModel);
                bundle.putSerializable("group", userGroupModel);

                NavHostFragment.findNavController(this).navigate(R.id.action_groupUserActionsDialog_to_groupAdminUserMessagesFragment, bundle);

                dismiss();
                break;
            }

            case R.id.group_user_actions_delete:
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage("Do you want to remove this user from group?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(!currentUserIsAdmin){
                                    Toast.makeText(getContext(), "Sorry, only Admins can delete other members", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(AppConfigHelper.getUserId().equals(groupUser.getUserId())){
                                    Toast.makeText(getContext(), "Please go to the bottom and select 'Leave Group'", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if(!NetworkHelper.isOnline()){
                                    Toast.makeText(getContext(), "No Network: This action needs network connection", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupUserRemove", groupUser);
                                navController.popBackStack();
                                dismiss();
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing

                    }
                });


                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                break;
            }


        }

    }



}
