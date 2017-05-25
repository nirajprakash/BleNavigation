package com.intugine.ble.beacon.navigation;

import com.intugine.ble.beacon.trilateration.LinearLeastSquaresSolver;
import com.intugine.ble.beacon.trilateration.TrilaterationFunction;

import java.util.Arrays;

import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 18-05-2017.
 */

public class Trilateration {

    private static final String TAG = makeLogTag(Trilateration.class);
    public static double []findPosition(double[][] position, double [] distance){

        //double[][] positions = new double[][] { { 5.0, -6.0 }, { 13.0, -15.0 }, { 21.0, -3.0 }, { 12.4, -21.2 } };
        //double[] distances = new double[] { 8.06, 13.97, 23.32, 15.31 };

        if(position==null || position.length<3  || position[0].length<2){
            return null;
        }
        if(distance==null || distance.length<3){
            return null;
        }

        /*for (int i = 0; i < distance.length; i++) {
            LOGW(TAG, "position: "+ Arrays.toString(position[i]) +
                    " distance: "+ distance[i]);
        }*/
        LinearLeastSquaresSolver solver = new LinearLeastSquaresSolver(new TrilaterationFunction(position, distance));
        //LeastSquaresOptimizer.Optimum optimum = solver.solve();

// the answer
        //double[] centroid = optimum.getPoint().toArray();

        double[] centroid = solver.solve().toArray();

        for (int i = 0; i < distance.length; i++) {
            LOGW(TAG, "position: "+ Arrays.toString(position[i]) +
                    " distance: "+ distance[i]);
        }

        LOGW(TAG, " centroid: "+ Arrays.toString(centroid));




// error and geometry information; may throw SingularMatrixException depending the threshold argument provided
        //RealVector standardDeviation = optimum.getSigma(0);
        //RealMatrix covarianceMatrix = optimum.getCovariances(0);
        return centroid;
    }


    public static double [] findPosition(){
        return null;
    }
}
