package com.java.kaboome.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.java.kaboome.R;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.presentation.views.features.home.HomeActivity;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SyncMessagesFromServerIntent extends IntentService {

    private static final String TAG = "KMSyncMessagesFrmSrvr";


    public SyncMessagesFromServerIntent() {
        super("SyncMessagesFromServer");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();


        //this pending intent later could be more specific to a place inside the app
        Intent activityIntent = new Intent(this, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, AppConfigHelper.CHANNEL_ID)
                    .setContentTitle("Sync Data From Server")
                    .setContentText("Running...")
                    .setContentIntent(contentIntent)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            startForeground(1, notification);
//        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent thread is "+Thread.currentThread().getName());

        for (int i = 0; i < 10; i++) {
            Log.d(TAG, "Sleeping - " + i);
            SystemClock.sleep(2000);
        }
    }
}


