/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.helpers;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;

public interface CredentialsHandler {

    void onSuccess(CognitoCachingCredentialsProvider credentialsProvider);

    void onFailure(Exception exception);
}
