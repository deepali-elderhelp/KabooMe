package com.java.kaboome.presentation.views.features;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.services.cognitoidentityprovider.model.UsernameExistsException;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.java.kaboome.R;
import com.java.kaboome.helpers.CognitoHelper;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "KMSignUpActivity";

    private CountryCodePicker ccp;
    private AlertDialog userDialog;
    private ProgressDialog waitDialog;

    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //following I added to so that the link "Already Member" shows up
        //otherwise it was being all the way down and user would need to scroll
        //down to see it.
        final NestedScrollView nestedScrollView = findViewById(R.id.signUpScrollView);
        nestedScrollView.post(new Runnable() {
            @Override
            public void run() {
                nestedScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        init();

    }


    private void init() {
        final TextInputEditText givenNameEdit = findViewById(R.id.signUpFullNameEditText);
        final TextInputEditText emailEdit = findViewById(R.id.signUpEmailEditText);
        final TextInputEditText phoneNumberEdit  = findViewById(R.id.signUpPhoneEditText);
        ccp = findViewById(R.id.signUpCountryCodeView);
        ccp.registerCarrierNumberEditText(phoneNumberEdit);



        AppCompatButton signUp =  findViewById(R.id.signUpActionButton);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenName = givenNameEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String phoneNumber = ccp.getFullNumberWithPlus();
                Log.d(TAG, "Final phone number - "+phoneNumber);

                CognitoUserAttributes userAttributes = new CognitoUserAttributes();

                userAttributes.addAttribute("name", givenName);
                userAttributes.addAttribute("email", email);
                userAttributes.addAttribute("phone_number", phoneNumber);

                showWaitDialog("Signing up...");

                // userName = "User"+phoneNumber;
                userName = phoneNumber;
                String password = "Cazt_User"+phoneNumber;

                CognitoHelper.getPool().signUpInBackground(userName, password, userAttributes, null, signUpHandler);

            }
        });

        TextView loginLink = findViewById(R.id.signUpLoginText);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to login
                Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
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


    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            closeWaitDialog();

            if (signUpConfirmationState) {
                // User is already confirmed - since we are using custom auth
                // sign in user and take him to the verification screen
//                CognitoHelper.getPool().getUser(userName).getSessionInBackground(authenticationHandler);
                Intent verificationIntent = new Intent(SignUpActivity.this, VerificationActivity.class);
                //pass username
                verificationIntent.putExtra("userName", userName);
                startActivity(verificationIntent);
                //not finishing it, user can get back here

            }
            else {
                //it should not come here since the user is auto confirmed by the pre signup lambda function

            }
        }

        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            if(exception != null && exception instanceof UsernameExistsException){
                showDialogMessage("User Exists","You are already a User. Please click on 'Already a member'");
            }
            else{
                showDialogMessage("Sign up failed",exception.getMessage());
            }

        }


    };





}
