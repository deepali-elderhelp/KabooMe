/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.images;

import java.net.URL;

public interface ImageLinkHandler {

    void onImageLinkReady(URL url);
    void onImageLinkError(Exception e);
}
