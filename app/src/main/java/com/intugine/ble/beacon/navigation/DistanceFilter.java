package com.intugine.ble.beacon.navigation;

import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.intugine.ble.beacon.util.LogUtils.LOGI;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;

/**
 * Created by niraj on 06-05-2017.
 */

public class DistanceFilter {

    private int mRssiInitial = 60;

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


    HashMap<Integer, double[]> filterMap = new HashMap<Integer, double[]>();
    HashMap<Integer, TH> rssiThMap = new HashMap<Integer, TH>();

    private int filterLength = 10;
    private double[] distancesT3 = new double[3];




    public boolean updateFilter(int beaconeI, int rssi){
        double[] filter = filterMap.get(beaconeI);
        //TODO toggle
        //double rawVal = getFormattedRawVal(rssi);
        double rawVal = getFormattedRawVal(beaconeI, rssi);
        //LOGW(TAG, "Updated  filter: "+ rawVal + " | beconeI: "+ beaconeI);

        if(filter==null){
            filter = new double[filterLength];
            for (int i = 0; i < filter.length; i++) {
                filter[i] = rawVal;
            }
            filterMap.put(beaconeI, filter);
            LOGW(TAG, "Updated  filter: "+ rawVal + " | beconeI: "+ beaconeI);

            return true;
            //return rawVal;
        }else {
            double avg = rawVal;
            for(int i=1;i<filter.length;i++){
                avg+=filter[i];
            }
            avg =avg/filterLength;
            //double avg = (filter[1] +filter[2] + filter[3]+ rawVal)/4;
            if(filter[filterLength-1]< rawVal && rawVal<2.5*filter[filterLength-1]){
                for(int i=0;i<filter.length-1; i++){
                    filter[i] = filter[i+1];
                }
                //filter[0] = filter[1];
                //filter[1] = filter[2];
                //filter[2] = filter[3];
                filter[filterLength-1] = rawVal;
                LOGW(TAG, "Updated  filter: "+ rawVal + " | beconeI: "+ beaconeI);

                return true;
            }else if(filter[filterLength-1]>rawVal){
                for(int i=0;i<filter.length-1; i++){
                    filter[i] = filter[i+1];
                }
                filter[filterLength-1] = rawVal;

//                filter[0] = filter[1];
//                filter[1] = filter[2];
//                filter[2] = filter[3];
//                filter[3] = rawVal;
//
                LOGW(TAG, "Updated  filter: "+ rawVal + " | beconeI: "+ beaconeI);
                return true;
            }else {
                return false;
            }

            //return avg;

        }
    }

    public double getCurrentDistance(int index){
        if(index< distancesT3.length ){
            return distancesT3[index];
        }

        return 0;
    }



    public int[] getFilterKeysForTrilateration(List<Integer> beaconKeyList){
        if(beaconKeyList!=null){
            double minD1=Double.MAX_VALUE;
            double minD2=Double.MAX_VALUE;
            double minD3=Double.MAX_VALUE;
            int i1 = 0;
            int i2 = 0;
            int i3 =0;
            for (int i = 0; i < beaconKeyList.size(); i++) {
                int beaconeI = beaconKeyList.get(i);
                double avg = getAvg(beaconeI);
                if(avg<minD1){
                    minD3 = minD2;
                    minD2 = minD1;
                    minD1 = avg;
                    i3 = i2;
                    i2 = i1;
                    i1 = beaconeI;
                }else if(avg>=minD1 && avg<minD2){
                    minD3 = minD2;
                    minD2 =avg;
                    i3 = i2;
                    i2 = beaconeI;
                }else if(avg>minD2 && avg<minD3){
                    minD3 =avg;
                    i3 = beaconeI;
                }
            }
            if(minD2 == Double.MAX_VALUE){
                minD2 = minD1;
                i2 = i1;
            }
            if(minD3 == Double.MAX_VALUE){
                minD3 = minD2;
                i3 =i2;
            }
            distancesT3[0] = minD1;
            distancesT3[1] = minD2;
            distancesT3[2] = minD3;
            //double ratio = minD1/minD2;
            //ratio = ratio*100;
            LOGW(TAG,"get filter result minD1: "+ minD1 +" | minD2 | "+ minD2 + " | minD3 | "+ minD3);

            LOGW(TAG,"get filter result: "+ i1 + ", "+ i2 +", "+ i3);
            return new int[]{ i1, i2, i3};
        }
        return null;
    }


    //navigation line
    // yRation, beaconeI, beaconJ
    public int[] getFilterResultMin2(List<Integer> beaconeIList){
        if(beaconeIList!=null){
            double minY1=Double.MAX_VALUE;
            double minY2=Double.MAX_VALUE;
            int i1 = 0;
            int i2 = 0;
            for (int i = 0; i < beaconeIList.size(); i++) {
                int beaconeI = beaconeIList.get(i);
                double avg = getAvg(beaconeI);
                if(avg<minY1){
                    minY2 = minY1;
                    minY1 = avg;
                    i2 = i1;
                    i1 = beaconeI;
                }else if(avg<minY2){
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

    private double getAvg(int beaconeI){
        double[] filter = filterMap.get(beaconeI);

        if(filter==null){
            return -1;
            //return rawVal;
        }else {
            double avg = 0 ;
            for (int i=0;i<filter.length; i++){
                avg +=filter[i];
            }
            return avg / filterLength;
        }
    }

    /*
    private int getFormattedRawVal(int beaconI, int rssi) {
        int x = -rssi;
        int rssiTh= rssiThMap.get(beaconI);

        x= x-rssiTh+ 10;
        return (x <= 0) ? 0 : x;
    }*/

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
}
