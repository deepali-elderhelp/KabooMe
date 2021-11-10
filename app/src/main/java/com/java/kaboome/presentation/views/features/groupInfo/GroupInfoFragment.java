package com.java.kaboome.presentation.views.features.groupInfo;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.java.kaboome.R;
import com.java.kaboome.constants.GroupActionConstants;
import com.java.kaboome.constants.ImageTypeConstants;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.NetworkHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.helpers.AvatarHelper;
import com.java.kaboome.presentation.helpers.DateFormatter;
import com.java.kaboome.presentation.helpers.DialogHelper;
import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.PrintHelper;
import com.java.kaboome.presentation.helpers.QRCodeHelper;
import com.java.kaboome.presentation.images.ImageHelper;
import com.java.kaboome.presentation.viewModelProvider.CustomViewModelProvider;
import com.java.kaboome.presentation.views.features.BaseFragment;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserImageClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserLongClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.viewmodel.GroupViewModel;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupAliasAndRoleEditClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserListViewAdapter;
import com.java.kaboome.presentation.views.features.home.HomeActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupInfoFragment extends BaseFragment implements GroupAliasAndRoleEditClickListener,
        GroupUserLongClickListener, GroupUserImageClickListener, GroupUserClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "KMGroupInfoFragment";

    private View rootView;
    private GroupViewModel groupViewModel;
    private UserGroupModel group;
    private GroupModel groupModel;
    //    private RequestManager requestManager;
    private Handler handler = new Handler(); //needed for Glide
    private CircleImageView groupImage;
    private ProgressBar groupImageProgressBar;
    private ImageView qrCodeImage;
    private TextView inviteMembers;
    private TextView shareQRCodeTextView;
    private TextView printQRCode;
    private GroupUserListViewAdapter adminAdapter;
    private GroupUserListViewAdapter memberAdapter;
    private RecyclerView groupAdminsRecyclerView;
    private RecyclerView groupMembersRecyclerView;
    private ProgressBar nameAndImageUpdateProgressBar;
    private ProgressBar editGroupDetailsProgressBar;
    private ProgressBar editGroupMembersProgressBar;
    private ProgressBar editQRCodeProgressBar;
    private ProgressBar fullProgressBar;
    private NavController navController;
    private boolean uploadInProgress = false;
    private boolean deleteInProgress = false;
    private Toolbar mainToolbar;
    private WebView mWebView;
    private TextView leaveAndDeleteGroup;
    private SwitchCompat acceptingRequest;
    private ImageView privateImage;
    private ImageView unicastImage;

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    private ImageView networkOffImageView;

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        group = (UserGroupModel)getArguments().getSerializable("group");

        groupViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(group.getGroupId())).get(GroupViewModel.class);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_group_info, container, false);

        groupAdminsRecyclerView = rootView.findViewById(R.id.group_info_admin_recycler);
        groupMembersRecyclerView = rootView.findViewById(R.id.group_info_members_recycler);

        nameAndImageUpdateProgressBar = rootView.findViewById(R.id.group_info_edit_image_card_progress_bar);
        editGroupDetailsProgressBar = rootView.findViewById(R.id.group_info_edit_details_card_progress_bar);
        editGroupMembersProgressBar = rootView.findViewById(R.id.group_info_members_card_progress_bar);
        editQRCodeProgressBar = rootView.findViewById(R.id.group_info_edit_qr_code_card_progress_bar);
        fullProgressBar = rootView.findViewById(R.id.group_info_full_progress_bar);

        groupImage = rootView.findViewById(R.id.group_info_image);
        acceptingRequest = rootView.findViewById(R.id.group_info_accept_requests);
//        groupImageProgressBar = rootView.findViewById(R.id.group_info_image_progress);
        qrCodeImage = rootView.findViewById(R.id.group_info_qr_image);

        inviteMembers = rootView.findViewById(R.id.group_info_invite_people);
        inviteMembers.setOnClickListener(inviteMembersClicked);

        shareQRCodeTextView = rootView.findViewById(R.id.group_info_share_qr_code);
        shareQRCodeTextView.setOnClickListener(shareQRCodeClicked);
        printQRCode = rootView.findViewById(R.id.group_info_print_qr_code);
        printQRCode.setOnClickListener(printQRCodeClicked);

        privateImage = rootView.findViewById(R.id.group_private_image);
        privateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showDialogMessage(getActivity(), "Private Group", getResources().getString(R.string.group_private_message));
            }
        });
        unicastImage = rootView.findViewById(R.id.group_broadcast_image);
        unicastImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.showDialogMessage(getActivity(), "Admin Messages Only", getResources().getString(R.string.announcement_only_alert));
            }
        });

        navController = NavHostFragment.findNavController(GroupInfoFragment.this);
        uploadInProgress = false;

        //for system back button
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(uploadInProgress || deleteInProgress){
                    Log.d(TAG, "handleOnBackPressed: upload or delete going on");
                    Snackbar mySnackbar = Snackbar.make(getView(), "Please Wait...", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                else{
                    Log.d(TAG, "handleOnBackPressed: upload or delete finished");
                    NavigationUI.navigateUp(navController, (DrawerLayout) null);
                }
            }
        });

        //for toolbar back button
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        mainToolbar = act.findViewById(R.id.mainToolbar);
        mainToolbar.getMenu().clear();
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uploadInProgress || deleteInProgress){
                    Log.d(TAG, "handleOnBackPressed: upload or delete going on");
                    Snackbar mySnackbar = Snackbar.make(getView(), "Please Wait...", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                else{
                    Log.d(TAG, "handleOnBackPressed: upload or delete finished");
                    NavigationUI.navigateUp(navController, (DrawerLayout) null);
                }

            }
        });


        networkOffImageView = act.findViewById(R.id.mainToolbarNetworkOff);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, group.getGroupName());
