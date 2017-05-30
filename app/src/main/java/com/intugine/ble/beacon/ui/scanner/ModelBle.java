package com.intugine.ble.beacon.ui.scanner;

import android.bluetooth.BluetoothDevice;

import com.intugine.ble.beacon.utils.UtilDateTime;

/**
 * Created by niraj on 04-05-2017.
 */
public class ModelBle {
    public BluetoothDevice bluetoothDevice;
    public int rssi;
    public long time;
    public ModelBle(BluetoothDevice bluetoothDevice, int rssi, long timeInMillis){
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.time = timeInMillis;
    }

    public ModelBle(ModelBle pModelBle) {
        this.bluetoothDevice = pModelBle.bluetoothDevice;
        this.rssi = pModelBle.rssi;
        this.time = pModelBle.time;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o){
            return true;
        }

        if(o== null || getClass() != o.getClass()){
            return false;
        }

        ModelBle model = (ModelBle) o;
        if(!bluetoothDevice.getAddress().equals(model.bluetoothDevice.getAddress())){
            return false;
        }

        return true;
        //return super.equals(obj);
    }

    @Override
    public String toString() {
        return "ModelBle: { "
                + "address" + ": " + bluetoothDevice.getAddress() + ", "
                + "rssi" + ": " + rssi+ ", "
                + "time"+ ": " + UtilDateTime.getTimeFormattedMillis(time)
                + "}";
    }

}
