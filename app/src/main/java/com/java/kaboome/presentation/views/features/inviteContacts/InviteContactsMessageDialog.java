package com.java.kaboome.presentation.views.features.inviteContacts;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.java.kaboome.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteContactsMessageDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "KMInviteContactsMesDlg";


    View rootView;
    private Context mContext;
    private TextInputEditText messageToInvitees;
    private Button skipButton;
    private Button sendButton;


    public InviteContactsMessageDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_invite_contacts_message_dialog, container, false);
        messageToInvitees = rootView.findViewById(R.id.invite_message_text);
        messageToInvitees.addTextChangedListener(new MessageTextWatcher());
        skipButton = rootView.findViewById(R.id.skipInviteMessageButton);
        skipButton.setOnClickListener(this);
        sendButton = rootView.findViewById(R.id.sendInviteMessageButton);
        sendButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {

        mContext = getContext();
        super.onResume();
    }




    public void onClick(View v) {

        switch (v.getId()){

            case R.id.skipInviteMessageButton: {
                Log.d(TAG, "onClick: skip is getting clicked");
                NavController navController = NavHostFragment.findNavController(this);
                //nothing written but calling this so that the next step gets called in the activity
                navController.getPreviousBackStackEntry().getSavedStateHandle().set("message", " ");
                navController.popBackStack();
                break;
            }
            case R.id.sendInviteMessageButton: {
                Log.d(TAG, "onClick: send is getting clicked");
                NavController navController = NavHostFragment.findNavController(this);
                if(messageToInvitees.getText() != null && !messageToInvitees.getText().toString().isEmpty()){
                    //nothing written but calling this so that the next step gets called in the activity
                    navController.getPreviousBackStackEntry().getSavedStateHandle().set("message", messageToInvitees.getText().toString());
                }
                else{
                    navController.getPreviousBackStackEntry().getSavedStateHandle().set("message", " ");
                }
                navController.popBackStack();

                break;
            }

        }

    }

    private class MessageTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String message = messageToInvitees.getText().toString().trim();
            sendButton.setEnabled(!message.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
