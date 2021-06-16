package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.repositories.ImageUploadRepository;
import com.java.kaboome.domain.repositories.InvitationsListRepository;

import java.util.HashMap;
import java.util.List;

public class GetUploadObserveUseCase extends BaseUseCase<DomainResource<HashMap<String, Object>>, Void> {

    private static final String TAG = "KMUpldObsUseCase";

    private ImageUploadRepository imageUploadRepository;

    public GetUploadObserveUseCase(ImageUploadRepository imageUploadRepository) {
        this.imageUploadRepository = imageUploadRepository;
    }

    @Override
    protected LiveData<DomainResource<HashMap<String, Object>>> executeUseCase(Void o) {
        Log.d(TAG, "executeUseCase: getting invitations");
        return imageUploadRepository.observeUpload();
    }

}
