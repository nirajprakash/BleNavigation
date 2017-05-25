package com.intugine.ble.beacon.ui.navigator;

import android.view.View;

import com.intugine.ble.beacon.ui.scanner.ModelBle;

/**
 * Created by niraj on 05-05-2017.
 */

public class WrapperIndicatorView {
    View indicator;
    ModelBle modelBle;
    public int yVal = 0;

    public WrapperIndicatorView(View wrappedView, ModelBle modelBle, int yVal) {
        this.indicator = wrappedView;
        this.modelBle = modelBle;
        this.yVal = yVal;

    }
}
