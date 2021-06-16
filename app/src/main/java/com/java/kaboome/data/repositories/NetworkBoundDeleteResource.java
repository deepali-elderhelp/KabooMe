package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.java.kaboome.data.executors.AppExecutors2;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)

//this class does the folowing -
//1. Delete in server
//2. If successful, delete from DB
//3. Else, do nothing
public abstract class NetworkBoundDeleteResource<CacheObject> {

    private static final String TAG = "KMNetworkBnddDeleteRes";

    private AppExecutors2 appExecutors;
    private MediatorLiveData<DeleteResource<CacheObject>> results = new MediatorLiveData<>();
    private String returnString;


    public NetworkBoundDeleteResource(AppExecutors2 appExecutors, String returnString) {
        this.appExecutors = appExecutors;
        this.returnString = returnString;
        init();
    }

    private void init(){

        // update LiveData for loading status
        results.setValue((DeleteResource<CacheObject>) DeleteResource.deleting(returnString));

        //save the new data to db

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {


                if(shouldDelete()){
                    // upload data to network
                    deleteOnNetwork();
                }

            }
        });


    }

    /**
     * 1) call the API to delete
     * 2)Check API result
     * 3)If successful, delete from local db and return successful
     * 4)If fail, fail and return
     */
    private void deleteOnNetwork(){

        final Call<ResponseBody> apiResponse = createCall();


        apiResponse.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "run: setting the value to successful");
                // save the response to the local db
                appExecutors.diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        deleteInDB();
                    }
                });

                results.postValue((DeleteResource<CacheObject>) DeleteResource.success(returnString));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, final Throwable t) {

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: setting the value to error "+t.getMessage());
                        setValue((DeleteResource<CacheObject>) DeleteResource.error("Network update failed", returnString));
                    }
                });
            }
        });


    }

    private void setValue(DeleteResource<CacheObject> newValue){
        if(results.getValue() != newValue){
            results.setValue(newValue);
        }
    }

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @WorkerThread
    protected abstract boolean shouldDelete();

    // Called to create the API call.
    @NonNull @WorkerThread
    protected abstract Call<ResponseBody> createCall();

    // Called to save the changes into the database before updating to server.
    @WorkerThread
    protected abstract void deleteInDB();


    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<DeleteResource<CacheObject>> getAsLiveData(){
        return results;
    };



}




