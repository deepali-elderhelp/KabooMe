package com.java.kaboome.data.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.data.entities.UserGroup;
import com.java.kaboome.data.repositories.DataGroupMessagesRepository;
import com.java.kaboome.data.repositories.DataUserGroupsListRepository;
import com.java.kaboome.domain.repositories.MessagesListRepository;
import com.java.kaboome.domain.repositories.UserGroupsListRepository;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.helpers.FileUtils;

import java.io.File;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.java.kaboome.data.constants.WorkerConstants.WORK_FAILURE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESPONSE;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_RESULT;
import static com.java.kaboome.data.constants.WorkerConstants.WORK_SUCCESS;

/**
 * This worker class is called to delete the attachment
 * of the message if it exists locally/downloaded by the user
 * * This action is only allowed when network is on
 */
public class DeleteMessageAttachmentWorker extends Worker {


    public static final String GROUP_ID = "groupId";
    public static final String MESSAGE_ID = "messageId";
    public static final String FILE_EXTENSION = "fileExtension";
    public static final String FILE_MIME = "fileMime";
    public static final String SENT_TO = "sentTo";



    public DeleteMessageAttachmentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String groupId = getInputData().getString(GROUP_ID);
        String messageId = getInputData().getString(MESSAGE_ID);
        String sentTo = getInputData().getString(SENT_TO);
        String fileExtension = getInputData().getString(FILE_EXTENSION);
        String fileMime = getInputData().getString(FILE_MIME);

        File attachmentFile = FileUtils.getAttachmentFileForMessage(messageId, sentTo, groupId, fileExtension, fileMime);
        if (attachmentFile.exists()) {
            attachmentFile.delete();
        }

        return Result.success();
    }




}
