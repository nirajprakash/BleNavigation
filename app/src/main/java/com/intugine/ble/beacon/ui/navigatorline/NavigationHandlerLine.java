package com.intugine.ble.beacon.ui.navigatorline;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.navigation.DistanceFilter;
import com.intugine.ble.beacon.ui.scanner.ModelBle;
import com.intugine.ble.beacon.utils.UtilsResource;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.intugine.ble.beacon.util.LogUtils.LOGI;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;

/**
 * Created by niraj on 23-05-2017.
 */

public class NavigationHandlerLine {

    private final int mOffsetWidth;
    public int mNavigationLength = 200;
    public int mNavigationWidth;
    public int mIndicatorAreaLength = 200;
    //private View mPointerView;
    public FrameLayout vFlNavigate;
    public FrameLayout vPointer;
    public AVLoadingIndicatorView vPointerRipple;
    DistanceFilter mDistanceFilter;
    private NavigationIndicatorLine[] mIndicators;
    private Context mContext;
    //public TextView vTvLable;
    private int mYPosition = 100;
    private int mYPositionPrev = 100;
    private int mIndicatorCount;


    public NavigationHandlerLine(Context context) {
        this.mContext = context;
        mDistanceFilter = new DistanceFilter();
        mOffsetWidth = UtilsResource.getResourceDimenValue(mContext, R.dimen.length_24);
    }

    public void initIndicator(int count, WrapperIndicator.onIndicatorClickListener onIndicatorClickListener) {
        LOGI(TAG, "Indicator Count:" + count);

        if (mIndicators == null && count > 1) {
            mIndicatorCount = count;
            mIndicators = new NavigationIndicatorLine[count];
            for (int i = 0; i < count; i++) {
                mIndicators[i] = new NavigationIndicatorLine(
                        new WrapperIndicator(mContext, vFlNavigate, i + 1),
                        mNavigationWidth / 2, mIndicatorAreaLength * i / (count - 1), mNavigationWidth, mIndicatorAreaLength);
                mIndicators[i].mWrapperIndicator.setOnIndicatorClickListener(onIndicatorClickListener);
            }
        }
    }

    public void updateArea(int measuredWidth, int measuredHeight) {
        mIndicatorAreaLength = measuredHeight;
        mNavigationLength = measuredHeight - UtilsResource.getResourceDimenValue(mContext, R.dimen.length_72);
        mNavigationWidth = measuredWidth;
        if (mIndicators != null) {
            if (mIndicators.length > 1) {
                for (int i = 0; i < mIndicators.length; i++) {
                    mIndicators[i].mPositionY = i * measuredHeight / (mIndicators.length - 1);
                }
            } else {
                mIndicators[0].mPositionY = 0;
            }
        }
    }

    public void attachBeaconToIndicator(int indicatorI, List<ModelBle> modelBleList) {
        if (modelBleList != null) {
            ModelBle modelBleMin = null;
            for (ModelBle modelBle :
                    modelBleList) {
                boolean isBeacon = false;
                for (int i = 0; i < mIndicators.length; i++) {
                    if (i != (indicatorI - 1)) {
                        if (mIndicators[i] != null
                                && mIndicators[i].mModelBle != null
                                && mIndicators[i].mModelBle.equals(modelBle)) {
                            isBeacon = true;
                        }
                    }
                }
                if (!isBeacon) {
                    if (modelBleMin == null) {
                        modelBleMin = modelBle;
                    } else {
                        if (modelBleMin.rssi < modelBle.rssi) {
                            modelBleMin = modelBle;
                        }
                    }
                }
            }
            if (modelBleMin != null && mIndicators[indicatorI - 1] != null) {
                mIndicators[indicatorI - 1].updateModelBleOnEdit(modelBleMin, mContext);//.mModelBle =modelBleMin;
                mDistanceFilter.addTh(indicatorI, modelBleMin.rssi);
                LOGW(TAG, "Added indicator at: " + indicatorI + " |  name: " + modelBleMin.bluetoothDevice.getName());
                //updateIndicatorColor(mIndicators[indicatorI - 1]);
            }
        }
    }


    public void updateBeaconRssiTh(int indicatorI, List<ModelBle> modelBleList) {
        if (modelBleList != null) {
            ModelBle indicatorBle = null;
            boolean isBeacon = false;

            for (ModelBle modelBle :
                    modelBleList) {

                if (mIndicators[indicatorI - 1] != null
                        && mIndicators[indicatorI - 1].mModelBle != null
                        && mIndicators[indicatorI - 1].mModelBle.equals(modelBle)) {
                    isBeacon = true;
                    indicatorBle = modelBle;
                }


            }
            if (indicatorBle != null && isBeacon) {
                mDistanceFilter.updateTh(indicatorI, indicatorBle.rssi);
                LOGW(TAG, "updateTh indicator at: " + indicatorI + " |  name: " + indicatorBle.bluetoothDevice.getName());
                //updateIndicatorColor(mIndicators[indicatorI - 1]);
            }
        }
    }

