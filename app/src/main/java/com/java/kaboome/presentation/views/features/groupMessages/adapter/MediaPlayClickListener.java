package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import android.net.Uri;

import com.java.kaboome.data.entities.Message;

import java.io.File;

public interface MediaPlayClickListener {
    void onMediaPlayClicked(Message message, Uri uri);

}
