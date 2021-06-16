package com.java.kaboome.domain.usecases;


import com.java.kaboome.domain.repositories.MessagesListRepository;


public class UpdateMessageAttachmentDetailsUseCase extends BaseSingleUseCase<Void, UpdateMessageAttachmentDetailsUseCase.Params> {

    private static final String TAG = "KMUpdateMessageUseCase";
    private MessagesListRepository messagesListRepository;

    public UpdateMessageAttachmentDetailsUseCase(MessagesListRepository messagesListRepository) {
        this.messagesListRepository = messagesListRepository;
    }


    @Override
    protected Void executeUseCase(Params params) {
//        this.groupRepository.updateGroup(params.groupId, params.groupName, params.privacy, params.action);
        this.messagesListRepository.updateMessageAttachmentDetails(params.messageId, params.hasAttachment, params.attachmentUploaded, params.attachmentLoadingGoingOn, params.mimeType, params.attachmentUri);
        return null;
    }

    public static final class Params {

        private final String messageId;

        private final Boolean hasAttachment;

        private final Boolean attachmentUploaded;

        private final Boolean attachmentLoadingGoingOn;

        private final String mimeType;

        private final String attachmentUri;

//        private final Boolean attachmentDownloaded;
//
//        private final String attachmentPath;

//        public Params(String messageId, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentDownloaded, String attachmentPath) {
//            this.messageId = messageId;
//            this.hasAttachment = hasAttachment;
//            this.attachmentUploaded = attachmentUploaded;
//            this.attachmentDownloaded = attachmentDownloaded;
//            this.attachmentPath = attachmentPath;
//        }
//
//
//        public static Params messageToBeUpdated(String messageId, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentDownloaded, String attachmentPath) {
//            return new Params(messageId, hasAttachment, attachmentUploaded, attachmentDownloaded, attachmentPath);
//        }

        public Params(String messageId, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentLoadingGoingOn, String mimeType, String attachmentUri) {
            this.messageId = messageId;
            this.hasAttachment = hasAttachment;
            this.attachmentUploaded = attachmentUploaded;
            this.attachmentLoadingGoingOn = attachmentLoadingGoingOn;
            this.mimeType = mimeType;
            this.attachmentUri = attachmentUri;
        }


        public static Params messageToBeUpdated(String messageId, Boolean hasAttachment, Boolean attachmentUploaded, Boolean attachmentLoadingGoingOn, String mimeType, String attachmentUri) {
            return new Params(messageId, hasAttachment, attachmentUploaded, attachmentLoadingGoingOn, mimeType, attachmentUri);
        }
    }
}
