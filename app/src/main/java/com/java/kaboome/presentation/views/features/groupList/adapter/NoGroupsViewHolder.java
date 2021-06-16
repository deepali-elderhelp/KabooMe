package com.java.kaboome.presentation.views.features.groupList.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.java.kaboome.R;
import com.java.kaboome.helpers.AppConfigHelper;

public class NoGroupsViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "KMNoGroupsViewHolder";

    private TextView addProfilePicTextView;

    public NoGroupsViewHolder(@NonNull View itemView) {
        
        super(itemView);
        addProfilePicTextView = itemView.findViewById(R.id.no_groups_create_profile);
    }

    public void onBind(){

        if(!AppConfigHelper.profilePicSelected()){
            SpannableString string = new SpannableString("If you haven't yet, create a profile pic, \n Click on   -> Profile");
//          string.setSpan(new ImageSpan(itemView.getContext(), R.drawable.nav_drawer_grey_icon), 52, 53, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            string.setSpan(new ImageSpan(itemView.getContext(), R.drawable.nav_drawer_grey_icon), 53, 54, DynamicDrawableSpan.ALIGN_BASELINE);

            addProfilePicTextView.setText(string);
        }

    }
}
