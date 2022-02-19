/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.helpers;


public interface UploadHandler {

    void uploadStarted();

    void uploadFailed(Exception exception);

    void uploadFinished();

    void uploadGoingOn();
}
