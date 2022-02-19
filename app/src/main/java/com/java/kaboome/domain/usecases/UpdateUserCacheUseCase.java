package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.UserGroupRepository;
import com.java.kaboome.domain.repositories.UserRepository;

public class UpdateUserCacheUseCase extends BaseSingleUseCase<Void, UpdateUserCacheUseCase.Params> {

    private static final String TAG = "KMUpdUsrCacheUseCase";
    private UserRepository userRepository;


    public UpdateUserCacheUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    @Override
    protected Void executeUseCase(Params params) {
        userRepository.updateUserInCache(params.user, params.action);
        return null;
    }

    public static final class Params {
        private final DomainUser user;
        private final String action;

        private Params(DomainUser user, String action) {
            this.user = user;
            this.action = action;
        }

        public static Params forUser(DomainUser user, String action){
            return new Params(user, action);
        }
    }
}
