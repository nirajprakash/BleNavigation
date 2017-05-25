package com.intugine.ble.beacon.ui.navigator2d;

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

import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 18-05-2017.
 */

public class DialogPosition extends DialogFragment {

    private static final String TAG = makeLogTag(DialogPosition.class);

    public static final String B_ARG_posix = "posi.x";
    public static final String B_ARG_posiy = "posi.y";
    public static final String B_ARG_KEY= "posi.x";
    private String mPosiX;
    private String mPosiY;
    //private EditText vEtComment;
    private int mKey;
    private Button vBtnDialogOk;
    private Button vBtnDialogCancel;
    private EditText vEtPositionX;
    private EditText vEtPositionY;
    private DialogPositionListener mDialogPositionListener;

    public static final SparseArray<String[]> MAP_Key_position= new SparseArray<String[]>() {
        {
            put(3, new String[]{"0","0"});
            put(4, new String[]{"4","0"});
            put(1, new String[]{"0","6"});
            put(2, new String[]{"4","6"});
        }
    };
    public static DialogPosition newInstance(int key, Bundle bundle) {
        DialogPosition fragment = new DialogPosition();
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
        return R.layout.dialog_position;
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

            if (mPosiX != null) {
                vEtPositionX.setText(mPosiX);
            }

            if (mPosiY != null) {
                vEtPositionY.setText(mPosiY);
            }
        }else {
            vEtPositionX.setText(MAP_Key_position.get(mKey)[0]);
            vEtPositionY.setText(MAP_Key_position.get(mKey)[1]);
        }

    }

    public void setDialogPositionListener(DialogPositionListener pDialogPositionListener) {
        this.mDialogPositionListener = pDialogPositionListener;
    }

    public void initView(View view, Bundle savedInstanceState) {
        vEtPositionX= (EditText) view.findViewById(R.id.dialog_position_tv_x);
        vEtPositionY= (EditText) view.findViewById(R.id.dialog_position_tv_y);

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
        Editable editableX = vEtPositionX.getEditableText();
        Editable editableY = vEtPositionY.getEditableText();
        if (editableX != null && editableY!=null) {
            String editableStringX = editableX.toString();
            String editableStringY =  editableY.toString();
            if (editableStringX != null && editableStringX.length() >= 0 && editableStringY != null && editableStringY.length()>=0) {
                if (mDialogPositionListener != null) {
                    mDialogPositionListener.onDialogCommentPositiveClick(mKey, editableStringX, editableStringY);
                }
            }

        }
    }

    private void onCancelBtnClicked() {
        LOGW(TAG, "cancel");
        if (mDialogPositionListener != null) {
            mDialogPositionListener.onDialogCommentNegativeClick(mKey);
        }

    }

    public interface DialogPositionListener {
        public void onDialogCommentPositiveClick(int key, String posiX, String posiY);
        public void onDialogCommentNegativeClick(int key);
    }

}
