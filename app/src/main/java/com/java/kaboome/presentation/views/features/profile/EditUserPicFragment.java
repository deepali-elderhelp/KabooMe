package com.java.kaboome.presentation.views.features.profile;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.presentation.entities.UserModel;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserPicFragment extends DialogFragment {

    private static final String TAG = "KMEditUserPicFragment";

    private View view;
    private UserModel userModel;
//    private ProfileViewModel profileViewModel;
    private boolean imageChanged = false;

    private Button saveButton;
    private ImageView closeButton;
    private CircleImageView userImage;
    private ProgressBar userImageProgress;
    private Handler handler = new Handler(); //needed for Glide

    private String picturePath;
    private String thumbnailPath;
    private NavController navController;


    public EditUserPicFragment() {
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
        userModel = (UserModel) getArguments().getSerializable("user");
        navController = NavHostFragment.findNavController(EditUserPicFragment.this);
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
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
                Log.d(TAG, "Received new user name - "+o);
                String[] imagePaths = (String[]) o;

                picturePath = imagePaths[0];
                thumbnailPath = imagePaths[2];
                if(picturePath != null){
                    Glide.with(EditUserPicFragment.this)
                            .applyDefaultRequestOptions(new RequestOptions()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE))
                            .asBitmap()
                            .load(picturePath)
                            .into(userImage);

                    imageChanged = true;
                    saveButton.setEnabled(true);
                }
                else{
                    imageChanged = false;
                    saveButton.setEnabled(false);
                }

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_user_pic, container, false);
        userImage = view.findViewById(R.id.user_change_image);
        userImageProgress = view.findViewById(R.id.user_change_image_progress);

        TextView changePictureLink = view.findViewById(R.id.change_picture_label);

        changePictureLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PictureDialog pictureDialog = new PictureDialog();
//                pictureDialog.setPictureDoneListener(EditUserPicFragment.this);
//                Bundle args = new Bundle();
//                args.putBoolean("pictureToBeSavedOnServer", false);
//                pictureDialog.setArguments(args);
//                pictureDialog.show(getActivity().getSupportFragmentManager(), "pictureDialog");
                Bundle args = new Bundle();
                args.putBoolean("pictureToBeSavedOnServer", false);
                args.putString("imageId", "userId");
                navController.navigate(R.id.action_editUserPicFragment_to_pictureDialog, args);

            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putBoolean("pictureToBeSavedOnServer", false);
                args.putString("imageId", "userId");
                navController.navigate(R.id.action_editUserPicFragment_to_pictureDialog, args);
            }
        });



        saveButton = view.findViewById(R.id.save_user_image);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate
                UserModel userModelTemp = new UserModel();
                userModelTemp.setUserId(userModel.getUserId());
                if(imageChanged){
                    userModelTemp.setImageChanged(true);
                    userModelTemp.setImagePath(picturePath);
                    userModelTemp.setThumbnailPath(thumbnailPath);
                }

                navController.getPreviousBackStackEntry().getSavedStateHandle().set("userImage", userModelTemp);
                navController.popBackStack();
                dismiss();
            }
        });

//        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, userModel.getUserName() != null? userModel.getUserName():"K M");
        Drawable imageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.account_gray_192);
        ImageHelper.getInstance().loadUserImage(userModel.getUserId(), ImageTypeConstants.MAIN, userModel.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, userImage, null);

        closeButton = view.findViewById(R.id.edit_user_pic_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }



    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;

    }

}
