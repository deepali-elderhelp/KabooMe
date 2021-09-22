package com.java.kaboome.presentation.views.features;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoServiceConstants;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException;
import com.java.kaboome.R;
import com.java.kaboome.helpers.CognitoHelper;
import com.java.kaboome.helpers.FirebaseTokenHelper;
import com.java.kaboome.presentation.views.features.onboarding.OnboardingActivity;

import java.util.HashMap;
import java.util.Locale;

public class VerificationActivity extends AppCompatActivity {

    private static final String TAG = "KMVerificationActivity";
    private String userName;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private ChallengeContinuation myCustomChallengeContinuation;
    private EditText inputCode;
    private Button confirm;
    private TextView resend;
    private int numberOfAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        init();
        userName = getIntent().getStringExtra("userName"); //make sure it is valid by doing verification in previous screen
        CognitoHelper.getPool().getUser(userName).getSessionInBackground(authenticationHandler); //this should trigger verfication code
    }

    private void init(){

        inputCode = (EditText) findViewById(R.id.verification_input_code);
        confirm = findViewById(R.id.verification_confirm_Button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
        resend = findViewById(R.id.verification_resend_code);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName != null){
                    numberOfAttempts = 0;
                    showDialogMessage("Pleas wait", "It may take few seconds for code to be resent. Please wait");
                    CognitoHelper.getPool().getUser(userName).getSessionInBackground(authenticationHandler); //this should trigger verfication code
                }
            }
        });

    }

    private void getCode() {
        String OTPCode = inputCode.getText().toString();

        if (OTPCode == null || OTPCode.length() < 1) {
            showDialogMessage("Code needed","Code cannot be empty");
            return;
        }
        //got the code, now check if it is right

        if(myCustomChallengeContinuation != null){
            showWaitDialog("Please wait");
            myCustomChallengeContinuation.setChallengeResponse(CognitoServiceConstants.CHLG_RESP_ANSWER, OTPCode);
            myCustomChallengeContinuation.continueTask();
        }

    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.d(TAG, " -- Auth Success");
            CognitoHelper.setCurrSession(cognitoUserSession);
            closeWaitDialog();
            launchUser();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            closeWaitDialog();
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {

        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            if(e instanceof UserNotConfirmedException){
                //go to confirmation screen
                Toast.makeText(VerificationActivity.this,"User is not confirmed, please confirm", Toast.LENGTH_SHORT).show();

            }
            else if(e instanceof UserNotFoundException){
                //wrong login, take them to signup

                showDialogMessage("Not registered", "User not registered, please make sure phone number entered is right");
//                Toast.makeText(VerificationActivity.this, "User not registered, please sign up", Toast.LENGTH_SHORT).show();

            }
            else if(e instanceof NotAuthorizedException){
                showDialogMessage("Wrong code", "Either wrong code was put too many times or it expired, please resend the code");
            }
            else{
                Log.d(TAG, "onFailure: "+e.getMessage());
                Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
            //for now only logging, later move to an error page or something
            Log.d(TAG, "Login failed due to "+e.getMessage());
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */

            closeWaitDialog();

            if(numberOfAttempts > 0){
                showDialogMessage("Wrong code", "Please check the code and enter again");
            }

            if(CognitoServiceConstants.CHLG_TYPE_CUSTOM_CHALLENGE.equals(continuation.getChallengeName())){
//                closeWaitDialog();
                myCustomChallengeContinuation = continuation;
                numberOfAttempts++;
            }
            else{
                Log.d(TAG, "authenticationChallenge: "+continuation.getChallengeName());
            }
        }
    };

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {

        if(username != null) {
            this.userName = username;
            CognitoHelper.setUser(username);
        }

        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.userName, new HashMap<String, String>(), null);
        authenticationDetails.setAuthenticationType(CognitoServiceConstants.CHLG_TYPE_CUSTOM_CHALLENGE);
        authenticationDetails.setAuthenticationParameter("USERNAME", this.userName);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private void launchUser() {

        //this may also be the place to keep a copy of userId in the sharedPreferences.xml
        //when the Cognito Session is null, it helps to know the userId and get data from cache

        //user is successfully login, check the fcm token once before going on
        FirebaseTokenHelper.updateDeviceToken();

        //user is successfully logged in, take him to Group Home Page
//        Intent homeIntent = new Intent(this, HomeActivity.class);
//        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(homeIntent);

        Intent welcomeIntent = new Intent(this, OnboardingActivity.class);
        startActivity(welcomeIntent);
        finish();

//        closeWaitDialog();
        //user has logged in/signedup, add it to the user shared preference
//        AppConfigHelper.setUsername(CognitoHelper.getCurrSession().getUsername());
//
//        //only thing this should do is check if new user or regular
//        //and based on that go to welcome or home activity
//        if("newUser".equals(purpose)){
//            Intent welcomeActivity = new Intent(LoginActivity.this, WelcomeActivity.class);
//            startActivity(welcomeActivity);
//        }
//        else  if("login".equals(purpose)){
//
////            FirebaseTokenHelper.updateDeviceToken(); //update the database with the new deviceId
//            Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(homeActivity);
//        }
//        else{
//            Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
//            startActivity(homeActivity);
//        }
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }


}
