package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;


public class RemoveDeletedUserGroupsUseCase extends BaseSingleUseCase<Void, RemoveDeletedUserGroupsUseCase.Params> {

    private static final String TAG = "KMRmDelUGUseCase";
    private UserGroupsListRepository userGroupsListRepository;

    public RemoveDeletedUserGroupsUseCase(UserGroupsListRepository userGroupsListRepository) {
        this.userGroupsListRepository = userGroupsListRepository;
    }

    @Override
    protected Void executeUseCase(Params params) {
        this.userGroupsListRepository.removeDeletedUserGroupsFromCache(params.userId);
        return null;
    }

    public static final class Params {

        private final String userId;


        private Params(String userId) {
            this.userId = userId;
        }

        public static RemoveDeletedUserGroupsUseCase.Params forUser(String userId) {
            return new RemoveDeletedUserGroupsUseCase.Params(userId);
        }
    }

}
