package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.remote.responses.ApiResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)

//this class does the folowing -
//1. Insert the changes in the local db
//2. Update the remote server
//3. If remote server successful, return
//4. If remote server fails, rollback the changes in db
public abstract class NetworkBoundUpdateResource<CacheObject> {

    private static final String TAG = "KMNetworkBnddUpdateRes";

    private AppExecutors2 appExecutors;
    private MediatorLiveData<UpdateResource<CacheObject>> results = new MediatorLiveData<>();
    private String returnString;


    public NetworkBoundUpdateResource(AppExecutors2 appExecutors, String returnString) {
        this.appExecutors = appExecutors;
        this.returnString = returnString;
        init();
    }

    private void init(){

        // update LiveData for loading status
        results.setValue((UpdateResource<CacheObject>) UpdateResource.updating(returnString));

        //save the new data to db

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {

                // save the response to the local db
                uploadToDB();
                if(shouldUpdate()){
                    // upload data to network
                    uploadToNetwork();
                }

            }
        });


    }

    /**
     * 1) call the API to upload
     * 2)Check API result
     * 3)If successful, throw local broadcast and return
     * 4)If fail, rollback local db, throw local broadcast for fail and return
     */
    private void uploadToNetwork(){

        final Call<ResponseBody> apiResponse = createCall();

        apiResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "run: setting the value to successful");
                results.postValue((UpdateResource<CacheObject>) UpdateResource.success(returnString));
//                appExecutors.mainThread().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, "run: setting the value to successful");
//                        // update LiveData for success status
//                        setValue((UpdateResource<CacheObject>) UpdateResource.success(returnString));
//                    }
//                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, final Throwable t) {
                rollbackDatabase();

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: setting the value to error "+t.getMessage());
                        setValue((UpdateResource<CacheObject>) UpdateResource.error("Network update failed", returnString));
                    }
                });
            }
        });


    }

    private void setValue(UpdateResource<CacheObject> newValue){
        if(results.getValue() != newValue){
            results.setValue(newValue);
        }
    }

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @WorkerThread
    protected abstract boolean shouldUpdate();

    // Called to create the API call.
    @NonNull @WorkerThread
    protected abstract Call<ResponseBody> createCall();

    // Called to save the changes into the database before updating to server.
    @WorkerThread
    protected abstract void uploadToDB();

    // Called to rollback the local cache changes if any
    @WorkerThread
    protected abstract void rollbackDatabase();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<UpdateResource<CacheObject>> getAsLiveData(){
        return results;
    };



}




