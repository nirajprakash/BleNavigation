package com.intugine.coalindiaclientdemo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gaurav on 19/5/17.
 */

public class SharedPreferenceHelper {
    private static SharedPreferenceHelper sharedPreferenceHelper;
    private final Context context;

    private SharedPreferenceHelper(Context context) {
        this.context = context;
    }

    public static SharedPreferenceHelper getInstance(Context context) {
        if (sharedPreferenceHelper == null) {
            sharedPreferenceHelper = new SharedPreferenceHelper(context);
        }
        return sharedPreferenceHelper;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("WifiChars",Context.MODE_PRIVATE);
    }

    public String getWifiName() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        if(sharedPreferences!=null){

            return getSharedPreferences().getString("wifi_name","");
        }else {
            return null;
        }
    }
    public String getDeviceName() {
        return getSharedPreferences().getString("device_name","");
    }
    public String getRecieverIP() {
        return getSharedPreferences().getString("reciever_ip","");
    }
    public void saveCharecteristics(String wifi_name, String device_name,String reciever_ip) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString("wifi_name",wifi_name);
        editor.putString("device_name",device_name);
        editor.putString("reciever_ip",reciever_ip);
        editor.apply();
    }
}
