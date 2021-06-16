package com.java.kaboome.presentation.views.features.groupMessages.viewmodel;

public class MessageTempDataHolder {

    String messageId;
    Long sentAt;
    String fileExtension;
    String fileMime;
    String filePath;

    public MessageTempDataHolder(String messageId, Long sentAt, String fileExtension, String fileMime, String filePath) {
        this.messageId = messageId;
        this.sentAt = sentAt;
        this.fileExtension = fileExtension;
        this.fileMime = fileMime;
        this.filePath = filePath;
    }

    //    public MessageTempDataHolder(String messageId, String filePath) {
//        this.messageId = messageId;
//        this.filePath = filePath;
//    }

//    public MessageTempDataHolder(String messageId, Long sentAt, String filePath) {
//        this.messageId = messageId;
//        this.sentAt = sentAt;
//        this.filePath = filePath;
//    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileMime() {
        return fileMime;
    }

    public void setFileMime(String fileMime) {
        this.fileMime = fileMime;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
