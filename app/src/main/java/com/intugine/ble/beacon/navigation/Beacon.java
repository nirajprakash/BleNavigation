package com.intugine.ble.beacon.navigation;

/**
 * Created by niraj on 18-05-2017.
 */

public class Beacon {

    String name;
    int id;
    double x;
    double y;

    public Beacon(String pName, double pX, double pY){
        name = pName;
        //id = pId;
        x = pX;
        y = pY;
    }

}
