package com.intugine.ble.beacon.ui.navigator2d;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.navigation.DistanceFilter;
import com.intugine.ble.beacon.navigation.PositonFind;
import com.intugine.ble.beacon.navigation.Trilateration;
import com.intugine.ble.beacon.ui.scanner.ModelBle;
import com.intugine.ble.beacon.utils.UtilsResource;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import static com.intugine.ble.beacon.util.LogUtils.LOGV;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 18-05-2017.
 */

public class NavigationHandler2d {

    private static final String TAG = makeLogTag(NavigationHandler2d.class);

    public int mNavigationLength = 200;
    public int mNavigationWidth;
    public FrameLayout vFlNavigate;
    public FrameLayout vPointer;
    public AVLoadingIndicatorView vPointerRipple;
    private NavigationIndicator2d[] mIndicators;
    private DistanceFilter mDistanceFilter;
    private Context mContext;
    private int mBeaconCount;
    private int mYPosition = 100;
    private int mXPosition = 100;
    private int mYPositionPrev = 100;
    private int mXPositionPrev = 100;

    private int mXDistance = 0;
    private int mYDistance = 0;

    public NavigationHandler2d(Context context, int beaconCount) {
        this.mContext = context;
        this.mBeaconCount = beaconCount;
        mDistanceFilter = new DistanceFilter();
        mIndicators = new NavigationIndicator2d[mBeaconCount];
    }

    public void addViews(ImageView view, int indicatorI) {
        if (indicatorI <= mBeaconCount) {
            if (mIndicators[indicatorI - 1] == null) {
                mIndicators[indicatorI - 1] = new NavigationIndicator2d(view, mContext);
            }
        }
    }

    public void updateArea(int measuredWidth, int measuredHeight) {
        int height = UtilsResource.getResourceDimenValue(mContext, R.dimen.length_72);
        LOGV(TAG, "height: " + height);
        mNavigationLength = measuredHeight - height;
        mNavigationWidth = measuredWidth - height;
        /*if(mIndicators.length>1){

            for (int i = 0; i < mIndicators.length; i++) {
                mIndicators[i].mPositionY = i*measuredHeight/(mIndicators.length-1);
            }
        }else{
            mIndicators[0].mPositionY= 0;
        }*/
    }

