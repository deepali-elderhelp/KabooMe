package com.java.kaboome.presentation.views.features.groupMessages;

import java.io.Serializable;

public class Attachment implements Serializable {

    private String attachmentURI;
    private String attachmentPath;
    private String attachmentMimeType;
    private String attachmentCaption;
    private boolean attachmentPriority;

    public Attachment(String attachmentURI, String attachmentCaption, String attachmentPath, boolean attachmentPriority) {
        this.attachmentURI = attachmentURI;
        this.attachmentPath = attachmentPath;
        this.attachmentCaption = attachmentCaption;
        this.attachmentPriority = attachmentPriority;
    }

//    public Attachment(String attachmentURI, String attachmentMimeType, String attachmentCaption, boolean attachmentPriority) {
//        this.attachmentURI = attachmentURI;
//        this.attachmentMimeType = attachmentMimeType;
//        this.attachmentCaption = attachmentCaption;
//        this.attachmentPriority = attachmentPriority;
//    }

    public String getAttachmentURI() {
        return attachmentURI;
    }


    public String getAttachmentCaption() {
        return attachmentCaption;
    }

    public void setAttachmentCaption(String attachmentCaption) {
        this.attachmentCaption = attachmentCaption;
    }

    public boolean isAttachmentPriority() {
        return attachmentPriority;
    }

    public void setAttachmentPriority(boolean attachmentPriority) {
        this.attachmentPriority = attachmentPriority;
    }

    public String getAttachmentMimeType() {
        return attachmentMimeType;
    }

    public void setAttachmentMimeType(String attachmentMimeType) {
        this.attachmentMimeType = attachmentMimeType;
    }


    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
}
