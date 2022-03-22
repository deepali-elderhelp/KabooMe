package com.java.kaboome.presentation.views.features.invitationsList;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.entities.UpdateResourceModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.DateFormatter;
import com.java.kaboome.presentation.helpers.ErrorMessageHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.joinGroup.viewmodel.JoinGroupViewModel;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinInvitedGroupDialog extends DialogFragment {

    private View view;
    private InvitationModel invitation;
//    private RequestManager requestManager;
    private Handler handler = new Handler(); //needed for Glide
    private CircleImageView groupImage;
    private CircleImageView userImage;
    private TextView groupName;
    private TextView changeUserPicLink;
    private TextView messageByInvitee;
    private TextView invitedBy;
    private TextInputEditText userAlias;
    private TextInputEditText userRole;
    private ImageView closeDialog;
    private ProgressBar progressBar;
    private ProgressBar groupImageProgressBar;

    private AppCompatButton joinGroupButton;
    private JoinGroupViewModel joinGroupViewModel;

    private String picturePath;
    private String thumbnailPath;
    private boolean imageChanged = false;
    private NavController navController;


    private static final String TAG = "KMJoinGroupDialog";


    public JoinInvitedGroupDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(JoinInvitedGroupDialog.this);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);

        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_join_invited_group_dialog, container, false);

        invitation = (InvitationModel) getArguments().getSerializable("invitation");

        joinGroupButton = view.findViewById(R.id.joinInvitedGroupButton);
        joinGroupButton.setOnClickListener(joinGroupClicked);

        groupImage = view.findViewById(R.id.group_join_invited_dialog_image);

        userImage = view.findViewById(R.id.group_join_invited_user_image);

        changeUserPicLink = view.findViewById(R.id.group_join_invited_change_picture_label);
        if(imageChanged){
            changeUserPicLink.setText(getString(R.string.reset_your_group_picture));
        }
        else{
            changeUserPicLink.setText(R.string.change_your_group_picture);
        }

        groupName = view.findViewById(R.id.group_join_invited_dialog_name);
        groupName.setText(invitation.getGroupName());

        messageByInvitee = view.findViewById(R.id.group_join_invited_message);

        if(invitation.getMessageByInvitee() != null && !invitation.getMessageByInvitee().trim().isEmpty()){
            messageByInvitee.setText(invitation.getMessageByInvitee());
        }
        else{
            messageByInvitee.setText("You have been invited to join the group");
        }

        invitedBy = view.findViewById(R.id.group_join_invited_by);
        invitedBy.setText("Invited By: "+invitation.getInvitedByAlias() +" on "+ DateFormatter.getDateFormattedPretty(invitation.getDateInvited()));


        userAlias = view.findViewById(R.id.group_join_invited_alias);
        userRole = view.findViewById(R.id.group_join_invited_role);
        userAlias.addTextChangedListener(new RoleAndAliasTextWatcher());
        userRole.addTextChangedListener(new RoleAndAliasTextWatcher());


        closeDialog = view.findViewById(R.id.group_join_invited_close_button);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        progressBar = view.findViewById(R.id.group_join_invited_progress_bar);
        groupImageProgressBar = view.findViewById(R.id.group_join_invited_dialog_image_progress);

        joinGroupViewModel = ViewModelProviders.of(this).get(JoinGroupViewModel.class);

