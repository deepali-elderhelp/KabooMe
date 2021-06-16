package com.java.kaboome.presentation.views.features.profile;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;
import com.java.kaboome.presentation.entities.UserModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserNameFragment extends DialogFragment {

    private View view;
    private UserModel userModel;
//    private ProfileViewModel profileViewModel;

    private Button saveButton;
    private TextInputEditText newUserName;
    private ImageView closeButton;


    public EditUserNameFragment() {
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel = (UserModel) getArguments().getSerializable("user");
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        profileViewModel = ViewModelProviders.of(getActivity()).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_user_name, container, false);
        newUserName = view.findViewById(R.id.editUserName);
        newUserName.setText(userModel.getUserName());

        newUserName.addTextChangedListener(new UserNameTextWatcher());


        saveButton = view.findViewById(R.id.save_user_name);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validate
                UserModel userModelTemp = new UserModel();
                userModelTemp.setUserId(userModel.getUserId());
                userModelTemp.setUserName(newUserName.getText().toString());
//                profileViewModel.updateUser(userModelTemp, UserActionConstants.UPDATE_USER_NAME.getAction());

                NavController navController = NavHostFragment.findNavController(EditUserNameFragment.this);

                navController.getPreviousBackStackEntry().getSavedStateHandle().set("userName", userModelTemp);
                navController.popBackStack();
                dismiss();
            }
        });

        closeButton = view.findViewById(R.id.edit_user_pic_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private class UserNameTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = s.toString().trim();
            saveButton.setEnabled(!usernameInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }



//    public void setUserModel(UserModel userModel) {
//        this.userModel = userModel;
//
//    }

}
