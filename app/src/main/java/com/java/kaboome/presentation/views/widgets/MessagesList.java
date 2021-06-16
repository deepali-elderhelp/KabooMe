/*******************************************************************************
 * Copyright 2016 stfalcon.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.java.kaboome.presentation.views.widgets;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.java.kaboome.R;
import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.presentation.views.features.groupMessages.adapter.MessageListViewAdapter;


/**
 * Component for displaying list of messages
 */
public class MessagesList extends RecyclerView {

    private static final String TAG = "KMMessagesList";
    private MessagesListStyle messagesListStyle;
    private LinearLayoutManager layoutManager;
    private View newerMessagesIcon;
    private Context context;

    public MessagesList(Context context) {
        super(context);
        this.context = context;
    }

    public MessagesList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        parseStyle(context, attrs);
        this.context = context;
    }

    public MessagesList(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseStyle(context, attrs);
        this.context = context;
    }


    @SuppressWarnings("ResourceType")
    private void parseStyle(Context context, AttributeSet attrs) {
        messagesListStyle = MessagesListStyle.parse(context, attrs);
    }

    public void setAdapter(MessageListViewAdapter adapter, boolean reverseLayout, RequestManager requestManager, final View newerMessagesIcon,
                           RecyclerView.AdapterDataObserver observer) {
//        SimpleItemAnimator itemAnimator = new DefaultItemAnimator();
//        itemAnimator.setSupportsChangeAnimations(false);

//        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
//                LinearLayoutManager.VERTICAL, reverseLayout);

        this.newerMessagesIcon = newerMessagesIcon;
        layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, reverseLayout);

        int resId = R.anim.layout_animation_from_bottom;
        LayoutAnimationController animationController = AnimationUtils.loadLayoutAnimation(context, resId);
        setLayoutAnimation(animationController);



//        setHasFixedSize(true);
//        layoutManager.setStackFromEnd(true);

//        setItemAnimator(itemAnimator);
        setLayoutManager(layoutManager);
//        adapter.setLayoutManager(layoutManager);

        adapter.setStyle(messagesListStyle);
        adapter.setRequestManager(requestManager);

        adapter.registerAdapterDataObserver(observer);

//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
////                super.onItemRangeInserted(positionStart, itemCount);
//                Log.d(TAG, "what is first visible right now - "+layoutManager.findFirstVisibleItemPosition());
//                Log.d(TAG, "what is position start right now - "+positionStart);
//                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
//                if(positionStart == 0 && firstVisiblePosition <= 1){  //first visible item 0 or 1
////                    layoutManager.scrollToPosition(0);
//                    smoothScrollToPosition(0);
//                    Log.d(TAG, "onItemRangeInserted: scrolling to position 0");
//                }
//                if(firstVisiblePosition > 1){
//                    newerMessagesIcon.setVisibility(VISIBLE);
////                    new Handler().postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            newerMessagesIcon.setVisibility(INVISIBLE);
////                        }
////                    }, 1000);
//                }
//            }
//
//
//        });

//        adapter.registerAdapterDataObserver(new AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//
//                    layoutManager.scrollToPosition((positionStart+itemCount) - 1);
//
//            }
//
//        });



        setAdapter(adapter);
    }

    @Nullable
    @Override
    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }
}
