package com.java.kaboome.presentation.views.features.groupActions;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.R;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.viewModelProvider.CustomViewModelProvider;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupActionsDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "KMGroupActionsDialog";


    View rootView;
    CircleImageView groupImage;
    ProgressBar groupImageProgressBar;
    ImageView privacyImage;
    TextView groupName;
    LinearLayoutCompat unicastLabel;
//    boolean showGoToMessages = true;
    private UserGroupModel group;
    private boolean showMessages = true;
    private Context mContext;
    private GroupActionsViewModel groupActionsViewModel;
    private FrameLayout requestFrame;
    private FrameLayout messageFrame;
    private TextView messageCount;
    Handler handler = new Handler(); //needed for Glide
    TextView requestCount;


    public GroupActionsDialog() {
        // Required empty public constructor
    }

//    @Override
//    public int getTheme() {
//
//        return R.style.CustomDialogTheme;
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        group = (UserGroupModel) getArguments().getSerializable("group");
        groupActionsViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(group.getGroupId())).get(GroupActionsViewModel.class);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

//        group = (UserGroupModel) getArguments().getSerializable("group");

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

        Log.d(TAG, "onCreateView: ");

        TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move);
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group_actions_dialog, container, false);

        groupImage = rootView.findViewById(R.id.group_actions_dialog_image);
        privacyImage = rootView.findViewById(R.id.group_actions_dialog_group_private);
        groupName = rootView.findViewById(R.id.group_actions_dialog_name);
        unicastLabel = rootView.findViewById(R.id.group_actions_unicast);
        groupImageProgressBar = rootView.findViewById(R.id.group_actions_dialog_image_progress);
        requestFrame = rootView.findViewById(R.id.group_actions_requests_count_frame);
        requestCount = rootView.findViewById(R.id.group_actions_requests_count_number);

        messageFrame = rootView.findViewById(R.id.group_actions_messages_unread);
        messageCount = rootView.findViewById(R.id.group_actions_unread_count_number);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            groupImage.setTransitionName(group.getGroupId());
        }

        if(group.getPrivate()){
            privacyImage.setVisibility(View.VISIBLE);
        }
        else{
            privacyImage.setVisibility(View.GONE);
        }

        if(group.getUnicastGroup()){
            unicastLabel.setVisibility(View.VISIBLE);
        }
        else{
            unicastLabel.setVisibility(View.GONE);
        }

//        BottomSheetBehavior.from(rootView).setState(BottomSheetBehavior.STATE_EXPANDED);
//        privacyImage = rootView.findViewById(R.id.dialog_group_privacy_img);


