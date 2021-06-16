package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;

import java.util.HashMap;

public class GetDownloadObserveUseCase extends BaseUseCase<DomainResource<HashMap<String, Object>>, Void> {

    private static final String TAG = "KMDwnObsUseCase";

    private ImageUploadRepository imageUploadRepository;

    public GetDownloadObserveUseCase(ImageUploadRepository imageUploadRepository) {
        this.imageUploadRepository = imageUploadRepository;
    }

    @Override
    protected LiveData<DomainResource<HashMap<String, Object>>> executeUseCase(Void o) {
        Log.d(TAG, "executeUseCase: getting invitations");
        return imageUploadRepository.observeDownload();
    }

}
