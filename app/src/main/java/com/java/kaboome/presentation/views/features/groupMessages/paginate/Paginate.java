package com.java.kaboome.presentation.views.features.groupMessages.paginate;

import android.widget.AbsListView;

import androidx.recyclerview.widget.RecyclerView;


public abstract class Paginate {

    public interface Callbacks {
        /**
         * Called when next page of data needs to be loaded.
         */
        void onLoadMore();

        /**
         * Called to check if loading of the next page is currently in progress. While loading is in progress
         * {@link Paginate.Callbacks#onLoadMore} won't be called.
         *
         * @return true if loading is currently in progress, false otherwise.
         */
        boolean isLoading();

        /**
         * Called to check if there is more data (more pages) to load. If there is no more pages to load, {@link
         * Paginate.Callbacks#onLoadMore} won't be called and loading row, if used, won't be added.
         *
         * @return true if all pages has been loaded, false otherwise.
         */
        boolean hasLoadedAllItems();
    }


    /**
     * Call unbind to detach list (RecyclerView or AbsListView) from Paginate when pagination functionality is no
     * longer needed on the list.
     * <p/>
     * Paginate is using scroll listeners and adapter data observers in order to perform required checks. It wraps
     * original (source) adapter with new adapter that provides loading row if loading row is used. When unbind is
     * called original adapter will be set on the list and scroll listeners and data observers will be detached.
     */
    abstract public void unbind();

//    abstract public void addToTotalCurrentCount(int countToAdd);
    abstract public Long getLastSentAt();
    abstract public void setLastSentAt(Long lastSentAt);

    /**
     * Create pagination functionality upon RecyclerView.
     *
     * @param recyclerView RecyclerView that will have pagination functionality.
     * @param callback     pagination callbacks.
     * @return {@link RecyclerPaginate.Builder}
     */
    public static RecyclerPaginate.Builder with(RecyclerView recyclerView, Paginate.Callbacks callback) {
        return new RecyclerPaginate.Builder(recyclerView, callback);
    }


}
