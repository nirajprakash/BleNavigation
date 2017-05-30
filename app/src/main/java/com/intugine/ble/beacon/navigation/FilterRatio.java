package com.intugine.ble.beacon.navigation;

/**
 * Created by niraj on 30-05-2017.
 */

public class FilterRatio {

    int mRatio;
    int mPrevRatio =100;
    final int step;

    int mKey1;
    int mKey2;


    public FilterRatio(int step) {
        this.step = step;
    }

    public int getFilteredRatio(int pRatio, int pkey1, int pkey2){
    //    if(ratio)
        int key1;
        int key2;
        boolean isOposite= false;
        if(pkey1>pkey2){
            isOposite =true;
            key1 = pkey2;
            key2 = pkey1;
        }else {
            key1 = pkey1;
            key2 = pkey2;
        }

        if(key1 == mKey1 && key2== mKey2){
            if(mPrevRatio != 100){
                int ratio;
                if(isOposite) {
                   ratio  = 100-pRatio;
                }else {
                   ratio = pRatio;
                }

                int diff = mPrevRatio-ratio;
                if((diff/step)!=0){
                    diff = (diff<0)?(-step): step;
                }
                mRatio = ratio + diff;
                mPrevRatio = mRatio;
            }
        }else {
            mKey1 = key1;
            mKey2 = key2;
            mRatio = isOposite?(100-pRatio):pRatio;
            mPrevRatio = mRatio;

        }

        if(isOposite){
            return 100-mRatio;
        }else {
            return mRatio;
        }
    }


}