//        if(!showGoToMessages){
//            messages.setVisibility(View.GONE);
//        }
//        else{
//            messages.setOnClickListener(this);
//        }

        ImageView messages = rootView.findViewById(R.id.group_actions_messages_icon);
        if(showMessages){
            messages.setEnabled(true);
            messages.setOnClickListener(this);
        }
        else{
            messages.setEnabled(false);
        }


        groupImage.setOnClickListener(this);

        ImageView info = rootView.findViewById(R.id.group_actions_group_info_icon);
        info.setOnClickListener(this);

        ImageView qrCode = rootView.findViewById(R.id.group_actions_qr_code_icon);
        qrCode.setOnClickListener(this);

        ImageView invite = rootView.findViewById(R.id.group_actions_invite_icon);
        invite.setOnClickListener(this);

        ImageView requests = rootView.findViewById(R.id.group_actions_requests_icon);
        requests.setOnClickListener(this);

        TextView messages_textview = rootView.findViewById(R.id.group_actions_messages);
        if(showMessages){
            messages_textview.setEnabled(true);
            messages_textview.setOnClickListener(this);
        }
        else{
            messages_textview.setEnabled(false);
        }


        TextView info_textview = rootView.findViewById(R.id.group_actions_group_info);
        info_textview.setOnClickListener(this);

        TextView qrCode_textview = rootView.findViewById(R.id.group_actions_qr_code);
        qrCode_textview.setOnClickListener(this);

        TextView invite_textview = rootView.findViewById(R.id.group_actions_invite);
        invite_textview.setOnClickListener(this);

        TextView requests_textview = rootView.findViewById(R.id.group_actions_requests);
        requests_textview.setOnClickListener(this);

        subscribeObservers();
        groupActionsViewModel.loadUnreadAndRequests();

        return rootView;
    }

    @Override
    public void onResume() {

        Log.d(TAG, "onResume: ");
        mContext = getContext();


        //set field values
        groupName.setText(group.getGroupName());

//        if(group.isPrivateGroup()){
//            privacyImage.setImageResource(R.drawable.private_group_white);
//        }
//        else{
//            privacyImage.setImageResource(R.drawable.public_group_white);
//        }

//        loadImage(group.getGroupId());

//        if("true".equalsIgnoreCase(group.getIsAdmin()) &&
//        group.getNumberOfRequests() > 0){
//            frameLayoutForRequestCount.setVisibility(View.VISIBLE);
//            requestCountTextView.setText(String.valueOf(group.getNumberOfRequests()));
//        }
//        else{
//            frameLayoutForRequestCount.setVisibility(View.INVISIBLE);
//        }
//        ImageHelper.loadGroupImage(group.getGroupId(), group.getImageUpdateTimestamp(), ImageHelper.getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);
        Drawable imageError = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, group.getGroupName());
        Drawable imagePlaceholder = AvatarHelper.generatePlaceholderAvatar(getContext(),R.dimen.group_actions_dialog_image_width, group.getGroupName());
        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
                ImageHelper.getInstance().getRequestManager(getContext()), imagePlaceholder, imageError,
                handler, groupImage, groupImageProgressBar, true);

        Log.d(TAG, "onResume: after image loading");


        super.onResume();
    }

    private void subscribeObservers(){

        Log.d(TAG, "subscribeObservers: ");
        groupActionsViewModel.getNumberOfUnreadMessages().removeObservers(getViewLifecycleOwner()); //if any old one hanging around
        groupActionsViewModel.getNumberOfUnreadMessages().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer > 0){
                    messageFrame.setVisibility(View.VISIBLE);
                    messageCount.setText(String.valueOf(integer));
                }
                else{
                    messageFrame.setVisibility(View.GONE);
                }
            }
        });

        groupActionsViewModel.getNumberOfRequests().removeObservers(getViewLifecycleOwner()); //if any old one hanging around
        groupActionsViewModel.getNumberOfRequests().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if("true".equalsIgnoreCase(group.getIsAdmin()) && (integer > 0)){
                    requestFrame.setVisibility(View.VISIBLE);
                    requestCount.setText(String.valueOf(integer));
                }
                else{
                    requestFrame.setVisibility(View.GONE);
                }
            }
        });
    }




    public void onClick(View v) {

        switch (v.getId()){

            case R.id.group_actions_group_info:
            case R.id.group_actions_group_info_icon:
            case R.id.group_actions_dialog_image: {
                Log.d(TAG, "onClick: Group info");
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", group);

                NavHostFragment.findNavController(this).navigate(R.id.action_groupActionsDialog_to_groupInfoFragment, bundle);
                this.dismiss();
                break;

//                Intent groupInfoIntent = new Intent(getContext(), GroupInfoActivity.class);
//                groupInfoIntent.putExtra("group", group);
//                startActivity(groupInfoIntent);
//                this.dismiss();
//                break;

            }

            case R.id.group_actions_invite:
            case R.id.group_actions_invite_icon:
            {
                Log.d(TAG, "onClick: Group invite");
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", group);

                NavHostFragment.findNavController(this).navigate(R.id.action_groupActionsDialog_to_inviteContactsFragment, bundle);
                this.dismiss();
                break;
            }

            case R.id.group_actions_messages:
            case R.id.group_actions_messages_icon:
            {
                Log.d(TAG, "onClick: Group messages");
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", group);

                NavHostFragment.findNavController(this).navigate(R.id.action_groupActionsDialog_to_groupMessagesFragment, bundle);
                this.dismiss();
                break;
                //change the transition name set on the image
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    groupImage.setTransitionName("group_messages_image");
//                }
//
//                Intent groupMessagesActivity = new Intent(getContext(), GroupMessagesActivity.class);
//                groupMessagesActivity.putExtra("group", group);
//                startActivity(groupMessagesActivity);
//                this.dismiss();

//                break;
            }

            case R.id.group_actions_qr_code:
            case R.id.group_actions_qr_code_icon:
            {
                Log.d(TAG, "onClick: Group QR");

                Bundle bundle = new Bundle();
                bundle.putSerializable("group", group);

                NavHostFragment.findNavController(this).navigate(R.id.action_groupActionsDialog_to_groupQRFragment, bundle);
                this.dismiss();
                break;
            }

            case R.id.group_actions_requests:
            case R.id.group_actions_requests_icon:
            {
                Log.d(TAG, "onClick: Group Requests");
                if("true".equalsIgnoreCase(group.getIsAdmin())){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", group);

                    NavHostFragment.findNavController(this).navigate(R.id.action_groupActionsDialog_to_groupRequestsFragment, bundle);
                    this.dismiss();
                }
                else{
                    Toast.makeText(getContext(), "Only Admins can see the Group Requests", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }

    }

//    public void setShowGoToMessages(boolean b) {
//        this.showGoToMessages = b;
//    }

    public void setGroup(UserGroupModel group) {
        this.group = group;
    }

    public boolean isShowMessages() {
        return showMessages;
    }

    public void setShowMessages(boolean showMessages) {
        this.showMessages = showMessages;
    }

}
