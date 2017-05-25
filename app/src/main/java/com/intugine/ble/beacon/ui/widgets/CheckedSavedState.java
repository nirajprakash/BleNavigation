package com.intugine.ble.beacon.ui.widgets;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by niraj on 08-05-2017.
 */

public class CheckedSavedState extends View.BaseSavedState {
    public boolean checked;

    public CheckedSavedState(Parcelable superState) {
        super(superState);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(checked ? 1 : 0);
    }

    public static final Creator<CheckedSavedState> CREATOR = new Creator<CheckedSavedState>() {
        public CheckedSavedState createFromParcel(Parcel in) {
            return new CheckedSavedState(in);
        }

        public CheckedSavedState[] newArray(int size) {
            return new CheckedSavedState[size];
        }
    };

    private CheckedSavedState(Parcel in) {
        super(in);
        checked = in.readInt() == 1;
    }
}
