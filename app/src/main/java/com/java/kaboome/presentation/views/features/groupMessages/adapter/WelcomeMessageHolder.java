package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.data.entities.Message;


public class WelcomeMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMWelcomeMessageHolder";

    private ConstraintLayout parentLayout;
    private TextView groupWelcomingMessage;
    private TextView publicPrivateTextView;
    private AppCompatButton inviteMembersButton;
    private ImageView closeButton;
    private boolean createdNotJoined = false;



    WelcomeMessageHolder(View itemView, boolean createdNotJoined) {
        super(itemView);
        this.createdNotJoined  =createdNotJoined;

        parentLayout = itemView.findViewById(R.id.welcome_parent_layout);
        groupWelcomingMessage =  itemView.findViewById(R.id.text_welcome_message_1a);
        publicPrivateTextView =  itemView.findViewById(R.id.text_welcome_message_1c);
        inviteMembersButton = itemView.findViewById(R.id.welcome_invite_button);
        closeButton = itemView.findViewById(R.id.welcome_close_button);

    }

    public void onBind(final Message message, boolean isPrivateGroup, final WelcomeMessageClickListener welcomeMessageClickListener) {
//        Log.d(TAG, "onBind: setting message  - "+message.getMessageText());
//        groupNameTextView.setText("Test Group Name");
        if(message.getDeleted() != null && !message.getDeleted()) {

            parentLayout.setVisibility(View.VISIBLE);
            if (createdNotJoined) {
                groupWelcomingMessage.setText(R.string.new_group_welcome_message_1a);
            } else {
                groupWelcomingMessage.setText(R.string.new_group_welcome_message_1aa);
            }
            if (isPrivateGroup) {
                publicPrivateTextView.setText(R.string.new_group_welcome_message_1c);
            } else {
                publicPrivateTextView.setText(R.string.new_group_welcome_message_1b);
            }

            inviteMembersButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    welcomeMessageClickListener.onInviteMembersClicked();
                }
            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    welcomeMessageClickListener.onCloseWelcomeMessageClicked(message);
                }
            });
        }
        else{
            parentLayout.setVisibility(View.GONE);
        }
    }


}
