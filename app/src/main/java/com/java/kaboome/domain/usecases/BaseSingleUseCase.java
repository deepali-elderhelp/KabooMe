package com.java.kaboome.domain.usecases;


public abstract class BaseSingleUseCase<T, Params> {
    protected abstract T executeUseCase(Params params) ;

    public T execute(Params params)  {
        return executeUseCase(params);

    }


}
