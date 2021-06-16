package com.java.kaboome.presentation.views.features.invitationsList;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.java.kaboome.R;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.presentation.entities.InvitationModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.DateFormatter;
import com.java.kaboome.presentation.images.ImageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvitedGroupDetailsDialog extends BottomSheetDialogFragment {

    View view;
    InvitationModel invitationModel;
//    private RequestManager requestManager;
    Handler handler = new Handler(); //needed for Glide
    CircleImageView groupImage;
    ProgressBar groupImageProgressBar;
    TextView groupName;
    TextView message;
    TextView messageLabel;

//    ImageView closeDialog;



    private static final String TAG = "KMJoinGroupDialog";


    public InvitedGroupDetailsDialog() {
        // Required empty public constructor
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//
//
//        return dialog;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_invited_group_details_dialog, container, false);

        invitationModel = (InvitationModel) getArguments().getSerializable("invitation");

        groupImage = view.findViewById(R.id.invi_details_dialog_image);
        groupImageProgressBar = view.findViewById(R.id.invi_details_dialog_image_progress);

        groupName = view.findViewById(R.id.invi_details_group_name);
        groupName.setText(invitationModel.getGroupName());

        message = view.findViewById(R.id.invi_details_message_by_invitee);
        if(invitationModel.getMessageByInvitee() != null && !invitationModel.getMessageByInvitee().trim().isEmpty()){
            message.setText(invitationModel.getMessageByInvitee());
        }
        else{
            message.setText("You have been invited to join the group");
        }

        messageLabel = view.findViewById(R.id.invi_details_invited_by);
        messageLabel.setText("Invited By: "+invitationModel.getInvitedByAlias() +" on "+ DateFormatter.getDateFormattedPretty(invitationModel.getDateInvited()));

//        closeDialog = view.findViewById(R.id.group_invi_details_close_button);
//        closeDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });

//        requestManager = initGlide();
//        ImageHelper.loadGroupImage(invitationModel.getGroupId(), null, ImageHelper.getRequestManager(getActivity(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, groupImageProgressBar);
        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, invitationModel.getGroupName());
        ImageHelper.getInstance().loadGroupImage(invitationModel.getGroupId(), ImageTypeConstants.MAIN, null,
                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                handler, groupImage, null);


        return view;
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



    public void setInvitationModel(InvitationModel invitationModel) {
        this.invitationModel = invitationModel;
    }


    private RequestManager initGlide() {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.account_group_grey)
                .error(R.drawable.account_group_grey);

        return Glide.with(this)
                .setDefaultRequestOptions(options);

    }



}
