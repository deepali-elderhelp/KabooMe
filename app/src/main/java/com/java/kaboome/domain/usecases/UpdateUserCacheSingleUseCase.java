package com.java.kaboome.domain.usecases;

import com.java.kaboome.domain.entities.DomainUser;
import com.java.kaboome.domain.repositories.UserRepository;

public class UpdateUserCacheSingleUseCase extends BaseSingleUseCase<Void, UpdateUserCacheSingleUseCase.Params> {

    private static final String TAG = "KMUpdUserCacheSUC";
    private UserRepository userRepository;

    public UpdateUserCacheSingleUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.userRepository.updateUserInCache(params.domainUser);
        return null;
    }

    public static final class Params {


        private final DomainUser domainUser;

        private Params(DomainUser domainUser) {
            this.domainUser = domainUser;
        }

        public static Params forUser(DomainUser domainUser) {
            return new Params(domainUser);
        }
    }
}
