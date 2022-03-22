package com.java.kaboome.helpers;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.java.kaboome.data.persistence.KabooMeDatabase;
import com.java.kaboome.data.remote.BackendAPI;
import com.java.kaboome.data.remote.BackendAPIInterfaceProvider;
import com.java.kaboome.presentation.entities.NotificationsModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.logging.HttpLoggingInterceptor;

public class AppConfigHelper extends Application {
    private static final String TAG = "KMAppConfigHelper";

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;
    private static BackendAPIInterfaceProvider backendApiServiceProvider;

    private static Map<String, ImageSignatureTimestamp> imageSignatureTimestampsInMemory;

//    private static HashMap<String, Integer> groupImageSignature;

//    private static HashMap<String, Long> lastVisitedGroupTime = new HashMap<>();

    //this hashmap keeps a log of
    //last message timestamp when the group was last accessed
    //this is being used by the home screen to show the count of unread messages
//    private static HashMap<String, Long> lastMsgTSOnGrpAccess;

//    private static int userImageSignature;
    //being used for Glide signature generation
    //changing this will cause the images to load again
//    private static final int appOpenDateTime = (int)(new Date().getTime())/1000;
    private static final int appOpenDateTime = 1;
    private static KabooMeDatabase kabooMeDatabaseInstance;
    private static boolean iotPolicyAttached = false;
//    private static boolean runDeletedGroupsMediaDelete = true;

    private static int notificationId = 1; //notificationId 0 is being kept for the summary
    //creates notification channel
    public static final String CHANNEL_ID = "KabooMeChannel";

    MutableLiveData<Boolean> connectivityOnLiveData = new MutableLiveData<>();


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        //initialize shared preferences
        sharedPreferences = getSharedPreferences("appprefrences.xml",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //load image signatures in memory
//        loadImageSignatures();

        createNotificationChannels();
        getExistingDeviceToken();

        Log.d(TAG, "onCreate: ");

    }

    private void getExistingDeviceToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.d(TAG, "token is - "+token);
                    }
                });
    }

    public static Context getContext(){
        return context;
    }

    public static BackendAPIInterfaceProvider getBackendApiServiceProvider() {
        if(backendApiServiceProvider == null){
            backendApiServiceProvider = BackendAPIInterfaceProvider.getApiServiceProvider(
                    BackendAPI.baseRemoteUrl,
                    10000,
                    10000,
                    HttpLoggingInterceptor.Level.BODY);
        }
        return backendApiServiceProvider;
    }

    public static KabooMeDatabase getKabooMeDatabaseInstance(){
        if(kabooMeDatabaseInstance == null){
            kabooMeDatabaseInstance = KabooMeDatabase.getInstance(context);
        }
        return kabooMeDatabaseInstance;
    }

    public static String getUserId(){
        if(CognitoHelper.getCurrSession() != null){
            return CognitoHelper.getCurrSession().getUsername();
        }

        return sharedPreferences.getString("userId", null);

    }

    public static boolean profilePicSelected(){
        return sharedPreferences.getBoolean("updatedProfilePic", false);
    }

    public static void setProfilePicSelected(){
        editor.putBoolean("updatedProfilePic", true);
        editor.commit();
    }

//    public static boolean isRunDeletedGroupsMediaDelete() {
//        return runDeletedGroupsMediaDelete;
//    }
//
//    public static void setRunDeletedGroupsMediaDelete(boolean runDeletedGroupsMediaDelete) {
//        AppConfigHelper.runDeletedGroupsMediaDelete = runDeletedGroupsMediaDelete;
//    }

