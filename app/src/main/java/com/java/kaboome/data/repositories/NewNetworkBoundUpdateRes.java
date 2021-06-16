package com.java.kaboome.data.repositories;

import android.util.Log;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.java.kaboome.data.executors.AppExecutors2;
import com.java.kaboome.data.remote.responses.ApiResponse;


//this class does the folowing -
//1. Update in server
//2. If successful, update DB
//3. Else, do nothing

//This class does not check for Network Connection
//If network connection is not there, an error is thrown

// ResultType: Type for the Resource data. (resource expected by the API call)
// RequestType: Type for the API response. (network request)
public abstract class NewNetworkBoundUpdateRes<ResultType, RequestType> {

    private static final String TAG = "KMNetworkBoundUpdateRes";

    private AppExecutors2 appExecutors;
    private MediatorLiveData<UpdateResource<ResultType>> result = new MediatorLiveData<>();

    public NewNetworkBoundUpdateRes(AppExecutors2 appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init() {

        // update LiveData for loading status - passing processResult to get the action string
        result.setValue((UpdateResource<ResultType>) UpdateResource.updating(processResult(null)));

        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();

        //maybe add a shouldUpdate() abstract call here to check for network update
        //something like following -
        //though ideally, an update call should not happen if there is no network
        //it should not even come here, UI should stop it
        /*
        if(shouldFetch(cacheObject)){
                    // get data from the network
                    fetchFromNetwork(dbSource);
                }
                else{
                    results.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject cacheObject) {
                            setValue(Resource.success(cacheObject));
                        }
                    });
                }
         */
        result.addSource(apiResponse, new Observer<ApiResponse<RequestType>>() {
            @Override
            public void onChanged(final ApiResponse<RequestType> requestTypeApiResponse) {

                result.removeSource(apiResponse);

                if (requestTypeApiResponse instanceof ApiResponse.ApiSuccessResponse) {
                    Log.d(TAG, "onChanged: ApiSuccessResponse");
                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // save the response to the local db
                            reflectDataInDB((RequestType) processResponse((ApiResponse.ApiSuccessResponse)requestTypeApiResponse));

                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
//                            result.setValue(Resource.success((RequestType)((ApiResponse.ApiSuccessResponse) requestTypeApiResponse).getBody()));
//                                    result.setValue(Resource.success(processResult(processResponse((ApiResponse.ApiSuccessResponse) requestTypeApiResponse))));
                                    setResultValue(UpdateResource.success(processResult(processResponse((ApiResponse.ApiSuccessResponse) requestTypeApiResponse))));
                                }
                            });
                        }
                    });


                } else if (requestTypeApiResponse instanceof ApiResponse.ApiEmptyResponse) {
                    Log.d(TAG, "onChanged: ApiEmptyResponse");
                    //treating this as a valid response in case of update not returning anything
                    //hence doing things like reflectDataInDB etc. on this response too
                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            // save the response to the local db
                            reflectDataInDB((RequestType) null);

                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    setResultValue((UpdateResource<ResultType>) UpdateResource.success(processResult(null)));
                                }
                            });
                        }
                    });
//                    Log.d(TAG, "onChanged: ApiEmptyResponse");
//                    appExecutors.mainThread().execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            setResultValue((UpdateResource<ResultType>) UpdateResource.success(null));
//                        }
//                    });
                } else if (requestTypeApiResponse instanceof ApiResponse.ApiErrorResponse) {
                    String errorMessage = ((ApiResponse.ApiErrorResponse)requestTypeApiResponse).getErrorMessage();
                    Log.d(TAG, "onChanged: ApiErrorResponse. error "+errorMessage);
//                    result.postValue(Resource.error("error", (ResultType) null));
//                    results.postValue(Resource.error("error", (RequestType) null));
                    setResultValue(UpdateResource.error(errorMessage, processResult(null)));


                }
            }


        });
    }

    private void setResultValue(UpdateResource<ResultType> newValue) {
        if (result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }

    @WorkerThread
    protected abstract ResultType processResult(RequestType requestType);

    @WorkerThread
    private RequestType processResponse(ApiResponse.ApiSuccessResponse response){
        return (RequestType) response.getBody();
    }



    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<UpdateResource<ResultType>> getAsLiveData(){
        return result;
    };

    @WorkerThread
    protected abstract void reflectDataInDB(RequestType requestType);
}
