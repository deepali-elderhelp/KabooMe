package com.java.kaboome.presentation.views.features.onboarding.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.java.kaboome.presentation.views.features.onboarding.first.OnboardingFirstPageFragment;
import com.java.kaboome.presentation.views.features.onboarding.second.OnboardingSecondPageFragment;
import com.java.kaboome.presentation.views.features.onboarding.third.OnboardingThirdPageFragment;


public class OnboardingPagerAdapter extends FragmentPagerAdapter {

    public OnboardingPagerAdapter(FragmentManager fragmentManager) {
//        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        super(fragmentManager);
    }

    @Override
    public @NonNull
    Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnboardingFirstPageFragment();
            case 1:
                return new OnboardingSecondPageFragment();
            case 2:
                return new OnboardingThirdPageFragment();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