    public void attachBeaconToIndicatorTesting(int indicatorI, List<ModelBle> modelBleList) {
        if (modelBleList != null) {
            for (ModelBle modelBle :
                    modelBleList) {
                boolean isBeacon = false;
                String[] beaconName = new String[]{"Bat1", "Bat2", "Bat3", "Bat4"};
                if(modelBle.bluetoothDevice.getName().equals(beaconName[indicatorI-1])){
                    mIndicators[indicatorI - 1].updateModelBleOnEdit(modelBle, mContext);//.mModelBle =modelBleMin;
                    mDistanceFilter.addTh(indicatorI, modelBle.rssi);
                    LOGW(TAG, "Added indicator at: " + indicatorI + " |  name: " + modelBle.bluetoothDevice.getName());

                }
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
                                && mIndicators[i].modelBle != null
                                && mIndicators[i].modelBle.equals(modelBle)) {
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


    public void attachBeaconToIndicator(int key, List<ModelBle> modelBleList, String posiX, String posiY) {
        //attachBeaconToIndicator(key, modelBleList);
        attachBeaconToIndicatorTesting(key, modelBleList);
        if (mIndicators[key - 1] != null && mIndicators[key - 1].modelBle != null) {
            mIndicators[key - 1].positionX = Integer.parseInt(posiX);
            mIndicators[key - 1].positionY = Integer.parseInt(posiY);

            LOGW(TAG, "Navigation indicator: positionX" + mIndicators[key - 1].positionX +
                    " mPositionY:" + mIndicators[key - 1].positionY +
                    "  indicator:" + key +
                    " ble: " + mIndicators[key - 1].modelBle.bluetoothDevice.getName());
        }


        updateAreaDistance();

    }

    private void updateAreaDistance() {
        int xDistanceMax = 0;
        int yDistanceMax = 0;
        for (NavigationIndicator2d navigaitonIndicator :
                mIndicators) {
            if (navigaitonIndicator != null) {
                if (navigaitonIndicator.positionX > xDistanceMax) {
                    xDistanceMax = navigaitonIndicator.positionX;
                }
                if (navigaitonIndicator.positionY > yDistanceMax) {
                    yDistanceMax = navigaitonIndicator.positionY;
                }
            }
        }
        if (xDistanceMax != 0) {
            mXDistance = xDistanceMax;
        }

        if (yDistanceMax != 0) {
            mYDistance = yDistanceMax;
        }


        LOGW(TAG, "Navigation distanceX: " + mXDistance +
                " distanceY:" + mYDistance);

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
//        if(mPointerView==null){
//            initPointer();
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mPointerView.getLayoutParams();//Replace the RelativeLayout with your myView parent layout
//            layoutParams.leftMargin = mNavigationWidth / 2;
//            layoutParams.topMargin = mNavigationLength / 2;
//            mPointerView.setLayoutParams(layoutParams);
//            vFlNavigate.addView(mPointerView, 0);
//            //vTvLable.setText(" PosiY: " + mNavigationLength/2);
//        }else {
//            mPointerView.setVisibility(View.VISIBLE);
//        }
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


    /* *************************************************************************************
     *                                    pointer simulation
     */

    public void updatePointerPosition(ModelBle modelBle) {
        boolean b = updateIndicatorBle(modelBle);

        //LOGW(TAG, "Updated Indicator: "+ b);
        boolean isPositionUpdated = false;
        if (b) {
            //isPositionUpdated = updatePositionByTilateration();
            isPositionUpdated = updatePositionByRatios();
            LOGW(TAG, "Updated Y Position: " + isPositionUpdated);
        }

        if (isPositionUpdated) {
            //if (mPointerView == null) {
            // initPointer();
            // }
            if (vPointer != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vPointer.getLayoutParams();
                //Replace the RelativeLayout with your myView parent layout
                //layoutParams.leftMargin = mNavigationWidth / 2;
                layoutParams.topMargin = mYPosition;
                layoutParams.leftMargin = mXPosition;
                vPointer.setLayoutParams(layoutParams);
                //vTvLable.setText("| PosiY: " + mYPosition);
                vFlNavigate.invalidate();
            }
        }

    }


    private boolean updateIndicatorBle(ModelBle modelBle) {
        for (int i = 0; i < mIndicators.length; i++) {
            if (mIndicators[i].modelBle != null && mIndicators[i].modelBle.equals(modelBle)) {
                boolean b = mDistanceFilter.updateFilter(i + 1, modelBle.rssi);
                if (b) {
                    mIndicators[i].updateModelBle(modelBle);
                    return true;
                }
            }
        }
        return false;
    }


    private boolean updatePositionByRatios() {
        List<Integer> indicatorsids = new ArrayList<Integer>();
        for (int i = 0; i < mIndicators.length; i++) {
            if (mIndicators[i].modelBle != null) {
                indicatorsids.add(i + 1);
            }
        }
        if (indicatorsids!= null && indicatorsids.size()>= 3) {

            /*
                for(int filterKey : filterBeaconKeys){
                }
            */
            LOGV(TAG, "centroid: ");
            double[] centroid = PositonFind.findPosition(mXDistance, mYDistance, mDistanceFilter.getCurrentDistancesOfAll(4));

                if (centroid != null && centroid.length >= 2) {

                    LOGV(TAG, "centroid: inner");
                    double x = (centroid[0]<0)?(-centroid[0]/5): centroid[0];
                    double y = (centroid[1]<0)?(-centroid[1]/5): centroid[1];
                    double rx = (x>mXDistance)? 1.0d :x/mXDistance;
                    double ry = (y>mYDistance)? 1.0d :y/mYDistance;
                    double yPosi = ry*mNavigationLength;
                    double xPosi = rx*mNavigationWidth;
                    mXPositionPrev = mXPosition;
                    mXPosition = (int) (xPosi*3/7 + mXPositionPrev*4/7);

                    //mXPosition = mXPosition*5;
                    mYPositionPrev = mYPosition;
                    mYPosition = (int) (yPosi*3/7 + mYPositionPrev*4/7);
                    //mYPosition = mYPosition*5;
                    return true;
                }

            }
            return false;

    }


    private boolean updatePositionByTilateration() {
        List<Integer> indicatorsids = new ArrayList<Integer>();
        for (int i = 0; i < mIndicators.length; i++) {
            if (mIndicators[i].modelBle != null) {
                indicatorsids.add(i + 1);
            }
        }
        int[] filterBeaconKeys = mDistanceFilter.getFilterKeysForTrilateration(indicatorsids);
        if (filterBeaconKeys != null && filterBeaconKeys.length >= 3) {
            if (filterBeaconKeys[0] != filterBeaconKeys[1] && filterBeaconKeys[1] != filterBeaconKeys[2]) {
                double[][] positions = new double[3][2];
                double[] distances = new double[3];
                for (int i = 0; i < 3; i++) {
                    double[] position = new double[2];
                    distances[i] = mDistanceFilter.getCurrentDistance(i);
                    position[0] = mIndicators[filterBeaconKeys[i] - 1].positionX;
                    position[1] = mIndicators[filterBeaconKeys[i] - 1].positionY;
                    positions[i] = position;
                }

                /*
                for(int filterKey : filterBeaconKeys){
                }
                */
                double[] centroid = Trilateration.findPosition(positions, distances);

                if (centroid != null && centroid.length >= 2) {

                    double x = (centroid[0]<0)?(-centroid[0]/5): centroid[0];
                    double y = (centroid[1]<1)?(-centroid[1]/5): centroid[1];
                    double rx = (x>mXDistance)? 1.0d :x/mXDistance;
                    double ry = (y>mYDistance)? 1.0d :y/mYDistance;
                    double yPosi = ry*mNavigationLength;
                    double xPosi = rx*mNavigationWidth;
                    mXPosition = (int) xPosi/5;
                    mXPosition = mXPosition*5;
                    mYPosition = (int) yPosi/5;
                    mYPosition = mYPosition*5;

                }

            } else {
                // (y-x)*r + x  ;; r:1
                /*
                int x = mIndicators[filteresult[1] - 1].mPositionY;
                int y = mIndicators[filteresult[2] - 1].mPositionY;
                int r = filteresult[0];
                int distance = ((y - x) * r) / (100 + r) + x;
                mYPositionPrev = mYPosition;
                mYPosition = distance;

                //mYPosition = distance/5;///10;
                mYPosition = mYPosition * 3 / 7 + mYPositionPrev * 4 / 7;
                LOGW(TAG, "X: " + x + " | Y: " + y + " | distance: " + distance);
                */
                //mYPosition = distance*10;

            }
            return true;
        }
        return false;
    }

    public int getPositionX() {
        return mXPosition;
    }

    public int getPositionY() {
        return mYPosition;
    }
}