//        ImageHelper.loadGroupImage(invitation.getGroupId(), null, ImageHelper.getRequestManager(getActivity(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);
        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, invitation.getGroupName());
        ImageHelper.getInstance().loadGroupImage(invitation.getGroupId(), ImageTypeConstants.MAIN,null,
                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, groupImage, null, true);

        Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
        ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN, AppConfigHelper.getCurrentUserImageTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                handler, userImage, null);

        changeUserPicLink.setOnClickListener(userImageChangeClicked);
        userImage.setOnClickListener(userImageChangeClicked);



        subscribeObservers();
        return view;
    }

    private View.OnClickListener userImageChangeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(imageChanged){
                //user had selected some other image, but now wants tp reset to the default one
                Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
                ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(), ImageTypeConstants.MAIN, AppConfigHelper.getCurrentUserImageTimestamp(),
                        ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                        handler, userImage, null);
                imageChanged = false;
            }
            else{
                Bundle args = new Bundle();
                args.putBoolean("pictureToBeSavedOnServer", false);
                args.putString("imageId", "userId");
                navController.navigate(R.id.action_joinInvitedGroupDialog_to_pictureDialog, args);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void subscribeObservers() {
//        joinGroupViewModel.getGroupJoinUpdate().observe(getViewLifecycleOwner(), new Observer<JoinGroupViewModel.Status>() {
//            @Override
//            public void onChanged(JoinGroupViewModel.Status status) {
//                if(status == JoinGroupViewModel.Status.UPDATING){
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//                if(status == JoinGroupViewModel.Status.SUCCESS){
//                    progressBar.setVisibility(View.INVISIBLE);
//                    showAlert("You will see it in the Groups List", "Group Joined", "Got It" ,new DialogInterface.OnClickListener(){
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dismiss();
//                        }
//                    });
//
//                }
//                if(status == JoinGroupViewModel.Status.ERROR){
//                    progressBar.setVisibility(View.INVISIBLE);
//                    showAlert("Sorry, something went wrong", "Failed", "Ok" ,new DialogInterface.OnClickListener(){
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dismiss();
//                        }
//                    });
//                }
//            }
//        });

        joinGroupViewModel.getGroupJoinUpdate().removeObservers(getViewLifecycleOwner()); //if any old hanging there
        joinGroupViewModel.getGroupJoinUpdate().observe(getViewLifecycleOwner(), new Observer<UpdateResourceModel<String>>() {
            @Override
            public void onChanged(final UpdateResourceModel<String> stringUpdateResourceModel) {
                if(stringUpdateResourceModel.status == UpdateResourceModel.Status.UPDATING){
                    progressBar.setVisibility(View.VISIBLE);
                }
                if(stringUpdateResourceModel.status == UpdateResourceModel.Status.SUCCESS){
                    progressBar.setVisibility(View.INVISIBLE);
                    showAlert("You will see it in the Groups List", "Group Joined", "Got It" ,new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                }
                if(stringUpdateResourceModel.status == UpdateResourceModel.Status.ERROR){
                    progressBar.setVisibility(View.INVISIBLE);
                    String errorMessage = ErrorMessageHelper.getErrorMessage(stringUpdateResourceModel.message);
                    showAlert(errorMessage, "Failed", "Ok" ,new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(stringUpdateResourceModel.message.contains("521")){
                                NavController navController = NavHostFragment.findNavController(JoinInvitedGroupDialog.this);
                                navController.getPreviousBackStackEntry().getSavedStateHandle().set("deletedGroup", invitation.getGroupId());
                                navController.popBackStack();
                            }
                            dismiss();
                        }
                    });
                }
            }
        });
    }


    View.OnClickListener joinGroupClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Long currentDate = (new Date()).getTime();
            UserGroupModel userGroupModel = new UserGroupModel();
            userGroupModel.setGroupId(invitation.getGroupId());
            userGroupModel.setGroupName(invitation.getGroupName());
            userGroupModel.setAlias(userAlias.getText().toString());
            userGroupModel.setRole(userRole.getText().toString());
            userGroupModel.setPrivate(invitation.getPrivateGroup());
            userGroupModel.setLastAccessed(currentDate);
            userGroupModel.setCacheClearTS(currentDate);
            userGroupModel.setAdminsLastAccessed(currentDate);
            userGroupModel.setAdminsCacheClearTS(currentDate);
            userGroupModel.setIsAdmin("false");


            if(imageChanged){//user selected a new image, so current date is the timestamp
//                userGroupModel.setImageUpdateTimestamp((new Date()).getTime());
                userGroupModel.setUserImageUpdateTimestamp((new Date()).getTime());
            }
            else{
//                userGroupModel.setImageUpdateTimestamp(AppConfigHelper.getCurrentUserImageTimestamp());
                userGroupModel.setUserImageUpdateTimestamp(AppConfigHelper.getCurrentUserImageTimestamp());
            }

            joinGroupViewModel.joinUserToGroup(userGroupModel, picturePath,thumbnailPath, imageChanged);

        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData imagePicked = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("imagePicked");
        imagePicked.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received new user group image - "+o);
                String[] imagePaths = (String[]) o;

                picturePath = imagePaths[0];
                thumbnailPath = imagePaths[2];
                imageChanged = true;

                Glide.with(JoinInvitedGroupDialog.this)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .asBitmap()
                        .load(picturePath)
                        .into(userImage);

            }
        });

    }

    private void showAlert(String message, String title, String positiveButton, DialogInterface.OnClickListener onPositiveClick) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(JoinInvitedGroupDialog.this.getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButton, onPositiveClick);
        builder.show();
    }

    private class RoleAndAliasTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String groupUserRole = userRole.getText().toString().trim();
            String groupUserAlias = userAlias.getText().toString().trim();
            joinGroupButton.setEnabled(!groupUserRole.isEmpty() && !groupUserAlias.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
