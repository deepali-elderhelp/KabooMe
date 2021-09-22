package com.java.kaboome.presentation.mappers;

/*
Ideally this breaks the isolation between presenter and data layer
but this has to be here due to the use of PagingLibrary which is using
the room database observation directly
 */

import com.java.kaboome.data.entities.Message;
import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.presentation.entities.IoTMessage;

public class MessageDomainMessageMapper {

    private static final String TAG = "KMIMessageDomainMsgMapper";


    public static DomainMessage transformFromMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
        }

        DomainMessage domainMessage = new DomainMessage();

        domainMessage.setMessageId(message.getMessageId());
        domainMessage.setGroupId(message.getGroupId());
        domainMessage.setSentBy(message.getSentBy());
        domainMessage.setSentByImageTS(message.getSentByImageTS());
        domainMessage.setAlias(message.getAlias());
        domainMessage.setIsAdmin(message.getIsAdmin());
        domainMessage.setRole(message.getRole());
        domainMessage.setSentAt(message.getSentAt());
        domainMessage.setSentTo(message.getSentTo());
        domainMessage.setNotify(message.getNotify());
        domainMessage.setMessageText(message.getMessageText());
        domainMessage.setHasAttachment(message.getHasAttachment());
        domainMessage.setAttachmentUploaded(message.getAttachmentUploaded());
        domainMessage.setAttachmentExtension(message.getAttachmentExtension());
        domainMessage.setAttachmentMime(message.getAttachmentMime());
        domainMessage.setAttachmentUri(message.getAttachmentUri());
//        domainMessage.setAttachmentDownloaded(message.getAttachmentDownloaded());
//        domainMessage.setAttachmentPath(message.getAttachmentURI());
        domainMessage.setUploadedToServer(message.isUploadedToServer());
        domainMessage.setAttachmentLoadingGoingOn(message.isAttachmentLoadingGoingOn());
        domainMessage.setLoadingProgress(message.getLoadingProgress());
        domainMessage.setDeleted(message.getDeleted());
        domainMessage.setDeletedLocally(message.getDeletedLocally());
        domainMessage.setSentToUserName(message.getSentToUserName());
        domainMessage.setSentToUserRole(message.getSentToUserRole());
        domainMessage.setSentToImageTS(message.getSentToImageTS());
        domainMessage.setIsSentToAdmin(message.getIsSentToAdmin());

        return domainMessage;
    }

    public static Message transformFromDomain(DomainMessage domainMessage) {
        if (domainMessage == null) {
            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
        }

        Message message = new Message();

        message.setMessageId(domainMessage.getMessageId());
        message.setGroupId(domainMessage.getGroupId());
        message.setSentBy(domainMessage.getSentBy());
        message.setSentByImageTS(domainMessage.getSentByImageTS());
        message.setAlias(domainMessage.getAlias());
        message.setIsAdmin(domainMessage.getIsAdmin());
        message.setRole(domainMessage.getRole());
        message.setSentAt(domainMessage.getSentAt());
        message.setSentTo(domainMessage.getSentTo());
        message.setNotify(domainMessage.getNotify());
        message.setMessageText(domainMessage.getMessageText());
        message.setUploadedToServer(domainMessage.isUploadedToServer());
        message.setHasAttachment(domainMessage.getHasAttachment());
        message.setAttachmentUploaded(domainMessage.getAttachmentUploaded());
        message.setAttachmentExtension(domainMessage.getAttachmentExtension());
        message.setAttachmentMime(domainMessage.getAttachmentMime());
        message.setAttachmentUri(domainMessage.getAttachmentUri());
//        message.setAttachmentDownloaded(domainMessage.getAttachmentDownloaded());
//        message.setAttachmentPath(domainMessage.getAttachmentURI());
        message.setAttachmentLoadingGoingOn(domainMessage.isAttachmentLoadingGoingOn());
        message.setLoadingProgress(domainMessage.getLoadingProgress());
        message.setDeleted(domainMessage.getDeleted());
        message.setDeletedLocally(domainMessage.getDeletedLocally());
        message.setSentToUserName(domainMessage.getSentToUserName());
        message.setSentToUserRole(domainMessage.getSentToUserRole());
        message.setSentToImageTS(domainMessage.getSentToImageTS());
        message.setIsSentToAdmin(domainMessage.getIsSentToAdmin());

        return message;
    }


}
