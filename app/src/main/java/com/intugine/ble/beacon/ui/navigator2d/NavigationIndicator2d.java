package com.intugine.ble.beacon.ui.navigator2d;

import android.content.Context;
import android.widget.ImageView;

import com.intugine.ble.beacon.ui.navigator.NavigationIndicator;
import com.intugine.ble.beacon.ui.scanner.ModelBle;

/**
 * Created by niraj on 18-05-2017.
 */

public class NavigationIndicator2d extends NavigationIndicator {

    //Beacon mBeacon;
    int positionX=0;

    public NavigationIndicator2d(ImageView wrappedView, ModelBle modelBle, int xVal, int yVal) {
        super(wrappedView, modelBle, yVal);
        positionX = xVal;
        //mBeacon = new Beacon(mModelBle.bluetoothDevice.getName(), xVal, yVal);
    }

    public NavigationIndicator2d(ImageView view, Context context) {
        super(view, context);
        positionX =0;
        positionY = 0;
    }

    public void updatePosition(int xVal, int yVal){

    }




}
