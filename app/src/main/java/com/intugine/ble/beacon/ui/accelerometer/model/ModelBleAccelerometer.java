package com.intugine.ble.beacon.ui.accelerometer.model;

import android.bluetooth.BluetoothDevice;

import com.intugine.ble.beacon.ui.scanner.ModelBle;

/**
 * Created by niraj on 05-05-2017.
 */

public class ModelBleAccelerometer extends ModelBle{
    public ModelBleAccelerometer(BluetoothDevice bluetoothDevice, int rssi, long timeInMillis) {
        super(bluetoothDevice, rssi, timeInMillis);
    }

    @Override
    public boolean equals(Object o) {
        boolean b =  super.equals(o);
        /*if(b){


        }*/
        return b;

    }

    /*
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
    }*/
}
