//package com.java.kaboome.domain.usecases;
//
//import com.java.kaboome.domain.entities.DomainMessage;
//import com.java.kaboome.domain.repositories.GroupsUnreadCountRepository;
//
//public class AddNewUnreadMessageUseCase extends BaseSingleUseCase<Void, AddNewUnreadMessageUseCase.Params> {
//
//    private GroupsUnreadCountRepository groupsUnreadCountRepository;
//
//    public AddNewUnreadMessageUseCase(GroupsUnreadCountRepository groupsUnreadCountRepository) {
//        this.groupsUnreadCountRepository = groupsUnreadCountRepository;
//    }
//
//
//    @Override
//    protected Void executeUseCase(Params params) {
//        this.groupsUnreadCountRepository.addNewUnreadMessage(params.domainMessage);
//        return null;
//    }
//
//    public static final class Params {
//
//        private final DomainMessage domainMessage;
//
//
//        private Params(DomainMessage domainMessage) {
//            this.domainMessage = domainMessage;
//        }
//
//        public static AddNewUnreadMessageUseCase.Params setMessage(DomainMessage domainMessage) {
//            return new AddNewUnreadMessageUseCase.Params(domainMessage);
//        }
//    }
//
//}
