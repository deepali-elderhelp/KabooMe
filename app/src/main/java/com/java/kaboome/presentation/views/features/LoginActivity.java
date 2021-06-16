package com.java.kaboome.presentation.views.features;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.java.kaboome.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "KMLoginActivity";
    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {

        final TextInputEditText phoneNumberEdit  = findViewById(R.id.loginPhoneEditText);
        ccp = findViewById(R.id.loginCountryCodeView);
        ccp.registerCarrierNumberEditText(phoneNumberEdit);



        AppCompatButton login = findViewById(R.id.loginActionButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber = ccp.getFullNumberWithPlus();
                Log.d(TAG, "Final phone number - "+phoneNumber);

                //validate input later
                //go to verification
                Intent verificationIntent = new Intent(LoginActivity.this, VerificationActivity.class);
                //pass username
                verificationIntent.putExtra("userName", phoneNumber);
                startActivity(verificationIntent);

            }
        });

        TextView signUp = findViewById(R.id.loginRegistrationText);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
                finish();
            }
        });

    }

}
