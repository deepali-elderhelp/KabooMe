package com.java.kaboome.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class NetworkHelper {

    private static final String TAG = "KMNetworkHelper";

    public static boolean isOnline() {

        //check if the cognito session is there
//        if(CognitoHelper.getCurrSession() == null){
//            Log.d(TAG, "isOnline: returning false for session");
//            return false;
//        }

        //disabling the following check because if the session
        //is not valid, then the Okhttp3 call will return back with 401 error code
        //which in turn will call the authenticator and new session would be refreshed
//        if(!CognitoHelper.getCurrSession().isValid()){
//            Log.d(TAG, "idToken is not valid - expired");
//            return false;
//        }


        //check if network connection is there
        ConnectivityManager cm =
                (ConnectivityManager) AppConfigHelper.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
