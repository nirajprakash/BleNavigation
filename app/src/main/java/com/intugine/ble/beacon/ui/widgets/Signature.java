package com.intugine.ble.beacon.ui.widgets;

/**
 * Created by niraj on 08-05-2017.
 */

public class Signature {
    long key = -1;
    public Signature(long pKey){
        this.key = pKey;
    }

    public long getKey() {
        return key;
    }
}
