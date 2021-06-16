package com.java.kaboome.presentation.views.features.groupInfo;


import android.app.Dialog;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.java.kaboome.helpers.AppConfigHelper.getContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserPicDisplayFragment extends DialogFragment {

    private static final String TAG = "KMUserPicDispFrag";

    View view;
   GroupUserModel groupUserModel;


    ImageView closeButton;
    CircleImageView userImage;
    ProgressBar userImageProgressBar;


    public UserPicDisplayFragment() {
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
    public int getTheme() {

        return R.style.MyCustomDialogTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupUserModel = (GroupUserModel) getArguments().getSerializable("groupUser");
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_pic_display, container, false);
        userImage = view.findViewById(R.id.fr_usr_pic_display_img);
        userImageProgressBar = view.findViewById(R.id.fr_usr_pic_display_img_pb);
        closeButton = view.findViewById(R.id.fr_usr_pic_display_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        Drawable userImageErrorAndPlaceholder = getContext().getResources().getDrawable(R.drawable.bs_profile);
        ImageHelper.getInstance().loadGroupUserImage(groupUserModel.getGroupId(), ImageTypeConstants.MAIN, groupUserModel.getUserId(), groupUserModel.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                new Handler(), userImage, userImageProgressBar);


        return view;
    }



}
