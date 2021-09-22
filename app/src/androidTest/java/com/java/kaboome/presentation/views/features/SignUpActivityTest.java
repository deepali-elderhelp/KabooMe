package com.java.kaboome.presentation.views.features;

import android.app.Activity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;


import com.java.kaboome.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SignUpActivityTest {

    @Test
    public void testSignUpUp(){
        ActivityScenario<SignUpActivity> signUpActivity = launch(SignUpActivity.class);

        onView(withId(R.id.signUpScrollView))
                .check(matches(isDisplayed())
                );
    }

}