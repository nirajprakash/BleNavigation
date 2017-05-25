package com.intugine.ble.beacon.utils;

/**
 * Created by niraj on 06-05-2017.
 */


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.util.TypedValue;

import com.intugine.ble.beacon.R;

import static com.intugine.ble.beacon.util.LogUtils.LOGV;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;


public class UtilsColor {



    private static final String TAG = makeLogTag(UtilsColor.class);


	/* colors
	 * 0 red           * 1 pink          * 2 purple
	 * 3 deep purple   * 4 indigo	     * 5 blue
	 * 6 light blue    * 7 cyan 	     * 8 teal
	 * 9 green  	   * 10 light green	 * 11 lime
	 * 12 yellow       * 13 amber   	 * 14 orange
	 * 15 deep orange  * 16 brown   	 * 17 grey
	 * 18 blue grey
	 */

    public static final String  COLOR_MAP_STRING[] = new String[] {
		/* 0 */	"j r",		/* 1 */	"p",		/* 2 */ "h",
		/* 3 */ "k v",		/* 4 */ "i m",		/* 5 */ "b",
		/* 6 */ "s",		/* 7 */ "c",		/* 8 */ "f t",
		/* 9 */ "g",		/* 10 */ "x",		/* 11 */ "l",
		/* 12 */ "y",		/* 13 */ "a",		/* 14 */ "o",
		/* 15 */ "d q",		/* 16 */ "n",		/* 17 */ "u w",
		/* 18 */ "e z"		};


    public static int[] getColorResourceForChar(String s){
        LOGV(TAG, "s:"+s);
        if(COLOR_MAP_STRING[0].contains(s)){
            return new int[]{R.color.red_500, R.color.red_50};
        }else if(COLOR_MAP_STRING[1].contains(s)){
            return new int[]{R.color.pink_500, R.color.pink_50};
        }else if(COLOR_MAP_STRING[2].contains(s)){
            return new int[]{R.color.purple_500, R.color.purple_50};
        }else if(COLOR_MAP_STRING[3].contains(s)){
            return new int[]{R.color.deep_purple_500, R.color.deep_purple_50};
        }else if(COLOR_MAP_STRING[4].contains(s)){
            return new int[]{R.color.indigo_500, R.color.indigo_50};
        }else if(COLOR_MAP_STRING[5].contains(s)){
            return new int[]{R.color.blue_500, R.color.blue_50};
        }else if(COLOR_MAP_STRING[6].contains(s)){
            return new int[]{R.color.light_blue_500, R.color.light_blue_50};
        }else if(COLOR_MAP_STRING[7].contains(s)){
            return new int[]{R.color.cyan_500, R.color.cyan_50};
        }else if(COLOR_MAP_STRING[8].contains(s)){
            return new int[]{R.color.teal_500, R.color.teal_50};
        }else if(COLOR_MAP_STRING[9].contains(s)){
            return new int[]{R.color.green_500, R.color.green_50};
        }else if(COLOR_MAP_STRING[10].contains(s)){
            return new int[]{R.color.light_green_500, R.color.light_green_50};
        }else if(COLOR_MAP_STRING[11].contains(s)){
            return new int[]{R.color.lime_500, R.color.lime_50};
        }else if(COLOR_MAP_STRING[12].contains(s)){
            return new int[]{R.color.yellow_500, R.color.yellow_50};
        }else if(COLOR_MAP_STRING[13].contains(s)){
            return new int[]{R.color.amber_500, R.color.amber_50};
        }else if(COLOR_MAP_STRING[14].contains(s)){
            return new int[]{R.color.orange_500, R.color.orange_50};
        }else if(COLOR_MAP_STRING[15].contains(s)){
            return new int[]{R.color.deep_orange_500, R.color.deep_orange_50};
        }else if(COLOR_MAP_STRING[16].contains(s)){
            return new int[]{R.color.brown_500, R.color.brown_50};
        }else if(COLOR_MAP_STRING[17].contains(s)){
            return new int[]{R.color.grey_500, R.color.grey_50};
        }else if(COLOR_MAP_STRING[18].contains(s)){
            return new int[]{R.color.blue_grey_500, R.color.blue_grey_50};
        }else{
            return new int[]{R.color.red_500, R.color.red_50};
        }

    }



    @TargetApi(23)
    public static int getColorForResource(Context context, int resource){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            return context.getResources().getColor(resource,null);
        }else{
            return context.getResources().getColor(resource);
        }
    }

    public static int getColorForAttr(Context context, int attr){
        TypedValue typedValue = new TypedValue();
        Theme theme =  context.getTheme();
        if(theme.resolveAttribute(attr, typedValue, true)){
            return typedValue.data;
        }
        return 0;
    }

    public static int getColorForAttr(Context context, int attr, int resource){
        int color = getColorForAttr(context, attr);
        if(color!=0){
            //LOGV(TAG, "Color attr val: "+ color);
            return color;
        }else{
            //LOGV(TAG, "Color  val: "+ color);
            return getColorForResource(context, resource);
        }
    }
}

