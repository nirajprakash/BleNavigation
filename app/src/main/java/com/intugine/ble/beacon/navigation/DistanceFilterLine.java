package com.intugine.ble.beacon.navigation;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.intugine.ble.beacon.util.LogUtils.LOGI;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;

/**
 * Created by niraj on 30-05-2017.
 */

public class DistanceFilterLine {


    private int mRssiInitial = 55;




    HashMap<Integer, FilterPoints> filterMap = new HashMap<Integer, FilterPoints>();
    HashMap<Integer, TH> rssiThMap = new HashMap<Integer, TH>();

    private int filterLength = 12;

    private double[] mCurrentAvg;

    private double[] mCurrentDistances;

    public boolean updateFilter(int beaconI, int rssi){
        FilterPoints filter = filterMap.get(beaconI);
        double rawVal = getFormattedRawVal(beaconI, rssi);
        boolean isUpdated = false;
        if(filter ==null){
            filter = new FilterPoints(rawVal, filterLength);
            filterMap.put(beaconI, filter);
            LOGW(TAG, "created  filter: "+ rawVal + " | beconeI: "+ beaconI);
            isUpdated = true;
        }else {
            isUpdated = filter.updateValue(rawVal);
            LOGW(TAG, "updated  filter: "+ rawVal + " | beconeI: "+ beaconI);

        }

        checkForDeadBeacon();
        return isUpdated;

    }

    private void checkForDeadBeacon() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        Iterator<Map.Entry<Integer, FilterPoints>> it = filterMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, FilterPoints> pair = (Map.Entry) it.next();
            FilterPoints filterPoints = pair.getValue();
            if(filterPoints!=null) {
                boolean isToUpdate = filterPoints.checkToUpdate(currentTime);
                //LOGI(TAG, pair.getKey() + " = " + pair.getValue());
                if (isToUpdate) {
                    filterPoints.updateValueDead();

                }
            }
            // avoids a ConcurrentModificationException
        }
    }



    public double[] updateFilterCurrentDistances(List<Integer> beaconeIList){
        if(beaconeIList!=null) {
            mCurrentDistances = new double[beaconeIList.size()];
            for (int i = 0; i < beaconeIList.size(); i++) {
                FilterPoints filter = filterMap.get(i+1);
                if(filter!=null){
                    mCurrentDistances[i]= filter.getLast();
                }

            }
        }
        return mCurrentDistances;
    }
    //navigation line
    // yRation, beaconeI, beaconJ
    public int[] getFilterResultMin2(List<Integer> beaconeIList){
        if(beaconeIList!=null){
            double minY1=Double.MAX_VALUE;
            double minY2=Double.MAX_VALUE;
            int i1 = 0;
            int i2 = 0;
            mCurrentAvg = new double[beaconeIList.size()];
            for (int i = 0; i < beaconeIList.size(); i++) {
                int beaconeI = beaconeIList.get(i);
                double avg = getAvg(beaconeI);
                mCurrentAvg[i] =avg;
                if(avg<=minY1){
                    minY2 = minY1;
                    minY1 = avg;
                    i2 = i1;
                    i1 = beaconeI;
                }else if(avg<=minY2){
                    minY2 =avg;
                    i2 = beaconeI;
                }
            }
            if(minY2 == Double.MAX_VALUE){
                minY2 = minY1;
                i2 = i1;
            }
            double ratio = minY1/minY2;
            ratio = ratio*100;
            LOGW(TAG,"get filter result minY1: "+ minY1 +" | minY2 | "+ minY2);

            LOGW(TAG,"get filter result: "+ ratio +" | "+ i1 + ", "+ i2 );
            return new int[]{(int) ratio, i1, i2};
        }
        return null;
    }

    private double getAvg(int beaconI){
        FilterPoints filter = filterMap.get(beaconI);
        if(filter==null){
            return Double.MAX_VALUE;
        }else {
            return filter.getAvg();
        }

    }

    private double  getFormattedRawVal(int beaconI, int rssi){
        double rssiDb = - (double) rssi;
        double rssiTh = (double)rssiThMap.get(beaconI).avg;
        rssiDb = rssiDb - rssiTh + mRssiInitial ;
        double n = 2.7d;
        double p = -12d;
        double distance =  Math.pow(10,(p+rssiDb)/(10d*n));//
        return distance;
    }
    private double  getFormattedRawVal(int rssi){
        double rssiDb = - (double) rssi;
        double n = 2.7d;
        double p = -12d;
        double distance =  Math.pow(10,(p+rssiDb)/(10d*n));//
        return distance;
    }


    /**
     *
     * n = 3.1d
     * p = -11d
     */


    public double[] getCurrentDistancesOfAll(int beaconcount) {
        if(beaconcount>0){
            double distances[] =  new double[beaconcount];
            for (int i = 1; i <= beaconcount; i++) {
                //int beaconeI = beaconcount.get(i);
                double avg = getAvg(i);
                distances[i-1] = avg;
            }
            return distances;
        }
        return null;
    }

    /* ************************************************************
     *                             tH VALUES
     */

    public void addTh(int beaconI, int rssi){
        rssiThMap.put(beaconI,new TH(-rssi));
    }

    public void updateTh(int indicatorI, int rssi) {
        TH th = rssiThMap.get(indicatorI);
        if(th!=null){
            th.add(-rssi);

            LOGI(TAG, "rssi: "+ rssi +", rssith: "+ th.avg+ ",  indicator: "+ indicatorI);
        }
    }


    public void setRSSIInitial(int rssiInitial){
        mRssiInitial = rssiInitial;
    }

    public double[] getFilterAvgs() {
        return mCurrentAvg;
    }


    class TH{
        int avg = 0 ;
        int count = 0;

        public TH(int pAvg) {
            avg = pAvg;
            count =1;
        }

        public void add(int value) {
            avg = (count*avg+value)/(count+1);
            count = count+1;
        }
    }

}

