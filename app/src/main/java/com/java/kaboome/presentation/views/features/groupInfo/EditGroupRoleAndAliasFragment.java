package com.java.kaboome.presentation.views.features.groupInfo;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.helpers.ImagesUtilHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.views.features.joinGroup.JoinPrivateGroupDialog;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditGroupRoleAndAliasFragment extends DialogFragment {

    private static final String TAG = "KMEditGroupRoleAliasFr";

    private View view;
    GroupUserModel groupUserModel;
//    GroupViewModel groupViewModel;

    private Button saveButton;
    private TextInputEditText newGroupUserAlias;
    private TextInputEditText newGroupUserRole;
    private ImageView closeButton;
    private CircleImageView userGroupImage;
    private TextView changePictureLabel;
    private String picturePath;
    private String thumbnailPath;
    private boolean imageChanged = false;
    private Handler handler = new Handler(); //needed for Glide
    private NavController navController;
    private ProgressBar userImageProgressBar;

    public EditGroupRoleAndAliasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupUserModel = (GroupUserModel) getArguments().getSerializable("groupUser");
        navController = NavHostFragment.findNavController(EditGroupRoleAndAliasFragment.this);
//        groupViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(groupModel.getGroupId())).get(GroupViewModel.class);

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
                saveButton.setEnabled(true);

                Glide.with(EditGroupRoleAndAliasFragment.this)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .asBitmap()
                        .load(picturePath)
                        .into(userGroupImage);

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_group_role_and_alias, container, false);
        userImageProgressBar = view.findViewById(R.id.group_edit_alias_role_user_image_pb);
        newGroupUserAlias = view.findViewById(R.id.editGroupAlias);
        newGroupUserRole = view.findViewById(R.id.editGroupRole);
        newGroupUserAlias.setText(groupUserModel.getAlias());
        newGroupUserAlias.setSelection(newGroupUserAlias.getText().length()); //cursor at end point
        newGroupUserRole.setText(groupUserModel.getRole());
        newGroupUserRole.setSelection(newGroupUserRole.getText().length()); //cursor at end point
        newGroupUserAlias.addTextChangedListener(new RoleAndAliasTextWatcher());
        newGroupUserRole.addTextChangedListener(new RoleAndAliasTextWatcher());
        saveButton = view.findViewById(R.id.save_group_aliasAndRole);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate
                //if user alias is empty, give error, we need an alias
                //if user role is empty, say that member will be used
                if(newGroupUserAlias.getText().toString() == null || newGroupUserAlias.getText().toString().isEmpty()){
                    DialogHelper.showDialogMessage(getContext(), "Missing Alias", getResources().getString(R.string.missing_alias_message));
                    return;
                }
                if(newGroupUserRole.getText().toString() == null || newGroupUserRole.getText().toString().isEmpty()){
                    DialogHelper.showDialogMessage(getContext(), "Missing Role", getResources().getString(R.string.missing_role_message));
                    newGroupUserRole.setText("Member");
                    return;
                }

                GroupUserModel groupUserModelTemp = new GroupUserModel();
                groupUserModelTemp.setGroupId(groupUserModel.getGroupId());
                groupUserModelTemp.setUserId(groupUserModel.getUserId());
                groupUserModelTemp.setAlias(newGroupUserAlias.getText().toString());
                groupUserModelTemp.setRole(newGroupUserRole.getText().toString());
                //it could be that the image is not changed, but then the old timestamp
                //needs to be passed
                groupUserModelTemp.setImageUpdateTimestamp(groupUserModel.getImageUpdateTimestamp());
                groupUserModelTemp.setImageChanged(imageChanged);
                groupUserModelTemp.setImagePath(picturePath);
                groupUserModelTemp.setThumbnailPath(thumbnailPath);
////                groupViewModel.updateGroupUser(groupUserModelTemp, "updateGroupUserRoleAndAlias");
//                groupViewModel.updateGroupUser(groupUserModelTemp, GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS.getAction());
//                dismiss();

                NavController navController = NavHostFragment.findNavController(EditGroupRoleAndAliasFragment.this);

                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupUserRoleAndAlias", groupUserModelTemp);
                navController.popBackStack();
                dismiss();
            }
        });
        closeButton = view.findViewById(R.id.group_edit_alias_role_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        userGroupImage = view.findViewById(R.id.group_edit_alias_role_user_image);
        changePictureLabel = view.findViewById(R.id.group_edit_alias_role_change_picture_label);
        if(imageChanged){
            changePictureLabel.setText(getString(R.string.reset_your_group_picture));
        }
        else{
            changePictureLabel.setText(R.string.change_your_user_group_picture);
        }
        Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
        ImageHelper.getInstance().loadGroupUserImage(groupUserModel.getGroupId(), ImageTypeConstants.MAIN, groupUserModel.getUserId(), groupUserModel.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                handler, userGroupImage, userImageProgressBar);
//        ImageHelper.loadUserImage(ImagesUtilHelper.getGroupUserImageName(groupUserModel.getGroupId(), groupUserModel.getUserId()), AppConfigHelper.getCurrentUserImageTimestamp(),
//                ImageHelper.getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
//                handler, userGroupImage, null);

        changePictureLabel.setOnClickListener(imageChangeListener);
        userGroupImage.setOnClickListener(imageChangeListener);
        return view;
    }

    View.OnClickListener imageChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(imageChanged){
                //user had selected some other image, but now wants tp reset to the default one
                Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
                ImageHelper.getInstance().loadUserImage(AppConfigHelper.getUserId(),ImageTypeConstants.MAIN, AppConfigHelper.getCurrentUserImageTimestamp(),
                        ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                        handler, userGroupImage, userImageProgressBar);
                imageChanged = false;
            }
            else{
                Bundle args = new Bundle();
                args.putBoolean("pictureToBeSavedOnServer", false);
                args.putString("imageId", "userId");
                navController.navigate(R.id.action_editGroupRoleAndAliasFragment_to_pictureDialog, args);
            }


        }
    };

    private class RoleAndAliasTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String groupUserRole = newGroupUserRole.getText().toString().trim();
            String groupUserAlias = newGroupUserAlias.getText().toString().trim();
            saveButton.setEnabled(!groupUserRole.isEmpty() || !groupUserAlias.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


}