//        ImageHelper.loadGroupImage(group.getGroupId(), group.getImageUpdateTimestamp(),
//                ImageHelper.getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, groupImage, null);
//
//        loadQRImage();
//
//        addEditListeners();
//        initRecyclerViews();
//        subscribeObservers();
//        initiateLoading();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // We use a String here, but any type that can be put in a Bundle is supported
        MutableLiveData groupDescLiveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("groupDescription");
        groupDescLiveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received new group desc - "+o);
                GroupModel groupModelTemp = (GroupModel) o;
                if(!NetworkHelper.isOnline()){
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }
                else{
//                    uploadInProgress = true;
//                    groupViewModel.updateGroup(groupModelTemp, GroupActionConstants.UPDATE_GROUP_DESC.getAction());
                    handleUpdatingGroup(groupModelTemp, GroupActionConstants.UPDATE_GROUP_DESC.getAction());
                }
            }
        });
        MutableLiveData groupExpiryLiveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("groupExpiry");
        groupExpiryLiveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received new group expiry - "+o);
                GroupModel groupModeltemp = (GroupModel) o;
                if(!NetworkHelper.isOnline()){
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }
                else{
//                    uploadInProgress = true;
//                    groupViewModel.updateGroup(groupModeltemp, GroupActionConstants.UPDATE_GROUP_EXPIRY.getAction());
                    handleUpdatingGroup(groupModeltemp, GroupActionConstants.UPDATE_GROUP_EXPIRY.getAction());
                }
            }
        });
        MutableLiveData groupUserNotificationLiveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("groupUserNotification");
        groupUserNotificationLiveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received new group user notification - "+o);
                GroupUserModel groupUserModel = (GroupUserModel) o;
                if(!NetworkHelper.isOnline()){
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }
                else{
//                    uploadInProgress = true;
//                    groupViewModel.updateGroupUser(groupUserModel, GroupActionConstants.UPDATE_GROUP_USER_NOTIFICATION.getAction());
                    handleUpdatingGroupUser(groupUserModel, GroupActionConstants.UPDATE_GROUP_USER_NOTIFICATION.getAction());
                }
            }
        });
        MutableLiveData groupUserRoleAliasLiveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("groupUserRoleAndAlias");
        groupUserRoleAliasLiveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received new group role and alias - "+o);
                GroupUserModel groupUserModel = (GroupUserModel) o;
                if(!NetworkHelper.isOnline()){
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }
                else{
//                    uploadInProgress = true;
//                    groupViewModel.updateGroupUser(groupUserModel, GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS.getAction());
                    handleUpdatingGroupUser(groupUserModel, GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS.getAction());
                }
            }
        });
        MutableLiveData groupNewAdminsLiveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("groupNewAdminMembers");
        groupNewAdminsLiveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received new group admin members - "+o);
                GroupModel groupModelTemp = new GroupModel();
                groupModelTemp.setGroupId(groupModel.getGroupId());
                List<GroupUserModel> listOfNewAdmins = (List<GroupUserModel>) o;
                groupModelTemp.setRegularMembers(listOfNewAdmins);
                if(!NetworkHelper.isOnline()){
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }
                else{
//                    uploadInProgress = true;
//                    for(GroupUserModel groupUserModel: listOfNewAdmins){
//                        groupViewModel.updateGroupUser(groupUserModel, GroupActionConstants.UPDATE_GROUP_USERS_TO_ADMIN.getAction());
//                    }
//                    handleUpdatingGroupUser(listOfNewAdmins, GroupActionConstants.UPDATE_GROUP_USERS_TO_ADMIN.getAction());
                    handleUpdatingGroup(groupModelTemp, GroupActionConstants.UPDATE_GROUP_USERS_TO_ADMIN.getAction());
                }

            }
        });

        //trying for going to conversations from here
