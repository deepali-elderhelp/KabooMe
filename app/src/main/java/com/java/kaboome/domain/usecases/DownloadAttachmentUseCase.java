package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;

import java.io.File;
import java.util.HashMap;


public class DownloadAttachmentUseCase extends BaseUseCase<DomainUpdateResource<String>, DownloadAttachmentUseCase.Params> {

    private static final String TAG = "KMDwnldAttachUseCase";
    private ImageUploadRepository imageUploadRepository;

    public DownloadAttachmentUseCase(ImageUploadRepository imageUploadRepository) {
        this.imageUploadRepository = imageUploadRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.imageUploadRepository.downloadFile(params.key, params.file, params.action, params.userData);
    }

    public static final class Params {


        private final File file;
        private final String key;
        private final String action;
        private final HashMap<String, Object> userData;

        public Params(File file, String key, String action, HashMap<String, Object> userData) {
            this.file = file;
            this.key = key;
            this.action = action;
            this.userData = userData;
        }

        public static Params downloadAttachment(File file, String key, String action, HashMap<String, Object> userData) {
            return new Params(file,key, action, userData);
        }
    }
}
