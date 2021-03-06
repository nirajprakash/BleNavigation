package com.intugine.ble.beacon.ui.navigator;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import android.widget.ImageView;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.intugine.ble.beacon.util.LogUtils.LOGD;
import static com.intugine.ble.beacon.util.LogUtils.LOGV;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 05-05-2017.
 */

public class FragmentNavigator extends FragmentBase implements BleScanHandler.OnBleScannedListener, WebSocketClientHandler.ServerClientHandlerListener, DialogWifi.DialogWifiListener {

    private static final String TAG = makeLogTag(FragmentNavigator.class);

    public static Fragment newInstance(int position, Bundle bundle) {
        FragmentNavigator fragment = new FragmentNavigator();
        Bundle args = new Bundle(bundle);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }


    List<WrapperIndicatorView> mIndicatorViewlist;
    NavigationHandler mNavigationHandler;
    private FloatingActionButtonCheckable vFab;


    private BleScanHandler mBleScanHandler;
    private RecyclerView vRecyclerView;
    private AdapterScannedBeacons mAdapterScannedBeacon;

    private boolean mIsEditing = true;

    private boolean isShowingBeaconList = false;

    @Override
    public boolean setNavigationHomeAsBack() {
        return false;
    }

    @Override
    protected String getToolBarDefaultTitle() {
        return "Navigation";
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.navigate_fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIndicatorViewlist = new ArrayList<WrapperIndicatorView>();
        mNavigationHandler = new NavigationHandler(getContext());

        Activity activity = getActivity();
        if(activity!=null){
            mBleScanHandler = BleScanHandler.with(activity);
            if(mBleScanHandler!=null){
                mBleScanHandler.setOnBleScannedListener(this);
            }
            WebSocketClientHandler.with(activity).setServerClientHandlerListener(this);
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
    }

    @Override
    public void initToolbar(View view, int toolbarId, int navigationDrawable) {
        super.initToolbar(view, toolbarId, navigationDrawable);
        vToolbar.inflateMenu(R.menu.menu_wireless);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id =item.getItemId();
        if (id == R.id.menu_action_bluetooth) {
            //Intent searchIntent = new Intent(this, ResearchActivitySearch.class);
            if(isShowingBeaconList){
                hideBeacons();
            }else {
                showBeacons();
            }
            //mHandlerAppUi.startFragmentForResult(KeyActivityFragment.FragmentId.SEARCH_KEYWORDS, new Bundle());
            return true;
        }else if (id == R.id.menu_action_wifi) {
            //Intent searchIntent = new Intent(this, ResearchActivitySearch.class);
            /*if(mClientHandler.isSending()){
                mClientHandler.setSending(false);
                mClientHandler.disconnectSocket();
            }else{*/
            startDialogWifi(123);
            //}
            //mHandlerAppUi.startFragmentForResult(KeyActivityFragment.FragmentId.SEARCH_KEYWORDS, new Bundle());
            return true;
        }
        return super.onMenuItemClick(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == KeyIntent.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK){
            LOGD(TAG,"bluetooth enabled");
            onBluetoothEnabled(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mBleScanHandler!=null)
            mBleScanHandler.stopScanning();

        //mHandler.removeCallbacksAndMessages(null);
        //pv_circular_determinate_in_out.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mBleScanHandler!=null)
            mBleScanHandler.startScanningForeGround();

        //mHandler.sendEmptyMessageDelayed(MSG_START_PROGRESS, START_DELAY);
    }


    private void initView(View view, Bundle savedInstanceState) {

        vFab= (FloatingActionButtonCheckable) view.findViewById(R.id.fab);
//        vFab.setImageDrawable(playToStopAnim);
        vFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                vFab.post(new Runnable() {
                    @Override
                    public void run() {
                        changeButtonIcon();
                    }
                });*/
                if(vFab.isChecked()){
                    startNavigation();
                }else{
                    stopNavigation();
                }
            }
        });
        /*final View vBtnStart = view.findViewById(R.id.action_btn_fl_start);
        final View vBtnStop = view.findViewById(R.id.action_btn_fl_stop);
        vBtnStop.setVisibility(View.GONE);
        vBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vBtnStart.setVisibility(View.GONE);
                vBtnStop.setVisibility(View.VISIBLE);
                mNavigationHandler.startPointer();

            }
        });
        vBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsEditing){
                    mIsEditing = true;
                    updateEditViews();
                }
                vBtnStart.setVisibility(View.VISIBLE);
                vBtnStop.setVisibility(View.GONE);
                mNavigationHandler.stopPointer();
            }
        });*/
        mNavigationHandler.addViews((ImageView) view.findViewById(R.id.navigate_fragment_indicator_image_1),  1);
        mNavigationHandler.addViews((ImageView) view.findViewById(R.id.navigate_fragment_indicator_image_2),  2);
        //mNavigationHandler.addViews((ImageView) view.findViewById(R.id.navigate_fragment_indicator_image_3),  3);
        //mNavigationHandler.addViews((ImageView) view.findViewById(R.id.navigate_fragment_indicator_image_4),  4);

        //vIndicatorImage1 = (ImageView) view.findViewById(R.id.navigate_fragment_indicator_image_1);