//        MutableLiveData groupSendPrivateMessageLiveData = navController.getCurrentBackStackEntry()
//                .getSavedStateHandle()
//                .getLiveData("groupSendAdminMessage");
//        groupSendPrivateMessageLiveData.observe(getViewLifecycleOwner(), new Observer() {
//            @Override
//            public void onChanged(Object o) {
//                Log.d(TAG, "Received send private message to member - "+o);
//                GroupUserModel memberToSendDataTo = (GroupUserModel) o;
//                UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
//                userGroupConversationModel.setGroupId(memberToSendDataTo.getGroupId());
//                userGroupConversationModel.setUserId(AppConfigHelper.getUserId());
//                userGroupConversationModel.setOtherUserId(memberToSendDataTo.getUserId());
//                userGroupConversationModel.setOtherUserName(memberToSendDataTo.getAlias());
//                userGroupConversationModel.setOtherUserRole(memberToSendDataTo.getRole());
//                userGroupConversationModel.setLastAccessed((new Date()).getTime());
//
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("conversation", userGroupConversationModel);
//                bundle.putSerializable("group", group);
//                if(!NetworkHelper.isOnline()){
//                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
//                        navController.navigate(R.id.action_groupInfoFragment_to_groupAdminUserMessagesFragment, bundle);
//                    }
//
//                }
//
//            }
//        });

        //till here

        MutableLiveData groupNewNamePrivacyImageLiveData = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("groupNamePrivacyAndImage");
        groupNewNamePrivacyImageLiveData.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received new group name - "+o);
                GroupModel groupModeltemp = (GroupModel) o;
                if(!NetworkHelper.isOnline()){
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }
                else{
//                    uploadInProgress = true;
//                    groupViewModel.updateGroup(groupModeltemp, GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction());
                    handleUpdatingGroup(groupModeltemp, GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE.getAction());
                }
            }
        });

        MutableLiveData groupUserRemove = navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("groupUserRemove");
        groupUserRemove.observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "Received group user to remove - "+o);
                GroupUserModel groupUserModel = (GroupUserModel) o;
                if(!NetworkHelper.isOnline()){
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }
                else{
                    removeGroupUser(groupUserModel);
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //not really sure why the following code was there in the first place
        //but it was causing a memory leak anyways, so commented it
//        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                NavigationUI.navigateUp(navController, (DrawerLayout) null);
//            }
//        });

        ((HomeActivity)getActivity()).resetToolbarBackButton();

    }

    View.OnClickListener inviteMembersClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            GroupUserModel currentUser = groupModel.getGroupUserById(AppConfigHelper.getUserId());
            UserGroupModel userGroupModel = new UserGroupModel();
            userGroupModel.setGroupId(groupModel.getGroupId());
            userGroupModel.setGroupName(groupModel.getGroupName());
            userGroupModel.setPrivate(groupModel.getGroupPrivate());
            userGroupModel.setAlias(currentUser.getAlias());

            bundle.putSerializable("group", userGroupModel);

            navController.navigate(R.id.action_groupInfoFragment_to_inviteContactsFragment, bundle);
        }
    };

    View.OnClickListener shareQRCodeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shareQRCode();
        }
    };

    View.OnClickListener printQRCodeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doWebViewPrint();
        }
    };


    private void addEditListeners() {

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", groupModel);
                if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment){
                    navController.navigate(R.id.action_groupInfoFragment_to_groupPicDisplayFragment, bundle);
                }
            }
        });

        ImageView groupNameAndImageEdit = rootView.findViewById(R.id.group_info_edit_image);
        groupNameAndImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("group", groupModel);

                if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment){
                    navController.navigate(R.id.action_groupInfoFragment_to_editGroupPicAndNameFragment, bundle);
                }

            }
        });

        ImageView groupDescriptionEdit = rootView.findViewById(R.id.group_info_edit_description);
        groupDescriptionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("group", groupModel);
                if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
                    navController.navigate(R.id.action_groupInfoFragment_to_editGroupDescription, bundle);
                }
            }
        });

        ImageView groupExpiryEdit = rootView.findViewById(R.id.group_info_edit_expiration);
        groupExpiryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("group", groupModel);

                if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
                    navController.navigate(R.id.action_groupInfoFragment_to_editGroupExpiryFragment, bundle);
                }
            }
        });

        ImageView groupNotificationEdit = rootView.findViewById(R.id.group_info_edit_notifications);
        groupNotificationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("group", groupModel);
                if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
                    navController.navigate(R.id.action_groupInfoFragment_to_editGroupUserNotificationFragment, bundle);
                }
            }
        });

        TextView addMoreAdmins = rootView.findViewById(R.id.group_info_add_more_admins);
        addMoreAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //the action is permitted only when the current user is an admin himself
                if(isCurrentUserAnAdmin()){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("group", groupModel);
                    if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
                        navController.navigate(R.id.action_groupInfoFragment_to_editGroupAddAdminFragment, bundle);
                    }
                }
                else{
                    Toast.makeText(getContext(), "Sorry, only admins can perform this action", Toast.LENGTH_SHORT).show();
                }


            }
        });

        leaveAndDeleteGroup = rootView.findViewById(R.id.group_info_edit_leave_group);
        leaveAndDeleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkHelper.isOnline()){
                    Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                }
                else{
//                    if(groupModel.getCurrentUserAdmin() && groupModel.getNumberOfAdmins() <= 1) {//this is the only admin
                    if(groupModel.getCurrentUserGroupStatus().equals(UserGroupStatusConstants.ADMIN_MEMBER) && groupModel.getNumberOfAdmins() <= 1) {//this is the only admin
                        DialogHelper.showAlert(getContext(), getResources().getString(R.string.leave_group_delete_message), "Delete Group", "Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleRemovingGroup();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                    else{
                        DialogHelper.showAlert(getContext(),getResources().getString(R.string.leave_group_message), "Leave Group", "Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleLeavingGroup();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                }

            }
        });
    }

    private void handleUpdatingGroup(GroupModel groupModel, String action){
        groupViewModel.getGroupEditActionUpdate().observe(getViewLifecycleOwner(), new Observer<GroupEditDetails>() {
            @Override
            public void onChanged(GroupEditDetails groupEditDetails) {
                if(groupEditDetails != null && (groupEditDetails.getStatus() == GroupEditDetails.Status.UPDATING)){
                    showHideProgressBar(groupEditDetails.getAction(), true);
                    uploadInProgress = true;
                }
                else if(groupEditDetails != null && (groupEditDetails.getStatus() == GroupEditDetails.Status.SUCCESS)){
                    showHideProgressBar(groupEditDetails.getAction(), false);
                    uploadInProgress = false;
                    groupViewModel.getGroupEditActionUpdate().removeObservers(getViewLifecycleOwner());
                }
                else if(groupEditDetails != null && (groupEditDetails.getStatus() == GroupEditDetails.Status.ERROR)){
                    showHideProgressBar(groupEditDetails.getAction(), false);
                    Toast.makeText(getContext(), "Sorry, update to the group failed, please try again", Toast.LENGTH_SHORT).show();
                    uploadInProgress = false;
                    groupViewModel.getGroupEditActionUpdate().removeObservers(getViewLifecycleOwner());
                }

            }
        });
        uploadInProgress = true;
        groupViewModel.updateGroup(groupModel, action);
    }

    private void handleUpdatingGroupUser(GroupUserModel groupUserModel, String action){
        groupViewModel.getGroupUserEditActionUpdate().observe(getViewLifecycleOwner(), new Observer<GroupEditDetails>() {
            @Override
            public void onChanged(GroupEditDetails groupUserEditDetails) {
                if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.UPDATING)){
                    showHideProgressBar(groupUserEditDetails.getAction(), true);
                    uploadInProgress = true;
                }
                else if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.SUCCESS)){
                    showHideProgressBar(groupUserEditDetails.getAction(), false);
                    //not sure if the user is member or admin
                    groupViewModel.getGroupUserEditActionUpdate().removeObservers(getViewLifecycleOwner());
                    adminAdapter.updateCurrentGroupUserImageTS(groupUserEditDetails.getImageUpdatedTimestamp());
                    memberAdapter.updateCurrentGroupUserImageTS(groupUserEditDetails.getImageUpdatedTimestamp());
                    uploadInProgress = false;
                }
                else if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.ERROR)){
                    showHideProgressBar(groupUserEditDetails.getAction(), false);
                    Toast.makeText(getContext(), "Sorry, update to the group user failed, please try again", Toast.LENGTH_SHORT).show();
                    groupViewModel.getGroupUserEditActionUpdate().removeObservers(getViewLifecycleOwner());
                    uploadInProgress = false;
                }
            }
        });
        uploadInProgress = true;
        groupViewModel.updateGroupUser(groupUserModel, action);
    }

    //TODO: fix it - this is not updating single group user
    //TODO: this is updating mutliple group users, there should be a different
    //TODO: API for that
    //for now, using Group Update to this rather, all users to be made admin are passed as regular members
    //and at Group level the update happens
    private void handleUpdatingGroupUser(List<GroupUserModel> groupUserModels, String action){

        groupViewModel.getGroupUserEditActionUpdate().observe(getViewLifecycleOwner(), new Observer<GroupEditDetails>() {
            @Override
            public void onChanged(GroupEditDetails groupUserEditDetails) {
                if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.UPDATING)){
                    showHideProgressBar(groupUserEditDetails.getAction(), true);
                    uploadInProgress = true;
                }
                else if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.SUCCESS)){
                    showHideProgressBar(groupUserEditDetails.getAction(), false);
                    //not sure if the user is member or admin
                    groupViewModel.getGroupUserEditActionUpdate().removeObservers(getViewLifecycleOwner());
                    adminAdapter.updateCurrentGroupUserImageTS(groupUserEditDetails.getImageUpdatedTimestamp());
                    memberAdapter.updateCurrentGroupUserImageTS(groupUserEditDetails.getImageUpdatedTimestamp());
                    uploadInProgress = false;


                }
                else if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.ERROR)){
                    showHideProgressBar(groupUserEditDetails.getAction(), false);
                    groupViewModel.getGroupUserEditActionUpdate().removeObservers(getViewLifecycleOwner());
                    Toast.makeText(getContext(), "Sorry, update to the group user failed, please try again", Toast.LENGTH_SHORT).show();
                    uploadInProgress = false;
                }
            }
        });
        uploadInProgress = true;

        for(GroupUserModel groupUserModel: groupUserModels){
            groupViewModel.updateGroupUser(groupUserModel, action);
        }
    }

    private void handleRemovingGroup() {
        deleteInProgress = true;
        final ProgressBar waitBeforeLeaving = rootView.findViewById(R.id.group_info_full_progress_bar);
        //first remove existing observers
        removeObservers();

        groupViewModel.getGroupDelete().observe(this, new Observer<GroupDeleteDetails>() {
            @Override
            public void onChanged(GroupDeleteDetails groupDeleteDetails) {
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.DELETING)){
                    waitBeforeLeaving.setVisibility(View.VISIBLE);
                    deleteInProgress = true;
                }
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.SUCCESS)){
                    Log.d(TAG, "onChanged: Group is finally removed - all done, now finish this fragment");
                    waitBeforeLeaving.setVisibility(View.INVISIBLE);
                    deleteInProgress = false;
                    navController.popBackStack();
                }
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.ERROR)){
                    Toast.makeText(getContext(), "Sorry, removing group action failed, please try again", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged: Error on Group removing");
                    waitBeforeLeaving.setVisibility(View.INVISIBLE);
                    deleteInProgress = false;
//                    navController.popBackStack(); //why should I leave?
                }

            }
        });

        groupViewModel.deleteGroup(groupModel.getGroupId(),null, null,  GroupActionConstants.REMOVE_GROUP_FOR_ALL);
    }


    private void handleLeavingGroup() {

        deleteInProgress = true;
        //first remove existing observers
        removeObservers();

        groupViewModel.getGroupDelete().observe(this, new Observer<GroupDeleteDetails>() {
            @Override
            public void onChanged(GroupDeleteDetails groupDeleteDetails) {
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.DELETING)){
//                    Toast.makeText(GroupInfoActivity.this, "Please wait "+groupDeleteDetails.getPurpose(), Toast.LENGTH_SHORT).show();
                    showHideProgressBar(groupDeleteDetails.getAction(), true);
                    deleteInProgress = true;
                }
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.SUCCESS)){
//                    Toast.makeText(GroupInfoActivity.this, "Success deleting " + groupDeleteDetails.getPurpose(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged: Group is finally removed - all done, now finish this fragment");
                    showHideProgressBar(groupDeleteDetails.getAction(), false);
                    deleteInProgress = false;
                    navController.popBackStack();
                }
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.ERROR)){
                    Toast.makeText(getContext(), "Sorry, leaving group action failed, please try again", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged: Error on Group removing");
                    showHideProgressBar(groupDeleteDetails.getAction(), false);
                    deleteInProgress = false;
//                    navController.popBackStack(); //why should I leave?
                }

            }
        });

