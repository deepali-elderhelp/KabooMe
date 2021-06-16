package com.java.kaboome.data.workers;

import androidx.work.Data;

public interface WorkerCallback {

    void onSuccess(Data response);
    void onError(Data failure);
}
