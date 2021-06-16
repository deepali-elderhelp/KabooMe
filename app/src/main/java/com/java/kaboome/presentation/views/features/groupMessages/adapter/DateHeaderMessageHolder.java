package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.data.entities.Message;
import com.java.kaboome.helpers.DateHelper;


public class DateHeaderMessageHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "KMDateHeaderMsgHolder";

    TextView dateHeader, newMessageHeader;
//    private boolean showNewMessageHeader;

    DateHeaderMessageHolder(View itemView) {
        super(itemView);

        dateHeader =  itemView.findViewById(R.id.messageDateText);
        newMessageHeader = itemView.findViewById(R.id.newMessagesLabel);
//        this.showNewMessageHeader = showNewMessageHeader;

    }

    public void onBind(Message message, boolean showNewMessageHeader){

        // Format the stored timestamp into a readable String using method.
        if(message.getSentAt() != null){
            Log.d(TAG, "Date header for - "+ DateHelper.getPrettyDate(message.getSentAt()));
            dateHeader.setText(DateHelper.dateForChatMessages(message.getSentAt()));
        }
        if(showNewMessageHeader){
            newMessageHeader.setVisibility(View.VISIBLE);
        }
        else{
            newMessageHeader.setVisibility(View.GONE);
        }

    }

}
