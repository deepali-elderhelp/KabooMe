package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.GroupRepository;

/**
 * This class is needed when the notification for a group deleted is received.
 * This group is already deleted in the server, it only needs to be set deleted true in the cache.
 */

public class DeleteGroupUserFromCacheUseCase extends BaseSingleUseCase<Void, DeleteGroupUserFromCacheUseCase.Params> {

    private static final String TAG = "KMDelLocalGrpUsrUseCase";
    private GroupRepository groupRepository;

    public DeleteGroupUserFromCacheUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.groupRepository.deleteGroupUserFromCache(params.groupId, params.groupUserId);
        return null;
    }

    public static final class Params {

        private final String groupId;
        private final String groupUserId;


        private Params(String groupId, String groupUserId) {
            this.groupId = groupId;
            this.groupUserId = groupUserId;
        }

        public static Params deleteGroup(String groupId, String groupUserId) {
            return new Params(groupId, groupUserId);
        }
    }
}
