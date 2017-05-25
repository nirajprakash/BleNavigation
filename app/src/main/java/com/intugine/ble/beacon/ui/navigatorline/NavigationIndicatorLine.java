package com.intugine.ble.beacon.ui.navigatorline;

import android.content.Context;

import com.intugine.ble.beacon.ui.scanner.ModelBle;

/**
 * Created by niraj on 23-05-2017.
 */

public class NavigationIndicatorLine {
    WrapperIndicator mWrapperIndicator;
    public ModelBle mModelBle;
    public int mPositionY = 0;

    public NavigationIndicatorLine(WrapperIndicator wrapperIndicator, ModelBle modelBle, int yVal) {
        this.mWrapperIndicator = wrapperIndicator;
        this.mModelBle = modelBle;
        this.mPositionY = yVal;
    }

    public NavigationIndicatorLine(WrapperIndicator wrapperIndicator,int xVal, int yVal, int xMax, int yMax) {
        this.mWrapperIndicator = wrapperIndicator;
        this.mPositionY = yVal;
        mWrapperIndicator.updateY(xVal, yVal,xMax, yMax);
    }

    public void updateModelBle(ModelBle modelBleMin) {
        this.mModelBle = modelBleMin;
    }

    public void updateModelBleOnEdit(ModelBle modelBleMin, Context context) {
        this.mModelBle = modelBleMin;
        mWrapperIndicator.updateIndicatorColor(context);
    }

    public void reset(Context context) {
        mWrapperIndicator.resetColor(context);
        mModelBle = null;
    }
}