//    public static int getGroupImageSignature(String groupId){
//        if(groupImageSignature == null){
//            groupImageSignature = new HashMap<>();
//            groupImageSignature.put(groupId, 0); //starting value
//        }
//        else{
//            Integer groupSignatureInt = groupImageSignature.get(groupId);
//            if(groupSignatureInt == null){
//                groupImageSignature.put(groupId, 0); //starting value
//            }
//        }
//
//        return appOpenDateTime+ groupImageSignature.get(groupId);
//    }

    public static Long getCurrentUserGroupUserImageTS(String groupId){
        String imageId = groupId+"_"+AppConfigHelper.getUserId();
        if(imageSignatureTimestampsInMemory == null){
            imageSignatureTimestampsInMemory = new Gson().fromJson(sharedPreferences.getString("imageSignatures", null), new TypeToken<Map<String, ImageSignatureTimestamp>>(){}.getType());
        }

        if(imageSignatureTimestampsInMemory == null){
            //no map created ever yet
            return 0L;
        }
        ImageSignatureTimestamp imageSignatureTimestamp = imageSignatureTimestampsInMemory.get(imageId);

        if(imageSignatureTimestamp == null){
            return 0L;
        }

        return imageSignatureTimestamp.getTimeStamp();


    }

    private static void loggingImageTimeSignatures(){
        if(imageSignatureTimestampsInMemory != null){
            Set<String> keys = imageSignatureTimestampsInMemory.keySet();
            for(String key: keys){
               ImageSignatureTimestamp imageSignatureTimestamp = imageSignatureTimestampsInMemory.get(key);
                Log.d(TAG, "For key - "+key+" timestamp - "+imageSignatureTimestamp.getTimeStamp()+" signature - "+imageSignatureTimestamp.getSignature());

            }
        }
    }

    public static int getImageSignature(String id, Long timestamp){

//        loggingImageTimeSignatures();

        if(imageSignatureTimestampsInMemory == null){
            imageSignatureTimestampsInMemory = new Gson().fromJson(sharedPreferences.getString("imageSignatures", null), new TypeToken<Map<String, ImageSignatureTimestamp>>(){}.getType());
        }

//        Map<String, ImageSignatureTimestamp> imageSignatureTimestamps = new Gson().fromJson(sharedPreferences.getString("imageSignatures", null), new TypeToken<Map<String, ImageSignatureTimestamp>>(){}.getType());

        Long currentTime = (new Date()).getTime();

        if(imageSignatureTimestampsInMemory == null){//there is no list created yet
            imageSignatureTimestampsInMemory = new HashMap<>();
//            imageSignatureTimestampsInMemory.put(id, new ImageSignatureTimestamp(timestamp == null? 0: timestamp, 0));
            imageSignatureTimestampsInMemory.put(id, new ImageSignatureTimestamp(timestamp == null? currentTime: timestamp, 0));
            editor.putString("imageSignatures", new Gson().toJson(imageSignatureTimestampsInMemory));
            editor.commit();
            return 0;

        }
        else{
            ImageSignatureTimestamp imageSignatureTimestamp = imageSignatureTimestampsInMemory.get(id);
            if(imageSignatureTimestamp == null){
//                imageSignatureTimestampsInMemory.put(id, new ImageSignatureTimestamp(timestamp == null? 0: timestamp, 0));
                imageSignatureTimestampsInMemory.put(id, new ImageSignatureTimestamp(timestamp == null? currentTime: timestamp, 0));
                editor.putString("imageSignatures", new Gson().toJson(imageSignatureTimestampsInMemory));
                editor.commit();
                return 0;
            }
//            if(imageSignatureTimestamp.getTimeStamp() == null){ //this happened when moved from release to debug after setting minimize to false
//                imageSignatureTimestamp.setTimeStamp(timestamp == null? currentTime: timestamp);
//                editor.putString("imageSignatures", new Gson().toJson(imageSignatureTimestampsInMemory));
//                editor.commit();
//            }
            if(timestamp != null && timestamp > imageSignatureTimestamp.getTimeStamp()){
                Log.d(TAG, "New signature bcoz old timestamp - "+imageSignatureTimestamp.getTimeStamp()+" and new one is - "+timestamp+" for id "+id);
                imageSignatureTimestamp.incrementSignature(timestamp);
                imageSignatureTimestamp.setTimeStamp(timestamp);
                editor.putString("imageSignatures", new Gson().toJson(imageSignatureTimestampsInMemory));
                editor.commit();
            }
            //for logging
            if(id.equals("group116081f5-5ff0-47c8-8490-04cab35d028e_MN")){
                Log.d(TAG, "116081f5-5ff0-47c8-8490-04cab35d028e: "+imageSignatureTimestamp.getSignature());
            }
            return imageSignatureTimestamp.getSignature();
        }

//        return appOpenDateTime+ groupImageSignature.get(groupId);
//        return 0;
    }


