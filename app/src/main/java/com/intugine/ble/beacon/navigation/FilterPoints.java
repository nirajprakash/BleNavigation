package com.intugine.ble.beacon.navigation;

import java.util.Calendar;

/**
 * Created by niraj on 30-05-2017.
 */

public class FilterPoints {

    double[] x;
    long lastUpdateTime;
    int filterLength = 0;

    public FilterPoints(double rawVal, int filterLength) {
        this.filterLength = filterLength;
        x= new double[filterLength];
        for (int i = 0; i < x.length; i++) {
            x[i] = rawVal;
        }
        lastUpdateTime = Calendar.getInstance().getTimeInMillis();

    }

    public boolean updateValueDead() {
        //lastUpdateTime = Calendar.getInstance().getTimeInMillis();
//        double avg = 0;
//        for(int i=1;i<x.length;i++){
//            avg+=x[i];
//        }
//        avg =avg/filterLength;
        double difference = x[filterLength-1]-x[filterLength-2];
        if(difference==0){
            difference=10;
        }


        difference = (difference<0)? (-difference): difference;

        double newVal = difference+x[filterLength-1];
        return updateValue(newVal);

    }

    public boolean updateValue(double rawVal) {
        lastUpdateTime = Calendar.getInstance().getTimeInMillis();
            /*double avg = rawVal;
            for(int i=1;i<x.length;i++){
                avg+=x[i];
            }
            avg =avg/filterLength;*/
        //double avg = (filter[1] +filter[2] + filter[3]+ rawVal)/4;

        for(int i=0;i<x.length-1; i++){
            x[i] = x[i+1];
        }
        x[filterLength-1] = rawVal;
        /*

        if(x[filterLength-1]< rawVal && rawVal<4*x[filterLength-1]){
            for(int i=0;i<x.length-1; i++){
                x[i] = x[i+1];
            }
            x[filterLength-1] = rawVal;

            return true;
        }else if(x[filterLength-1]>rawVal){
            for(int i=0;i<x.length-1; i++){
                x[i] = x[i+1];
            }
            x[filterLength-1] = rawVal;
            return true;
        }else {
            return false;
        }*/
        return true;

    }

    public boolean checkToUpdate(long currentTime) {
        long timeDelta = currentTime - this.lastUpdateTime;
        //LOGV(TAG, "time Delta: "+ timeDelta);
        if((timeDelta)>3000){
            //LOGI(TAG, "time Delta: "+ timeDelta);
            return true;
        }
        return false;
    }


    public double getAvg() {
        double avg = 0 ;
        for (int i=0;i<x.length; i++){
            avg +=x[i];
        }
        return avg / filterLength;
    }

    public double getLast() {
        return x[filterLength - 1];
    }
}
