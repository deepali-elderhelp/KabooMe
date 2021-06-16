package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.GroupRequestRepository;
/**
 * This class is needed when the notification for a request cancelled is received.
 * This request is already cancelled in the server, it only needs to be removed from the cache.
 */

public class DeleteLocalRequestUseCase extends BaseSingleUseCase<Void, DeleteLocalRequestUseCase.Params> {

    private static final String TAG = "KMDelLocalReqUseCase";
    private GroupRequestRepository groupRequestRepository;

    public DeleteLocalRequestUseCase(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.groupRequestRepository.deleteRequestOnlyLocal(params.userId, params.groupId);
        return null;
    }

    public static final class Params {

        private final String userId;

        private final String groupId;

        private Params(String userId, String groupId) {
            this.userId = userId; this.groupId = groupId;
        }

        public static Params deleteRequest(String userId, String groupId) {
            return new Params(userId, groupId);
        }
    }
}
