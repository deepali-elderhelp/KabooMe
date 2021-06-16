package com.java.kaboome.domain.repositories;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUpdateResource;

import java.io.File;
import java.util.HashMap;

public interface ImageUploadRepository {

    LiveData<DomainUpdateResource<String>> uploadImage(String key, File fileTpUpload, String action, HashMap<String,Object> userData);

    void startBackgroundUploadFile(String key, File fileToUpload,boolean downloadImage);

//    LiveData<DomainUpdateResource<Long>> uploadImage(String key, File fileTpUpload);

    LiveData<DomainUpdateResource<String>> downloadFile(String key, File downloadFile, String action, HashMap<String,Object> userData);

    LiveData<DomainUpdateResource<String>> copyImage(String toBeCopiedKey, String newKey, String action);

    void startBackgroundCopyImage(String toBeCopiedKey, String newKey);

    LiveData<DomainResource<HashMap<String,Object>>> observeUpload();

    LiveData<DomainResource<HashMap<String,Object>>> observeDownload();

}
