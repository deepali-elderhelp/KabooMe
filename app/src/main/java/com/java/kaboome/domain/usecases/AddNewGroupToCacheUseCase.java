package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainUserGroup;
import com.java.kaboome.domain.repositories.InvitationsListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;

/**
 * This class is needed to add the newly created group to the UserGroup DAO
 * so that is is visible when the user goes to the GroupListFragment
 */
public class AddNewGroupToCacheUseCase extends BaseSingleUseCase<Void, AddNewGroupToCacheUseCase.Params> {

    private static final String TAG = "KMAddNewMsgUseCase";
    private UserGroupsListRepository userGroupsListRepository;

    public AddNewGroupToCacheUseCase(UserGroupsListRepository userGroupsListRepository) {
        this.userGroupsListRepository = userGroupsListRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.userGroupsListRepository.addNewGroupToCache(params.domainUserGroup);
        return null;
    }

    public static final class Params {

        private final DomainUserGroup domainUserGroup;

        public Params(DomainUserGroup domainUserGroup) {
            this.domainUserGroup = domainUserGroup;
        }

        public static Params newGroup(DomainUserGroup domainUserGroup) {
            return new Params(domainUserGroup);
        }
    }
}
