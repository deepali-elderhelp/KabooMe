package com.java.kaboome.domain.usecases;


import androidx.lifecycle.LiveData;

import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainUpdateResource;
import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.repositories.GroupRepository;
import com.java.kaboome.domain.repositories.UserRepository;


public class UpdateUserUseCase extends BaseUseCase<DomainUpdateResource<String>, UpdateUserUseCase.Params> {

    private static final String TAG = "KMUpdateUserUseCase";
    private UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    protected LiveData<DomainUpdateResource<String>> executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        return this.userRepository.updateUser(params.user, params.action);
    }

    public static final class Params {


        private final DomainUser user;

        private final String action;

        private Params(DomainUser user, String action) {
            this.user = user;
            this.action = action;
        }

        public static Params userUpdated(DomainUser user, String action) {
            return new Params(user, action);
        }
    }
}
