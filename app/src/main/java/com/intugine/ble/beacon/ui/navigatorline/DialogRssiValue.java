package com.intugine.ble.beacon.ui.navigatorline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intugine.ble.beacon.R;

import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 23-05-2017.
 */

public class DialogRssiValue extends DialogFragment {

    private static final String TAG = makeLogTag(DialogRssiValue.class);

    public static final String B_ARG_RSSI = "count.dialog";
    public static final String B_ARG_KEY= "dialog.key";
    private String mRssi ="60";
    private String mPosiY;
    //private EditText vEtComment;
    private int mKey;
    private Button vBtnDialogOk;
    private Button vBtnDialogCancel;
    private EditText vEtCount;
    private DialogRssiListener mDialogRssiValueListener;
    private TextView vTvTitle;

    public static DialogRssiValue newInstance(int key, Bundle bundle) {
        DialogRssiValue fragment = new DialogRssiValue();
        Bundle args = new Bundle(bundle);
        args.putInt(B_ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //if(vEtComment!=null){
        outState.putString(B_ARG_RSSI, mRssi);
        //}

    }

    public void onRestoreInstanceState(Bundle b) {

        Bundle arg = getArguments();
        mKey = getArguments().getInt(B_ARG_KEY);
        if (b != null) {
            mRssi = b.getString(B_ARG_RSSI, "3");
        }
    }

    public int getLayoutResourceId() {
        return R.layout.dialog_count;
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
        vTvTitle = (TextView) view.findViewById(R.id.dialog_tv_title);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //getDialog().getWindow().se
        initView(view, savedInstanceState);
        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //LOGV(TAG, "comments: " + mComments);
        if(savedInstanceState!=null) {

            if (mRssi != null) {
                vEtCount.setText(mRssi);
            }

        }else {
            vEtCount.setText(mRssi);
        }
        vTvTitle.setText("Initial Rssi");

    }

    public void setDialogRssiValueListener(DialogRssiListener pDialogRssiListener) {
        this.mDialogRssiValueListener = pDialogRssiListener;
    }

    public void initView(View view, Bundle savedInstanceState) {
        vEtCount = (EditText) view.findViewById(R.id.dialog_count_tv);

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
        Editable editableCount = vEtCount.getEditableText();
        if (editableCount!= null ) {
            String editableStringCount = editableCount.toString();
            if (editableStringCount != null && editableStringCount.length() >= 0) {
                if (mDialogRssiValueListener!= null) {
                    mDialogRssiValueListener.onDialogRssiPositiveClick(mKey, Integer.valueOf(editableStringCount));
                }
            }

        }
    }

    private void onCancelBtnClicked() {
        LOGW(TAG, "cancel");
        if (mDialogRssiValueListener!= null) {
            //mDialogCountListener.onDialogCountNegativeClick(mKey);
        }

    }

    public interface DialogRssiListener{

        public void onDialogRssiPositiveClick(int key, int count);
        //public void onDialogCountNegativeClick(int key);
    }

}