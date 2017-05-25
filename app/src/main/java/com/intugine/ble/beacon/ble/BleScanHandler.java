package com.intugine.ble.beacon.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.intugine.ble.beacon.keys.KeyIntent;

import static com.intugine.ble.beacon.util.LogUtils.LOGD;
import static com.intugine.ble.beacon.util.LogUtils.LOGV;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 04-05-2017.
 */
public class BleScanHandler {

    private static final String TAG = makeLogTag(BleScanHandler.class);
    private static BleScanHandler sBleScanHandler;


    BluetoothManager mBluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner mBluetoothLeScanner;
    boolean mIsScanning = false;
    Activity mContext;
    OnBleScannedListener mOnBleScannedListener;
    private ScanCallback leScanCallbackV21;
    BluetoothAdapter.LeScanCallback leScanCallback;

    public synchronized static BleScanHandler with(Activity activity){
        //sBleScanHandler = null;
        if(activity !=null){
            if(sBleScanHandler==null){

                sBleScanHandler = new BleScanHandler();
                sBleScanHandler.mIsScanning = false;
                boolean isInit = sBleScanHandler.init(activity);
                if(isInit){
                    return sBleScanHandler;
                }
            }else{
                if(sBleScanHandler.mContext != activity){
                    sBleScanHandler.changeContext(activity);
                }
                return sBleScanHandler;
            }

        }
        return null;
    }

    private void changeContext(Activity activity) {
        mContext = activity;
        if(activity instanceof OnBleScannedListener){
            mOnBleScannedListener = (OnBleScannedListener) activity;
        }
    }

    private boolean init(Activity activity){
        LOGD(TAG, "init");
        mContext = activity;
        if(checkIfAvailableBle()){

            mBluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }
            if(activity instanceof OnBleScannedListener){
                mOnBleScannedListener = (OnBleScannedListener) activity;
            }
            initScanCallback();
            return true;
        }
            return false;

    }

    private void initScanCallback(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            leScanCallbackV21 = new ScanCallback() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    //LOGD(TAG, "Device uuid: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");
                    if(mOnBleScannedListener!=null){
                        mOnBleScannedListener.onBleScanned(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                    }
                }
            };
        }else{
            leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    //LOGD(TAG, "Device Name: " + device.getName() + " rssi: " + rssi + "\n");
                    if(mOnBleScannedListener!=null){
                        mOnBleScannedListener.onBleScanned(device,rssi, scanRecord);
                    }
                }
            };
        }
    }

    private boolean checkIfAvailableBle(){
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, "ble_not_supported", Toast.LENGTH_SHORT).show();
            // finish();
            return false;
        }
        return true;
    }


    public boolean isBluetoothEnable(){
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            LOGD(TAG, "isBluetoothEnable: false");
            return false;
        }
        LOGD(TAG, "isBluetoothEnable: true");
        return true;
    }

    public boolean requestBluetoothEnable(Fragment fragment){
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            fragment.startActivityForResult(enableIntent, KeyIntent.REQUEST_ENABLE_BT);
            LOGD(TAG, "requestBluetoothEnable: true");
            return true;
        }
        LOGD(TAG, "requestBluetoothEnable: false");
        return false;
    }

    public boolean requestBluetoothEnable(){
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivityForResult(enableIntent, KeyIntent.REQUEST_ENABLE_BT);
            LOGD(TAG, "requestBluetoothEnable: true");
            return true;
        }
        LOGD(TAG, "requestBluetoothEnable: false");
        return false;
    }

    public void setOnBleScannedListener(OnBleScannedListener mOnBleScannedListener) {
        this.mOnBleScannedListener = mOnBleScannedListener;
    }

    public void startScanningForeGround(){
        LOGW(TAG, "start Scanning Foregraund");
        if(!isBluetoothEnable()){
            return;
        }
        if(mIsScanning){
            return;
        }
        LOGD(TAG ,"start scanning");
        //peripheralTextView.setText("");
        //startScanningButton.setVisibility(View.INVISIBLE);
        //stopScanningButton.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                mIsScanning= true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //LOGV(TAG, "lollipop");
                    ScanSettings settings = new ScanSettings.Builder()
                            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                            .setReportDelay(0)
                            .build();

                    mBluetoothLeScanner.startScan(null, settings, getLeScanCallbackV21());
                    //mBluetoothLeScanner.startScan(getLeScanCallbackV21());
                }else{
                    //LOGV(TAG, "prelollipop");

                    mBluetoothAdapter.startLeScan(getLeScanCallback());
                }
            }
        });
    }

    public void stopScanning() {
        LOGD(TAG, "stopping scanning 1");
        if(!isBluetoothEnable()){
            return;
        }

        if(!mIsScanning){
            return;
        }

        LOGD(TAG, "stopping scanning");
        //peripheralTextView.append("Stopped Scanning");
        //startScanningButton.setVisibility(View.VISIBLE);
        //stopScanningButton.setVisibility(View.INVISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    LOGV(TAG, "lollipop");
                    mBluetoothLeScanner.stopScan(getLeScanCallbackV21());
                }else {
                    LOGV(TAG, "prelollipop");
                    mBluetoothAdapter.stopLeScan(getLeScanCallback());
                }

                mIsScanning = false;
            }
        });
    }



    private ScanCallback getLeScanCallbackV21() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return leScanCallbackV21;
        }else {
            return null;
        }
    }


    private BluetoothAdapter.LeScanCallback getLeScanCallback() {
        return leScanCallback;
    }




    public interface OnBleScannedListener{
        public void onBleScanned(BluetoothDevice device, int rssi, byte[] scanRecord);
    }


}
