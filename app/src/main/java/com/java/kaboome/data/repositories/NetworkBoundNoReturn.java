package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.java.kaboome.data.executors.AppExecutors2;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)

//this class does the following -
//1. Call remote server API call
//2. return
public abstract class NetworkBoundNoReturn {

    private static final String TAG = "KMNetworkBoundNoReturn";

    private AppExecutors2 appExecutors;

    public NetworkBoundNoReturn(AppExecutors2 appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init(){


        //call the API in the server

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {

                final Call<ResponseBody> apiResponse = createCall();

                try {
                    apiResponse.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: error - "+e.getMessage());
                }


            }
        });

    }

    // Called to create the API call.
    @NonNull @WorkerThread
    protected abstract Call<ResponseBody> createCall();
}




