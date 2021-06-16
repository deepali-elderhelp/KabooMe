package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;

import java.io.File;


public class UploadImageUseCase_old extends BaseUseCase<DomainUpdateResource<String>, UploadImageUseCase_old.Params> {

    private static final String TAG = "KMUpdateGroupUseCase";
    private ImageUploadRepository imageUploadRepository;

    public UploadImageUseCase_old(ImageUploadRepository imageUploadRepository) {
        this.imageUploadRepository = imageUploadRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.imageUploadRepository.uploadImage(params.key, params.file, params.action, null);
    }

    public static final class Params {


        private final File file;
        private final String key;
        private final String action;

        public Params(File file, String key, String action) {
            this.file = file;
            this.key = key;
            this.action = action;
        }

        public static Params imageUpload(File file, String key, String action) {
            return new Params(file,key, action);
        }
    }
}