//        groupViewModel.deleteGroup(groupModel.getGroupId(),AppConfigHelper.getRequestUserId(), AppConfigHelper.getRequestUserId(),  "groupRemoveForCurrentUser");
        groupViewModel.deleteGroup(groupModel.getGroupId(), AppConfigHelper.getUserId(), AppConfigHelper.getUserId(),  GroupActionConstants.REMOVE_GROUP_FOR_USER);
    }

    @Override
    public void onGroupUserLongClick(final GroupUserModel groupUserModel) {
        //show dialog box to confirm if the user should be removed from the group
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Do you want to remove this user from group?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if(!isCurrentUserAnAdmin()){
                            Toast.makeText(getContext(), "Sorry, only Admins can delete other members", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(AppConfigHelper.getUserId().equals(groupUserModel.getUserId())){
                            Toast.makeText(getContext(), "Please go to the bottom and select 'Leave Group'", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!NetworkHelper.isOnline()){
                            Toast.makeText(getContext(), "No Network: This action needs network connection", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        removeGroupUser(groupUserModel);
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void removeGroupUser(GroupUserModel groupUserModel) {
        deleteInProgress = true;

        groupViewModel.getGroupDelete().observe(this, new Observer<GroupDeleteDetails>() {
            @Override
            public void onChanged(GroupDeleteDetails groupDeleteDetails) {
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.DELETING)){
//                    Toast.makeText(GroupInfoActivity.this, "Please wait "+groupDeleteDetails.getPurpose(), Toast.LENGTH_SHORT).show();
                    showHideProgressBar(groupDeleteDetails.getAction(), true);
                    deleteInProgress = true;
                }
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.SUCCESS)){
//                    Toast.makeText(GroupInfoActivity.this, "Success deleting " + groupDeleteDetails.getPurpose(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged: Group is finally removed - all done, now finish this fragment");
                    showHideProgressBar(groupDeleteDetails.getAction(), false);
                    deleteInProgress = false;
                    //re render the group users
                }
                if(groupDeleteDetails != null && (groupDeleteDetails.getStatus() == GroupDeleteDetails.Status.ERROR)){
                    Toast.makeText(getContext(), "Sorry, removing the group user failed, please try again", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged: Group is finally removed - all done, now finish this fragment");
                    showHideProgressBar(groupDeleteDetails.getAction(), false);
                    deleteInProgress = false;
                }

            }
        });

        groupViewModel.deleteGroup(groupModel.getGroupId(), AppConfigHelper.getUserId(), groupUserModel.getUserId(),  GroupActionConstants.REMOVE_GROUP_FOR_OTHER_USER);

    }

    private boolean isCurrentUserAnAdmin(){
//        List<GroupUserModel> admins = groupModel.getAdmins();
//        for(GroupUserModel groupUserModel: admins){
//            if(groupUserModel.getUserId().equals(AppConfigHelper.getUserId())){
//                return true;
//            }
//        }
//        return false;
        return groupModel.getCurrentUserGroupStatus().equals(UserGroupStatusConstants.ADMIN_MEMBER);


    }

    @Override
    public void onGroupAliasAndRoleEditClick(GroupUserModel groupUserModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("groupUser", groupUserModel);

        if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
            navController.navigate(R.id.action_groupInfoFragment_to_editGroupRoleAndAliasFragment, bundle);
        }

    }




    private void loadQRImage() {
        Bitmap qrCodeImageBitmap = QRCodeHelper.generate(getContext(), group.getGroupId(), ErrorCorrectionLevel.H, 2, 4.8, 2.6);
        qrCodeImage.setImageBitmap(qrCodeImageBitmap);
    }

    private void initiateLoading() {
        Log.d(TAG, "initiateLoading: ");
        groupViewModel.loadGroup();
    }

    private void subscribeObservers(){


        groupViewModel.getGroupForView().removeObservers(getViewLifecycleOwner()); //if any old hanging there
        groupViewModel.getGroupForView().observe(getViewLifecycleOwner(), new Observer<GroupModel>() {
            @Override
            public void onChanged(GroupModel groupModelReturned) {
                //here is the new group
                //call methods to update the group and to update the recycler view
//                Log.d(TAG, "onChanged: GroupModel received is "+groupModelReturned);
                Log.d(TAG, "onChanged: ");
                groupModel = groupModelReturned;
                renderData(groupModel);
            }
        });


//        groupViewModel.getGroupEditActionUpdate().observe(getViewLifecycleOwner(), new Observer<GroupEditDetails>() {
//            @Override
//            public void onChanged(GroupEditDetails groupEditDetails) {
//                if(groupEditDetails != null && (groupEditDetails.getStatus() == GroupEditDetails.Status.UPDATING)){
//                    showHideProgressBar(groupEditDetails.getAction(), true);
//                    uploadInProgress = true;
////                    Toast.makeText(GroupInfoActivity.this, "Please wait"+groupEditDetails.getPurpose(), Toast.LENGTH_SHORT).show();
//                }
//                else if(groupEditDetails != null && (groupEditDetails.getStatus() == GroupEditDetails.Status.SUCCESS)){
////                        Toast.makeText(GroupInfoActivity.this, "Success uploading " + groupEditDetails.getPurpose(), Toast.LENGTH_SHORT).show();
//                    showHideProgressBar(groupEditDetails.getAction(), false);
//                    uploadInProgress = false;
//                }
//                else if(groupEditDetails != null && (groupEditDetails.getStatus() == GroupEditDetails.Status.ERROR)){
//                    showHideProgressBar(groupEditDetails.getAction(), false);
//                    Toast.makeText(getContext(), "Sorry, update to the group failed, please try again", Toast.LENGTH_SHORT).show();
////                    Toast.makeText(GroupInfoActivity.this, "Failed uploading "+groupEditDetails.getPurpose(), Toast.LENGTH_SHORT).show();
//
//                    uploadInProgress = false;
//                }
//
//            }
//        });


//        groupViewModel.getGroupUserEditActionUpdate().observe(getViewLifecycleOwner(), new Observer<GroupEditDetails>() {
//            @Override
//            public void onChanged(GroupEditDetails groupUserEditDetails) {
//                if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.UPDATING)){
//                    showHideProgressBar(groupUserEditDetails.getAction(), true);
//                    uploadInProgress = true;
//                }
//                else if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.SUCCESS)){
//                    showHideProgressBar(groupUserEditDetails.getAction(), false);
//                    //not sure if the user is member or admin
//
//                    adminAdapter.updateCurrentGroupUserImageTS(groupUserEditDetails.getImageUpdatedTimestamp());
//                    memberAdapter.updateCurrentGroupUserImageTS(groupUserEditDetails.getImageUpdatedTimestamp());
//                    //might want to refresh the adapter for GroupUsers for this particular user
//                    //also if the image is updated, reload the image
////                    if(groupUserEditDetails.getAction() == GroupActionConstants.UPDATE_GROUP_IMAGE){
//////                        ImageHelper.loadGroupImage(groupModel.getGroupId(), groupEditDetails.getImageUpdatedTimestamp(), ImageHelper.getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.bs_profile), handler, groupImage, null);
////                        Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, groupModel.getGroupName());
////                        ImageHelper.loadGroupImage(groupModel.getGroupId(), groupEditDetails.getImageUpdatedTimestamp(),
////                                ImageHelper.getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
////                                handler, groupImage, null);
////
////                    }
//                    uploadInProgress = false;
////                    Toast.makeText(GroupInfoActivity.this, "Success uploading "+groupEditDetails.getPurpose(), Toast.LENGTH_SHORT).show();
//                }
//                else if(groupUserEditDetails != null && (groupUserEditDetails.getStatus() == GroupEditDetails.Status.ERROR)){
//                    showHideProgressBar(groupUserEditDetails.getAction(), false);
//                    Toast.makeText(getContext(), "Sorry, update to the group user failed, please try again", Toast.LENGTH_SHORT).show();
//                    uploadInProgress = false;
////                    Toast.makeText(GroupInfoActivity.this, "Failed uploading "+groupEditDetails.getPurpose(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void showHideProgressBar(GroupActionConstants groupActionConstants, boolean b) {
        if(groupActionConstants == null){ return; }

        if(groupActionConstants == GroupActionConstants.UPDATE_GROUP_NAME_PRIVACY_IMAGE){
            nameAndImageUpdateProgressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }
        if(groupActionConstants == GroupActionConstants.UPDATE_GROUP_DESC){
            editGroupDetailsProgressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }
        if(groupActionConstants == GroupActionConstants.UPDATE_GROUP_EXPIRY){
            editGroupDetailsProgressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }

        if(groupActionConstants == GroupActionConstants.UPDATE_GROUP_REQUESTS_SETTING){
            editGroupDetailsProgressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }

        if(groupActionConstants == GroupActionConstants.UPDATE_GROUP_USER_NOTIFICATION){
            editGroupDetailsProgressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }

        if(groupActionConstants == GroupActionConstants.UPDATE_GROUP_USER_ROLE_AND_ALIAS){
            editGroupMembersProgressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }
        if(groupActionConstants == GroupActionConstants.UPDATE_GROUP_USERS_TO_ADMIN){
            editGroupMembersProgressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }
        if(groupActionConstants == GroupActionConstants.REMOVE_GROUP_FOR_USER){
            fullProgressBar.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        }

    }

    private void renderData(GroupModel groupModel){

        //some changes in the render data are being done when there is an absence of data
        //there could be case that no data comes from server - network is off and also
        //cache is empty for some reason. In that case, empty data comes from the server


        TextView groupName = rootView.findViewById(R.id.group_info_name);
        if(groupModel.getGroupName() != null && !groupModel.getGroupName().isEmpty()){
            groupName.setText(groupModel.getGroupName());
        }
        else{
            groupName.setText(group.getGroupName());
        }


        TextView groupMemberNumber = rootView.findViewById(R.id.group_info_members_number);
        if(groupModel.getNumberOfMembers() > 0){
            groupMemberNumber.setText(groupModel.getNumberOfMembers()+" members");
        }
        else{
            groupMemberNumber.setText("");
        }


        TextView groupDescription = rootView.findViewById(R.id.group_info_description);
        groupDescription.setText(groupModel.getGroupDescription());

        TextView groupExpiration = rootView.findViewById(R.id.group_info_expiration);

        if(groupModel.getExpiryDate() == null || groupModel.getExpiryDate() == 0){
            groupExpiration.setText("Manual");
        }
        else{
            groupExpiration.setText(DateFormatter.getDateFormattedPretty(groupModel.getExpiryDate()));
        }

        acceptingRequest.setOnCheckedChangeListener(null);
        if(groupModel.getOpenToRequests() == null || groupModel.getOpenToRequests()){
            acceptingRequest.setChecked(false);
        }
        else{
            acceptingRequest.setChecked(true);
        }

        acceptingRequest.setOnCheckedChangeListener(requestChangeListener);
        if(groupModel.getCurrentUserGroupStatus() == null || !groupModel.getCurrentUserGroupStatus().equals(UserGroupStatusConstants.ADMIN_MEMBER)){
            acceptingRequest.setEnabled(false);
        }

        TextView notification = rootView.findViewById(R.id.group_info_notifications);
        notification.setText(groupModel.getNotifications());

        TextView groupAdminLabel = rootView.findViewById(R.id.group_info_group_admins);
        groupAdminLabel.setText("Group Admins ("+groupModel.getNumberOfAdmins()+")");

        TextView membersLabel = rootView.findViewById(R.id.group_info_group_members);
        membersLabel.setText("Members ("+groupModel.getNumberOfRegularMembers()+")");

        adminAdapter.setGroupUsers(groupModel.getAdmins());
        memberAdapter.setGroupUsers(groupModel.getRegularMembers());

        TextView seeAllMembers = rootView.findViewById(R.id.group_info_see_all_member);
        if(groupModel.getNumberOfRegularMembers() > 10){
            seeAllMembers.setVisibility(View.VISIBLE);
        }
        else{
            seeAllMembers.setVisibility(View.GONE);
        }

        //render image again if changed
        if(groupModel.getImageUpdateTimestamp() != null && group.getImageUpdateTimestamp() != null && (groupModel.getImageUpdateTimestamp() > group.getImageUpdateTimestamp())){
            //        ImageHelper.loadGroupImage(groupModel.getGroupId(), groupModel.getImageUpdateTimestamp(), ImageHelper.getRequestManager(getContext(), R.drawable.account_group_grey, R.drawable.account_group_grey), handler, groupImage, null);
            Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(),R.dimen.group_actions_dialog_image_width, groupModel.getGroupName());
            ImageHelper.getInstance().loadGroupImage(groupModel.getGroupId(), ImageTypeConstants.MAIN, groupModel.getImageUpdateTimestamp(),
                    ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                    handler, groupImage, null);
        }

        if(groupModel.getGroupPrivate() != null && groupModel.getGroupPrivate()) {
            privateImage.setVisibility(View.VISIBLE);
        }
        else{
            privateImage.setVisibility(View.GONE);
        }

        if(groupModel.getUnicastGroup() != null && groupModel.getUnicastGroup()){
            unicastImage.setVisibility(View.VISIBLE);
        }
        else{
            unicastImage.setVisibility(View.GONE);
        }

    }

    private void initRecyclerViews(){
//        adminAdapter = new GroupUserListViewAdapter(initGlide(), GroupUserListViewAdapter.UserRecyclerType.ADMIN_USERS_TYPE, this, this, this, this);
        adminAdapter = new GroupUserListViewAdapter(GroupUserListViewAdapter.UserRecyclerType.ADMIN_USERS_TYPE, this, this, this, this);
        groupAdminsRecyclerView.setAdapter(adminAdapter);
        groupAdminsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        memberAdapter = new GroupUserListViewAdapter(initGlide(), GroupUserListViewAdapter.UserRecyclerType.MEMBER_USERS_TYPE, this, this, this, this);
        memberAdapter = new GroupUserListViewAdapter(GroupUserListViewAdapter.UserRecyclerType.MEMBER_USERS_TYPE, this, this, this, this);
        groupMembersRecyclerView.setAdapter(memberAdapter);
        groupMembersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



    }

    private RequestManager initGlide() {

//        RequestOptions options = new RequestOptions()
////                .placeholder(R.drawable.bs_profile)
//                .error(R.drawable.bs_profile);
        RequestOptions options = new RequestOptions();

        return Glide.with(this)
                .setDefaultRequestOptions(options);

    }

    private void removeObservers(){
        groupViewModel.getGroupUserEditActionUpdate().removeObservers(this);
//        groupViewModel.getGroupUserUpdate().removeObservers(this);
        groupViewModel.getGroupEditActionUpdate().removeObservers(this);
//        groupViewModel.getGroupUpdate().removeObservers(this);
        groupViewModel.getGroupForView().removeObservers(this);
//        groupViewModel.getGroup().removeObservers(this);

    }

    //it this works without the permission, hence commenting it
    //now we are storing the image in cache directory, it should not need the permission
//    @AfterPermissionGranted(REQUEST_WRITE_EXTERNAL_STORAGE)
    private void shareQRCode(){

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Bitmap groupQRCodeBitmap = QRCodeHelper.getQRImage(getContext(), group.getGroupId(),2, 8.2, 6.2);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            groupQRCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            Bitmap decodedByte = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            FileOutputStream stream = null; // overwrites this image every time
            try {
                stream = new FileOutputStream(FileUtils.getCacheFilePathForImages(getContext()) + "/qr_code_image.jpg");
                decodedByte.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                stream.close();
            } catch (FileNotFoundException e) {
                //handle error
                e.printStackTrace();
            }
            catch (IOException e) {
                //handle error
                e.printStackTrace();
            }
            File imageFile = FileUtils.getCacheImageFile(getContext());
            Uri imageToShare = FileProvider.getUriForFile(getContext(), "com.java.kaboome.fileprovider", imageFile);
            String textToShare = PrintHelper.getTextForSharing(getContext(),groupModel.getGroupPrivate(), groupModel.getGroupName(), groupModel.getGroupDescription() );

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(textToShare));
            share.putExtra(Intent.EXTRA_STREAM, imageToShare);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
//            startActivity(Intent.createChooser(share, "Share with"));
        //this is how it works with android 11 otherwise using the line above, the error thrown was
        //security exception : permission denied
            startActivity(share);

//        }
//        else{
//            EasyPermissions.requestPermissions(this, "This permission is needed to access your gallery", REQUEST_WRITE_EXTERNAL_STORAGE, perms);
//        }


    }

    private void doWebViewPrint() {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(getActivity());
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i(TAG, "page finished loading " + url);
//                createWebPrintJob(view);
                String jobName = getString(R.string.app_name) + " Document";
                PrintHelper.createWebPrintJob(view, getActivity(), jobName);
                mWebView = null;
            }
        });

        // Generate an HTML document on the fly:
