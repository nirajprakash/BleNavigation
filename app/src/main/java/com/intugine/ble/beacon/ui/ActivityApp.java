package com.intugine.ble.beacon.ui;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.keys.KeyFragment;
import com.intugine.ble.beacon.keys.KeyManifest;
import com.intugine.ble.beacon.model.ModelSchedule;
import com.intugine.ble.beacon.server.ClientHandler;
import com.intugine.ble.beacon.ui.base.ActivityBase;
import com.intugine.ble.beacon.ui.navigator.FragmentNavigator;
import com.intugine.ble.beacon.ui.navigator2d.FragmentNavigator2d;
import com.intugine.ble.beacon.ui.navigatorline.FragmentNavigatorLine;
import com.intugine.ble.beacon.ui.viewer.FragmentViewer;
import com.intugine.ble.beacon.ui.widgets.FloatingActionButtonCheckable;
import com.intugine.coalindiaserverdemo.DataInterface;
import com.intugine.coalindiaserverdemo.MyWsServer;

import java.io.IOException;

import static com.intugine.ble.beacon.util.LogUtils.LOGE;
import static com.intugine.ble.beacon.util.LogUtils.LOGV;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 05-05-2017.
 */

public class ActivityApp extends ActivityBase implements DataInterface {

    private static final String TAG = makeLogTag(ActivityApp.class);
    private FloatingActionButtonCheckable vFab;
    public boolean isShowingBeaconList =false;

    /*
    if (!mIsEditing) {
                mNavigationHandler.updatePointerPosition(mModelBle);
            }
            Activity activity = getActivity();
            if(activity instanceof ActivityApp){
                if(((ActivityApp) activity).isShowingBeaconList && mAdapterScannedBeacon != null){
                    mAdapterScannedBeacon.checkToRemoveDeadBle();
                    mAdapterScannedBeacon.addItem(mModelBle);
                }
            }
     */

    //private AnimatedVectorDrawableCompat playToStopAnim;
    //private AnimatedVectorDrawableCompat stopToPlayAnim;



    private MyWsServer wsServer;
    private String mServerData;
    private String mServerIP;

    private ClientHandler mClientHandler;

    @Override
    public void onCreateBeforeViewSet(Bundle savedInstanceState) {

    }

    @Override
    public void onCreateAfterViewSet(Bundle saveInstanceState) {
        initToolbar(saveInstanceState, R.id.toolbar);
        initView(saveInstanceState);
        //startFragmentNavigation2d();
    }


    @Override
    public int getLayoutResourceId() {
        return R.layout.app_activity;
    }

    @Override
    public int getMainFrameContentId() {
        return R.id.frame_content;
    }

    @Override
    public String getToolBarDefaultTitle() {
        return "Navigation Demo";
    }

    @Override
    public void restartActivity() {

    }

