package com.java.kaboome.data.repositories;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.java.kaboome.data.entities.Message;

public class MessagesDataSourceFactory extends DataSource.Factory<Long, Message> {

    private MutableLiveData<MessagesDataSource> sourceLiveData =
            new MutableLiveData<>();

    private MessagesDataSource latestSource;

    @Override
    public DataSource<Long, Message> create() {
        latestSource = new MessagesDataSource();
        sourceLiveData.postValue(latestSource);
        return latestSource;
    }

    public MutableLiveData<MessagesDataSource> getSourceLiveData() {
        return sourceLiveData;
    }
}
