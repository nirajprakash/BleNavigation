package com.intugine.ble.beacon.ui.navigator2d;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.ui.ActivityApp;
import com.intugine.coalindiaclientdemo.SharedPreferenceHelper;

import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 20-05-2017.
 */

public class DialogWifi extends DialogFragment {

    private static final String TAG = makeLogTag(DialogPosition.class);

    public static final String B_ARG_posix = "posi.x";
    public static final String B_ARG_posiy = "posi.y";
    public static final String B_ARG_KEY= "arg.key";
    private String mPosiX;
    private String mPosiY;
    //private EditText vEtComment;
    private int mKey;
    private Button vBtnDialogOk;
    private Button vBtnDialogCancel;
    private EditText vEtWifiName;
    private EditText vEtDeviceName;
    private EditText vEtIp;
    private DialogWifiListener mDialogWifiListener;

    public static final SparseArray<String[]> MAP_Key_position= new SparseArray<String[]>() {
        {
            put(3, new String[]{"0","0"});
            put(4, new String[]{"3","0"});
            put(1, new String[]{"0","4"});
            put(2, new String[]{"3","4"});
        }
    };
    private String mWifiName;
    private String mDeviceName;
    private String mReceiverIp;

    public static DialogWifi newInstance(int key, Bundle bundle) {
        DialogWifi fragment = new DialogWifi();
        Bundle args = new Bundle(bundle);
        args.putInt(B_ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //if(vEtComment!=null){
        outState.putString(B_ARG_posix, mPosiX);
        outState.putString(B_ARG_posiy, mPosiY);
        //}

    }

    public void onRestoreInstanceState(Bundle b) {

        Bundle arg = getArguments();
        mKey = getArguments().getInt(B_ARG_KEY);
        if (b != null) {
            mPosiX = b.getString(B_ARG_posix);
            mPosiY = b.getString(B_ARG_posiy);
        }
    }

    public int getLayoutResourceId() {
        return R.layout.dialog_wifi;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRestoreInstanceState(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View view = inflater.inflate(getLayoutResourceId(), container, false);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //getDialog().getWindow().se
        initView(view, savedInstanceState);
        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState ==null){
            setUpWifiCharecterisitics();
        }


    }

    private void setUpWifiCharecterisitics() {
        Activity activity = getActivity();
        if(activity instanceof ActivityApp){

            mWifiName = SharedPreferenceHelper.getInstance(activity).getWifiName();
            mDeviceName = SharedPreferenceHelper.getInstance(activity).getDeviceName();
            mReceiverIp = SharedPreferenceHelper.getInstance(activity).getRecieverIP();

        }
        //textWifiName.setText("Wifi Name: " + wifi_name);
        //textDeviceName.setText("Device Name: " + device_name);
        //textRecieverIp.setText("Reciever Ip: " + reciever_ip);
        vEtWifiName.setText(mWifiName);
        vEtDeviceName.setText(mDeviceName);
        vEtIp.setText(mReceiverIp);
    }

    public void setDialogWifiListener(DialogWifiListener pDialogWifiListener) {
        this.mDialogWifiListener = pDialogWifiListener;
    }

    public void initView(View view, Bundle savedInstanceState) {
        vEtWifiName= (EditText) view.findViewById(R.id.dialog_wifi_tv_wifi_name);
        vEtDeviceName= (EditText) view.findViewById(R.id.dialog_wifi_tv_device_name);
        vEtIp = (EditText) view.findViewById(R.id.dialog_wifi_tv_ip);

        vBtnDialogOk = (Button) view.findViewById(R.id.btn_ok);
        vBtnDialogOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onOkBtnClicked();
            }
        });
        vBtnDialogCancel = (Button) view.findViewById(R.id.btn_cancel);
        vBtnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onCancelBtnClicked();
            }
        });
    }

    private void onOkBtnClicked() {
        //if()
        Editable editableWifiName = vEtWifiName.getEditableText();
        Editable editableDeviceName= vEtDeviceName.getEditableText();
        Editable editableIp= vEtIp.getEditableText();
        if (editableWifiName != null && editableDeviceName!=null && editableIp !=null) {
            String editableStringWifiName = editableWifiName.toString();
            String editableStringDeviceName= editableDeviceName.toString();
            String editableStringIp= editableIp.toString();

            if (editableStringWifiName != null && editableStringWifiName.length() >= 0
                    && editableStringDeviceName != null && editableStringDeviceName.length()>=0
                    && editableStringIp != null && editableStringIp.length()>=0) {
                if (mDialogWifiListener != null) {
                    mDialogWifiListener.onDialogWifiPositiveClick(mKey, editableStringWifiName, editableStringDeviceName, editableStringIp);
                }
            }

        }
    }

    private void onCancelBtnClicked() {
        LOGW(TAG, "cancel");
        if (mDialogWifiListener!= null) {
            //mDialogWifiListener.onDialogWifiPositiveClick(mKey, w);
        }

    }

    public interface DialogWifiListener {
        public void onDialogWifiPositiveClick(int key, String wifiName, String deviceName, String ip);
        //public void onDialogWifiNegativeClick(int key);
    }

}
