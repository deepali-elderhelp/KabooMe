package com.java.kaboome.presentation.helpers;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QRCodeHelper {

    /**
     * QRCodeWriter qrCodeWriter = new QRCodeWriter();
     *         BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
     */

    public static Bitmap generate(Context context, String mContent, ErrorCorrectionLevel mErrorCorrectionLevel, int mMargin, double heightDivider, double widthDivider) {

        int mHeight = (int) (context.getResources().getDisplayMetrics().heightPixels / heightDivider);
        int mWidth = (int) (context.getResources().getDisplayMetrics().widthPixels / widthDivider);



        Map<EncodeHintType, Object> hintsMap = new HashMap<>();
        hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hintsMap.put(EncodeHintType.ERROR_CORRECTION, mErrorCorrectionLevel);
        hintsMap.put(EncodeHintType.MARGIN, mMargin);

        //do not use these hints, creates problems in generic reading of the qr code
        //reading does not have ERROR_CORRECTION and MARGIN setting for decoding hints
        try {
//            BitMatrix bitMatrix = new QRCodeWriter().encode(mContent, BarcodeFormat.QR_CODE, mWidth, mHeight, hintsMap);
            BitMatrix bitMatrix = new QRCodeWriter().encode(mContent, BarcodeFormat.QR_CODE, mWidth, mHeight);

            int[] pixels = new int[mWidth * mHeight];
            for (int i = 0; i < mHeight; i++) {
                for (int j = 0; j < mWidth; j++) {
                    if (bitMatrix.get(j, i)) {
                        pixels[i * mWidth + j] = 0xFF000000;
                    } else {
//                        pixels[i * mWidth + j] = 0x282946;
                        pixels[i * mWidth + j] = 0xFFFFFFFF;
                        // pixels[i * mWidth + j] = 0xFF309f96;
//                        pixels[i * mWidth + j] = 0xFF00bcd4;
                    }

                }
            }
            return Bitmap.createBitmap(pixels, mWidth, mHeight, Bitmap.Config.ARGB_8888);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Bitmap getQRImage(Context context, String textToEncodeInQR, int margin, double heightDivider, double widthDivider) {
        return QRCodeHelper.generate(context, textToEncodeInQR, ErrorCorrectionLevel.H, margin, heightDivider, widthDivider);

    }



}
