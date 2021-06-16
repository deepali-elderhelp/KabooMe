//package com.java.kaboome.domain.usecases;
//
//import com.java.kaboome.domain.repositories.GroupsUnreadCountRepository;
//
//public class ResetGroupsUnreadCountUseCase extends BaseSingleUseCase<Void, ResetGroupsUnreadCountUseCase.Params> {
//
//    private GroupsUnreadCountRepository groupsUnreadCountRepository;
//
//    public ResetGroupsUnreadCountUseCase(GroupsUnreadCountRepository groupsUnreadCountRepository) {
//        this.groupsUnreadCountRepository = groupsUnreadCountRepository;
//    }
//
//
//    @Override
//    protected Void executeUseCase(Params params) {
//        this.groupsUnreadCountRepository.resetGroupsUnreadCountList(params.groupId);
//        return null;
//    }
//
//    public static final class Params {
//
//        private final String groupId;
//
//        private Params(String groupId) {
//            this.groupId = groupId;
//        }
//
//        public static ResetGroupsUnreadCountUseCase.Params setGroup(String groupId) {
//            return new ResetGroupsUnreadCountUseCase.Params(groupId);
//        }
//    }
//
//}
