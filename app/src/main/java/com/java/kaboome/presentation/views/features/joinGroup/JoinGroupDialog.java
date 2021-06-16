package com.java.kaboome.presentation.views.features.joinGroup;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.UpdateResourceModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.helpers.ErrorMessageHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.joinGroup.viewmodel.JoinGroupViewModel;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class JoinGroupDialog extends DialogFragment {

    private View view;
    private GroupModel group;
    private RequestManager requestManager;
    private Handler handler = new Handler(); //needed for Glide
    private CircleImageView groupImage;
    private CircleImageView userImage;
    private TextView groupName;
    private TextView groupDescription;
    private TextView createdByAlias;
    private TextView changeUserPicLink;
    private TextInputEditText userAlias;
    private TextInputEditText userRole;
    private ImageView closeDialog;
    private ProgressBar progressBar;
    private ProgressBar groupImageProgressBar;
    private TextView numberOfMembers;

    private AppCompatButton joinGroupButton;
    private JoinGroupViewModel joinGroupViewModel;

    private String picturePath;
    private String thumbnailPath;
    private boolean imageChanged = false;
    private static final String TAG = "KMJoinGroupDialog";
    private NavController navController;


    public JoinGroupDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(JoinGroupDialog.this);
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
        view =  inflater.inflate(R.layout.fragment_join_group_dialog, container, false);

        group = (GroupModel) getArguments().getSerializable("group");

        groupImage = view.findViewById(R.id.group_join_dialog_image);
        userImage = view.findViewById(R.id.group_join_user_image);

        changeUserPicLink = view.findViewById(R.id.group_join_change_picture_label);
        if(imageChanged){
            changeUserPicLink.setText(getString(R.string.reset_your_group_picture));
        }
        else{
            changeUserPicLink.setText(R.string.change_your_group_picture);
        }

        groupName = view.findViewById(R.id.group_join_dialog_name);
        groupName.setText(group.getGroupName());

        groupDescription = view.findViewById(R.id.group_join_description);
        groupDescription.setText(group.getGroupDescription());

        createdByAlias = view.findViewById(R.id.group_join_createdby);
        createdByAlias.setText(group.getCreatedByAlias());

        userAlias = view.findViewById(R.id.group_join_alias);
        userRole = view.findViewById(R.id.group_join_role);
        userAlias.addTextChangedListener(new RoleAndAliasTextWatcher());
        userRole.addTextChangedListener(new RoleAndAliasTextWatcher());

        joinGroupButton = view.findViewById(R.id.joinGroupButton);
        joinGroupButton.setOnClickListener(joinGroupClicked);

        closeDialog = view.findViewById(R.id.group_join_close_button);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        progressBar = view.findViewById(R.id.group_join_progress_bar);
        groupImageProgressBar = view.findViewById(R.id.group_join_dialog_image_progress);

        joinGroupViewModel = ViewModelProviders.of(this).get(JoinGroupViewModel.class);

//        ImageHelper.loadGroupImage(group.getGroupId(), group.getImageUpdateTimestamp(), ImageHelper.getRequestManager(getActivity(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);

        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, group.getGroupName());
        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, groupImage, null);

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
                navController.navigate(R.id.action_joinGroupDialog_to_pictureDialog, args);
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

                Glide.with(JoinGroupDialog.this)
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

        joinGroupViewModel.getGroupJoinUpdate().removeObservers(getViewLifecycleOwner()); //in case if there is any old one hanging around
        joinGroupViewModel.getGroupJoinUpdate().observe(getViewLifecycleOwner(), new Observer<UpdateResourceModel<String>>() {
            @Override
            public void onChanged(UpdateResourceModel<String> stringUpdateResourceModel) {
                if(stringUpdateResourceModel.status == UpdateResourceModel.Status.UPDATING){
                    progressBar.setVisibility(View.VISIBLE);
                }
                if(stringUpdateResourceModel.status == UpdateResourceModel.Status.SUCCESS){
                    progressBar.setVisibility(View.INVISIBLE);

                    UserGroupModel domainUserGroup = joinGroupViewModel.getUserGroupModel();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", domainUserGroup);
                    if(navController.getCurrentDestination().getId() == R.id.joinGroupDialog) {
                        //first go to Group List and then go to GMF from there
                        navController.popBackStack(R.id.groupsListFragment, false);
                        navController.navigate(R.id.action_groupsListFragment_to_groupMessagesFragment, bundle);

                    }


//                    DialogHelper.showOnlyYesAlert(getContext(),getString(R.string.join_create_success), getString(R.string.group_join_create_success_label), "Got It" ,new DialogInterface.OnClickListener(){
//
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dismiss();
//                            navController.popBackStack(R.id.groupsListFragment, false);
////                            getActivity().finish(); //it is successful, now remove the search activity too
//                        }
//                    });
                }
                if(stringUpdateResourceModel.status == UpdateResourceModel.Status.ERROR){
                    progressBar.setVisibility(View.INVISIBLE);
                    String errorMessage = ErrorMessageHelper.getErrorMessage(stringUpdateResourceModel.message);
                    DialogHelper.showOnlyYesAlert(getContext(),errorMessage, "Failed", "Ok" ,new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
                }
            }
        });
    }

    public void setGroup(GroupModel group) {
        this.group = group;
    }

    View.OnClickListener joinGroupClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Long currentDate = (new Date()).getTime();
            UserGroupModel userGroupModel = new UserGroupModel();
            userGroupModel.setGroupId(group.getGroupId());
            userGroupModel.setGroupName(group.getGroupName());
            userGroupModel.setAlias(userAlias.getText().toString());
            userGroupModel.setRole(userRole.getText().toString());
            userGroupModel.setIsAdmin("false");
            userGroupModel.setPrivate(group.getGroupPrivate());
            userGroupModel.setGroupExpiry(group.getExpiryDate());
            userGroupModel.setLastAccessed(currentDate);
            userGroupModel.setAdminsLastAccessed(currentDate);
            userGroupModel.setCacheClearTS(currentDate);
            userGroupModel.setAdminsCacheClearTS(currentDate);

            if(imageChanged){//user selected a new image, so current date is the timestamp
//                userGroupModel.setImageUpdateTimestamp((new Date()).getTime());
                userGroupModel.setUserImageUpdateTimestamp((new Date()).getTime());
            }
            else{
//                userGroupModel.setImageUpdateTimestamp(AppConfigHelper.getCurrentUserImageTimestamp());
                userGroupModel.setUserImageUpdateTimestamp(AppConfigHelper.getCurrentUserImageTimestamp());
            }

            if(NetworkHelper.isOnline()){
                joinGroupViewModel.joinUserToGroup(userGroupModel, picturePath, thumbnailPath, imageChanged);
            }
            else{
                Toast.makeText(getContext(), "Sorry, no network! This action needs network connection", Toast.LENGTH_SHORT).show();
            }


        }
    };


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
