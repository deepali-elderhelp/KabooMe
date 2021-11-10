package com.java.kaboome.presentation.views.features.groupInfo;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.java.kaboome.R;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserImageClickListener;
import com.java.kaboome.presentation.views.features.groupInfo.adapter.GroupUserListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditGroupAddAdminFragment extends DialogFragment implements GroupUserImageClickListener {

    private static final String TAG = "KMEditGroupAddAdminFrag";

    View view;
    GroupModel groupModel;
//    GroupViewModel groupViewModel;

    RecyclerView addAdminRecyclerView;


    private GroupUserListViewAdapter addAdminAdapter;
    private NavController navController;


    public EditGroupAddAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupModel = (GroupModel) getArguments().getSerializable("group");
        navController = NavHostFragment.findNavController(EditGroupAddAdminFragment.this);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_group_add_admin, container, false);

        addAdminRecyclerView = view.findViewById(R.id.edit_group_add_admin_recycler);
        TextView noRegularUsers = view.findViewById(R.id.edit_group_add_admin_no_users);
        Button saveButton = view.findViewById(R.id.edit_group_add_admin_save_button);

        if(groupModel.getRegularMembers() == null || groupModel.getRegularMembers().size() <= 0){
            addAdminRecyclerView.setVisibility(View.GONE);
            noRegularUsers.setVisibility(View.VISIBLE);
            saveButton.setText("Okay");
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        else{
            addAdminRecyclerView.setVisibility(View.VISIBLE);
            noRegularUsers.setVisibility(View.GONE);
            saveButton.setText("SAVE");
            initRecyclerView();
            addAdminAdapter.setGroupUsers(groupModel.getRegularMembers());
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<GroupUserModel> newMembers = getNewAdminMembers(groupModel.getRegularMembers());

                    if(newMembers == null || newMembers.size() <= 0){
                        Toast.makeText(getContext(), "No members selected", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupNewAdminMembers", newMembers);
                        navController.popBackStack();
                        dismiss();
                    }


                }
            });
        }

        AppCompatImageView closeButton = view.findViewById(R.id.edit_group_add_admin_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

//    public void setGroupModel(GroupModel groupModel) {
//        this.groupModel = groupModel;
//    }

    private void initRecyclerView(){
//        addAdminAdapter = new GroupUserListViewAdapter(initGlide(), GroupUserListViewAdapter.UserRecyclerType.ADD_ADMINS_TYPE, null, null, this, null);
        addAdminAdapter = new GroupUserListViewAdapter(GroupUserListViewAdapter.UserRecyclerType.ADD_ADMINS_TYPE, null, null, this, null);
        addAdminRecyclerView.setAdapter(addAdminAdapter);
        addAdminRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private RequestManager initGlide() {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.bs_profile)
                .error(R.drawable.bs_profile);

        return Glide.with(this)
                .setDefaultRequestOptions(options);

    }

    private List<GroupUserModel> getNewAdminMembers(List<GroupUserModel> regularMembers) {

        Log.d(TAG, "addMembersAsAdmins: "+regularMembers);
        List<GroupUserModel> newAdminMembers = new ArrayList<>();
        for(GroupUserModel groupUserModel: regularMembers){
            if(groupUserModel.getCheckedToBeAdmin()){
//                groupViewModel.updateGroupUser(groupUserModel, "updateGroupUserIsAdmin");
//                groupViewModel.updateGroupUser(groupUserModel, GroupActionConstants.UPDATE_GROUP_USERS_TO_ADMIN.getAction());
                newAdminMembers.add(groupUserModel);
            }

        }
        return newAdminMembers;

    }


    @Override
    public void onGroupUserImageClick(GroupUserModel groupUserModel) {
        //show user image big
        Bundle bundle = new Bundle();
        bundle.putSerializable("groupUser", groupUserModel);

        navController.navigate(R.id.action_editGroupAddAdminFragment_to_groupUserPicDisplayFragment, bundle);
    }
}
