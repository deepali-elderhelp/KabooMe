package com.java.kaboome.presentation.mappers;

import com.java.kaboome.constants.InvitationListStatusConstants;
import com.java.kaboome.constants.InvitationStatusConstants;
import com.java.kaboome.domain.entities.DomainInvitation;
import com.java.kaboome.domain.entities.DomainResource;
import com.java.kaboome.presentation.entities.InvitationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvitationModelMapper {
    private static final String TAG = "KMInvitationModelMapper";


    public static DomainInvitation transformFromInvitation(InvitationModel invitationModel) {
        if (invitationModel == null) {
            throw new IllegalArgumentException("Cannot transformFromInvitation a null value");
        }

        DomainInvitation domainInvitation = new DomainInvitation();

        domainInvitation.setGroupId(invitationModel.getGroupId());
        domainInvitation.setGroupName(invitationModel.getGroupName());
        domainInvitation.setDateInvited(invitationModel.getDateInvited());
        domainInvitation.setPrivateGroup(invitationModel.getPrivateGroup());
        domainInvitation.setInvitedBy(invitationModel.getInvitedBy());
        domainInvitation.setInvitedByAlias(invitationModel.getInvitedByAlias());
        domainInvitation.setMessageByInvitee(invitationModel.getMessageByInvitee());
        domainInvitation.setInvitationStatus(invitationModel.getInvitationStatus().getStatus());

        return domainInvitation;
    }

    public static InvitationModel transformFromDomain(DomainInvitation domainInvitation) {
        if (domainInvitation == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }

        InvitationModel invitationModel = new InvitationModel();

        invitationModel.setGroupId(domainInvitation.getGroupId());
        invitationModel.setGroupName(domainInvitation.getGroupName());
        invitationModel.setDateInvited(domainInvitation.getDateInvited());
        invitationModel.setPrivateGroup(domainInvitation.getPrivateGroup());
        invitationModel.setInvitedBy(domainInvitation.getInvitedBy());
        invitationModel.setInvitedByAlias(domainInvitation.getInvitedByAlias());
        invitationModel.setMessageByInvitee(domainInvitation.getMessageByInvitee());
        invitationModel.setInvitationStatus(getInvitationStatusEnumFromString(domainInvitation.getInvitationStatus()));

        return invitationModel;
    }

    public static List<InvitationModel> transformAllFromDomainToModel(DomainResource<List<DomainInvitation>> domainInvitationsResource) {

        List<DomainInvitation> domainInvitations = domainInvitationsResource.data;

        List<InvitationModel> invitationModels = new ArrayList<>();

        if (domainInvitations != null && !domainInvitations.isEmpty()) {
            invitationModels = new ArrayList<>();
            for (DomainInvitation domainInvitation : domainInvitations) {
                invitationModels.add(transformFromDomain(domainInvitation));
            }
        }

        if(domainInvitationsResource.status == DomainResource.Status.LOADING){
            InvitationModel invitationModel = new InvitationModel();
            invitationModel.setGroupId(InvitationListStatusConstants.LOADING.toString());
            invitationModels.add(invitationModel);
        }
        else if(domainInvitationsResource.status == DomainResource.Status.SUCCESS){
            if(invitationModels.isEmpty()){
                InvitationModel invitationModel = new InvitationModel();
                invitationModel.setGroupId(InvitationListStatusConstants.NO_INVITATIONS.toString());
                invitationModels.add(invitationModel);
            }
            //else it is already done
        }

        return invitationModels;
    }


    private static InvitationStatusConstants getInvitationStatusEnumFromString(String invitationStatus){
        if(invitationStatus == null || invitationStatus.isEmpty()){
            return InvitationStatusConstants.NO_ACTION;
        }
        return InvitationStatusConstants.get(invitationStatus);

    }


}
