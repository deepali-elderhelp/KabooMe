package com.java.kaboome.presentation.views.features.groupList.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;

public class LoadingViewHolder extends RecyclerView.ViewHolder{

    private static final String TAG = "KMLoadingViewHolder";

    public LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        Log.d(TAG, "LoadingViewHolder: ");

    }
}
