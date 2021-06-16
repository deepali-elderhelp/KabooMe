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
//1. Delete in server
//2. If successful, delete from DB
//3. Else, do nothing

// ResultType: Type for the Resource data. (resource expected by the API call)
// RequestType: Type for the API response. (network request)
public abstract class NewNetworkBoundDelRes<ResultType, RequestType> {

    private static final String TAG = "KMNetworkBoundDeleteRes";

    private AppExecutors2 appExecutors;
    private MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    public NewNetworkBoundDelRes(AppExecutors2 appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init() {

        // update LiveData for loading status
        result.setValue((Resource<ResultType>) Resource.loading(null));

        final LiveData<ApiResponse<RequestType>> apiResponse = createCall();


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
                                    setResultValue(Resource.success(processResult(processResponse((ApiResponse.ApiSuccessResponse) requestTypeApiResponse))));
                                }
                            });
                        }
                    });


                } else if (requestTypeApiResponse instanceof ApiResponse.ApiEmptyResponse) {
                    Log.d(TAG, "onChanged: ApiEmptyResponse");
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
//                            results.setValue(Resource.success((RequestType)null));
//                            result.setValue((Resource<ResultType>) Resource.success(null));
                            setResultValue((Resource<ResultType>) Resource.success(null));
                        }
                    });
                } else if (requestTypeApiResponse instanceof ApiResponse.ApiErrorResponse) {
                    Log.d(TAG, "onChanged: ApiErrorResponse.");
//                    result.postValue(Resource.error("error", (ResultType) null));
//                    results.postValue(Resource.error("error", (RequestType) null));
                    setResultValue(Resource.error("error", (ResultType) null));


                }
            }


        });
    }

    private void setResultValue(Resource<ResultType> newValue) {
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
    public final LiveData<Resource<ResultType>> getAsLiveData(){
        return result;
    };

    @WorkerThread
    protected abstract void reflectDataInDB(RequestType requestType);
}
