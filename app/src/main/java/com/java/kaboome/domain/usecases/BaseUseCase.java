package com.java.kaboome.domain.usecases;

import androidx.lifecycle.LiveData;

public abstract class BaseUseCase<T, Params> {
    protected abstract LiveData<T> executeUseCase(Params params);

    public LiveData<T> execute(Params params){
        return executeUseCase(params);

    }


}
