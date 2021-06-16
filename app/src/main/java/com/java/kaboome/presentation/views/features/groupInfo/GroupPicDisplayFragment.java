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

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupPicDisplayFragment extends DialogFragment {

    private static final String TAG = "KMGroupPicDispFrag";

    View view;
   GroupModel group;


    ImageView closeButton;
//    CircleImageView groupImage;
    ImageView groupImage;


    public GroupPicDisplayFragment() {
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
        group = (GroupModel) getArguments().getSerializable("group");
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_group_pic_display, container, false);
        groupImage = view.findViewById(R.id.fr_grp_pic_display_img);
        closeButton = view.findViewById(R.id.fr_grp_pic_display_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, group.getGroupName());
        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                new Handler(), groupImage, null);


        return view;
    }



}
