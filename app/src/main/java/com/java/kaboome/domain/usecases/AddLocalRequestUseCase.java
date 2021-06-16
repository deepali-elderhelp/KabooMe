package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.entities.DomainGroupRequest;
import com.java.kaboome.domain.repositories.GroupRequestRepository;
import com.java.kaboome.presentation.entities.GroupRequestModel;

/**
 * This class is needed when the notification for a new request is received.
 * This request is already in the server, it only needs to be added to the cache.
 */
public class AddLocalRequestUseCase extends BaseSingleUseCase<Void, AddLocalRequestUseCase.Params> {

    private static final String TAG = "KMDelLocalReqUseCase";
    private GroupRequestRepository groupRequestRepository;

    public AddLocalRequestUseCase(GroupRequestRepository groupRequestRepository) {
        this.groupRequestRepository = groupRequestRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
        this.groupRequestRepository.addRequestOnlyLocal(params.domainGroupRequest);
        return null;
    }

    public static final class Params {

        private final DomainGroupRequest domainGroupRequest;


        private Params(DomainGroupRequest domainGroupRequest) {
            this.domainGroupRequest = domainGroupRequest;
        }

        public static Params addRequest(DomainGroupRequest domainGroupRequest) {
            return new Params(domainGroupRequest);
        }
    }
}
