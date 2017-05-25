package com.intugine.ble.beacon.navigation;

import java.util.Arrays;

import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 21-05-2017.
 */

public class PositonFind {

    private static final String TAG = makeLogTag(PositonFind.class);

    public static double[] findPosition(double width,double height, double [] distance){

        // 1 2 3 4  top right to left and thent left bottom
        //double[][] positions = new double[][] { { 5.0, -6.0 }, { 13.0, -15.0 }, { 21.0, -3.0 }, { 12.4, -21.2 } };
        //double[] distances = new double[] { 8.06, 13.97, 23.32, 15.31 };

        if(width<=0 || height<=0){
            return null;
        }
        if(distance==null || distance.length<4){
            return null;
        }

        /*for (int i = 0; i < distance.length; i++) {
            LOGW(TAG, "position: "+ Arrays.toString(position[i]) +
                    " distance: "+ distance[i]);
        }*/
        double x1 = weightedDistance(distance[0], distance[2]);
        double x2 = weightedDistance(distance[1],distance[3]);
        double y2 = weightedDistance(distance[2] , distance[3]);
        double y1 = weightedDistance(distance[0] , distance[1]);
        double rx = weightedRatio(x1,x2);//x1/(x1+x2);
        double ry = weightedRatio(y1,y2);//y1/(y1+y2);

        double position[] = new double[2];
        position[0] = rx*width;
        position[1] = ry*height;

        LOGW(TAG, " distances: "+ Arrays.toString(distance));
        LOGW(TAG, " width: "+ width + " height: "+ height);
        LOGW(TAG, " centroid: "+ Arrays.toString(position));
        //error and geometry information; may throw SingularMatrixException depending the threshold argument provided
        //RealVector standardDeviation = optimum.getSigma(0);
        //RealMatrix covarianceMatrix = optimum.getCovariances(0);
        return position;
    }

    public static double weightedDistance(double distance1, double distance2){
        double factor = 0;
        if(distance1>distance2){
            factor = distance1/distance2;
        }else {
            factor = distance2/distance1;
        }

        if(factor >1.5){
            return (distance1>distance2)?distance2: distance1;
        }
        return (distance1 + distance2)/2;
    }


    public static double weightedRatio(double distance1, double distance2){
        double factor = 0;
        if(distance1>distance2){
            factor = distance1/distance2;
        }else {
            factor = distance2/distance1;
        }

        double ratio = distance1/( distance1 + distance2);

        if(factor >1.5 && factor < 3){
            if(distance1>distance2){
                ratio = (distance1*Math.pow(factor,2))/(distance1*Math.pow(factor,2) + distance2);
            }else {
                ratio = (distance1)/(distance1 + distance2*Math.pow(factor,2));
            }
        }else if(factor>0){
            if(distance1>distance2){
                ratio = (distance1*factor)/(distance1*factor + distance2);
            }else {
                ratio = (distance1)/(distance1 + distance2*factor);
            }
        }
        return ratio;
    }
}