package com.java.kaboome.data.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.util.Log;

import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.remote.responses.ApiResponse;


// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)
public abstract class MessageNetworkBoundResource_old<RequestObject> {

    private static final String TAG = "KMNetworkBoundResource";

    private AppExecutors2 appExecutors;
    private MediatorLiveData<Resource<RequestObject>> results = new MediatorLiveData<>();

    public MessageNetworkBoundResource_old(AppExecutors2 appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init(){


        if(shouldFetch()){
            // get data from the network
            fetchFromNetwork();
        }
    }

    /**
     * 2) if <condition/> query the network
     * 3) stop observing the local db
     * 4) insert new data into local db
     *
     */
    private void fetchFromNetwork(){

        Log.d(TAG, "fetchFromNetwork: called.");



        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();

        results.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(@Nullable final ApiResponse<RequestObject> requestObjectApiResponse) {
                results.removeSource(apiResponse);

                /*
                    3 cases:
                       1) ApiSuccessResponse
                       2) ApiErrorResponse
                       3) ApiEmptyResponse
                 */

                if(requestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse){
                    Log.d(TAG, "onChanged: ApiSuccessResponse");
                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            // save the response to the local db
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse)requestObjectApiResponse));

                        }
                    });
                }
                else if(requestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse){
                    Log.d(TAG, "onChanged: ApiEmptyResponse");
                    setValue(Resource.success((RequestObject) processResponse((ApiResponse.ApiSuccessResponse)requestObjectApiResponse)));

                }
                else if(requestObjectApiResponse instanceof ApiResponse.ApiErrorResponse){
                    Log.d(TAG, "onChanged: ApiErrorResponse.");
//                    setValue(Resource.error(((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage(), null ));
                    setValue((Resource<RequestObject>) Resource.error(((ApiResponse.ApiErrorResponse)requestObjectApiResponse).getErrorMessage(), null));
//                    setValue(Resource.error((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage(), null);
//                    setValue(new Resource<RequestObject>(Resource.Status.ERROR, null, ((ApiResponse.ApiErrorResponse) requestObjectApiResponse).getErrorMessage()));

                }
            }
        });
    }

    private RequestObject processResponse(ApiResponse.ApiSuccessResponse response){
        return (RequestObject) response.getBody();
    }

    private void setValue(Resource<RequestObject> newValue){
        if(results.getValue() != newValue){
            results.setValue(newValue);
        }
    }



    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch();

    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<RequestObject>> getAsLiveData(){
        return results;
    };
}




