package com.java.kaboome.presentation.views.features.groupInfo;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.presentation.entities.GroupModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditGroupDescription extends DialogFragment{

    private static final String TAG = "KMEditGroupDescription";

    View view;
    GroupModel groupModel;
//    GroupViewModel groupViewModel;

    Button saveButton;
    TextInputEditText newGroupDescription;
    ImageView closeButton;


    public EditGroupDescription() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupModel = (GroupModel) getArguments().getSerializable("group");
//        groupViewModel = ViewModelProviders.of(this, new CustomViewModelProvider(groupModel.getGroupId())).get(GroupViewModel.class);

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
        view = inflater.inflate(R.layout.fragment_edit_group_description, container, false);
        newGroupDescription = view.findViewById(R.id.editGroupDesc);
        saveButton = view.findViewById(R.id.save_group_desc);
        closeButton = view.findViewById(R.id.group_info_desc_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        newGroupDescription.setText(groupModel.getGroupDescription());
        newGroupDescription.addTextChangedListener(new DescriptionTextWatcher());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate
                GroupModel groupModelTemp = new GroupModel();
                groupModelTemp.setGroupId(groupModel.getGroupId());
                groupModelTemp.setGroupDescription(newGroupDescription.getText().toString());
                NavController navController = NavHostFragment.findNavController(EditGroupDescription.this);
                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupDescription", groupModelTemp);
                navController.popBackStack();
                dismiss();
            }
        });

//        if(!groupModel.getCurrentUserAdmin()) {
        if(!groupModel.getCurrentUserGroupStatus().equals(UserGroupStatusConstants.ADMIN_MEMBER)) {
            saveButton.setVisibility(View.GONE);
            newGroupDescription.setEnabled(false);
        }

        return view;
    }


    private class DescriptionTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String groupDescription = s.toString().trim();
            saveButton.setEnabled(!groupDescription.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
