package com.java.kaboome.presentation.views.features.groupInfo;


import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;
import com.java.kaboome.constants.UserGroupStatusConstants;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.helpers.DateFormatter;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditGroupExpiryFragment extends DialogFragment {

    View view;
    GroupModel groupModel;
//    GroupViewModel groupViewModel;

    Button saveButton;
    TextInputEditText newGroupExpiry;
    TextView resetLink;
    Calendar selectedDate;
    ImageView closeButton;

    public EditGroupExpiryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupModel = (GroupModel) getArguments().getSerializable("group");

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


    //editGroupExpiry

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_group_expiry, container, false);
        resetLink = view.findViewById(R.id.resetExpiryToManual);
        newGroupExpiry = view.findViewById(R.id.editGroupExpiry);
        newGroupExpiry.setText("Manual");
        if(groupModel.getExpiryDate() !=null && groupModel.getExpiryDate() != 0){
            newGroupExpiry.setText(DateFormatter.getDateFormattedPretty(groupModel.getExpiryDate()));
        }


        newGroupExpiry.addTextChangedListener(new ExpiryTextWatcher());
        newGroupExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                selectedDate = Calendar.getInstance();
                                selectedDate.set(year, monthOfYear, dayOfMonth);
                                newGroupExpiry.setText(DateFormatter.getDateFormattedPretty(selectedDate.getTimeInMillis()));


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        resetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupExpiry.setText("Manual");
                selectedDate = null;
            }
        });

        saveButton = view.findViewById(R.id.save_group_expiry);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate
                GroupModel groupModelTemp = new GroupModel();
                groupModelTemp.setGroupId(groupModel.getGroupId());
                if(selectedDate != null){
                    groupModelTemp.setExpiryDate(selectedDate.getTimeInMillis());
                }
                else{
                    groupModelTemp.setExpiryDate(0L);
                }


//                groupViewModel.updateGroup(groupModelTemp, "updateGroupExpiry");
//                groupViewModel.updateGroup(groupModelTemp, GroupActionConstants.UPDATE_GROUP_EXPIRY.getAction());
                NavController navController = NavHostFragment.findNavController(EditGroupExpiryFragment.this);

                navController.getPreviousBackStackEntry().getSavedStateHandle().set("groupExpiry", groupModelTemp);
                navController.popBackStack();
                dismiss();

            }
        });

        closeButton = view.findViewById(R.id.edit_group_expiry_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if(!groupModel.getCurrentUserGroupStatus().equals(UserGroupStatusConstants.ADMIN_MEMBER)) {
            saveButton.setVisibility(View.GONE);
            resetLink.setVisibility(View.GONE);
            newGroupExpiry.setEnabled(false);
        }

        return view;
    }

    private class ExpiryTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String groupExpiry = s.toString().trim();
            saveButton.setEnabled(!groupExpiry.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }


//    public void setGroupModel(GroupModel groupModel) {
//        this.groupModel = groupModel;
//    }


}
