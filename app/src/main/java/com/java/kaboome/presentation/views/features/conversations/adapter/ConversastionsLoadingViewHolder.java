package com.java.kaboome.presentation.views.features.conversations.adapter;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConversastionsLoadingViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "KMLoadingViewHolder";

    public ConversastionsLoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        Log.d(TAG, "LoadingViewHolder: ");

    }
}
