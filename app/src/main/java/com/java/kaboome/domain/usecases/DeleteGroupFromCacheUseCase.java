package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.GroupRepository;

/**
 * This class is needed when the notification for a group deleted is received.
 * This group is already deleted in the server, it only needs to be set deleted true in the cache.
 */

public class DeleteGroupFromCacheUseCase extends BaseSingleUseCase<Void, DeleteGroupFromCacheUseCase.Params> {

    private static final String TAG = "KMDelLocalReqUseCase";
    private GroupRepository groupRepository;

    public DeleteGroupFromCacheUseCase(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.groupRepository.deleteGroupFromCache(params.groupId);
        return null;
    }

    public static final class Params {

        private final String groupId;

        private Params(String groupId) {
            this.groupId = groupId;
        }

        public static Params deleteGroup(String groupId) {
            return new Params(groupId);
        }
    }
}
