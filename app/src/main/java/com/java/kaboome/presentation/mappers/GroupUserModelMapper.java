package com.java.kaboome.presentation.mappers;

import com.java.kaboome.domain.entities.DomainContact;
import com.java.kaboome.domain.entities.DomainGroup;
import com.java.kaboome.domain.entities.DomainGroupUser;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.presentation.entities.ContactModel;
import com.java.kaboome.presentation.entities.GroupModel;
import com.java.kaboome.presentation.entities.GroupUserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupUserModelMapper {

    public static DomainGroupUser getDomainFromGroupUserModel(GroupUserModel groupUserModel){

        DomainGroupUser domainGroupUser = new DomainGroupUser();

        domainGroupUser.setUserId(groupUserModel.getUserId());
        domainGroupUser.setGroupId(groupUserModel.getGroupId());
        domainGroupUser.setNotify(groupUserModel.getNotify());
        domainGroupUser.setDeviceId(groupUserModel.getDeviceId());
        domainGroupUser.setUserName(groupUserModel.getAlias());
        domainGroupUser.setRole(groupUserModel.getRole());
        domainGroupUser.setIsAdmin(groupUserModel.getIsAdmin());
        domainGroupUser.setCheckedToBeAdmin(groupUserModel.getCheckedToBeAdmin());
        domainGroupUser.setImageUpdateTimestamp(groupUserModel.getImageUpdateTimestamp());
        domainGroupUser.setGroupUserPicLoadingGoingOn(groupUserModel.getGroupUserPicLoadingGoingOn());
        domainGroupUser.setGroupUserPicUploaded(groupUserModel.getGroupUserPicUploaded());


        return domainGroupUser;
    }

    public static GroupUserModel getGroupUserModelFromDomain(DomainGroupUser domainGroupUser){

        GroupUserModel groupUserModel = new GroupUserModel();

        groupUserModel.setUserId(domainGroupUser.getUserId());
        groupUserModel.setGroupId(domainGroupUser.getGroupId());
        groupUserModel.setNotify(domainGroupUser.getNotify());
        groupUserModel.setDeviceId(domainGroupUser.getDeviceId());
        groupUserModel.setAlias(domainGroupUser.getUserName());
        groupUserModel.setRole(domainGroupUser.getRole());
        groupUserModel.setIsAdmin(domainGroupUser.getIsAdmin());
        groupUserModel.setCheckedToBeAdmin(domainGroupUser.getCheckedToBeAdmin());
        groupUserModel.setImageUpdateTimestamp(domainGroupUser.getImageUpdateTimestamp());
        groupUserModel.setGroupUserPicLoadingGoingOn(domainGroupUser.getGroupUserPicLoadingGoingOn());
        groupUserModel.setGroupUserPicUploaded(domainGroupUser.getGroupUserPicUploaded());

        return groupUserModel;
    }

    public static List<GroupUserModel> transformAllFromDomainToModel(List<DomainGroupUser> domainGroupUsers) {
        List<GroupUserModel> groupUserModels;

        if (domainGroupUsers != null && !domainGroupUsers.isEmpty()) {
            groupUserModels = new ArrayList<>();
            for (DomainGroupUser domainGroupUser : domainGroupUsers) {
                groupUserModels.add(getGroupUserModelFromDomain(domainGroupUser));
            }
        } else {
            groupUserModels = Collections.emptyList();
        }

        return groupUserModels;
    }




}
