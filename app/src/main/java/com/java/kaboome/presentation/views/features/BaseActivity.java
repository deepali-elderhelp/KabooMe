package com.java.kaboome.presentation.views.features;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.exceptions.CognitoInternalErrorException;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.java.kaboome.R;
import com.java.kaboome.helpers.AppConfigHelper;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.CredentialsHandler;
import com.java.kaboome.helpers.FirebaseTokenHelper;
import com.java.kaboome.helpers.LoginHandler;

public abstract class BaseActivity extends AppCompatActivity implements LoginHandler{
//public abstract class BaseActivity extends AppCompatActivity{

    private static final String TAG = "KMUBaseActivity";

    public ProgressBar mProgressBar;
    private BaseViewModel viewModel;
    protected ConnectivityLiveData connectivityLiveData;
//    private LoginHandler loginHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        connectivityLiveData = new ConnectivityLiveData(this);
        viewModel.setConnectivityLiveData(connectivityLiveData);

    }

    @Override
    public void setContentView(int layoutResID) {

        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        mProgressBar = constraintLayout.findViewById(R.id.progress_bar);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(constraintLayout);
    }

    public void showProgressBar(boolean visibility){
        mProgressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

//    public LoginHandler getLoginHandler() {
//        return loginHandler;
//    }
//
//    public void setLoginHandler(LoginHandler loginHandler) {
//        this.loginHandler = loginHandler;
//    }

    @Override
    protected void onResume() {


        //check authorization here, if not, go to login
        // Initialize application
        CognitoHelper.init(getApplicationContext());
        whileLoginInProgress();
        findCurrent();

        super.onResume();

    }

    private void findCurrent() {
        CognitoUser user = CognitoHelper.getPool().getCurrentUser();
        if(user == null){
            authenticationHandler.onFailure(new Exception("Not logged in"));
        }
        String userName = user.getUserId();
        if(userName == null){
            authenticationHandler.onFailure(new Exception("Not logged in"));
        }
        if(userName != null) {
            Log.d(TAG, "user and user name is there, going to authenticate now");
            CognitoHelper.setUser(userName);
            user.getSessionInBackground(authenticationHandler);
        }
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(final CognitoUserSession cognitoUserSession, CognitoDevice device) {
//            AppConfigHelper.setUserLoggedIn(true);
            Log.d(TAG, " -- Auth Success");
            CognitoHelper.setCurrSession(cognitoUserSession);
            //check device token, in case it has changed but not captured
            //user is successfully login, check the fcm token once before going on
            FirebaseTokenHelper.updateDeviceToken();
            //do nothing since you are in the required page
            //save userId
            AppConfigHelper.setUserId(cognitoUserSession.getUsername());
            //get the credentials now
            try {
                CognitoHelper.getCredentialsProvider(new CredentialsHandler() {
                    @Override
                    public void onSuccess(CognitoCachingCredentialsProvider credentialsProvider) {

//                        viewModel.setUserLoggedIn(true);
                        BaseActivity.this.onLoginSuccess();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Log.d(TAG, "Getting credentials failed due to - "+exception.getMessage());
                        //still getting the user to go on, may be the user intends to do stuff without needing credentials
                        BaseActivity.this.onLoginSuccess();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId){
            Log.d(TAG, "getAuthenticationDetails: should not come here, throw error");
            onFailure(new Exception("Fail intentionally, so it goes for login"));

        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
            Log.d(TAG, "getMFACode: ");
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            Log.d(TAG, "authenticationChallenge: ");
        }


        @Override
        public void onFailure(Exception e) {


            //((CognitoInternalErrorException)e).getCause().getMessage().contains("Unable to resolve host");
            Log.d(TAG, "Login failed due to "+e.getMessage()+" in thread "+Thread.currentThread().getName());

            if(e instanceof  CognitoInternalErrorException){
                //if no network, this happens to be true
                if(e.getCause().getMessage().contains("Unable to resolve host")){
                    Log.d(TAG, "onFailure: Still going to success with no connection");
                    //login failed because token expired(1 hour limit) and now there is no internet connection
                    //to get new tokens from refresh token
                    //let the user still in, but in offline mode
                    BaseActivity.this.onLoginSuccess();
                }
                else{
                    BaseActivity.this.onLoginFailure(e);
                }
            }
            else{
                BaseActivity.this.onLoginFailure(e);
            }



        }


    };

    public int getSoftInputMode(){
        if(this != null)
            return this.getWindow().getAttributes().softInputMode;
        else
            return WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
    }

}
