package com.java.kaboome.presentation.views.features;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class ConnectivityLiveData extends LiveData<Boolean> {

    private static final String TAG = "KMConnectivityLiveData";
    private Context context;
    private NetworkReceiver receiver;

    public ConnectivityLiveData(Context context){
        this.context = context;
    }

    @Override
    protected void onActive() {
        Log.d(TAG, "onActive: ");
        super.onActive();
        receiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(receiver, filter);
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive: ");
        super.onInactive();
        context.unregisterReceiver(receiver);
    }

    private class NetworkReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()){
                Log.d(TAG, "Network connected");
                postValue(true);
            }
            else{
                Log.d(TAG, "Network disconnected");
                postValue(false);
            }

        }
    }
}
