package com.java.kaboome.presentation.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import com.java.kaboome.R;

import java.util.Arrays;
import java.util.List;

public class AvatarHelper {

    Context context;

//    private List<Integer> randomColorsList = Arrays.asList(-0xef5350, -0xEC407A, -0xAB47BC, -0x7E57C2,
//            -0x5C6BC0, -0x42A5F5, -0x29B6F6, -0x26C6DA,
//            -0x26A69A, -0x66BB6A, -0x9CCC65, -0xD4E157,
//            -0xFFEE58, -0xFFCA28, -0xFFA726, -0xFF7043,
//            -0x8D6E63, -0xBDBDBD, -0x78909C);

    private static List<Integer> randomColorsList = Arrays.asList(-0xddeecb,
            -0xf0d2cb,
            -0xf2d2b8,
            -0xf2e1b8,
            -0xf2f2b8,
            -0xe6f2b8,
            -0xd7f2b8,
            -0xd5f2ce,
            -0xdbf0df,
            -0xc2dbcf,
            -0xcfecea,
            -0xcfe7ec,
            -0xcfddec,
            -0xcfd2ec,
            -0xd9cfec,
            -0xe6cfec,
            -0xeccfe8,
            -0xeccfdc,
            -0xeccfd2,
            -0xd4b1d1);


//    public AvatarHelper(Context context) {
//        this.context = context;
//    }

    public static Drawable generateAvatar(Context context, Integer dimensionId, String name){

        if(context == null){
            return null;
        }

        if(name == null || name.isEmpty()){
            return context.getResources().getDrawable(R.drawable.account_group_grey);
        }
        int size = context.getResources().getDimensionPixelSize(dimensionId);
        Paint painter = new Paint();
//        painter.setColor(randomColorsList.get((int) (Math.random()*19)));
//        painter.setColor(0xECEBEB);
        painter.setColor(context.getResources().getColor(R.color.greyTransparent30));

        float textSize = ((float) (size / 8.125));

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize * context.getResources().getDisplayMetrics().scaledDensity);
//        textPaint.setColor(Color.WHITE);
//        textPaint.setColor(0xC5C5C5);
        textPaint.setColor(context.getResources().getColor(R.color.white));

        Rect areaRect = new Rect(0,0, size, size);

        String[] words = name.split(" ");
        String finalLetters = "";
        if(words.length > 1){
//            if(words[1].length() > 1 ){
//                finalLetters = words[1].substring(0,2).toUpperCase();
//            }
//            else{
                finalLetters = words[0].substring(0,1).toUpperCase()+words[1].substring(0,1).toUpperCase();
//            }

        }
        else{
            finalLetters = words[0].substring(0,1).toUpperCase();
        }

        Bitmap newImage = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newImage);
        canvas.drawRect(areaRect, painter);

        RectF bounds = new RectF(areaRect);

        if(finalLetters.length() == 1){
            bounds.right = textPaint.measureText(finalLetters, 0, 1);
        }
        else{
            bounds.right = textPaint.measureText(finalLetters, 0, 2);
        }

        bounds.bottom = textPaint.descent() - textPaint.ascent();

        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;

        canvas.drawCircle(size / 2, size / 2, size / 2, painter);
        canvas.drawText(finalLetters, bounds.left, bounds.top - textPaint.ascent(), textPaint);
        return new BitmapDrawable(context.getResources(), newImage);

    }

    public static Drawable generatePlaceholderAvatar(Context context, Integer dimensionId, String name){

        if(context == null){
            return null;
        }

        if(name == null || name.isEmpty()){
            return context.getResources().getDrawable(R.drawable.account_group_grey);
        }
        int size = context.getResources().getDimensionPixelSize(dimensionId);
        Paint painter = new Paint();
        painter.setColor(-0x000000);

        float textSize = ((float) (size / 8.125));

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize * context.getResources().getDisplayMetrics().scaledDensity);
        textPaint.setColor(Color.GRAY);

        Rect areaRect = new Rect(0,0, size, size);

        String[] words = name.split(" ");
        String finalLetters = "";
        if(words.length > 1){
//            if(words[1].length() > 1 ){
//                finalLetters = words[1].substring(0,2).toUpperCase();
//            }
//            else{
            finalLetters = words[0].substring(0,1).toUpperCase()+words[1].substring(0,1).toUpperCase();
//            }

        }
        else{
            finalLetters = words[0].substring(0,1).toUpperCase();
        }

        Bitmap newImage = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newImage);
        canvas.drawRect(areaRect, painter);

        RectF bounds = new RectF(areaRect);

        if(finalLetters.length() == 1){
            bounds.right = textPaint.measureText(finalLetters, 0, 1);
        }
        else{
            bounds.right = textPaint.measureText(finalLetters, 0, 2);
        }

        bounds.bottom = textPaint.descent() - textPaint.ascent();

        bounds.left += (areaRect.width() - bounds.right) / 2.0f;
        bounds.top += (areaRect.height() - bounds.bottom) / 2.0f;

        canvas.drawCircle(size / 2, size / 2, size / 2, painter);
        canvas.drawText(finalLetters, bounds.left, bounds.top - textPaint.ascent(), textPaint);
        return new BitmapDrawable(context.getResources(), newImage);

    }

}
