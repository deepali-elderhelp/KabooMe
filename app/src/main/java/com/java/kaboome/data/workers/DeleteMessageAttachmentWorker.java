package com.java.kaboome.data.workers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
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
 */
public class DeleteMessageAttachmentWorker extends Worker {


    private static final String TAG = "KMDelMessageAttachment";
    public static final String GROUP_ID = "groupId";
    public static final String GROUP_NAME = "groupName";
    public static final String MESSAGE_ID = "messageId";
    public static final String FILE_EXTENSION = "fileExtension";
    public static final String FILE_MIME = "fileMime";
    public static final String SENT_TO = "sentTo";
    public static final String SENT_BY = "sentBy";
    private static final String MESSAGE_ATTACHMENT_URI = "attachmentURI";


    public DeleteMessageAttachmentWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String messageAttachmentUriString = getInputData().getString(MESSAGE_ATTACHMENT_URI);
        String groupId = getInputData().getString(GROUP_ID);
        String groupName = getInputData().getString(GROUP_NAME);
        String messageId = getInputData().getString(MESSAGE_ID);
        String sentTo = getInputData().getString(SENT_TO);
        String sentBy = getInputData().getString(SENT_BY);
        String fileExtension = getInputData().getString(FILE_EXTENSION);
        String fileMime = getInputData().getString(FILE_MIME);

        if(messageAttachmentUriString != null){
            Uri messageAttachmentUri = Uri.parse(messageAttachmentUriString);

//            DocumentFile srcDoc = DocumentFile.fromSingleUri(AppConfigHelper.getContext(), messageAttachmentUri);
//            if(srcDoc.delete()){
//                Log.d(TAG, "Successfully deleted");
//            }
//            else{
//                Log.d(TAG, "Failed");
//            }

            int returnValue = 0;
            try {
                returnValue = AppConfigHelper.getContext().getContentResolver().delete(messageAttachmentUri, null, null);
            } catch (Exception e) {
                e.printStackTrace();
                //let's try this now
                DocumentFile srcDoc = DocumentFile.fromSingleUri(AppConfigHelper.getContext(), messageAttachmentUri);
                try
                {
                    srcDoc.delete();
                }
                catch(Exception exn)
                {
                    exn.printStackTrace();
                }
            }
            Log.d(TAG, "return value - "+returnValue);
//            File attachmentFile = new File(messageAttachmentUri.getPath());
//            if (attachmentFile.exists()) {
//                if (attachmentFile.delete()) {
//                    Log.d(TAG, "deleted: ");
//                } else {
//                    Log.d(TAG, "Not deleted: ");
//                }
//            }
        }

//        File attachmentFile = FileUtils.getAttachmentFileForMessage(messageId, sentTo, groupName, fileExtension, fileMime);
//        if (attachmentFile.exists()) {
//            attachmentFile.delete();
//        }

        return Result.success();
    }




}
