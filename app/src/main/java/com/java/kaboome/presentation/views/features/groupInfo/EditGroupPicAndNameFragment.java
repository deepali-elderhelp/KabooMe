package com.java.kaboome.presentation.views.features.groupInfo;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditGroupPicAndNameFragment extends DialogFragment {

    private static final String TAG = "KMEditGrpPic&NameFrag";

    View view;
    GroupModel groupModel;

    Button saveButton;
    TextInputEditText newGroupName;
    SwitchCompat publicOrPrivateSwitch;
    ImageView closeButton;
    CircleImageView groupImage;
    ProgressBar groupImageProgressBar;
    TextView changePicture;
//    TextView savePicture;
    private boolean privacy = false;
    private NavController navController;
    private Handler handler = new Handler(); //needed for Glide
    private String picturePath;
    private String thumbnailPath;
    private boolean imageChanged = false;


    public EditGroupPicAndNameFragment() {
        //Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupModel = (GroupModel) getArguments().getSerializable("group");
        navController = NavHostFragment.findNavController(EditGroupPicAndNameFragment.this);
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        groupViewModel = ViewModelProviders.of(getActivity()).get(GroupViewModel.class);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_group_pic_and_name, container, false);
        saveButton = view.findViewById(R.id.save_group_name_and_image); //disabled by default
        publicOrPrivateSwitch = view.findViewById(R.id.edit_group_name_private_switch);
        if(groupModel.getGroupPrivate()){
            privacy = true;
            publicOrPrivateSwitch.setChecked(true);
        }
        else{
            privacy = false;
            publicOrPrivateSwitch.setChecked(false);
        }
        publicOrPrivateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                saveButton.setEnabled(true);
                if (bChecked) {
                    DialogHelper.showDialogMessage(getActivity(), "Private Group", getResources().getString(R.string.group_private_alert));
                    privacy = true;
                } else {
                    DialogHelper.showDialogMessage(getActivity(), "Public Group", getResources().getString(R.string.group_public_alert));
                    privacy = false;
                }
            }
        });
        newGroupName = view.findViewById(R.id.editGroupName);
        newGroupName.setText(groupModel.getGroupName());
        newGroupName.addTextChangedListener(new GroupNameTextWatcher());
        changePicture = view.findViewById(R.id.change_picture_label);
        if(imageChanged){
            changePicture.setText(getString(R.string.reset_your_group_picture));
        }
        else{
            changePicture.setText(R.string.change_your_group_picture);
        }


        changePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageChanged){
                    //user had selected some other image, but now wants tp reset to the default one
                    Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.bs_profile);
                    ImageHelper.getInstance().loadGroupImage(groupModel.getGroupId(), ImageTypeConstants.MAIN,  groupModel.getImageUpdateTimestamp(),
                            ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                            handler, groupImage, null);
                    imageChanged = false;
                }
                else{
                    Bundle args = new Bundle();
                    args.putBoolean("pictureToBeSavedOnServer", false);
                    args.putString("imageId", "groupId");
                    if(navController.getCurrentDestination().getId() == R.id.editGroupPicAndNameFragment) {
                        navController.navigate(R.id.action_editGroupPicAndNameFragment_to_pictureDialog, args);
                    }
                }


            }
        });

//        savePicture = view.findViewById(R.id.save_picture_label);
//        if(imageChanged){
//            changePicture.setVisibility(View.GONE);
//            savePicture.setVisibility(View.VISIBLE);
//        }
//        else{
//            changePicture.setVisibility(View.VISIBLE);
//            savePicture.setVisibility(View.GONE);
//        }
//        changePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //hide this button and show the save image one
//                Bundle args = new Bundle();
//                args.putBoolean("pictureToBeSavedOnServer", false);
//                navController.navigate(R.id.action_editGroupPicAndNameFragment_to_pictureDialog, args);
//            }
//        });
//        savePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupImagePath", picturePath);
//                navController.popBackStack();
//                dismiss();
//            }
//        });
        groupImage = view.findViewById(R.id.edit_group_pic_name_image);
        groupImageProgressBar = view.findViewById(R.id.edit_group_pic_name_image_progress);

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(navController.getCurrentDestination().getId() == R.id.editGroupPicAndNameFragment){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", groupModel);
                    navController.navigate(R.id.action_editGroupPicAndNameFragment_to_groupPicDisplayFragment, bundle);
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate
                GroupModel groupModeltemp = new GroupModel();
                groupModeltemp.setGroupId(groupModel.getGroupId());
                groupModeltemp.setGroupName(newGroupName.getText().toString());
                groupModeltemp.setGroupPrivate(privacy);
                groupModeltemp.setImageUpdateTimestamp(groupModel.getImageUpdateTimestamp());
                groupModeltemp.setImageChanged(imageChanged);
                groupModeltemp.setImagePath(picturePath);
                groupModeltemp.setThumbnailPath(thumbnailPath);


                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupNamePrivacyAndImage", groupModeltemp);
                navController.popBackStack();
                dismiss();
            }
        });
        closeButton = view.findViewById(R.id.edit_group_pic_name_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        loadImage();
//        ImageHelper.loadGroupImage(groupModel.getGroupId(), groupModel.getImageUpdateTimestamp(), ImageHelper.getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);
        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, groupModel.getGroupName());
        ImageHelper.getInstance().loadGroupImage(groupModel.getGroupId(),ImageTypeConstants.MAIN,  groupModel.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, groupImage, null);

        if(!groupModel.getCurrentUserGroupStatus().equals(UserGroupStatusConstants.ADMIN_MEMBER)) {
            saveButton.setVisibility(View.GONE);
            changePicture.setVisibility(View.GONE);
            publicOrPrivateSwitch.setEnabled(false);
            newGroupName.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData imagePicked = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("imagePicked");
        imagePicked.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received new user image - "+o.toString());
                String[] imagePaths = (String[]) o;

                picturePath = imagePaths[0];
                thumbnailPath = imagePaths[2];

                Glide.with(EditGroupPicAndNameFragment.this)
                        .applyDefaultRequestOptions(new RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE))
                        .asBitmap()
                        .load(picturePath)
                        .into(groupImage);
//                enableSaveImage();

                imageChanged = true;
                saveButton.setEnabled(true);
            }
        });

    }
    private class GroupNameTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String groupName = s.toString().trim();
            saveButton.setEnabled(!groupName.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
