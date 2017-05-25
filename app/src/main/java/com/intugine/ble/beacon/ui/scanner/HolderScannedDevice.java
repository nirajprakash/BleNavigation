package com.intugine.ble.beacon.ui.scanner;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.utils.UtilDateTime;

/**
 * Created by niraj on 04-05-2017.
 */
public class HolderScannedDevice extends RecyclerView.ViewHolder{
    private final TextView vTvScannedDeviceAddress;
    private final TextView vTvScannedDeviceRssi;
    private final TextView vTvScannedDeviceTime;
    public Context mContext;

    public static int getLayoutResId(){
        return R.layout.scanned_device_viewholder;
    }

    public HolderScannedDevice(View itemView) {
        super(itemView);
        mContext =  itemView.getContext();

        vTvScannedDeviceAddress= (TextView) itemView.findViewById(R.id.scanned_device_viewholder_name_tv);
        vTvScannedDeviceRssi= (TextView) itemView.findViewById(R.id.scanned_device_viewholder_rssi_tv);

        vTvScannedDeviceTime= (TextView) itemView.findViewById(R.id.scanned_device_viewholder_time_tv);

    }

    public void bind(ModelBle modelBle){
        vTvScannedDeviceAddress.setText(modelBle.bluetoothDevice.getAddress());
        vTvScannedDeviceRssi.setText(String.valueOf(modelBle.rssi));
        vTvScannedDeviceTime.setText(UtilDateTime.getTimeFormattedMillis(modelBle.time));
    }
}