//    public static void setGroupImageSignature(String groupId){
//        if(groupImageSignature == null){
//            groupImageSignature = new HashMap<>();
//            groupImageSignature.put(groupId, -1); //before starting value
//        }
//        else {
//            Integer groupSignatureInt = groupImageSignature.get(groupId);
//            if(groupSignatureInt == null){
//                groupImageSignature.put(groupId, -1); //before starting value
//            }
//        }
//
//        //increase the value be one
//        int currentValue = groupImageSignature.get(groupId);
//        Integer newValue = new Integer(currentValue+1);
//        groupImageSignature.put(groupId, newValue);
//    }

//    public static int getUserProfilePicSignature(String userId) {
//        return userImageSignature;
//    }

//    public static void increaseUserImageSignature() {
//        ++userImageSignature;
//    }

    public static void setDeviceId(String deviceId){
        editor.putString("deviceId", deviceId);
        editor.commit();
    }

    public static String getDeviceId(){
        return sharedPreferences.getString("deviceId", "NA");
    }

    public static void setUserId(String userId){
        editor.putString("userId", userId);
        editor.commit();
    }


    public static void setCurrentUserImageTimestamp(Long currentUserImageTimestamp) {
        editor.putString("currentUserImageTimestamp", String.valueOf(currentUserImageTimestamp));
        editor.commit();
    }

    public static Long getCurrentUserImageTimestamp() {
        return Long.valueOf(sharedPreferences.getString("currentUserImageTimestamp", "0"));
    }

    public static boolean isIotPolicyAttached() {
        return iotPolicyAttached;
    }

    public static void setIotPolicyAttached(boolean iotPolicyAttached) {
        AppConfigHelper.iotPolicyAttached = iotPolicyAttached;
    }

//    private static void loadImageSignatures(){
//        Map<String, ImageSignatureTimestamp> imageSignatureTimestamps = new Gson().fromJson(sharedPreferences.getString("imageSignatures", null), new TypeToken<Map<String, ImageSignatureTimestamp>>(){}.getType());
//        if(imageSignatureTimestamps != null){
//            imageSignatureTimestampsInMemory = imageSignatureTimestamps;
//        }
//
//    }



//    public static long getGroupLastSeenMsgTS(String groupId){
//
//        Map<String, Long> lastMsgTSOnGrpAccess = new Gson().fromJson(sharedPreferences.getString("groupLastSeenMsgTSMap", null), new TypeToken<Map<String, Long>>(){}.getType());;
//
//        if(lastMsgTSOnGrpAccess == null){//there is no list created yet
//            lastMsgTSOnGrpAccess = new HashMap<>();
//            lastMsgTSOnGrpAccess.put(groupId, 0L); //may be current time date stamp should be put here
//            editor.putString("groupLastSeenMsgTSMap", new Gson().toJson(lastMsgTSOnGrpAccess));
//            editor.commit();
//            return 0L;
//        }
//        else{
//            Long groupLastSeenMsgTS = lastMsgTSOnGrpAccess.get(groupId);
//            if(groupLastSeenMsgTS == null){
//                lastMsgTSOnGrpAccess.put(groupId, 0L);
//                editor.putString("groupLastSeenMsgTSMap", new Gson().toJson(lastMsgTSOnGrpAccess));
//                editor.commit();
//                return 0L;
//            }
//            else{
//                return  groupLastSeenMsgTS;
//            }
//
//        }
//
//    }
//
//    public static void setGroupLastSeenMsgTS(String groupId, Long groupLastSeenMsgTS){
//
//        Map<String, Long> lastMsgTSOnGrpAccess = new Gson().fromJson(sharedPreferences.getString("groupLastSeenMsgTSMap", null), new TypeToken<Map<String, Long>>(){}.getType());;
//
//        if(lastMsgTSOnGrpAccess == null){//there is no list created yet
//            lastMsgTSOnGrpAccess = new HashMap<>();
//            lastMsgTSOnGrpAccess.put(groupId, groupLastSeenMsgTS);
//            editor.putString("groupLastSeenMsgTSMap", new Gson().toJson(lastMsgTSOnGrpAccess));
//            editor.commit();
//        }
//        else{
//            lastMsgTSOnGrpAccess.put(groupId, groupLastSeenMsgTS);
//            editor.putString("groupLastSeenMsgTSMap", new Gson().toJson(lastMsgTSOnGrpAccess));
//            editor.commit();
//        }
//    }

