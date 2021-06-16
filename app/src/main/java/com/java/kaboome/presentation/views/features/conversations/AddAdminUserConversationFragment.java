package com.java.kaboome.presentation.views.features.conversations;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.java.kaboome.R;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.entities.UserGroupConversationModel;
import com.java.kaboome.presentation.entities.UserGroupModel;
import com.java.kaboome.presentation.viewModelProvider.CustomViewModelProvider;
import com.java.kaboome.presentation.views.features.conversations.adapter.ConversationImageClickListener;
import com.java.kaboome.presentation.views.features.conversations.adapter.GroupUsersForConvListViewAdapter;
import com.java.kaboome.presentation.views.features.conversations.adapter.NewConversationClickListener;
import com.java.kaboome.presentation.views.features.conversations.viewmodel.AddConversationViewModel;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserListViewAdapter;
import com.java.kaboome.presentation.views.features.groupInfo.viewmodel.GroupViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAdminUserConversationFragment extends DialogFragment implements ConversationImageClickListener, NewConversationClickListener {

    private static final String TAG = "KMAddAdminUserConvFrag";

    View view;
    AddConversationViewModel addConversationViewModel;
    UserGroupModel userGroupModel;
    List<UserGroupConversationModel> oldConversations;
    TextView noMembersAvailable;


    RecyclerView groupUsersRecyclerView;


    private GroupUsersForConvListViewAdapter groupUserListViewAdapter;
    private NavController navController;


    public AddAdminUserConversationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userGroupModel = (UserGroupModel) getArguments().getSerializable("group");
        oldConversations = getArguments().getParcelableArrayList("conversations");
        addConversationViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(userGroupModel.getGroupId())).get(AddConversationViewModel.class);
        navController = NavHostFragment.findNavController(AddAdminUserConversationFragment.this);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_admin_user_conversation, container, false);

        groupUsersRecyclerView = view.findViewById(R.id.add_admin_user_conv_recycler);
        noMembersAvailable = view.findViewById(R.id.add_admin_user_conv_no_users);

        subscribeObservers();
        initRecyclerView();

//        addConversationViewModel.loadGroupUsers();
        addConversationViewModel.loadGroup();
        AppCompatImageView closeButton = view.findViewById(R.id.add_admin_user_conv_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }


    private void initRecyclerView(){
        groupUserListViewAdapter = new GroupUsersForConvListViewAdapter(this);
        groupUsersRecyclerView.setAdapter(groupUserListViewAdapter);
        groupUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void subscribeObservers() {


//        addConversationViewModel.getGroupUsersForView().observe(getViewLifecycleOwner(), new Observer<List<GroupUserModel>>() {
//            @Override
//            public void onChanged(List<GroupUserModel> groupUserModels) {
//                if(groupUserListViewAdapter != null) {
//                    List<GroupUserModel> filteredMembers = filterMembers(groupUserModels);
//                    if(filteredMembers != null && filteredMembers.size() > 0){
//                        groupUsersRecyclerView.setVisibility(View.VISIBLE);
//                        noMembersAvailable.setVisibility(View.GONE);
//                        groupUserListViewAdapter.setGroupUsers(filterMembers(groupUserModels));
//                    }
//                    else{
//                        groupUsersRecyclerView.setVisibility(View.GONE);
//                        noMembersAvailable.setVisibility(View.VISIBLE);
//
//                    }
//                    }
//
//            }
//        });

        addConversationViewModel.getGroupForView().observe(getViewLifecycleOwner(), new Observer<GroupModel>() {
            @Override
            public void onChanged(GroupModel groupModel) {
                if(groupModel != null && groupModel.getRegularMembers() != null && groupModel.getRegularMembers().size() > 0) {

                    List<GroupUserModel> filteredMembers = filterMembers(groupModel.getRegularMembers());
                    if (filteredMembers != null && filteredMembers.size() > 0) {
                        groupUsersRecyclerView.setVisibility(View.VISIBLE);
                        noMembersAvailable.setVisibility(View.GONE);
                        groupUserListViewAdapter.setGroupUsers(filteredMembers);
                    } else {
                        groupUsersRecyclerView.setVisibility(View.GONE);
                        noMembersAvailable.setVisibility(View.VISIBLE);

                    }
                }
                else{ //if there are no regular members
                    groupUsersRecyclerView.setVisibility(View.GONE);
                    noMembersAvailable.setVisibility(View.VISIBLE);
                }
                }
            });

    }

    private List<GroupUserModel> filterMembers(List<GroupUserModel> originalList){
        List<GroupUserModel> listOfFilteredGroupUsers = new ArrayList<>();
        for(GroupUserModel groupUserModel: originalList) {
            if(!containsConversation(groupUserModel.getUserId())){
                listOfFilteredGroupUsers.add(groupUserModel);
            }
        }
        return listOfFilteredGroupUsers;

    }

//    private List<GroupUserModel> filterMembers(List<GroupUserModel> originalList){
//        List<GroupUserModel> listOfRegularGroupUsers = new ArrayList<>();
//        for(GroupUserModel groupUserModel: originalList) {
//            if (groupUserModel.getIsAdmin().equals("false")) {
//                if(!containsConversation(groupUserModel.getUserId())){
//                    listOfRegularGroupUsers.add(groupUserModel);
//                }
//            }
//        }
//        return listOfRegularGroupUsers;
//
//    }

    private boolean containsConversation(String otherUserId){
        if(oldConversations == null || otherUserId == null){
            return false;
        }
        for(UserGroupConversationModel oldConversation : oldConversations){

            if(oldConversation != null &&
                    oldConversation.getOtherUserId() != null &&
                    oldConversation.getOtherUserId().equals(otherUserId)){
                return true;
            }
        }
        return false;
    }

    private RequestManager initGlide() {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.bs_profile)
                .error(R.drawable.bs_profile);

        return Glide.with(this)
                .setDefaultRequestOptions(options);

    }





    @Override
    public void onConvImageClick(UserGroupConversationModel conversationModel, View transitionView) {

    }

    @Override
    public void onNewConvClick(GroupUserModel groupUserModel) {
        UserGroupConversationModel userGroupConversationModel = new UserGroupConversationModel();
        userGroupConversationModel.setGroupId(userGroupModel.getGroupId());
        userGroupConversationModel.setUserId(AppConfigHelper.getUserId());
        userGroupConversationModel.setOtherUserId(groupUserModel.getUserId());
        userGroupConversationModel.setOtherUserName(groupUserModel.getAlias());
        userGroupConversationModel.setOtherUserRole(groupUserModel.getRole());
        userGroupConversationModel.setLastAccessed((new Date()).getTime());

        Bundle bundle = new Bundle();
        bundle.putSerializable("conversation", userGroupConversationModel);
        bundle.putSerializable("group", userGroupModel);
        if(navController.getCurrentDestination().getId() == R.id.addAdminUserConversationFragment) {
            navController.navigate(R.id.action_addAdminUserConversationFragment_to_groupAdminUserMessagesFragment, bundle);
        }
    }
}
