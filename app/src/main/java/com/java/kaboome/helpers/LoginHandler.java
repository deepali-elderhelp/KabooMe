/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.helpers;


public interface LoginHandler {

    void onLoginSuccess();

    void onLoginFailure(Exception exception);

    void whileLoginInProgress();
}
