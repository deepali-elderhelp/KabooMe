package com.java.kaboome.presentation.views.features.conversations.adapter;

import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.kaboome.R;
import com.java.kaboome.helpers.AppConfigHelper;

public class NoConversationsViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "KMNoConvsViewHolder";

    public NoConversationsViewHolder(@NonNull View itemView) {
        
        super(itemView);
    }

    public void onBind(){

    }
}
