package com.java.kaboome.data.remote;

import androidx.lifecycle.LiveData;


import com.java.kaboome.data.remote.responses.ApiResponse;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

    private Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(final Call<R> call) {
        return new LiveData<ApiResponse<R>>() {
//            @Override
//            protected void onActive() {
//                super.onActive();
//                final ApiResponse apiResponse = new ApiResponse();
//                call.enqueue(new Callback<R>() {
//                    @Override
//                    public void onResponse(Call<R> call, Response<R> response) {
//                        postValue(apiResponse.create(response));
//                    }
//
//                    @Override
//                    public void onFailure(Call<R> call, Throwable t) {
//                        postValue(apiResponse.create(t));
//                    }
//                });
//
//
//            }

            AtomicBoolean started = new AtomicBoolean(false);
            @Override
            protected void onActive() {
                super.onActive();
                final ApiResponse apiResponse = new ApiResponse();
                if (started.compareAndSet(false, true)) {
                    //this is where you should be adding "clone" if illegalstateexception : already executed persists
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(Call<R> call, Response<R> response) {
                            postValue(apiResponse.create(response));
                        }

                        @Override
                        public void onFailure(Call<R> call, Throwable throwable) {
                            postValue(apiResponse.create(throwable));
                        }
                    });
                }
            }
        };
    }


}
















