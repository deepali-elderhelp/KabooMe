//package com.java.kaboome.domain.usecases;
//
//import androidx.lifecycle.LiveData;
//
//import com.java.kaboome.domain.entities.DomainGroupUnreadData;
//import com.java.kaboome.domain.repositories.GroupsUnreadCountRepository;
//
//import java.util.List;
//
//public class GetGroupUnreadCountUseCase extends BaseUseCase<DomainGroupUnreadData, GetGroupUnreadCountUseCase.Params> {
//
//    private GroupsUnreadCountRepository groupsUnreadCountRepository;
//
//    public GetGroupUnreadCountUseCase(GroupsUnreadCountRepository groupsUnreadCountRepository) {
//        this.groupsUnreadCountRepository = groupsUnreadCountRepository;
//    }
//
//
//    @Override
//    protected LiveData<DomainGroupUnreadData> executeUseCase(Params params) {
//        return groupsUnreadCountRepository.getGroupUnreadCount(params.groupId);
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
//        public static GetGroupUnreadCountUseCase.Params getUnreadCountForGroup(String groupId) {
//            return new GetGroupUnreadCountUseCase.Params(groupId);
//        }
//    }
//}
