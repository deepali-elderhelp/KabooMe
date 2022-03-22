package com.java.kaboome.presentation.views.features.groupMessages.paginate;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.java.kaboome.presentation.views.features.groupMessages.adapter.MessageListViewAdapter;

public class RecyclerPaginate extends Paginate {

    private static final String TAG = "KMRecyclerPaginate";

    private final RecyclerView recyclerView;
    private final Paginate.Callbacks callbacks;
    private final int loadingTriggerThreshold;
    private int currentTotalItems = 0;
    private int limit;

    public RecyclerPaginate(RecyclerView recyclerView, Paginate.Callbacks callbacks, int loadingTriggerThreshold, int limit) {
        this.recyclerView = recyclerView;
        this.callbacks = callbacks;
        this.loadingTriggerThreshold = loadingTriggerThreshold;
        this.limit = limit;

        // Attach scrolling listener in order to perform end offset check on each scroll event
        recyclerView.addOnScrollListener(mOnScrollListener);
        checkEndOffset();

    }

    @Override
    public void unbind() {
        recyclerView.removeOnScrollListener(mOnScrollListener);
    }

    @Override
    public void addToTotalCurrentCount(int countToAdd) {
        currentTotalItems+=countToAdd;
    }

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            checkEndOffset(); // Each time when list is scrolled check if end of the list is reached
        }
    };

    private void checkEndOffset() {
        int visibleItemCount = recyclerView.getChildCount();
//        int totalItemCount = currentTotalItems;
        int totalItemCount = recyclerView.getAdapter().getItemCount();
        int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        Log.d(TAG, "checkEndOffset: firstVisibleItemPosition "+firstVisibleItemPosition+" with id "+recyclerView.getAdapter().getItemId(firstVisibleItemPosition));
        // Check if end of the list is reached (counting threshold) or if there is no items at all
        if ((totalItemCount - visibleItemCount) <= (firstVisibleItemPosition + loadingTriggerThreshold) || totalItemCount == 0) {
            // Call load more only if loading is not currently in progress and if there is more items to load
            if (!callbacks.isLoading() && !callbacks.hasLoadedAllItems()) {
                callbacks.onLoadMore();
//                currentTotalItems+=limit;
            }
        }
    }


    public static class Builder {

        private final RecyclerView recyclerView;
        private final Paginate.Callbacks callbacks;
        private int limit;

        private int loadingTriggerThreshold = 5;

        public Builder(RecyclerView recyclerView, Paginate.Callbacks callbacks) {
            this.recyclerView = recyclerView;
            this.callbacks = callbacks;
        }

        /**
         * Set the offset from the end of the list at which the load more event needs to be triggered.
         * Default offset if 5.
         *
         * @param threshold number of items from the end of the list.
         * @return {@link RecyclerPaginate.Builder}
         */
        public RecyclerPaginate.Builder setLoadingTriggerThreshold(int threshold) {
            this.loadingTriggerThreshold = threshold;
            return this;
        }

        /**
         * Set the offset from the end of the list at which the load more event needs to be triggered.
         * Default offset if 5.
         *
         * @param limit - the number of items to be loaded every time from backend
         * @return {@link RecyclerPaginate.Builder}
         */
        public RecyclerPaginate.Builder setLimit(int limit) {
            this.limit = limit;
            return this;
        }


        /**
         * Create pagination functionality upon RecyclerView.
         *
         * @return {@link Paginate} instance.
         */
        public Paginate build() {
            if (recyclerView.getAdapter() == null) {
                throw new IllegalStateException("Adapter needs to be set!");
            }

            if (recyclerView.getLayoutManager() == null) {
                throw new IllegalStateException("LayoutManager needs to be set on the RecyclerView");
            }

            return new RecyclerPaginate(recyclerView, callbacks, loadingTriggerThreshold, limit);
        }
    }
}