    @Override
    protected boolean setNavigationHomeAsBack() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_bluetooth) {
            //Intent searchIntent = new Intent(this, ResearchActivitySearch.class);
            if(isShowingBeaconList){
                hideBeaaconList();
                isShowingBeaconList = false;
            }else {
                showBeaconsList();
                isShowingBeaconList = true;
            }
            //mHandlerAppUi.startFragmentForResult(KeyActivityFragment.FragmentId.SEARCH_KEYWORDS, new Bundle());
            return true;
        }/*else if (id == R.id.menu_action_add) {
            //Intent searchIntent = new Intent(this, ResearchActivitySearch.class);
            addBeacone();
            //mHandlerAppUi.startFragmentForResult(KeyActivityFragment.FragmentId.SEARCH_KEYWORDS, new Bundle());
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }



    private void initView(Bundle saveInstanceState) {
        findViewById(R.id.action_btn_fl_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentNavigation();
            }
        });

        findViewById(R.id.action_btn_fl_navigation2d).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentNavigation2d();
            }
        });


        findViewById(R.id.action_btn_fl_navigationLine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentNavigationLine();
            }
        });

        findViewById(R.id.action_btn_fl_viewer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragmentViewer();
            }
        });



//        playToStopAnim = AnimatedVectorDrawableCompat.create(this,
//                R.drawable.animate_vector_play_to_stop);
//        stopToPlayAnim = AnimatedVectorDrawableCompat.create(this,
//                R.drawable.animate_vector_stop_to_play);

//        vFab= (FloatingActionButtonCheckable) findViewById(R.id.fab);
//        vFab.setImageDrawable(playToStopAnim);
//        vFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                /*
//                vFab.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        changeButtonIcon();
//                    }
//                });*/
//                if(vFab.isChecked()){
//                    startNavigation();
//                }else{
//                    stopNavigation();
//                }
//            }
//        });
    }

    /* ****************************************************************
     *                                Fab
     */


    /* ******************************************************************
     *                             Fraagment
     */

    private void startFragmentViewer() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(getMainFrameContentId(),
                FragmentViewer.newInstance(KeyFragment.VIEWER, new Bundle()), FragmentViewer.class.getSimpleName())
                .addToBackStack(FragmentViewer.class.getSimpleName()).commit();


    }

    private void startFragmentNavigation2d() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.navigation_drawer_layout,
                FragmentNavigator2d.newInstance(KeyFragment.NAVIGATOR_2D, new Bundle()), FragmentNavigator2d.class.getSimpleName())
                .addToBackStack(FragmentNavigator2d.class.getSimpleName()).commit();
        //fragmentPosition = 0;
    }


    private void startFragmentNavigationLine() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.navigation_drawer_layout,
                FragmentNavigatorLine.newInstance(KeyFragment.NAVIGATOR_LINE, new Bundle()), FragmentNavigatorLine.class.getSimpleName())
                .addToBackStack(FragmentNavigatorLine.class.getSimpleName()).commit();
        //fragmentPosition = 0;
    }

    private void startFragmentNavigation() {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.navigation_drawer_layout,
                FragmentNavigator.newInstance(KeyFragment.NAVIGATOR, new Bundle()), FragmentNavigator.class.getSimpleName())
                .addToBackStack(FragmentNavigator.class.getSimpleName()).commit();

    }

    public Fragment getTopFragment() {
        int count = mFragmentManager.getBackStackEntryCount();
        if (count > 0) {
            String fName = mFragmentManager.getBackStackEntryAt(count - 1).getName();
            //mFragmentStackList.remove(fName);
            LOGV(TAG, "back stack: " + fName);
            if (fName != null) {
                return mFragmentManager.findFragmentByTag(fName);
            }
        }

        return mFragmentManager
                .findFragmentByTag(FragmentNavigator2d.class.getSimpleName());
    }

    /* ***************************************************************************************88
     *                                        Navigation
     */

    public void startNavigation(){
        Fragment fragment = getTopFragment();
        if(fragment instanceof FragmentNavigator){
            LOGW(TAG, "start Navigation");
            ((FragmentNavigator)fragment).startNavigation();
        }else if(fragment instanceof FragmentNavigator2d){
            LOGW(TAG, "start Navigation");
            ((FragmentNavigator2d) fragment).startNavigation();
        }
    }

    public void stopNavigation(){
        Fragment fragment = getTopFragment();
        if(fragment instanceof FragmentNavigator){
            LOGW(TAG, "stop Navigation");
            ((FragmentNavigator)fragment).stopNavigation();
        }else if(fragment instanceof FragmentNavigator2d){
            LOGW(TAG, "stop Navigation");
            ((FragmentNavigator2d) fragment).stopNavigation();
        }
    }

    public void showBeaconsList(){
        Fragment fragment = getTopFragment();
        if(fragment instanceof FragmentNavigator){
            ((FragmentNavigator)fragment).showBeacons();
            //vFab.setChecked(false);
        }else if(fragment instanceof FragmentNavigator2d){
            ((FragmentNavigator2d)fragment).showBeacons();
        }
    }


    private void hideBeaaconList() {
        Fragment fragment = getTopFragment();
        if(fragment instanceof FragmentNavigator){
            ((FragmentNavigator)fragment).hideBeacons();
        }else if(fragment instanceof FragmentNavigator2d){
            ((FragmentNavigator2d)fragment).hideBeacons();
        }
    }


    private void addBeacone() {
        Fragment fragment = getTopFragment();
        if(fragment instanceof FragmentNavigator2d){
            ((FragmentNavigator2d)fragment).addBeaconIndicator();
        }

    }

    /* ****************************************************************************************888
     *                                      Server work
     *
     */

    public void initServer() {
        if(wsServer ==null){
            wsServer = new MyWsServer(getIP(), 8080, this);
        }

    }

    public void startServer(){
       initServer();
        try {
            wsServer.start();
        } catch (IOException e) {
            LOGE(TAG, e.toString());
        }
    }


    public void stopServer(){

        wsServer.stop();

    }


    private String getIP() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wm!=null) {
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            mServerIP = ip;
            Log.d("hostname", ip);
            return ip;
        }
        return null;
    }


    @Override
    public void showData(String data) {
        mServerData = data;
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Fragment fragment = getTopFragment();
                              if(fragment instanceof FragmentViewer){
                                  ((FragmentViewer)fragment).onServerMessageReceived(mServerData);
                              }
                          }
                      }
        );
    }


    /* ********************************************************************************************
     *                                       Permission
     */




    public void requestPermission_ACCESS_COARSE_LOCATION() {
        ListenerPermissionChange listenerPermissionChange = new ListenerPermissionChange() {
            @Override
            public void onPermissionResult(int reqKey, final boolean isAllowed) {
                LOGW(TAG, "permissionResult: " + reqKey);
                if (reqKey == KeyManifest.UsesPermission.GPS_1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onRequestPermissionResult_ACCESS_COARSE_LOCATION(isAllowed);
                        }
                    }, ModelSchedule.DELAY_UI_UPDATE_FAST);
                }
            }
        };

        if (checkForPermission(KeyManifest.UsesPermission.GPS_1)) {
            onRequestPermissionResult_ACCESS_COARSE_LOCATION(true);
        } else {

            requestForPermission(KeyManifest.UsesPermission.GPS_1, listenerPermissionChange);

        }
    }

    private void onRequestPermissionResult_ACCESS_COARSE_LOCATION(boolean isAllow) {
        LOGV(TAG, "called ACCESS_COARSE result");
        Fragment fragment = getTopFragment();
        if(fragment instanceof FragmentNavigator){
            LOGV(TAG, "called ACCESS_COARSE result for framgnet");
            ((FragmentNavigator) fragment).onUsesPermissionAllowed(isAllow);
        }else if(fragment instanceof FragmentNavigator2d){
            LOGV(TAG, "called ACCESS_COARSE result for framgnet");
            ((FragmentNavigator2d) fragment).onUsesPermissionAllowed(isAllow);
        }else if(fragment instanceof FragmentNavigatorLine){
            LOGV(TAG, "called ACCESS_COARSE result for framgnet");
            ((FragmentNavigatorLine) fragment).onUsesPermissionAllowed(isAllow);
        }
    }


    public String getServerIp() {
        return mServerIP;
    }
}
