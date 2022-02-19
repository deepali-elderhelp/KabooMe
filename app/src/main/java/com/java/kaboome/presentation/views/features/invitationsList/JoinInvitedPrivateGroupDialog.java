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
import android.widget.Toast;

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
import com.java.kaboome.constants.InvitationStatusConstants;
import com.java.kaboome.constants.RequestActionConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.entities.UpdateResourceModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.DateFormatter;
import com.java.kaboome.presentation.helpers.ErrorMessageHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.joinGroup.viewmodel.JoinPrivateGroupViewModel;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinInvitedPrivateGroupDialog extends DialogFragment {

    private View view;
    private InvitationModel invitation;
    private Handler handler = new Handler(); //needed for Glide
    private CircleImageView groupImage;
    private CircleImageView userImage;
    private TextView groupName;
    private TextInputEditText userAlias;
    private TextInputEditText userRole;
    private TextInputEditText messageToAdmin;
    private ImageView closeDialog;
    private ProgressBar progressBar;
    private ProgressBar groupImageProgressBar;
    private TextView changeUserPicLink;
    private AppCompatButton sendRequestButton;
    private JoinPrivateGroupViewModel joinPrivateGroupViewModel;


    private static final String TAG = "KMJoinPrivGrpDialog";
    private TextView messageByInvitee;
    private TextView invitedBy;

    NavController navController;

    private String picturePath;
    private String thumbnailPath;
    private boolean imageChanged = false;


    public JoinInvitedPrivateGroupDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(JoinInvitedPrivateGroupDialog.this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_join_invited_private_group_dialog, container, false);

        invitation = (InvitationModel) getArguments().getSerializable("invitation");

        groupImage = view.findViewById(R.id.group_join_invited_private_dialog_image);
        userImage = view.findViewById(R.id.group_join_invited_private_user_image);

        changeUserPicLink = view.findViewById(R.id.group_join_invited_private_change_picture_label);
        if(imageChanged){
            changeUserPicLink.setText(getString(R.string.reset_your_group_picture));
        }
        else{
            changeUserPicLink.setText(R.string.change_your_group_picture);
        }

        groupName = view.findViewById(R.id.group_join_invited_private_dialog_name);
        groupName.setText(invitation.getGroupName());

        messageByInvitee = view.findViewById(R.id.group_join_invited_private_message);

        if(invitation.getMessageByInvitee() != null && !invitation.getMessageByInvitee().trim().isEmpty()){
            messageByInvitee.setText(invitation.getMessageByInvitee());
        }
        else{
            messageByInvitee.setText("You have been invited to join the group");
        }

        invitedBy = view.findViewById(R.id.group_join_invited_private_by);
        invitedBy.setText("Invited By: "+invitation.getInvitedByAlias() +" on "+ DateFormatter.getDateFormattedPretty(invitation.getDateInvited()));


        userAlias = view.findViewById(R.id.group_join_invited_private_alias);
        userRole = view.findViewById(R.id.group_join_invited_private_role);

        userAlias.addTextChangedListener(new RoleAndAliasTextWatcher());
        userRole.addTextChangedListener(new RoleAndAliasTextWatcher());

        messageToAdmin = view.findViewById(R.id.group_join_invited_private_message_to);

        sendRequestButton = view.findViewById(R.id.sendRequestButtonFromInvited);
        sendRequestButton.setOnClickListener(sendRequestClicked);

        closeDialog = view.findViewById(R.id.group_join_invited_private_close_button);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        progressBar = view.findViewById(R.id.group_join_invited_private_progress_bar);
        groupImageProgressBar = view.findViewById(R.id.group_join_invited_private_dialog_image_progress);

        joinPrivateGroupViewModel = ViewModelProviders.of(this).get(JoinPrivateGroupViewModel.class);


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
                navController.navigate(R.id.action_joinInvitedPrivateGroupDialog_to_pictureDialog, args);
            }
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

                Glide.with(JoinInvitedPrivateGroupDialog.this)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .asBitmap()
                        .load(picturePath)
                        .into(userImage);

            }
        });

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

//    private void subscribeObservers() {
//        joinPrivateGroupViewModel.getRequest().observe(getViewLifecycleOwner(), new Observer<JoinPrivateGroupViewModel.Status>() {
//            @Override
//            public void onChanged(JoinPrivateGroupViewModel.Status status) {
//                if(status == JoinPrivateGroupViewModel.Status.UPDATING){
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//                if(status == JoinPrivateGroupViewModel.Status.SUCCESS){
//                    progressBar.setVisibility(View.INVISIBLE);
//                    showAlert("You will see the request in Invitations", "Group Join Requested", "Got It" ,new DialogInterface.OnClickListener(){
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dismiss();
//                            getActivity().finish(); //it is successful, now remove the search activity too
//                        }
//                    });
//                }
//                if(status == JoinPrivateGroupViewModel.Status.ERROR){
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
//    }

    private void subscribeObservers() {
        joinPrivateGroupViewModel.getRequest().observe(getViewLifecycleOwner(), new Observer<UpdateResourceModel<String>>() {
            @Override
            public void onChanged(final UpdateResourceModel<String> stringUpdateResourceModel) {
                if(stringUpdateResourceModel.status == UpdateResourceModel.Status.UPDATING){
                    progressBar.setVisibility(View.VISIBLE);
                }
                if(stringUpdateResourceModel.status == UpdateResourceModel.Status.SUCCESS){
                    progressBar.setVisibility(View.INVISIBLE);
                    showAlert("You will see the request in Invitations", "Group Join Requested", "Got It" ,new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
//                            getActivity().finish(); //it is successful, now remove the search activity too
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
                                NavController navController = NavHostFragment.findNavController(JoinInvitedPrivateGroupDialog.this);
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


    View.OnClickListener sendRequestClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(invitation.getInvitationStatus() == InvitationStatusConstants.PENDING){
                Toast.makeText(getContext(), "Your request is already pending with the Admin", Toast.LENGTH_SHORT).show();
            }
            else {

                GroupRequestModel groupRequestModel = new GroupRequestModel();
                groupRequestModel.setUserId(AppConfigHelper.getUserId());
                groupRequestModel.setGroupId(invitation.getGroupId());
                groupRequestModel.setRequestMessage(messageToAdmin.getText().toString());
                groupRequestModel.setUserAlias(userAlias.getText().toString());
                groupRequestModel.setUserRole(userRole.getText().toString());
                if (imageChanged) {
                    groupRequestModel.setImageUpdateTimestamp((new Date()).getTime());
                } else {
                    groupRequestModel.setImageUpdateTimestamp(AppConfigHelper.getCurrentUserImageTimestamp());
                }

                groupRequestModel.setDateRequestMade((new Date()).getTime());

                joinPrivateGroupViewModel.createRequest(groupRequestModel, invitation.getGroupName(), String.valueOf(invitation.getPrivateGroup()), RequestActionConstants.REQUEST_TO_JOIN.getAction(), picturePath, thumbnailPath, imageChanged);
            }
        }
    };


    private void showAlert(String message, String title, String positiveButton, DialogInterface.OnClickListener onPositiveClick) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(JoinInvitedPrivateGroupDialog.this.getContext());
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
            sendRequestButton.setEnabled(!groupUserRole.isEmpty() && !groupUserAlias.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
