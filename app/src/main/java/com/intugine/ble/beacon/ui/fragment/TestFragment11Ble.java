package com.intugine.ble.beacon.ui.fragment;

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
import android.view.View;
import android.view.ViewGroup;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.ble.BleScanHandler;
import com.intugine.ble.beacon.keys.KeyIntent;
import com.intugine.ble.beacon.model.ModelSchedule;
import com.intugine.ble.beacon.ui.MainActivity;
import com.intugine.ble.beacon.ui.base.FragmentBase;
import com.intugine.ble.beacon.ui.scanner.AdapterScannedDevice;
import com.intugine.ble.beacon.ui.scanner.ModelBle;

import java.util.Calendar;

import static com.intugine.ble.beacon.util.LogUtils.LOGD;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 04-05-2017.
 */
public class TestFragment11Ble extends FragmentBase implements BleScanHandler.OnBleScannedListener {

    private static final String TAG = makeLogTag(TestFragment11Ble.class);

    private BleScanHandler mBleScanHandler;
    private RecyclerView vRecyclerView;

    public static Fragment newInstance(int position, Bundle bundle) {
        TestFragment11Ble fragment = new TestFragment11Ble();
        Bundle args = new Bundle(bundle);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }


    private AdapterScannedDevice mAdapterScannedDevice;
    @Override
    public boolean setNavigationHomeAsBack() {
        return true;
    }

    @Override
    protected String getToolBarDefaultTitle() {
        return "BLE test";
    }


    /*
    test11_fragment_view
     */
    @Override
    public int getLayoutResourceId() {
        return R.layout.test_fragment_11;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if(activity!=null){
            mBleScanHandler = BleScanHandler.with(activity);
            if(mBleScanHandler!=null){
                mBleScanHandler.setOnBleScannedListener(this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        /*initCollapsingToolbar(true, view,
                R.id.toolbar_collapsing,
                R.id.toolbar,
                R.drawable.ic_arrow_back_white_24dp);*/
        //initView(view, savedInstanceState);
        initToolbar(view, R.id.toolbar, R.drawable.ic_arrow_back_white_24dp);
        initView(view, savedInstanceState);
        //mHandler = new Handler(this);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*if(savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            mFragmentProgress = FragmentProgress.newInstance(ModelProgress.ViewType.PROGRESS_CIRCULAR,
                    ProgressView.MODE_INDETERMINATE,
                    ModelProgress.Size.RES_LENGTH_SMALL);
            ft.replace(R.id.test11_frame_progress, mFragmentProgress
                    , FragmentProgress.TAG).commit();
        }else{
            mFragmentProgress = (FragmentProgress) getChildFragmentManager().findFragmentByTag(FragmentProgress.TAG);
        }*/

        startScanning();


    }

    private void initAdapter() {
        vRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterScannedDevice = new AdapterScannedDevice(getContext());
        vRecyclerView.setAdapter(mAdapterScannedDevice);
        //mAdapter.setAdapterOrderListener(this);
    }

    private void initView(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.action_btn_fl_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartScan();
            }
        });
        vRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        vRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        vRecyclerView.setItemViewCacheSize(0);
        initAdapter();
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


    private void restartScan() {
        this.mAdapterScannedDevice.resetItems(null);

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
            if(activity instanceof MainActivity){
                ((MainActivity) activity).requestPermission_ACCESS_COARSE_LOCATION();
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
            /*new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(mBleScanHandler !=null) {
                        mBleScanHandler.stopScanning();
                    }
                }
            }, ModelSchedule.DELAY_GPS);*/
        }
    }

    @Override
    public void onBleScanned(BluetoothDevice device, int rssi, byte[] scanRecord) {
        ModelBle modelBle = new ModelBle(device, rssi, Calendar.getInstance().getTimeInMillis());
        if(mAdapterScannedDevice!=null){
            mAdapterScannedDevice.addItem(modelBle);
        }
    }
}
