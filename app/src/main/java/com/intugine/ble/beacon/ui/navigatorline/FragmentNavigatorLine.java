package com.intugine.ble.beacon.ui.navigatorline;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.ble.BleScanHandler;
import com.intugine.ble.beacon.keys.KeyIntent;
import com.intugine.ble.beacon.model.ModelSchedule;
import com.intugine.ble.beacon.server.WebSocketClientHandler;
import com.intugine.ble.beacon.ui.ActivityApp;
import com.intugine.ble.beacon.ui.base.FragmentBase;
import com.intugine.ble.beacon.ui.navigator.adapter.AdapterScannedBeacons;
import com.intugine.ble.beacon.ui.navigator2d.DialogWifi;
import com.intugine.ble.beacon.ui.scanner.ModelBle;
import com.intugine.ble.beacon.ui.widgets.FloatingActionButtonCheckable;
import com.intugine.coalindiaclientdemo.SharedPreferenceHelper;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Calendar;

import static com.intugine.ble.beacon.util.LogUtils.LOGD;
import static com.intugine.ble.beacon.util.LogUtils.LOGI;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 22-05-2017.
 */

public class FragmentNavigatorLine extends FragmentBase implements BleScanHandler.OnBleScannedListener,
        DialogWifi.DialogWifiListener,
        WebSocketClientHandler.ServerClientHandlerListener,
        DialogCount.DialogCountListener,
        WrapperIndicator.onIndicatorClickListener, DialogRssiValue.DialogRssiListener {

    private static final String TAG = makeLogTag(FragmentNavigatorLine.class);
    //List<WrapperIndicatorView> mIndicatorViewlist;
    NavigationHandlerLine mNavigationHandler;
    private FloatingActionButtonCheckable vFab;
    private ModelBle mCurrentModelBle;
    private BleScanHandler mBleScanHandler;

    //ClientHandler mClientHandler;
    private RecyclerView vRecyclerView;
    private AdapterScannedBeacons mAdapterScannedBeacon;
    private boolean mIsEditing = true;
    private boolean isShowingBeaconList = false;
    private boolean mIsIndicatorPressed = false;
    private int mIndicatorPosition = 0;

    public static Fragment newInstance(int position, Bundle bundle) {
        FragmentNavigatorLine fragment = new FragmentNavigatorLine();
        Bundle args = new Bundle(bundle);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean setNavigationHomeAsBack() {
        return true;
    }

    @Override
    protected String getToolBarDefaultTitle() {
        return "Navigation Line";
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.navigator_line_fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mIndicatorViewlist = new ArrayList<WrapperIndicatorView>();
        mNavigationHandler = new NavigationHandlerLine(getContext());

        Activity activity = getActivity();
        if (activity != null) {
            mBleScanHandler = BleScanHandler.with(activity);
            if (mBleScanHandler != null) {
                mBleScanHandler.setOnBleScannedListener(this);
            }
            /*
            mClientHandler = ClientHandler.with(activity);
            mClientHandler.attachWifiConnectReceiver(this);
            mClientHandler.setServerClientHandlerListener(this);
            */
            try {
                WebSocketClientHandler.with(activity)
                        .setServerClientHandlerListener(FragmentNavigatorLine.this);
            } catch (Exception e) {

            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initToolbar(view, R.id.toolbar, R.drawable.ic_arrow_back_white_24dp);
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startScanning();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startDialogBleCount();
            }
        }, ModelSchedule.DELAY_UI_UPDATE_SLOW);
    }


    @Override
    public void initToolbar(View view, int toolbarId, int navigationDrawable) {
        super.initToolbar(view, toolbarId, navigationDrawable);
        vToolbar.inflateMenu(R.menu.menu_navigator_line);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_bluetooth) {
            //Intent searchIntent = new Intent(this, ResearchActivitySearch.class);
            if (isShowingBeaconList) {
                hideBeacons();
                isShowingBeaconList = false;
            } else {
                showBeacons();
                isShowingBeaconList = true;
            }
            //mHandlerAppUi.startFragmentForResult(KeyActivityFragment.FragmentId.SEARCH_KEYWORDS, new Bundle());
            return true;
        } else if (id == R.id.menu_action_wifi) {
            startDialogWifi(123);
            return true;
        }else if(id == R.id.menu_action_rssi){
            startDialogRssi(234);
        }
        return super.onMenuItemClick(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KeyIntent.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            LOGD(TAG, "bluetooth enabled");
            onBluetoothEnabled(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mBleScanHandler != null)
            mBleScanHandler.stopScanning();

        Activity activity = getActivity();
        if (activity instanceof ActivityApp) {
            BroadcastReceiver broadcastReceiver = WebSocketClientHandler.getConnectionStateReciever();
            if (broadcastReceiver != null) {
                activity.unregisterReceiver(broadcastReceiver);
            }
            //activity.unregisterReceiver(mClientHandler.getConnectionStateReciever());
        }
        //mHandler.removeCallbacksAndMessages(null);
        //pv_circular_determinate_in_out.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBleScanHandler != null)
            mBleScanHandler.startScanningForeGround();
        Activity activity = getActivity();
        if (activity instanceof ActivityApp) {
            IntentFilter filter = new IntentFilter();

            LOGI(TAG, "onResume");
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            BroadcastReceiver broadcastReceiver = WebSocketClientHandler.getConnectionStateReciever();
            if (broadcastReceiver != null) {
                LOGI(TAG, "connect broadcast receiver");
                activity.registerReceiver(broadcastReceiver, filter);
            }
        }
        //mHandler.sendEmptyMessageDelayed(MSG_START_PROGRESS, START_DELAY);
    }

    @Override
    public void onDestroy() {

        WebSocketClientHandler.disposeSocket();
        super.onDestroy();
    }

    private void initAdapter() {
        vRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterScannedBeacon = new AdapterScannedBeacons(getContext());
        vRecyclerView.setAdapter(mAdapterScannedBeacon);
        vRecyclerView.setVisibility(View.INVISIBLE);
        //mAdapter.setAdapterOrderListener(this);
    }

    private void initView(View view, Bundle savedInstanceState) {

        vFab = (FloatingActionButtonCheckable) view.findViewById(R.id.fab);
//        vFab.setImageDrawable(playToStopAnim);
        vFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (vFab.isChecked()) {
                            startNavigation();
                        } else {
                            stopNavigation();
                        }
                    }
                }, 50);

            }
        });



        //mNavigationHandler.vTvLable = (TextView) view.findViewById(R.id.navigate_fragment_lable_tv);
        mNavigationHandler.vFlNavigate = (FrameLayout) view.findViewById(R.id.navigate_fragment_indicator_container);
        mNavigationHandler.vPointer = (FrameLayout) view.findViewById(R.id.navigate_fragment_pointer);
        mNavigationHandler.vPointerRipple = (AVLoadingIndicatorView) view.findViewById(R.id.navigate_fragment_pointer_ripple);


        ViewTreeObserver viewTreeObserver = mNavigationHandler.vFlNavigate.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mNavigationHandler.vFlNavigate.getViewTreeObserver().removeOnPreDrawListener(this);
                mNavigationHandler.updateArea(mNavigationHandler.vFlNavigate.getMeasuredWidth(), mNavigationHandler.vFlNavigate.getMeasuredHeight());
                LOGD(TAG, "height: " + mNavigationHandler.mNavigationLength);
                LOGD(TAG, "width: " + mNavigationHandler.mNavigationWidth);
                if (!mIsEditing) {
                    // mNavigationHandler.initPointer();
                }
                //mAnim2DropDown = new AnimDropDown(v2IVDropDown, v2LLContainer, mHeightSection2, false, mAnimDuration);
                return true;
            }
        });

        vRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        vRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vRecyclerView.setItemViewCacheSize(0);
        initAdapter();
    }


    @Override
    public void onIndicatorReleased(int position) {
        if(mIsEditing){
            mIsIndicatorPressed = false;
            mIndicatorPosition = 0;
        }
    }

    @Override
    public void onIndicatorPressed(int position) {
        if (mIsEditing) {
            mBleScanHandler.stopScanning();
            LOGI(TAG, "clicked");
            mIsIndicatorPressed = true;
            mIndicatorPosition = position;
            mNavigationHandler.attachBeaconToIndicator(position, mAdapterScannedBeacon.modelBleList);
            startScanning();
        }

    }

    @Override
    public void onIndicatorClicked(int position) {
        /*if (mIsEditing) {
            mBleScanHandler.stopScanning();
            LOGI(TAG, "clicked");
            mNavigationHandler.attachBeaconToIndicator(position, mAdapterScannedBeacon.modelBleList);
            startScanning();
        }*/
    }



    /* ************************************************************************************
     *                                         Design
     */

    private void updateEditViews() {

        LOGW(TAG, "updateEditViews isEditing: " + mIsEditing);
        if (!mIsEditing) {
            vRecyclerView.setVisibility(View.INVISIBLE);
            //mNavigationHandler.vTvLable.setVisibility(View.VISIBLE);
            mNavigationHandler.startPointer();
        } else {
            //mNavigationHandler.vTvLable.setVisibility(View.GONE);
            mNavigationHandler.stopPointer();
        }
    }

    private void showBeaconListView() {
        if (mIsEditing) {
            vRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void hideBeaconListView() {
        vRecyclerView.setVisibility(View.INVISIBLE);
    }



    /* *****************************************************************************************8
     *                                         Pointer
     */

    public void startNavigation() {
        if (mIsEditing) {
            mIsEditing = false;
            updateEditViews();
        }
    }

    public void stopNavigation() {
        if (!mIsEditing) {
            mIsEditing = true;
            updateEditViews();
        }
    }

    public void showBeacons() {
        LOGW(TAG, "show beacon");
        showBeaconListView();
    }


    public void hideBeacons() {
        //if(!mIsEditing){
        //    mIsEditing = true;
        hideBeaconListView();
        //    showBeaconListView();
        //}
    }

    /* ******************************************************************8
                                             Server
     */

    /*
    @Override
    public void checkSocketConnection() {
        //if(!is)
        if (!mClientHandler.isSending()) {
            mClientHandler.setSending(true);
            mClientHandler.disconnectSocket();
            mClientHandler.connectSocket(this);
        }
    }

    @Override
    public void exceptionOccured() {
        mClientHandler.setSending(false);
        mClientHandler.disconnectSocket();
    }
    */


    @Override
    public String onServerSendData() {

        String positi = mNavigationHandler.getPosition();
        /*String message = (mNavigationHandler.getPositionX()*100)/mNavigationHandler.mNavigationWidth+","
                + (mNavigationHandler.getPositionY()*100)/mNavigationHandler.mNavigationLength;*/
        LOGI(TAG, "server send message: "+ positi);
        return positi;
    }

    /* *********************************************************************************************
     *                               Dialog rssi
     */




    private void startDialogRssi(int key) {
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        //b.putString(DialogPosition.B_ARG_KEY, String.valueOf(beaconIndicatorKey));
        DialogRssiValue dialogRssiValue = DialogRssiValue.newInstance(key, b);
        dialogRssiValue.setCancelable(false);
        dialogRssiValue.show(getFragmentManager(), DialogWifi.class.getSimpleName());
        dialogRssiValue.setDialogRssiValueListener(this);
    }


    @Override
    public void onDialogRssiPositiveClick(int key, int rssi) {
        if (rssi > 40) {
            mNavigationHandler.mDistanceFilter.setRSSIInitial(rssi);
        }
    }



    /* *****************************************************************************
     *                                     Dialog System
     */

    private void startDialogWifi(int id) {
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        //b.putString(DialogPosition.B_ARG_KEY, String.valueOf(beaconIndicatorKey));
        DialogWifi dialogWifi = DialogWifi.newInstance(id, b);
        dialogWifi.setCancelable(false);
        dialogWifi.show(getFragmentManager(), DialogWifi.class.getSimpleName());
        dialogWifi.setDialogWifiListener(this);
    }

    @Override
    public void onDialogWifiPositiveClick(int key, String wifiName, String deviceName, String ip) {
        Activity activity = getActivity();
        if (activity instanceof ActivityApp) {
            SharedPreferenceHelper.getInstance(activity).saveCharecteristics(
                    wifiName,
                    deviceName,
                    ip
            );

            WebSocketClientHandler.restartSocket(activity);
            //mClientHandler.toggleSending();
        }
    }

    /* *****************************************************************************
     *                                     Dialog Ble Count
     */

    private void startDialogBleCount() {
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle b = new Bundle();
        //b.putString(DialogPosition.B_ARG_KEY, String.valueOf(beaconIndicatorKey));
        DialogCount dialogCount = DialogCount.newInstance(2224, b);
        dialogCount.setCancelable(false);
        //dialogNumberPicker.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE,
        // R.style.App_DialogFragment);
        //dialogNumberPicker.setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE,
        // R.style.App_DialogFragment);
        dialogCount.show(getFragmentManager(), DialogCount.class.getSimpleName());
        dialogCount.setDialogCountListener(this);
    }


    @Override
    public void onDialogCountPositiveClick(int key, int count) {
        if(mNavigationHandler!=null){
            mNavigationHandler.initIndicator(count, this);
        }
    }




    /* **************************************************************************************
     *                                    Bluetooth works
     */


    private void restartScan() {
        //TODO work here
        //this.mAdapterScannedDevice.resetItems(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanning();
            }
        }, ModelSchedule.DELAY_UI_UPDATE_SLOW);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startScanning();
            }
        }, ModelSchedule.DELAY_UI_UPDATE_SLOW);
    }

    private void stopScanning() {
        if (mBleScanHandler != null) {
            mBleScanHandler.stopScanning();
        }
    }

    private void startScanning() {

        if (mBleScanHandler != null) {
            if (!mBleScanHandler.isBluetoothEnable()) {
                mBleScanHandler.requestBluetoothEnable(this);
            } else {
                onBluetoothEnabled(true);
            }
        }
    }

    public void onBluetoothEnabled(boolean isEnabled) {
        if (isEnabled) {
            Activity activity = getActivity();
            if (activity instanceof ActivityApp) {
                ((ActivityApp) activity).requestPermission_ACCESS_COARSE_LOCATION();
            }
        }
    }

    public void onUsesPermissionAllowed(boolean isAllowed) {
        if (isAllowed) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mBleScanHandler != null) {
                        mBleScanHandler.startScanningForeGround();
                    }
                }
            }, ModelSchedule.DELAY_UI_UPDATE_FAST);
        }
    }

    @Override
    public void onBleScanned(BluetoothDevice device, int rssi, byte[] scanRecord) {
        ModelBle modelBle = new ModelBle(device, rssi, Calendar.getInstance().getTimeInMillis());
        //if(mAdapterScannedDevice!=null){
        //    mAdapterScannedDevice.addItem(mModelBle);
        //}
        //LOGV(TAG, "ble: "+ mModelBle.rssi);
        if (device.getName() != null && device.getName().contains("Bat")) {
            if (mIsEditing && mAdapterScannedBeacon != null) {
                mAdapterScannedBeacon.checkToRemoveDeadBle();
                mAdapterScannedBeacon.addItem(modelBle);
                if(mIsIndicatorPressed){
                    mNavigationHandler.updateBeaconRssiTh(mIndicatorPosition, mAdapterScannedBeacon.modelBleList);
                }
            } else {
                mCurrentModelBle = modelBle;
                mNavigationHandler.updatePointerPosition(mCurrentModelBle);
                /*
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mNavigationHandler.updatePointerPosition(mCurrentModelBle);
                    }
                }, 20);
                */

            }

            Activity activity = getActivity();
            if (activity instanceof ActivityApp) {
                if (((ActivityApp) activity).isShowingBeaconList) {
                }
            }
        }

        //updateIndicators(mModelBle);
    }


    public void addBeaconIndicator() {

    }


}
