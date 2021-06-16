package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.ImageUploadRepository;

import java.io.File;

/**
 * This class is needed when we need to copy the image from S3 bucket key to same bucket another key
 * Needed when user uses the same profile pic for a group
 * This runs in the background, does not notify when done
 */
public class CopyImageSingleUseCase extends BaseSingleUseCase<Void, CopyImageSingleUseCase.Params> {

    private static final String TAG = "KMUploadImageSUC";
    private ImageUploadRepository imageUploadRepository;

    public CopyImageSingleUseCase(ImageUploadRepository imageUploadRepository) {
        this.imageUploadRepository = imageUploadRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.imageUploadRepository.startBackgroundCopyImage(params.key, params.newKey);
        return null;
    }

    public static final class Params {

        private final String key;
        private final String newKey;

        private Params(String key, String newKey) {
            this.key = key;
            this.newKey = newKey;
        }

        public static Params imageToCopy(String key, String newKey) {
            return new Params(key, newKey);
        }
    }
}
