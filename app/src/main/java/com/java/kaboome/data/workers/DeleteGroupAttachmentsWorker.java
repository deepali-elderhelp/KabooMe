package com.java.kaboome.data.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.java.kaboome.presentation.helpers.FileUtils;
import com.java.kaboome.presentation.helpers.GeneralHelper;
import com.java.kaboome.presentation.helpers.MediaHelper;

import java.io.File;

/**
 * This worker class is called to delete all the attachments downloaded for the group
 * Kind of delete group media
 * This action is only allowed when network is on - not true anymore
 */
public class DeleteGroupAttachmentsWorker extends Worker {


    private static final String TAG = "KMDeleteGroupAttmsW";
    public static final String GROUP_NAME = "groupName";
    public static final String SENT_TO = "sentTo";



    public DeleteGroupAttachmentsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        String groupName = getInputData().getString(GROUP_NAME);
        String sentTo = getInputData().getString(SENT_TO);

        //go through all app directories -
        //1. image
        //2. audio
        //3. video
        //and see if there is any file that starts with the group name
        //if yes, then delete it


//        String stringToMatch = groupId+"_"+sentTo;

//        File imageDirectory = FileUtils.getAppDirForMime("image", sentTo,false);
//        if(imageDirectory != null){
//            if (imageDirectory.exists()) {
//                File[] files = imageDirectory.listFiles();
//                for (int i = 0; i < files.length; ++i) {
//                    File file = files[i];
//                    if(file.getName() != null && file.getName().contains(stringToMatch)){
//                        file.delete();
//                    }
//                }
//            }
//        }

        File imageDirectory = FileUtils.getAppDirForMime("image", groupName, sentTo,false);
        if(imageDirectory != null){
            if (imageDirectory.exists()) {
                deleteFilesInsideIt(imageDirectory);
                boolean deleteWorked = imageDirectory.delete();
                Log.d(TAG, "delete worked - "+deleteWorked);
            }
        }

        File videoDirectory = FileUtils.getAppDirForMime("video", groupName, sentTo, false);
        if(videoDirectory != null){
            if (videoDirectory.exists()) {
                deleteFilesInsideIt(videoDirectory);
                videoDirectory.delete();
            }
        }

        File audioDirectory = FileUtils.getAppDirForMime("audio", groupName, sentTo, false);
        if(audioDirectory != null){
            if (audioDirectory.exists()) {
                deleteFilesInsideIt(audioDirectory);
                audioDirectory.delete();
            }
        }


//        File videoDirectory = FileUtils.getAppDirForMime("video", false);
//        if(videoDirectory != null){
//            if (videoDirectory.exists()) {
//                File[] files = videoDirectory.listFiles();
//                for (int i = 0; i < files.length; ++i) {
//                    File file = files[i];
//                    if(file.getName() != null && file.getName().contains(stringToMatch)){
//                        file.delete();
//                    }
//                }
//            }
//        }

//        File audioDirectory = FileUtils.getAppDirForMime("audio", false);
//        if(audioDirectory != null){
//            if (audioDirectory.exists()) {
//                File[] files = audioDirectory.listFiles();
//                for (int i = 0; i < files.length; ++i) {
//                    File file = files[i];
//                    if(file.getName() != null && file.getName().contains(stringToMatch)){
//                        file.delete();
//                    }
//                }
//            }
//        }



        return Result.success();
    }

    private void deleteFilesInsideIt(File imageDirectory) {
        File[] files = imageDirectory.listFiles();
                for (int i = 0; i < files.length; ++i) {
                   boolean fileDeleted =  files[i].delete();
                    Log.d(TAG, "fileDeleted - "+fileDeleted);
                }
    }


}
