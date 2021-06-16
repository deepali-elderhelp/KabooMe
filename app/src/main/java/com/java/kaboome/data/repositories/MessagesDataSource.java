package com.java.kaboome.data.repositories;

import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PositionalDataSource;
import androidx.annotation.NonNull;

import com.java.kaboome.data.entities.Message;

import java.util.List;

public class MessagesDataSource extends ItemKeyedDataSource<Long, Message> {

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Message> callback) {
        //get initial messages from Room and also start a WorkManager to load from database in the meantime
        List<Message> messages = fetchItems(params.requestedInitialKey, params.requestedLoadSize);
        callback.onResult(messages);

        //now start a worker thread to load fresh data and update that in the db in the meantime
    }


    @Override
    public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Message> callback) {
        List<Message> messages  = fetchItems(params.key, params.requestedLoadSize);
        callback.onResult(messages);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Message> callback) {
        List<Message> messages = fetchItems(params.key, params.requestedLoadSize);
        callback.onResult(messages);
    }

    @NonNull
    @Override
    public Long getKey(@NonNull Message item) {
        return item.getSentAt();
    }

    private List<Message> fetchItems(Long requestedInitialKey, int requestedLoadSize) {
        //call the dao for data here
        return null;
    }

}
