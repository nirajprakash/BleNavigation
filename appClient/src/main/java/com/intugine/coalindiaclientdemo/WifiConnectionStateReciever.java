package com.intugine.coalindiaclientdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by gaurav on 19/5/17.
 */

public class WifiConnectionStateReciever extends BroadcastReceiver {
    WifiConnectionStateInterface connectionStateInterface;

    public WifiConnectionStateReciever(WifiConnectionStateInterface connectionStateInterface) {
        this.connectionStateInterface = connectionStateInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d("Wifi", "Reciever is called");
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State state = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (state != null && state == NetworkInfo.State.CONNECTED) {
                WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.d("Wifi name", wifiInfo.getSSID());
                String wifi_name = SharedPreferenceHelper.getInstance(context)
                        .getWifiName();
                if (wifiInfo.getSSID().equals(wifi_name) ||
                        wifiInfo.getSSID().equals("\"" + wifi_name + "\"")) {
                    Log.d("Wifi", "Connected to appropriate wifi. Starting to look for server");
                    connectionStateInterface.checkSocketConnection();
                }
            }
        }
    }
}
