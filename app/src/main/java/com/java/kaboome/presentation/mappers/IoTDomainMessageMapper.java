package com.java.kaboome.presentation.mappers;

import com.java.kaboome.domain.entities.DomainMessage;
import com.java.kaboome.presentation.entities.IoTMessage;

public class IoTDomainMessageMapper {

    private static final String TAG = "KMIoTDomainMsgMapper";


    public static DomainMessage transformFromIoTMessage(IoTMessage ioTMessage) {
        if (ioTMessage == null) {
            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
        }

        DomainMessage domainMessage = new DomainMessage();

        domainMessage.setMessageId(ioTMessage.getMessageId());
        domainMessage.setGroupId(ioTMessage.getGroupId());
        domainMessage.setSentBy(ioTMessage.getSentBy());
        domainMessage.setSentByImageTS(ioTMessage.getSentByImageTS());
        domainMessage.setAlias(ioTMessage.getAlias());
        domainMessage.setIsAdmin(ioTMessage.getIsAdmin());
        domainMessage.setRole(ioTMessage.getRole());
        domainMessage.setSentAt(ioTMessage.getSentAt());
        domainMessage.setSentTo(ioTMessage.getSentTo());
        domainMessage.setNotify(ioTMessage.getNotify());
        domainMessage.setMessageText(ioTMessage.getMessageText());
        domainMessage.setHasAttachment(ioTMessage.getHasAttachment());
        domainMessage.setAttachmentUploaded(ioTMessage.getAttachmentUploaded());
        domainMessage.setAttachmentExtension(ioTMessage.getAttachmentExtension());
        domainMessage.setAttachmentMime(ioTMessage.getAttachmentMime());
        //the following two fields are filled up locally, they do not need to be overwritten by the server values
//        domainMessage.setAttachmentDownloaded(ioTMessage.getAttachmentDownloaded()); //todo: check this for server-client messing up
//        domainMessage.setAttachmentPath(ioTMessage.getAttachmentURI());//todo: check this for server-client messing up
        domainMessage.setUploadedToServer(ioTMessage.isUploadedToServer());
        domainMessage.setAttachmentLoadingGoingOn(ioTMessage.isAttachmentLoadingGoingOn());
        domainMessage.setLoadingProgress(ioTMessage.getLoadingProgress());
        domainMessage.setDeleted(ioTMessage.getDeleted());
        domainMessage.setDeletedLocally(ioTMessage.getDeletedLocally());
        domainMessage.setTnBlob(ioTMessage.getTnBlob());
        domainMessage.setSentToUserName(ioTMessage.getSentToUserName());
        domainMessage.setSentToUserRole(ioTMessage.getSentToUserRole());
        domainMessage.setSentToImageTS(ioTMessage.getSentToImageTS());
        domainMessage.setIsSentToAdmin(ioTMessage.getIsSentToAdmin());

        return domainMessage;
    }

    public static IoTMessage transformFromDomain(DomainMessage domainMessage) {
        if (domainMessage == null) {
            throw new IllegalArgumentException("Cannot transformFromMessage a null value");
        }

        IoTMessage ioTMessage = new IoTMessage();

        ioTMessage.setMessageId(domainMessage.getMessageId());
        ioTMessage.setGroupId(domainMessage.getGroupId());
        ioTMessage.setSentBy(domainMessage.getSentBy());
        ioTMessage.setSentByImageTS(domainMessage.getSentByImageTS());
        ioTMessage.setAlias(domainMessage.getAlias());
        ioTMessage.setIsAdmin(domainMessage.getIsAdmin());
        ioTMessage.setRole(domainMessage.getRole());
        ioTMessage.setSentAt(domainMessage.getSentAt());
        ioTMessage.setSentTo(domainMessage.getSentTo());
        ioTMessage.setNotify(domainMessage.getNotify());
        ioTMessage.setMessageText(domainMessage.getMessageText());
        ioTMessage.setHasAttachment(domainMessage.getHasAttachment());
        ioTMessage.setAttachmentUploaded(domainMessage.getAttachmentUploaded());
        ioTMessage.setAttachmentExtension(domainMessage.getAttachmentExtension());
        ioTMessage.setAttachmentMime(domainMessage.getAttachmentMime());
//        ioTMessage.setAttachmentDownloaded(domainMessage.getAttachmentDownloaded());
//        ioTMessage.setAttachmentPath(domainMessage.getAttachmentURI());
        ioTMessage.setUploadedToServer(domainMessage.isUploadedToServer());
        ioTMessage.setAttachmentLoadingGoingOn(domainMessage.isAttachmentLoadingGoingOn());
        ioTMessage.setLoadingProgress(domainMessage.getLoadingProgress());
        ioTMessage.setDeleted(domainMessage.getDeleted());
        ioTMessage.setDeletedLocally(domainMessage.getDeletedLocally());
        ioTMessage.setTnBlob(domainMessage.getTnBlob());
        ioTMessage.setSentToUserName(domainMessage.getSentToUserName());
        ioTMessage.setSentToUserRole(domainMessage.getSentToUserRole());
        ioTMessage.setSentToImageTS(domainMessage.getSentToImageTS());
        ioTMessage.setIsSentToAdmin(domainMessage.getIsSentToAdmin());

        return ioTMessage;
    }


}
