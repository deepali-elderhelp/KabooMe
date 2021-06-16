package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.ImageUploadRepository;

import java.io.File;

/**
 * This class is needed when the notification for a new request is received.
 * This request is already in the server, it only needs to be added to the cache.
 */
public class UploadImageSingleUseCase extends BaseSingleUseCase<Void, UploadImageSingleUseCase.Params> {

    private static final String TAG = "KMUploadImageSUC";
    private ImageUploadRepository imageUploadRepository;

    public UploadImageSingleUseCase(ImageUploadRepository imageUploadRepository) {
        this.imageUploadRepository = imageUploadRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.imageUploadRepository.startBackgroundUploadFile(params.key, params.fileToUpload, params.downloadImage);
        return null;
    }

    public static final class Params {

        private final String key;
        private final File fileToUpload;
        private final boolean downloadImage;


        private Params(String key, File fileToUpload, boolean downloadImage) {
            this.key = key;
            this.fileToUpload = fileToUpload;
            this.downloadImage = downloadImage;
        }

        public static Params imageToUpload(String key, File fileToUpload, boolean downloadImage) {
            return new Params(key, fileToUpload, downloadImage);
        }
    }
}
