/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.images.glide;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    private static final String TAG = "KMMyAppGlideModule";

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        Log.d(TAG, "applyOptions");
        super.applyOptions(context, builder);

        //limit the cache size
        builder.setDiskCache(
                new InternalCacheDiskCacheFactory(context, 50 * 1024 * 1024));
//        builder.setDiskCache(
//                new ExternalPreferredCacheDiskCacheFactory(context, 50 * 1024 * 1024));
        builder.setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor(100, "kaboome", GlideExecutor.UncaughtThrowableStrategy.DEFAULT));

        builder.setLogLevel(Log.VERBOSE);
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }




}