        view.findViewById(R.id.navigate_fragment_indicator_btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsEditing) {
                    mBleScanHandler.stopScanning();
                    mNavigationHandler.attachBeaconToIndicator(1, mAdapterScannedBeacon.modelBleList);
                    startScanning();
                }
            }
        });

        view.findViewById(R.id.navigate_fragment_indicator_btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsEditing) {
                    mBleScanHandler.stopScanning();
                    mNavigationHandler.attachBeaconToIndicator(2, mAdapterScannedBeacon.modelBleList);
                    startScanning();
                }
                //mNavigationHandler.attachBeaconToIndicator(vIndicatorImage2, 2, mAdapterScannedBeacon.modelBleList);
            }
        });
        /*
        view.findViewById(R.id.navigate_fragment_indicator_btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsEditing) {
                    mBleScanHandler.stopScanning();
                    mNavigationHandler.attachBeaconToIndicator(3, mAdapterScannedBeacon.modelBleList);
                    startScanning();//mNavigationHandler.attachBeaconToIndicator(vIndicatorImage3, 3, mAdapterScannedBeacon.modelBleList);
                }
            }
        });
        view.findViewById(R.id.navigate_fragment_indicator_btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsEditing) {
                    mBleScanHandler.stopScanning();
                    mNavigationHandler.attachBeaconToIndicator(4, mAdapterScannedBeacon.modelBleList);
                    startScanning();
                }
                //mNavigationHandler.attachBeaconToIndicator(vIndicatorImage4, 4, mAdapterScannedBeacon.modelBleList);
            }
        });*/

        //mNavigationHandler.vTvLable = (TextView) view.findViewById(R.id.navigate_fragment_lable_tv);
        mNavigationHandler.vFlNavigate = (FrameLayout) view.findViewById(R.id.navigate_fragment_indicator_container);
        mNavigationHandler.vPointer = (FrameLayout)view.findViewById(R.id.navigate_fragment_pointer);
        mNavigationHandler.vPointerRipple = (AVLoadingIndicatorView) view.findViewById(R.id.navigate_fragment_pointer_ripple);


        ViewTreeObserver viewTreeObserver = mNavigationHandler.vFlNavigate.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mNavigationHandler.vFlNavigate.getViewTreeObserver().removeOnPreDrawListener(this);
                mNavigationHandler.updateArea(mNavigationHandler.vFlNavigate.getMeasuredWidth(), mNavigationHandler.vFlNavigate.getMeasuredHeight());
                LOGD(TAG, "height: "+ mNavigationHandler.mNavigationLength);
                LOGD(TAG, "width: "+ mNavigationHandler.mNavigationWidth);
                if(!mIsEditing) {
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



    private void initAdapter() {
        vRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterScannedBeacon= new AdapterScannedBeacons(getContext());
        vRecyclerView.setAdapter(mAdapterScannedBeacon);
        vRecyclerView.setVisibility(View.INVISIBLE);
        //mAdapter.setAdapterOrderListener(this);
    }


    /* ************************************************************************************
     *                                         Design
     */

    private void updateEditViews() {

        LOGW(TAG, "updateEditViews isEditing: "+ mIsEditing);
        if(!mIsEditing) {
            vRecyclerView.setVisibility(View.INVISIBLE);
            //mNavigationHandler.vTvLable.setVisibility(View.VISIBLE);
            mNavigationHandler.startPointer();
        }else {
            //mNavigationHandler.vTvLable.setVisibility(View.GONE);
            mNavigationHandler.stopPointer();
        }
    }


    private void showBeaconListView() {

        if(mIsEditing){
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
        if(mIsEditing){
            mIsEditing = false;
            updateEditViews();
        }
    }

    public void stopNavigation() {
        if(!mIsEditing){
            mIsEditing = true;
            updateEditViews();
        }
    }

    public void showBeacons(){
            showBeaconListView();

    }


    public void hideBeacons() {
        //if(!mIsEditing){
        //    mIsEditing = true;
        hideBeaconListView();
        //    showBeaconListView();
        //}
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
        if(activity instanceof ActivityApp) {
            SharedPreferenceHelper.getInstance(activity).saveCharecteristics(
                    wifiName,
                    deviceName,
                    ip
            );

            WebSocketClientHandler.restartSocket(activity);
            //mClientHandler.toggleSending();
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
        if(mBleScanHandler!=null){
            mBleScanHandler.stopScanning();
        }
    }

    private void startScanning(){

        if(mBleScanHandler !=null){
            if(!mBleScanHandler.isBluetoothEnable()){
                mBleScanHandler.requestBluetoothEnable(this);
            }else{
                onBluetoothEnabled(true);
            }
        }
    }

    public void onBluetoothEnabled(boolean isEnabled){
        if(isEnabled){
            Activity activity = getActivity();
            if(activity instanceof ActivityApp){
                ((ActivityApp) activity).requestPermission_ACCESS_COARSE_LOCATION();
            }
        }
    }

    public void onUsesPermissionAllowed(boolean isAllowed) {
        if(isAllowed){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mBleScanHandler !=null) {
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
        LOGV(TAG, "ble: "+ modelBle.rssi);
        if(device.getName()!=null && device.getName().contains("Bat")) {
            if (mIsEditing && mAdapterScannedBeacon != null) {
                mAdapterScannedBeacon.checkToRemoveDeadBle();
                mAdapterScannedBeacon.addItem(modelBle);
            } else {
                mNavigationHandler.updatePointerPosition(modelBle);
            }

            Activity activity = getActivity();
            if(activity instanceof ActivityApp){
                if(((ActivityApp) activity).isShowingBeaconList){

                }
            }
        }

        //updateIndicators(mModelBle);
    }


    @Override
    public String onServerSendData() {

        return (mNavigationHandler.getPositionX()*100)/mNavigationHandler.mNavigationWidth+","
                + (mNavigationHandler.getPositionY()*100)/mNavigationHandler.mNavigationLength;
    }
}
