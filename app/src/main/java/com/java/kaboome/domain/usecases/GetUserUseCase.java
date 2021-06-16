package com.java.kaboome.domain.usecases;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.repositories.UserRepository;

public class GetUserUseCase extends BaseUseCase<DomainResource<DomainUser>, GetUserUseCase.Params> {

    private static final String TAG = "KMGetGroupUseCase";

    private UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected LiveData<DomainResource<DomainUser>> executeUseCase(GetUserUseCase.Params params) {
        Log.d(TAG, "executeUseCase: getting group");
        return userRepository.getUser(params.userId);
    }


    public static final class Params {

        private final String userId;


        private Params(String userId) {
            this.userId = userId;
        }

        public static Params forUser(String userId) {
            return new Params(userId);
        }
    }

}