//        String htmlDocument = getHtmlOfLayout();
        String htmlDocument = PrintHelper.getHtmlOfLayout(getContext(), groupModel.getGroupId(), groupModel.getGroupPrivate(),
                groupModel.getGroupName(), groupModel.getGroupDescription()
        );


//        String htmlDocument = "<html><body><h1>"+userGroupModel.getGroupName()+"</h1><b>"+groupPrivacyString+"</b><p>"+groupModel.getGroupDescription()+"</p><p><img src='"+image+"' /></p></body></html>";
//        htmlDocument.replace("{IMAGE_PLACEHOLDER}", image);
        Log.d(TAG, "html "+htmlDocument);
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if(requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            shareQRCode();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onLoginSuccess() {

        if(getContext() != null) {
            RequestManager requestManagerForGroupImage = Glide.with(groupImage);
            Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(), R.dimen.group_actions_dialog_image_width, group.getGroupName());
//        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
//                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, groupImage, null);
            ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
                    requestManagerForGroupImage, imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                    handler, groupImage, null);
        }

//        loadQRImage();
//
//        addEditListeners();
//        initRecyclerViews();
//        subscribeObservers();
        initiateLoading();
    }

    @Override
    public void whileLoginInProgress() {
        if(getContext() != null) {
            RequestManager requestManagerForGroupImage = Glide.with(groupImage);
            Drawable imageErrorAndPlaceholder = AvatarHelper.generateAvatar(getContext(), R.dimen.group_actions_dialog_image_width, group.getGroupName());
//        ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
//                ImageHelper.getInstance().getRequestManager(getContext()), imageErrorAndPlaceholder, imageErrorAndPlaceholder,
//                handler, groupImage, null);
            ImageHelper.getInstance().loadGroupImage(group.getGroupId(), ImageTypeConstants.MAIN, group.getImageUpdateTimestamp(),
                    requestManagerForGroupImage, imageErrorAndPlaceholder, imageErrorAndPlaceholder,
                    handler, groupImage, null);


            loadQRImage();
        }

        addEditListeners();
        initRecyclerViews();
        subscribeObservers();
        initiateLoading();
    }

    @Override
    public void onNetworkOff() {
        networkOffImageView.setVisibility(View.VISIBLE);
        if(leaveAndDeleteGroup != null) {leaveAndDeleteGroup.setEnabled(false);}
    }

    @Override
    public void onNetworkOn() {
        networkOffImageView.setVisibility(View.GONE);
        if(leaveAndDeleteGroup != null) {leaveAndDeleteGroup.setEnabled(true);}
    }

    @Override
    public void onGroupUserImageClick(GroupUserModel groupUserModel) {
        //show user image big
        Bundle bundle = new Bundle();
        bundle.putSerializable("groupUser", groupUserModel);

        if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
            navController.navigate(R.id.action_groupInfoFragment_to_groupUserPicDisplayFragment, bundle);
        }
    }

    @Override
    public void onGroupUserClick(GroupUserModel groupUserModel) {
        //show user image big
        Bundle bundle = new Bundle();
        bundle.putSerializable("groupUser", groupUserModel);
        bundle.putSerializable("userGroup", group);
        bundle.putBoolean("currentUserIsAdmin", isCurrentUserAnAdmin());

        if(AppConfigHelper.getUserId().equals(groupUserModel.getUserId())){
            //this is the current user, user clicked on his name
            if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
                navController.navigate(R.id.action_groupInfoFragment_to_editGroupRoleAndAliasFragment, bundle);
            }

        }
        else{
            if(navController.getCurrentDestination().getId() == R.id.groupInfoFragment) {
                navController.navigate(R.id.action_groupInfoFragment_to_groupUserActionsDialog, bundle);
            }
        }


    }

    CompoundButton.OnCheckedChangeListener requestChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {

            if(!NetworkHelper.isOnline()){
                Toast.makeText(getContext(), "Sorry, this action needs network connection", Toast.LENGTH_SHORT).show();
                return;
            }
            if (bChecked) {
                DialogHelper.showAlert(getContext(),getResources().getString(R.string.group_requests_close), "Requests Close","Continue",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //call backend only if selection different from current
                                if(groupModel.getOpenToRequests()){
                                    GroupModel groupModeltemp = new GroupModel();
                                    groupModeltemp.setGroupId(groupModel.getGroupId());
                                    groupModeltemp.setOpenToRequests(false);
                                    if(!NetworkHelper.isOnline()){
                                        Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        handleUpdatingGroup(groupModeltemp, GroupActionConstants.UPDATE_GROUP_REQUESTS_SETTING.getAction());
                                    }
                                }

                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                acceptingRequest.setOnCheckedChangeListener(null);
                                acceptingRequest.setChecked(false);
                                acceptingRequest.setOnCheckedChangeListener(requestChangeListener);
                                dialog.dismiss();
                            }
                        });
            }
            else{
                DialogHelper.showAlert(getContext(),getResources().getString(R.string.group_requests_open), "Requests Open", "Continue",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //call backend only if selection different from current
                                if(!groupModel.getOpenToRequests()){
                                    GroupModel groupModeltemp = new GroupModel();
                                    groupModeltemp.setGroupId(groupModel.getGroupId());
                                    groupModeltemp.setOpenToRequests(true);
                                    if(!NetworkHelper.isOnline()){
                                        Toast.makeText(getContext(), "Sorry, this action needs Network Connection", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        handleUpdatingGroup(groupModeltemp, GroupActionConstants.UPDATE_GROUP_REQUESTS_SETTING.getAction());
                                    }
                                }

                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                acceptingRequest.setOnCheckedChangeListener(null);
                                acceptingRequest.setChecked(true);
                                acceptingRequest.setOnCheckedChangeListener(requestChangeListener);
                                dialog.dismiss();
                            }
                        });
            }

        }
    };
}
