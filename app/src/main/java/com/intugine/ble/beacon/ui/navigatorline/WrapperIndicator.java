package com.intugine.ble.beacon.ui.navigatorline;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.utils.UtilsColor;
import com.intugine.ble.beacon.utils.UtilsResource;

import java.util.Calendar;

import static android.content.ContentValues.TAG;
import static com.intugine.ble.beacon.util.LogUtils.LOGI;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;

/**
 * Created by niraj on 23-05-2017.
 */

public class WrapperIndicator {

    private final ViewGroup vParent;
    private final int mPosition;
    private ImageView vImageNavigate;
    private FrameLayout vBtnIndicator;

    public int mOffset=12;

    onIndicatorClickListener mOnIndicatorClickListener;

    public WrapperIndicator(Context context, ViewGroup parent, int position){
        vParent = parent;
        mPosition = position;
        View v = LayoutInflater.from(context).inflate(R.layout.navigate_indicator,
                parent, false);
        vBtnIndicator = (FrameLayout) v.findViewById(R.id.navigate_indicator_btn_fl);

        vImageNavigate = (ImageView) v.findViewById(R.id.navigate_indicator_image);
        Drawable drawable = UtilsResource.getDrawable(context, R.drawable.ic_fiber_manual_record_white_24dp);
        //LOGW(TAG, "else color: ");
        LOGW(TAG, "else color: ");

        int color = UtilsColor.getColorForResource(context, R.color.white);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        vImageNavigate.setImageDrawable(wrappedDrawable);
        mOffset =  UtilsResource.getResourceDimenValue(context, R.dimen.length_32);
        LOGI(TAG, "offset:" + mOffset);
        /*
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                vPointerRipple.show();
            }
        });
        */
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGI(TAG,"action click: "+  String.valueOf(Calendar.getInstance().getTimeInMillis()));

                if(mOnIndicatorClickListener!=null){
                    //mOnIndicatorClickListener.onIndicatorClicked(mPosition);
                }
            }
        });

        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    LOGI(TAG,"action down: "+  String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    mOnIndicatorClickListener.onIndicatorPressed(mPosition);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    LOGI(TAG,"action up: "+  String.valueOf(Calendar.getInstance().getTimeInMillis()));
                    mOnIndicatorClickListener.onIndicatorReleased(mPosition);
                } else if(event.getAction()== MotionEvent.ACTION_MOVE){
                    //LOGI(TAG,event.getAction()+ ": "+  String.valueOf(Calendar.getInstance().getTimeInMillis()));
                }
                return false;
            }
        });

        vParent.addView(v);
        //mLastUpdateTime = Calendar.getInstance().getTimeInMillis();

    }


    public void updateIndicatorColor(Context context) {

        Drawable drawable = UtilsResource.getDrawable(context, R.drawable.ic_fiber_manual_record_white_24dp);
        //LOGW(TAG, "else color: ");
        LOGW(TAG, "else color: ");

        int color = UtilsColor.getColorForResource(context, R.color.teal_500);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        vImageNavigate.setImageDrawable(wrappedDrawable);
    }

    public void resetColor(Context context) {
        Drawable drawable = UtilsResource.getDrawable(context, R.drawable.ic_fiber_manual_record_white_24dp);
        //LOGW(TAG, "else color: ");
        LOGW(TAG, "else color: ");

        int color = UtilsColor.getColorForResource(context, R.color.white);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        vImageNavigate.setImageDrawable(wrappedDrawable);
    }


    public void setOnIndicatorClickListener(onIndicatorClickListener onIndicatorClickListener) {
        this.mOnIndicatorClickListener = onIndicatorClickListener;
    }

    public void updateY(int xVal, int yVal, int xMax, int yMax) {
        LOGI(TAG, "position:" + xVal + ", "+ yVal);
        if(vBtnIndicator!=null){
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) vBtnIndicator.getLayoutParams();
            //Replace the RelativeLayout with your myView parent layout

            if(xVal==0|| xVal==xMax){
                layoutParams.leftMargin = (xVal==0)?xVal:(xVal - mOffset);
            }else{
                layoutParams.leftMargin = xVal-mOffset/2;
            }
            if(yVal==0|| yVal==yMax){
                layoutParams.topMargin= (yVal==0)?yVal:(yVal - mOffset);
            }else{
                layoutParams.topMargin = yVal-mOffset/2;
            }

            vBtnIndicator.setLayoutParams(layoutParams);
            //vTvLable.setText("| PosiY: " + mYPosition);
            vParent.invalidate();
        }
    }

    public interface onIndicatorClickListener {

        public void onIndicatorReleased(int position);
        public void onIndicatorPressed(int position);
        public void onIndicatorClicked(int position);
    }
}
