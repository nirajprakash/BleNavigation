package com.intugine.ble.beacon.ui.navigator;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.navigation.DistanceFilter;
import com.intugine.ble.beacon.ui.scanner.ModelBle;
import com.intugine.ble.beacon.utils.UtilsResource;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;

/**
 * Created by niraj on 06-05-2017.
 */

public class NavigationHandler {


    public int mNavigationLength = 200;
    public int mNavigationWidth;

    private NavigationIndicator[] mIndicators;
    private Context mContext;

    //private View mPointerView;
    public FrameLayout vFlNavigate;
    //public TextView vTvLable;
    private int mYPosition = 100;
    private int mYPositionPrev = 100;
    DistanceFilter mDistanceFilter;
    public FrameLayout vPointer;

    public AVLoadingIndicatorView vPointerRipple;


    public NavigationHandler(Context context) {
        this.mContext = context;
        mDistanceFilter = new DistanceFilter();
        mIndicators = new NavigationIndicator[2];
    }
    public void addViews(ImageView view, int indicatorI){
        if(mIndicators[indicatorI-1] == null){
            mIndicators[indicatorI-1] = new NavigationIndicator(view, mContext);
        }
    }

    public void updateArea(int measuredWidth, int measuredHeight) {
        mNavigationLength = measuredHeight - UtilsResource.getResourceDimenValue(mContext, R.dimen.length_72);
        mNavigationWidth = measuredWidth;
        if(mIndicators.length>1){

            for (int i = 0; i < mIndicators.length; i++) {
                mIndicators[i].positionY = i*measuredHeight/(mIndicators.length-1);
            }
        }else{
            mIndicators[0].positionY= 0;
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
                                && mIndicators[i].modelBle!=null
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
            if (modelBleMin != null && mIndicators[indicatorI-1]!=null) {
                mIndicators[indicatorI - 1].updateModelBleOnEdit(modelBleMin, mContext);//.mModelBle =modelBleMin;
                mDistanceFilter.addTh(indicatorI, modelBleMin.rssi);
                LOGW(TAG, "Added indicator at: "+ indicatorI+ " |  name: "+ modelBleMin.bluetoothDevice.getName());

                //updateIndicatorColor(mIndicators[indicatorI - 1]);
            }
        }
    }

    public void resetIndicators(){

        for (NavigationIndicator indicator :mIndicators) {
            indicator.resetColor(mContext);
            indicator.modelBle = null;
        }

    }

    public void updatePointerPosition(ModelBle modelBle){

        boolean b= updateIndicatorBle(modelBle);

        //LOGW(TAG, "Updated Indicator: "+ b);


        boolean isYPositionUpdated = false;
        if(b){
            isYPositionUpdated = updateYPosition();

            LOGW(TAG, "Updated Y Position: "+ isYPositionUpdated);

        }
        if(isYPositionUpdated) {
//            if (mPointerView == null) {
//                initPointer();
//            }
            if(vPointer!=null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vPointer.getLayoutParams();
                //Replace the RelativeLayout with your myView parent layout
                //layoutParams.leftMargin = mNavigationWidth / 2;
                layoutParams.topMargin = mYPosition;
                vPointer.setLayoutParams(layoutParams);
                //vTvLable.setText("| PosiY: " + mYPosition);
                vFlNavigate.invalidate();
            }
        }

    }

    private boolean updateIndicatorBle(ModelBle modelBle) {
        for (int i = 0; i < mIndicators.length; i++) {
            if(mIndicators[i].modelBle!=null && mIndicators[i].modelBle.equals(modelBle)){
                boolean b = mDistanceFilter.updateFilter(i+1, modelBle.rssi);
                if(b){
                    mIndicators[i].updateModelBle(modelBle);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean updateYPosition(){
        List<Integer> indicatorsids = new ArrayList<Integer>();
        for (int i = 0; i < mIndicators.length; i++) {
            if(mIndicators[i].modelBle!=null){
                indicatorsids.add(i+1);
            }
        }
        int [] filteresult = mDistanceFilter.getFilterResultMin2(indicatorsids);
        if(filteresult!=null){
            if(filteresult[1] == filteresult[2]){
                mYPosition = mIndicators[filteresult[1]-1].positionY;

            }else {
                // (y-x)*r + x  ;; r:1
                int x = mIndicators[filteresult[1]-1].positionY;
                int y = mIndicators[filteresult[2]-1].positionY;
                int r = filteresult[0];
                int distance = ((y-x)*r)/(100+r) + x;
                mYPositionPrev = mYPosition;
                mYPosition = distance;

                //mYPosition = distance/5;///10;
                mYPosition = mYPosition*3/7 + mYPositionPrev*4/7;
                LOGW(TAG, "X: "+ x+ " | Y: " + y + " | distance: "+ distance);
                //mYPosition = distance*10;

            }
            return true;
        }
        return false;
    }


    public void startPointer() {

        if(vPointer!=null){
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
        if(vPointer!=null){
            vPointer.setVisibility(View.INVISIBLE);
            vPointerRipple.hide();
            //vPointer.setVisibility(View.GONE);
        }
    }

    public int getPositionX() {
        return mNavigationWidth/2;
    }

    public int getPositionY() {
        return mYPosition;
    }

    /*

    public void initPointer(){
        if(vFlNavigate!=null) {
            if(mPointerView==null){
                mPointerView = LayoutInflater.from(mContext).inflate(R.layout.indicator_blue,
                        vFlNavigate, false);
            }
        }
    }
    */

}
