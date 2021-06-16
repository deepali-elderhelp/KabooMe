package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;

import java.io.File;
import java.util.HashMap;


public class UploadImageUseCase extends BaseUseCase<DomainUpdateResource<String>, UploadImageUseCase.Params> {

    private static final String TAG = "KMUpdateGroupUseCase";
    private ImageUploadRepository imageUploadRepository;

    public UploadImageUseCase(ImageUploadRepository imageUploadRepository) {
        this.imageUploadRepository = imageUploadRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.imageUploadRepository.uploadImage(params.key, params.file, params.action, params.userData);
    }

    public static final class Params {


        private final File file;
        private final String key;
        private final String action;
        private final HashMap<String,Object> userData;

        public Params(File file, String key, String action, HashMap<String,Object> userData) {
            this.file = file;
            this.key = key;
            this.action = action;
            this.userData = userData;
        }

        public static Params imageUpload(File file, String key, String action, HashMap<String,Object> userData) {
            return new Params(file,key, action, userData);
        }
    }
}