//    public static Long getLastVisitedGroupTime(String groupId) {
//        if(lastVisitedGroupTime == null){
//            return null;
//        }
//        return lastVisitedGroupTime.get(groupId);
//    }
//
//    public static void setLastVisitedGroupTime(String groupId, Long timeVisited) {
//        lastVisitedGroupTime.put(groupId, timeVisited);
//    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID,
                    "KabooMe Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is KabooMe Main Notification channel");
//            channel1.setShowBadge(false);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    public static int getNotificationId() {
        if(notificationId == Integer.MAX_VALUE){
            notificationId = 1; //saving 0 for summary
        }
        return ++notificationId;
    }

    public static void setNotificationId(int notificationId) {
        AppConfigHelper.notificationId = notificationId;
    }

    public static void persistNotification(NotificationsModel notificationsModel){
        //get the notifications map
        Set<String> notifications  = sharedPreferences.getStringSet("notifications", null);
        if(notifications == null || notifications.isEmpty()){
            //create new Set
            notifications = new HashSet<>();

        }

        String notification_string = new Gson().toJson(notificationsModel);
        notifications.add(notification_string);
        editor.putStringSet("notifications", notifications);
        editor.commit();


    }

    public static void persistNotification(Set<NotificationsModel> notificationsModels){
        Set<String> notifications = new HashSet<>();
        if(notificationsModels != null && !notificationsModels.isEmpty()){
            for(NotificationsModel notificationsModel: notificationsModels){
                String notification = new Gson().toJson(notificationsModel);
                notifications.add(notification);
            }

        }
        editor.putStringSet("notifications", notifications);
        editor.commit();
    }

    public static Set<String> getPersistedNotifications(){
        return sharedPreferences.getStringSet("notifications", null);
    }

    public static Set<NotificationsModel> getPersistedNotificationModels(){
        Set<String> notifications  =  sharedPreferences.getStringSet("notifications", null);
        Set<NotificationsModel> notificationsModels = new HashSet<>();
        if(notifications == null || notifications.isEmpty()){
            return notificationsModels;
        }
        for(String notification : notifications){
            NotificationsModel notificationsModel = new Gson().fromJson(notification, NotificationsModel.class);
            notificationsModels.add(notificationsModel);
        }
        return notificationsModels;
    }

    public static void deletePersistedNotifications(){
        editor.remove("notifications");
    }

}

class ImageSignatureTimestamp{

    private Long timeStamp = 0l;
    private int signature;

    public ImageSignatureTimestamp(Long timeStamp, int signature) {
        this.timeStamp = timeStamp;
        this.signature = signature;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getSignature() {
        return signature;
    }

    public void setSignature(int signature) {
        this.signature = signature;
    }

    public void incrementSignature(Long timestamp) {
        this.timeStamp = timestamp;
        ++signature;
    }
}
