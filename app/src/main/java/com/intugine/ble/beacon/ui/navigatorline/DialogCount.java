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

import com.intugine.ble.beacon.R;

import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 23-05-2017.
 */

public class DialogCount extends DialogFragment {

    private static final String TAG = makeLogTag(DialogCount.class);

    public static final String B_ARG_count = "count.dialog";
    public static final String B_ARG_KEY= "dialog.key";
    private String mCount="2";
    private String mPosiY;
    //private EditText vEtComment;
    private int mKey;
    private Button vBtnDialogOk;
    private Button vBtnDialogCancel;
    private EditText vEtCount;
    private DialogCountListener mDialogCountListener;

    public static DialogCount newInstance(int key, Bundle bundle) {
        DialogCount fragment = new DialogCount();
        Bundle args = new Bundle(bundle);
        args.putInt(B_ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //if(vEtComment!=null){
        outState.putString(B_ARG_count, mCount);
        //}

    }

    public void onRestoreInstanceState(Bundle b) {

        Bundle arg = getArguments();
        mKey = getArguments().getInt(B_ARG_KEY);
        if (b != null) {
            mCount = b.getString(B_ARG_count, "3");
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

            if (mCount != null) {
                vEtCount.setText(mCount);
            }

        }else {
            vEtCount.setText(mCount);
        }

    }

    public void setDialogCountListener(DialogCount.DialogCountListener pDialogCountListener) {
        this.mDialogCountListener = pDialogCountListener;
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
                if (mDialogCountListener!= null) {
                    mDialogCountListener.onDialogCountPositiveClick(mKey, Integer.valueOf(editableStringCount));
                }
            }

        }
    }

    private void onCancelBtnClicked() {
        LOGW(TAG, "cancel");
        if (mDialogCountListener != null) {
            //mDialogCountListener.onDialogCountNegativeClick(mKey);
        }

    }

    public interface DialogCountListener{

        public void onDialogCountPositiveClick(int key, int count);
        //public void onDialogCountNegativeClick(int key);
    }

}
