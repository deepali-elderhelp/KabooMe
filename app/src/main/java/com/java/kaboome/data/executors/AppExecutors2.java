package com.java.kaboome.data.executors;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors2 {


    private static AppExecutors2 instance;

    public static AppExecutors2 getInstance(){
        if(instance == null){
            instance = new AppExecutors2();
        }
        return instance;
    }

    private final Executor mDiskIO = Executors.newSingleThreadExecutor();

    private final Executor serviceDiskIO = Executors.newFixedThreadPool(8);

    private final Executor mMainThreadExecutor = new MainThreadExecutor();


    public Executor diskIO(){
        return mDiskIO;
    }

    public Executor getServiceDiskIO() {
        return serviceDiskIO;
    }

    public Executor mainThread(){
        return mMainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor{

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
