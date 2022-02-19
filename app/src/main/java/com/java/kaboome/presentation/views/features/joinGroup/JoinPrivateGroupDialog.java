package com.java.kaboome.presentation.views.features.joinGroup;


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
import com.java.kaboome.constants.RequestActionConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupRequestModel;
import com.java.kaboome.presentation.entities.UpdateResourceModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.ErrorMessageHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.joinGroup.viewmodel.JoinPrivateGroupViewModel;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinPrivateGroupDialog extends DialogFragment {

    private View view;
    private GroupModel group;
    private Handler handler = new Handler(); //needed for Glide
    private CircleImageView groupImage;
    private CircleImageView userImage;
    private TextView groupName;
    private TextView groupDescription;
    private TextView createdByAlias;
    private TextView changeUserPicLink;
    private TextInputEditText userAlias;
    private TextInputEditText userRole;
    private TextInputEditText messageToAdmin;
    private ImageView closeDialog;
    private ProgressBar progressBar;
    private ProgressBar groupImageProgressBar;

    AppCompatButton sendRequestButton;
    JoinPrivateGroupViewModel joinPrivateGroupViewModel;
    NavController navController;

    private String picturePath;
    private String thumbnailPath;
    private boolean imageChanged = false;


    private static final String TAG = "KMJoinPrivGrpDialog";


    public JoinPrivateGroupDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(JoinPrivateGroupDialog.this);
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
        view =  inflater.inflate(R.layout.fragment_join_private_group_dialog, container, false);

        group = (GroupModel) getArguments().getSerializable("group");

        groupImage = view.findViewById(R.id.group_join_private_dialog_image);
        userImage = view.findViewById(R.id.group_join_private_user_image);

        changeUserPicLink = view.findViewById(R.id.group_join_private_change_picture_label);
        if(imageChanged){
            changeUserPicLink.setText(getString(R.string.reset_your_group_picture));
        }
        else{
            changeUserPicLink.setText(R.string.change_your_group_picture);
        }

        groupName = view.findViewById(R.id.group_join_private_dialog_name);
        groupName.setText(group.getGroupName());

        groupDescription = view.findViewById(R.id.group_join_private_description);
        groupDescription.setText(group.getGroupDescription());

        createdByAlias = view.findViewById(R.id.group_join_private_createdby);
        createdByAlias.setText(group.getCreatedByAlias());

        userAlias = view.findViewById(R.id.group_join_private_alias);
        userRole = view.findViewById(R.id.group_join_private_role);
        messageToAdmin = view.findViewById(R.id.group_join_private_message);

        userAlias.addTextChangedListener(new RoleAndAliasTextWatcher());
        userRole.addTextChangedListener(new RoleAndAliasTextWatcher());

        sendRequestButton = view.findViewById(R.id.sendRequestButton);
        sendRequestButton.setOnClickListener(sendRequestClicked);

        closeDialog = view.findViewById(R.id.group_join_private_group_close_button);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        progressBar = view.findViewById(R.id.group_join_private_progress_bar);
        groupImageProgressBar = view.findViewById(R.id.group_join_private_dialog_image_progress);

        joinPrivateGroupViewModel = ViewModelProviders.of(this).get(JoinPrivateGroupViewModel.class);

        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, group.getGroupName());
        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
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
                navController.navigate(R.id.action_joinPrivateGroupDialog_to_pictureDialog, args);
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

                Glide.with(JoinPrivateGroupDialog.this)
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

    private void subscribeObservers() {
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

        joinPrivateGroupViewModel.getRequest().observe(getViewLifecycleOwner(), new Observer<UpdateResourceModel<String>>() {
            @Override
            public void onChanged(UpdateResourceModel<String> stringUpdateResourceModel) {
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
                            dismiss();
                        }
                    });
                }
            }
        });
    };

    public void setGroup(GroupModel group) {
        this.group = group;
    }

    View.OnClickListener sendRequestClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//            if(userAlias.getText().toString() == null || userAlias.getText().toString().isEmpty()){
//                showAlert("Please enter an alias the group will know you by", "Fill In Alias", "Ok" ,new DialogInterface.OnClickListener(){
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//            }
//            else if(userRole.getText().toString() == null || userRole.getText().toString().isEmpty()){
//                showAlert("Please enter the role you play in the group. (Default role -'Member')", "Fill In Alias", "Ok" ,new DialogInterface.OnClickListener(){
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        userRole.setText("Member");
//
//                    }
//                });
//            }
//            else{

                GroupRequestModel groupRequestModel = new GroupRequestModel();
                groupRequestModel.setUserId(AppConfigHelper.getUserId());
                groupRequestModel.setGroupId(group.getGroupId());
                groupRequestModel.setRequestMessage(messageToAdmin.getText().toString());
                groupRequestModel.setUserAlias(userAlias.getText().toString());
                groupRequestModel.setUserRole(userRole.getText().toString());
                if(imageChanged){//user selected a new image, so current date is the timestamp
                    groupRequestModel.setImageUpdateTimestamp((new Date()).getTime());
                }
                else{
                    groupRequestModel.setImageUpdateTimestamp(AppConfigHelper.getCurrentUserImageTimestamp());
                }
//                groupRequestModel.setImageUpdateTimestamp(AppConfigHelper.getCurrentUserImageTimestamp());
                groupRequestModel.setDateRequestMade((new Date()).getTime());

                if(NetworkHelper.isOnline()){
                    joinPrivateGroupViewModel.createRequest(groupRequestModel, group.getGroupName(), String.valueOf(group.getGroupPrivate()), RequestActionConstants.REQUEST_TO_JOIN.getAction(), picturePath, thumbnailPath, imageChanged);
                }
                else{
                    Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
                }



        }
    };


    private void showAlert(String message, String title, String positiveButton, DialogInterface.OnClickListener onPositiveClick) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(JoinPrivateGroupDialog.this.getContext());
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
