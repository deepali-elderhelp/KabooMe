package com.java.kaboome.data.mappers;

import com.java.kaboome.data.entities.Message;
import com.java.kaboome.data.remote.responses.GroupMessagesResponse;
import com.java.kaboome.domain.entities.DomainMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageDataDomainMapper {
    public MessageDataDomainMapper() {
    }

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
//        domainMessage.setAttachmentDownloaded(message.getAttachmentDownloaded());
//        domainMessage.setAttachmentPath(message.getAttachmentURI());
        domainMessage.setUploadedToServer(message.isUploadedToServer());
        domainMessage.setWaitingToBeDeleted(message.isWaitingToBeDeleted());
        domainMessage.setAttachmentLoadingGoingOn(message.isAttachmentLoadingGoingOn());
        domainMessage.setLoadingProgress(message.getLoadingProgress());
        domainMessage.setDeleted(message.getDeleted());
        domainMessage.setDeletedLocally(message.getDeletedLocally());
        domainMessage.setTnBlob(message.getTnBlob());

        domainMessage.setSentToUserName(message.getSentToUserName());
        domainMessage.setSentToUserRole(message.getSentToUserRole());
        domainMessage.setSentToImageTS(message.getSentToImageTS());
        domainMessage.setIsSentToAdmin(message.getIsSentToAdmin());
        domainMessage.setUnread(message.getUnread());
        domainMessage.setAttachmentUri(message.getAttachmentUri());

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
        message.setHasAttachment(domainMessage.getHasAttachment());
        message.setAttachmentUploaded(domainMessage.getAttachmentUploaded());
        message.setAttachmentExtension(domainMessage.getAttachmentExtension());
        message.setAttachmentMime(domainMessage.getAttachmentMime());
        //these two fields should not be overwritten by the new message arrival
        //since their cache value has already been updated - the following two statements
        //will make it loose those values
//        message.setAttachmentDownloaded(domainMessage.getAttachmentDownloaded());
//        message.setAttachmentPath(domainMessage.getAttachmentURI());
        message.setUploadedToServer(domainMessage.isUploadedToServer());
        message.setWaitingToBeDeleted(domainMessage.isWaitingToBeDeleted());
        message.setAttachmentLoadingGoingOn(domainMessage.isAttachmentLoadingGoingOn());
        message.setLoadingProgress(domainMessage.getLoadingProgress());
        message.setDeleted(domainMessage.getDeleted());
        message.setDeletedLocally(domainMessage.getDeletedLocally());
        message.setTnBlob(domainMessage.getTnBlob());

        message.setSentToUserName(domainMessage.getSentToUserName());
        message.setSentToUserRole(domainMessage.getSentToUserRole());
        message.setSentToImageTS(domainMessage.getSentToImageTS());
        message.setIsSentToAdmin(domainMessage.getIsSentToAdmin());
        message.setUnread(domainMessage.getUnread());
        message.setAttachmentUri(domainMessage.getAttachmentUri());

        return message;
    }

    public static List<DomainMessage> transformFromMessage(List<Message> messages) {
        List<DomainMessage> domainMessages;

        if (messages != null && !messages.isEmpty()) {
            domainMessages = new ArrayList<>();
            for (Message message : messages) {
                domainMessages.add(transformFromMessage(message));
            }
        } else {
            domainMessages = Collections.emptyList();
        }

        return domainMessages;
    }

    public static List<DomainMessage> transformFromMessage(GroupMessagesResponse groupMessagesResponse) {
        if(groupMessagesResponse == null)
            return null;
        List<Message> messages = groupMessagesResponse.getMessages();
        return transformFromMessage(messages);
    }
}
