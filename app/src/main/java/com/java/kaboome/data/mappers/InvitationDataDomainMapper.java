package com.java.kaboome.data.mappers;


import com.java.kaboome.data.entities.Invitation;
import com.java.kaboome.domain.entities.DomainInvitation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InvitationDataDomainMapper {

    public static DomainInvitation transformFromInvitation(Invitation invitation) {
        if (invitation == null) {
            throw new IllegalArgumentException("Cannot transformFromInvitation a null value");
        }

        DomainInvitation domainInvitation = new DomainInvitation();

        domainInvitation.setGroupId(invitation.getGroupId());
        domainInvitation.setGroupName(invitation.getGroupName());
        domainInvitation.setDateInvited(invitation.getDateInvited());
        domainInvitation.setPrivateGroup(invitation.getPrivateGroup());
        domainInvitation.setInvitedBy(invitation.getInvitedBy());
        domainInvitation.setInvitedByAlias(invitation.getInvitedByAlias());
        domainInvitation.setMessageByInvitee(invitation.getMessageByInvitee());
        domainInvitation.setInvitationStatus(invitation.getInvitationStatus());

        return domainInvitation;
    }

    public static Invitation transformFromDomain(DomainInvitation domainInvitation) {
        if (domainInvitation == null) {
            throw new IllegalArgumentException("Cannot transformFromDomain a null value");
        }

        Invitation invitation = new Invitation();

        invitation.setGroupId(domainInvitation.getGroupId());
        invitation.setGroupName(domainInvitation.getGroupName());
        invitation.setDateInvited(domainInvitation.getDateInvited());
        invitation.setPrivateGroup(domainInvitation.getPrivateGroup());
        invitation.setInvitedBy(domainInvitation.getInvitedBy());
        invitation.setInvitedByAlias(domainInvitation.getInvitedByAlias());
        invitation.setMessageByInvitee(domainInvitation.getMessageByInvitee());
        invitation.setInvitationStatus(domainInvitation.getInvitationStatus());

        return invitation;

    }



    public static List<DomainInvitation> transformAllFromInvitation(List<Invitation> invitations) {
        List<DomainInvitation> domainInvitations;

        if (invitations != null && !invitations.isEmpty()) {
            domainInvitations = new ArrayList<>();
            for (Invitation invitation : invitations) {
                domainInvitations.add(transformFromInvitation(invitation));
            }
        } else {
            domainInvitations = Collections.emptyList();
        }

        return domainInvitations;
    }


}
