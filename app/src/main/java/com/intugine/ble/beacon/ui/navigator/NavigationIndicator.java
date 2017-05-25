package com.intugine.ble.beacon.ui.navigator;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.ui.scanner.ModelBle;
import com.intugine.ble.beacon.utils.UtilsColor;
import com.intugine.ble.beacon.utils.UtilsResource;

import static android.content.ContentValues.TAG;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;

/**
 * Created by niraj on 06-05-2017.
 */

public class NavigationIndicator {
    ImageView indicator;
    public ModelBle modelBle;
    public int positionY = 0;

    public NavigationIndicator(ImageView wrappedView, ModelBle modelBle, int yVal) {
        this.indicator = wrappedView;
        this.modelBle = modelBle;
        this.positionY = yVal;

    }

    public NavigationIndicator(ImageView view, Context context) {
        Drawable drawable = UtilsResource.getDrawable(context, R.drawable.ic_fiber_manual_record_white_24dp);
        //LOGW(TAG, "else color: ");
        LOGW(TAG, "else color: ");

        int color = UtilsColor.getColorForResource(context, R.color.white);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        view.setImageDrawable(wrappedDrawable);
        indicator = view;
    }


    public void updateModelBle(ModelBle modelBleMin) {
        this.modelBle = modelBleMin;
    }
    public void updateModelBleOnEdit(ModelBle modelBleMin, Context context) {
        this.modelBle = modelBleMin;
        updateIndicatorColor(context);
    }
    private void updateIndicatorColor(Context context) {

        Drawable drawable = UtilsResource.getDrawable(context, R.drawable.ic_fiber_manual_record_white_24dp);
        //LOGW(TAG, "else color: ");
        LOGW(TAG, "else color: ");

        int color = UtilsColor.getColorForResource(context, R.color.teal_500);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        indicator.setImageDrawable(wrappedDrawable);
    }

    public void resetColor(Context context) {
        Drawable drawable = UtilsResource.getDrawable(context, R.drawable.ic_fiber_manual_record_white_24dp);
        //LOGW(TAG, "else color: ");
        LOGW(TAG, "else color: ");

        int color = UtilsColor.getColorForResource(context, R.color.white);
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        indicator.setImageDrawable(wrappedDrawable);
    }
}
