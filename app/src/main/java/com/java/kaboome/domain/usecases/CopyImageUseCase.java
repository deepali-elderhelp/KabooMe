package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;

import java.io.File;


public class CopyImageUseCase extends BaseUseCase<DomainUpdateResource<String>, CopyImageUseCase.Params> {

    private static final String TAG = "KMCopyImageUseCase";
    private ImageUploadRepository imageUploadRepository;

    public CopyImageUseCase(ImageUploadRepository imageUploadRepository) {
        this.imageUploadRepository = imageUploadRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.imageUploadRepository.copyImage(params.keyToBeCopied, params.newKey, params.action);
    }

    public static final class Params {


        private final String keyToBeCopied;
        private final String newKey;
        private final String action;

        public Params(String keyToBeCopied, String newKey, String action) {
            this.keyToBeCopied = keyToBeCopied;
            this.newKey = newKey;
            this.action = action;
        }

        public static Params imageToBeCopied(String keyToBeCopied, String newKey, String action) {
            return new Params(keyToBeCopied, newKey, action);
        }
    }
}
