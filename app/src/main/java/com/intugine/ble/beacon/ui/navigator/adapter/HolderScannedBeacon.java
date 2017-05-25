package com.intugine.ble.beacon.ui.navigator.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.ui.scanner.ModelBle;
import com.intugine.ble.beacon.utils.UtilDateTime;

/**
 * Created by niraj on 04-05-2017.
 */
public class HolderScannedBeacon extends RecyclerView.ViewHolder{
    private final TextView vTvScannedDeviceAddress;
    private final TextView vTvScannedDeviceRssi;
    private final TextView vTvScannedDeviceTime;
    private final TextView vTvScannedDeviceName;
    public Context mContext;

    public static int getLayoutResId(){
        return R.layout.scanned_beacon_holder;
    }

    public HolderScannedBeacon(View itemView) {
        super(itemView);
        mContext =  itemView.getContext();

        vTvScannedDeviceName= (TextView) itemView.findViewById(R.id.scanned_device_viewholder_name_tv);
        vTvScannedDeviceAddress= (TextView) itemView.findViewById(R.id.scanned_device_viewholder_address_tv);
        vTvScannedDeviceRssi= (TextView) itemView.findViewById(R.id.scanned_device_viewholder_rssi_tv);

        vTvScannedDeviceTime= (TextView) itemView.findViewById(R.id.scanned_device_viewholder_time_tv);

    }

    public void bind(ModelBle modelBle){
        vTvScannedDeviceName.setText(modelBle.bluetoothDevice.getName());
        vTvScannedDeviceAddress.setText(modelBle.bluetoothDevice.getAddress());
        vTvScannedDeviceRssi.setText(String.valueOf(modelBle.rssi));
        vTvScannedDeviceTime.setText(UtilDateTime.getTimeFormattedMillis(modelBle.time));
    }
}
