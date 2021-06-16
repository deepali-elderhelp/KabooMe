package com.java.kaboome.presentation.views.features.groupMessages.adapter;

import com.java.kaboome.data.entities.Message;

import java.io.File;

public interface DownloadClickListener {
    void onDownloadClicked(Message message);

    void onUploadClicked(Message message, File fileToUpload);
}
