/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.presentation.images.glide;

import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.GlideUrl;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

public class GlideUrlWithQueryParameter  extends GlideUrl {

    private static final String TAG = "KMGlideUrlWithQueryPar";
    private String mSourceUrl;
    private URL completeUrl;
    private int hashCode;


    public GlideUrlWithQueryParameter(String baseUrl, URL completeUrl) {
        super(completeUrl);

        mSourceUrl = baseUrl;
        this.completeUrl = completeUrl;
    }

    private static String buildUrl(String baseUrl, String key, String value) {
        StringBuilder stringBuilder = new StringBuilder(baseUrl);

        if (stringBuilder.toString().contains("?")) {
            stringBuilder.append("&");
        }
        else {
            stringBuilder.append("?");
        }

        stringBuilder.append(key);
        stringBuilder.append("=");
        stringBuilder.append(value);

        return stringBuilder.toString();
    }

    @Override
    public String getCacheKey() {
        return mSourceUrl;
    }

    @Override
    public String toString() {
        return super.getCacheKey();
    }

    @Override
    public URL toURL() throws MalformedURLException {
        return completeUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GlideUrlWithQueryParameter) {
            GlideUrlWithQueryParameter other = (GlideUrlWithQueryParameter) o;
            return completeUrl.equals(other.completeUrl);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = completeUrl.hashCode();
            hashCode = 31 * hashCode;
        }
        return hashCode;
    }

}


