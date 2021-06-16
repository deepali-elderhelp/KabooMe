//package com.java.kaboome.domain.usecases;
//
//import androidx.lifecycle.LiveData;
//
//import com.java.kaboome.domain.entities.DomainGroupUnreadData;
//import com.java.kaboome.domain.repositories.GroupsUnreadCountRepository;
//
//import java.util.List;
//
//public class GetGroupsUnreadCountUseCase extends BaseUseCase<List<DomainGroupUnreadData>, Void> {
//
//    private GroupsUnreadCountRepository groupsUnreadCountRepository;
//
//    public GetGroupsUnreadCountUseCase(GroupsUnreadCountRepository groupsUnreadCountRepository) {
//        this.groupsUnreadCountRepository = groupsUnreadCountRepository;
//    }
//
//    @Override
//    protected LiveData<List<DomainGroupUnreadData>> executeUseCase(Void unused) {
//        return groupsUnreadCountRepository.getGroupsUnreadCountList();
//    }
//
//    public class Params {
//    }
//}
