package com.intugine.ble.beacon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.keys.KeyIntent;
import com.intugine.ble.beacon.keys.KeyManifest;
import com.intugine.ble.beacon.model.ModelSchedule;
import com.intugine.ble.beacon.ui.base.ActivityBase;
import com.intugine.ble.beacon.ui.fragment.FragmentButton;
import com.intugine.ble.beacon.ui.fragment.TestFragment11Ble;

import static com.intugine.ble.beacon.util.LogUtils.LOGV;
import static com.intugine.ble.beacon.util.LogUtils.LOGW;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

public class MainActivity extends ActivityBase {

    private static final String TAG = makeLogTag(MainActivity.class);

    private static final String FRAGMENT_BUTTONS_VAL = "main buttons";
    @Override
    public void onCreateBeforeViewSet(Bundle savedInstanceState) {

    }

    @Override
    public void onCreateAfterViewSet(Bundle saveInstanceState) {
        initToolbar(saveInstanceState, R.id.toolbar);
        startFirstFragment();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    public int getMainFrameContentId() {
        return R.id.frame_content;
    }

    @Override
    public String getToolBarDefaultTitle() {
        return null;
    }

    @Override
    public void restartActivity() {

    }

    @Override
    protected boolean setNavigationHomeAsBack() {
        return false;
    }

    public void startFragment(View view) {

        /*if (view.getId() == R.id.btn_test12) {
            Intent i = new Intent(getApplicationContext(), Test12Activity12View.class);
            startActivityForResult(i, ACTIVITY[0]);
        } else if (view.getId() == R.id.btn_test21) {
            Intent i = new Intent(getApplicationContext(), Test21ActivityNavigationView.class);
            startActivityForResult(i, ACTIVITY[1]);
        }else if (view.getId() == R.id.btn_app1) {
            Intent i = new Intent(getApplicationContext(), ActivityWelcome.class);
            startActivityForResult(i, ACTIVITY[5]);
        }else if (view.getId() == R.id.btn_test61) {
            Intent i = new Intent(getApplicationContext(), PaymentActivity.class);
            startActivityForResult(i, ACTIVITY[6]);
        } else {*/
            FragmentManager fm = getSupportFragmentManager();
            FragmentButton fragment = (FragmentButton) fm.findFragmentByTag(FRAGMENT_BUTTONS_VAL);
        if (view.getId() == R.id.btn_app1) {
            Intent i = new Intent(getApplicationContext(), ActivityApp.class);
            startActivityForResult(i, KeyIntent.ACITIVITY_APP);
        }else if (fragment != null) {
                fragment.startFragment(view, fm);
            }
        //}
    }

    public void startFirstFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        /*
        ft.setCustomAnimations(R.anim.anim_trans_y_to_full,
				R.anim.anim_trans_y_to_zero);
				*/

        Bundle b = new Bundle();
        Fragment fragmentButton = FragmentButton.getInstance(0, b);

        ft.replace(R.id.frame_content, fragmentButton, FRAGMENT_BUTTONS_VAL);

        // Start the animated transition.
        ft.commit();
    }

    /* *********************************************8
     *                       Permission work
     *
     */

    public void requestPermission_ACCESS_COARSE_LOCATION() {
        ListenerPermissionChange listenerPermissionChange = new ListenerPermissionChange() {
            @Override
            public void onPermissionResult(int reqKey, final boolean isAllowed) {
                LOGW(TAG, "permissionResult: " + reqKey);
                if (reqKey == KeyManifest.UsesPermission.ACCESS_COARSE_LOCATION) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onRequestPermissionResult_ACCESS_COARSE_LOCATION(isAllowed);
                        }
                    }, ModelSchedule.DELAY_UI_UPDATE_FAST);
                }
            }
        };

        if (checkForPermission(KeyManifest.UsesPermission.ACCESS_COARSE_LOCATION)) {
            onRequestPermissionResult_ACCESS_COARSE_LOCATION(true);
        } else {

            requestForPermission(KeyManifest.UsesPermission.ACCESS_COARSE_LOCATION, listenerPermissionChange);

        }
    }

    private void onRequestPermissionResult_ACCESS_COARSE_LOCATION(boolean isAllow) {
        LOGV(TAG, "called Access Coarse Location result");
        Fragment fragment = getTopFragment();
        if(fragment instanceof TestFragment11Ble){
            LOGV(TAG, "called Access Coarse Location for framgnet: "+ TestFragment11Ble.class.getSimpleName());
            ((TestFragment11Ble) fragment).onUsesPermissionAllowed(isAllow);
        }else {

        }
    }

    public Fragment getTopFragment() {
        int count = mFragmentManager.getBackStackEntryCount();
        if (count > 0) {
            String fName = mFragmentManager.getBackStackEntryAt(count - 1).getName();
            //mFragmentStackList.remove(fName);
            LOGV(TAG, "back stack: " + fName);
            if (fName != null) {
                return mFragmentManager.findFragmentByTag(fName);
            }
        }
        return null;
    }
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    */
}
