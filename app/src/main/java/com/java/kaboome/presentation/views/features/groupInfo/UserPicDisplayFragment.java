package com.java.kaboome.presentation.views.features.groupInfo;


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
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.presentation.entities.UserModel;
import com.java.kaboome.presentation.images.ImageHelper;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserPicDisplayFragment extends DialogFragment {

    private static final String TAG = "KMUserPicDispFrag";

    View view;
   UserModel userModel;


    ImageView closeButton;
    ImageView userImage;
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
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    @Override
    public int getTheme() {

        return R.style.MyCustomDialogTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel = (UserModel) getArguments().getSerializable("user");
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
        ImageHelper.getInstance().loadUserImage(userModel.getUserId(), ImageTypeConstants.MAIN, userModel.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), userImageErrorAndPlaceholder, userImageErrorAndPlaceholder,
                new Handler(), userImage, userImageProgressBar);

        return view;
    }



}
