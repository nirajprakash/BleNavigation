package com.intugine.ble.beacon.ui.viewer;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intugine.ble.beacon.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Calendar;

import static com.intugine.ble.beacon.util.LogUtils.LOGI;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 20-05-2017.
 */

public class WrapperPointer {

    private static final String TAG = makeLogTag(WrapperPointer.class);
    LinearLayout vPointerContainer;
    FrameLayout vPointer;// = (FrameLayout)view.findViewById(R.id.navigate_fragment_pointer);
    AVLoadingIndicatorView vPointerRipple;// = (AVLoadingIndicatorView) view.findViewById(R.id.navigate_fragment_pointer_ripple);
    TextView vTextLable;
    String mName;
    int positionX;
    int positionY;

    ViewGroup vParent;

    long mLastUpdateTime;



    public WrapperPointer(Context context, ViewGroup parent, String name){
        vParent = parent;
        View v = LayoutInflater.from(context).inflate(R.layout.navigate_pointer,
                parent, false);
        vPointerContainer = (LinearLayout) v.findViewById(R.id.navigation_pointer_container);
        vPointer = (FrameLayout) v.findViewById(R.id.navigation_pointer);

        vPointerRipple = (AVLoadingIndicatorView) v.findViewById(R.id.navigation_pointer_ripple);
        vTextLable = (TextView) v.findViewById(R.id.navigation_pointer_name_tv);
        vTextLable.setText(name);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                vPointerRipple.show();
            }
        });
        vParent.addView(v);
        mLastUpdateTime = Calendar.getInstance().getTimeInMillis();

    }


    public void setPosition(int x, int y){
        LOGI(TAG, "setting Position"+ x+ "  "+ y);
        positionX = x;
        positionY = y;
        mLastUpdateTime = Calendar.getInstance().getTimeInMillis();
        if(vPointerContainer!=null){
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vPointerContainer.getLayoutParams();
            //Replace the RelativeLayout with your myView parent layout
            layoutParams.leftMargin = positionX;
            layoutParams.topMargin = positionY;
            vPointerContainer.setLayoutParams(layoutParams);
            //vTvLable.setText("| PosiY: " + mYPosition);
            vParent.invalidate();
        }
    }

    public void setName(String name){
        mName = name;
    }

    public boolean checkToRemove(long currentTime){
        long timeDelta = currentTime - mLastUpdateTime;
        //LOGV(TAG, "time Delta: "+ timeDelta);
        if((timeDelta)>5000){
            LOGI(TAG, "time Delta: "+ timeDelta);
            vParent.removeView(vPointerContainer);
            return true;
        }
        return false;
    }
}
