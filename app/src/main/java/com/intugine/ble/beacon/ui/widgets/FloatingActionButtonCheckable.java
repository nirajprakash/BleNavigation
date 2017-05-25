package com.intugine.ble.beacon.ui.widgets;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

/**
 * Created by niraj on 08-05-2017.
 */

public class FloatingActionButtonCheckable extends FloatingActionButton implements Checkable {


    private static final int[] CheckedStateSet = {
            android.R.attr.state_checked,
    };


    private boolean checked = false;

    public FloatingActionButtonCheckable(Context context) {
        this(context, null);
    }

    public FloatingActionButtonCheckable(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FloatingActionButtonCheckable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            View.mergeDrawableStates(drawableState, CheckedStateSet);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked == this.checked) {
            return;
        }
        this.checked = checked;
    }

    @Override
    public boolean isChecked() {
        return this.checked;
    }

    @Override
    public void toggle() {
        setChecked(!this.checked);
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        CheckedSavedState result = new CheckedSavedState(super.onSaveInstanceState());
        result.checked = checked;
        return result;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof CheckedSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        CheckedSavedState ss = (CheckedSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
    }

}
