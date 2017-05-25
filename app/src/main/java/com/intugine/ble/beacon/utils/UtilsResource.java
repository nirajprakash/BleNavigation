package com.intugine.ble.beacon.utils;

/**
 * Created by niraj on 06-05-2017.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

public class UtilsResource {

    @SuppressLint("NewApi")
    public static Drawable getDrawable(Context context, int resource) {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            Theme theme = context.getTheme();
            return context.getResources().getDrawable(resource,theme);
        }else{
            return context.getResources().getDrawable(resource);//(resource, outValue, true);
        }
    }



    public static Drawable getDrawableCompat(Context context, int resource) {
        return ContextCompat.getDrawable(context, resource);
    }

    public static int getResourceDimenValue(Context context, int resource) {
        TypedValue outValue = new TypedValue();
        context.getResources().getValue(resource, outValue, true);
        return  (int) outValue.getDimension(context.getResources().getDisplayMetrics());
    }
}

