package com.java.kaboome.presentation.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class CreateGroupViewPager extends ViewPager {


        public CreateGroupViewPager(@NonNull Context context) {
            super(context);
        }

        public CreateGroupViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return super.onInterceptTouchEvent(ev);
            // Never allow swiping to switch between pages
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
//        return super.onTouchEvent(ev);
            // Never allow swiping to switch between pages
            return false;
        }
}
