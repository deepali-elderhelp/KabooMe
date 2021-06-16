package com.java.kaboome.presentation.views.features.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.java.kaboome.R;
import com.java.kaboome.presentation.views.features.home.HomeActivity;
import com.java.kaboome.presentation.views.features.onboarding.adapter.OnboardingPagerAdapter;


public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        findViewById(R.id.onboardingSkipText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(OnboardingActivity.this, HomeActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(homeIntent);
                finish();
            }
        });

        ViewPager pager = findViewById(R.id.onboardingViewPager);
        pager.setAdapter(new OnboardingPagerAdapter(getSupportFragmentManager()));
    }
}
