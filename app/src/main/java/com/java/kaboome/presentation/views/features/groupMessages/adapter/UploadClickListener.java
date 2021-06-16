package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import com.java.kaboome.data.entities.Message;

import java.io.File;

public interface UploadClickListener {
    void onUploadClicked(Message message, File file);
}