    public void resetIndicators() {
        for (NavigationIndicatorLine indicator : mIndicators) {
            indicator.reset(mContext);
        }
    }

    public void updatePointerPosition(ModelBle modelBle) {
        boolean b = updateIndicatorBle(modelBle);
        //LOGW(TAG, "Updated Indicator: "+ b);

        boolean isYPositionUpdated = false;
        if (b) {
            isYPositionUpdated = updateYPosition();
            LOGW(TAG, "Updated Y Position: " + isYPositionUpdated);
        }
        if (isYPositionUpdated) {
//            if (mPointerView == null) {
//                initPointer();
//            }
            if (vPointer != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vPointer.getLayoutParams();
                //Replace the RelativeLayout with your myView parent layout
                layoutParams.leftMargin = mNavigationWidth / 2 - mOffsetWidth;
                layoutParams.topMargin = mYPosition;
                vPointer.setLayoutParams(layoutParams);
                //vTvLable.setText("| PosiY: " + mYPosition);
                vFlNavigate.invalidate();
            }
        }

    }

    private boolean updateIndicatorBle(ModelBle modelBle) {
        for (int i = 0; i < mIndicators.length; i++) {
            if (mIndicators[i].mModelBle != null && mIndicators[i].mModelBle.equals(modelBle)) {
                boolean b = mDistanceFilter.updateFilter(i + 1, modelBle.rssi);
                if (b) {
                    mIndicators[i].updateModelBle(modelBle);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean updateYPosition() {
        if (mIndicatorCount <= 1) {
            return false;
        }
        List<Integer> indicatorsids = new ArrayList<Integer>();
        for (int i = 0; i < mIndicators.length; i++) {
            if (mIndicators[i].mModelBle != null) {
                indicatorsids.add(i + 1);
            }
        }
        int[] filteresult = mDistanceFilter.getFilterResultMin2(indicatorsids);
        if (filteresult != null) {
            if (filteresult[1] == filteresult[2]) {
                mYPosition = mIndicators[filteresult[1] - 1].mPositionY;
            } else {
                // (y-x)*r + x  ;; r:1
                int x = mNavigationLength * (filteresult[1] - 1) / (mIndicatorCount - 1);//mIndicators[filteresult[1]-1].mPositionY;
                int y = mNavigationLength * (filteresult[2] - 1) / (mIndicatorCount - 1);//mIndicators[filteresult[2]-1].mPositionY;
                int r = filteresult[0];
                int distance = ((y - x) * r) / (100 + r) + x;
                mYPositionPrev = mYPosition;
                mYPosition = distance;
                //mYPosition = distance/5;///10;
                mYPosition = mYPosition * 3 / 7 + mYPositionPrev * 4 / 7;
                LOGW(TAG, "X: " + x + " | Y: " + y + " | distance: " + distance);
                //mYPosition = distance*10;
            }
            return true;
        }
        return false;
    }


    public void startPointer() {
        if (vPointer != null) {
            vPointer.setVisibility(View.VISIBLE);
            vPointerRipple.smoothToShow();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vPointer.getLayoutParams();
            //Replace the RelativeLayout with your myView parent layout
            //layoutParams.leftMargin = mNavigationWidth / 2;
            layoutParams.topMargin = mYPosition;
            vPointer.setLayoutParams(layoutParams);
            //vTvLable.setText("| PosiY: " + mYPosition);
            vFlNavigate.invalidate();
            //vPointer.setVisibility(View.GONE);
        }
    }

    public void stopPointer() {
       /* if(mPointerView!=null){
            mPointerView.setVisibility(View.GONE);
        }
        */
        if (vPointer != null) {
            vPointer.setVisibility(View.INVISIBLE);
            vPointerRipple.hide();
            //vPointer.setVisibility(View.GONE);
        }
    }

    public int getPositionX() {
        return mNavigationWidth / 2;
    }

    public int getPositionY() {
        LOGI(TAG, "getPositionY: "+ mYPosition);
        return mYPosition;
    }

    public String getPosition() {
        int y = (mYPosition*100)/mNavigationLength;
        int x = 50;
        return String.valueOf(x)+ ","+ String.valueOf(y);
    }
}
