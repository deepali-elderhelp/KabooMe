package com.java.kaboome.presentation.views.features;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.exceptions.CognitoInternalErrorException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.AuthenticationCallback;
import com.java.kaboome.helpers.CognitoAuthenticationHandler;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.CredentialsHandler;
import com.java.kaboome.helpers.FirebaseTokenHelper;
import com.java.kaboome.helpers.LoginHandler;
import com.java.kaboome.helpers.NetworkHandler;

import me.leolin.shortcutbadger.ShortcutBadger;

public abstract class BaseFragment extends Fragment implements LoginHandler, NetworkHandler {

    private static final String TAG = "KMUBaseFragment";
    protected BaseViewModel baseViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseViewModel = ViewModelProviders.of(getActivity()).get(BaseViewModel.class);

    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        CognitoHelper.init(AppConfigHelper.getContext());
//        findCurrent();
//    }

    @Override
    public void onResume() {

        Log.d(TAG, "onResume: ");

        //cancel old notifications still hanging in the tray
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AppConfigHelper.getContext());
        notificationManager.cancelAll();

        //remove badge count from app icon as well
        resetBadgeCounterOfPushMessages();

        //also remove from shared preference
        AppConfigHelper.deletePersistedNotifications();

        baseViewModel.getConnectivityLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean != null) {
                    if (!aBoolean) {
                        Log.d(TAG, "Network not connected");
                        onNetworkOff();

                    }

                    else {
                        Log.d(TAG, "Network connected");
                        onNetworkOn();
                    }
                }
            }
        });

        CognitoHelper.init(AppConfigHelper.getContext());
        findCurrent();
        super.onResume();

    }

    private void resetBadgeCounterOfPushMessages() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        }
        ShortcutBadger.removeCount(getContext());
    }

    private void findCurrent() {
        CognitoUser user = CognitoHelper.getPool().getCurrentUser();
        if(user == null){
            BaseFragment.this.onLoginFailure(new Exception("Not logged in"));
        }
        String userName = user.getUserId();
        if(userName == null){
            BaseFragment.this.onLoginFailure(new Exception("Not logged in"));
        }
        if(userName != null) {
            CognitoHelper.setUser(userName);
            user.getSessionInBackground(new CognitoAuthenticationHandler(new AuthenticationCallback() {

                @Override
                public void onFailure(Exception exception) {
                    if(exception instanceof CognitoInternalErrorException){
                        if(exception.getCause().getMessage().contains("Unable to resolve host")){
                            //login failed because token expired(1 hour limit) and now there is no internet connection
                            //to get new tokens from refresh token
                            //let the user still in, but in offline mode
                            //so do nothing, just call it success
                            BaseFragment.this.onLoginSuccess();
                        }
                        else{
                            BaseFragment.this.onLoginFailure(exception);
                        }
                    }
                    else{
                        BaseFragment.this.onLoginFailure(exception);
                    }
                }

                @Override
                public void onSuccess(CognitoUserSession cognitoUserSession) {
                    CognitoHelper.setCurrSession(cognitoUserSession);
                    //check device token, in case it has changed but not captured
                    //user is successfully login, check the fcm token once before going on
                    FirebaseTokenHelper.updateDeviceToken();
                    //do nothing since you are in the required page
                    //save userId
                    AppConfigHelper.setUserId(cognitoUserSession.getUsername());
//                    BaseFragment.this.onLoginSuccess();

                    try {
                        CognitoHelper.getCredentialsAfterSession(new CredentialsHandler() {
                            @Override
                            public void onSuccess(CognitoCachingCredentialsProvider credentialsProvider) {

                                BaseFragment.this.onLoginSuccess();
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Log.d(TAG, "Getting credentials failed due to - "+exception.getMessage());
                                //still getting the user to go on, may be the user intends to do stuff without needing credentials
                                //treating it as success
                                BaseFragment.this.onLoginSuccess();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "Getting credentials failed due to - "+e.getMessage());
                        //still getting the user to go on, may be the user intends to do stuff without needing credentials
                        //treating it as success
                        BaseFragment.this.onLoginSuccess();
                    }
                }
            }));
        }
    }

    @Override
    public void onLoginFailure(Exception exception) {
        //user cannot continue, his login is invalid
        Intent intent = new Intent(getContext(), SignUpActivity.class);
        startActivity(intent);
        getActivity().finish();

    }

    //    private void findCurrent() {
//        CognitoUser user = CognitoHelper.getPool().getCurrentUser();
//        if(user == null){
//            authenticationHandler.onFailure(new Exception("Not logged in"));
//        }
//        String userName = user.getUserId();
//        if(userName == null){
//            authenticationHandler.onFailure(new Exception("Not logged in"));
//        }
//        if(userName != null) {
//            CognitoHelper.setUser(userName);
//            user.getSessionInBackground(authenticationHandler);
//        }
//    }

//    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
//        @Override
//        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
////            AppConfigHelper.setUserLoggedIn(true);
//            Log.d(TAG, " -- Auth Success");
//            CognitoHelper.setCurrSession(cognitoUserSession);
//            //check device token, in case it has changed but not captured
//            //user is successfully login, check the fcm token once before going on
//            FirebaseTokenHelper.updateDeviceToken();
//            //do nothing since you are in the required page
//            //save userId
//            AppConfigHelper.setUserId(cognitoUserSession.getUsername());
//
//            BaseFragment.this.onLoginSuccess();
//        }
//
//        @Override
//        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId){
//            Log.d(TAG, "getAuthenticationDetails: should not come here, throw error");
//            onFailure(new Exception("Fail intentionally, so it goes for login"));
//
//        }
//
//        @Override
//        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
//            Log.d(TAG, "getMFACode: ");
//        }
//
//        @Override
//        public void authenticationChallenge(ChallengeContinuation continuation) {
//            Log.d(TAG, "authenticationChallenge: ");
//        }
//
//
//        @Override
//        public void onFailure(Exception e) {
//
////            AppConfigHelper.setUserLoggedIn(false);
//            //if no network, this happens to be true
//            //((CognitoInternalErrorException)e).getCause().getMessage().contains("Unable to resolve host");
//            Log.d(TAG, "Login failed due to "+e.getMessage()+" in thread "+Thread.currentThread().getName());
//            //go to Login page
////            Intent signUpActivity = new Intent(BaseActivity.this, SignUpActivity.class);
////            startActivity(signUpActivity);
////            finish();
//
//            BaseFragment.this.onLoginFailure(e);
//
////            if(e instanceof CognitoInternalErrorException){
////                if(e.getCause().getMessage().contains("Unable to resolve host")){
////                    //login failed because token expired(1 hour limit) and now there is no internet connection
////                    //to get new tokens from refresh token
////                    //let the user still in, but in offline mode
////                    BaseFragment.this.onLoginSuccess();
////                }
////                else{
////                    BaseFragment.this.onLoginFailure(e);
////                }
////            }
////            else{
////                BaseFragment.this.onLoginFailure(e);
////            }
//
//
//
//        }
//
//
//    };


}
