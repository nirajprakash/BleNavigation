package com.intugine.ble.beacon;

import android.app.Application;
import android.support.multidex.MultiDex;

/**
 * Created by niraj on 05-05-2017.
 */

public class ApplicationIntugine extends Application{




    /*
    public DataApp getDataApp() {
        return mDataApp;
    }

    public void setDataApp(DataApp dataApp) {
        this.mDataApp = mDataApp;
    }
*/


    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        }
}
