package com.intugine.ble.beacon.ui.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.keys.KeyManifest;

import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 04-05-2017.
 */
public abstract class ActivityBase extends AppCompatActivity {

    private static final String TAG = makeLogTag(ActivityBase.class);
    private final String B_ARG_TOOLBAR_TITLE = "toolbartitle";
    public Toolbar vToolbar;
    public FragmentManager mFragmentManager;
    ListenerPermissionChange mListenerPermissionChange;
    private String mTitle = "ToolBase Title";
    private boolean mIsTitleAddedInToolbar;
    private boolean mIsCollapsingToolbar = false;
    private TextView vTVToolbar;
    private CollapsingToolbarLayout vCollapsingToolbar;
    private boolean mIsApiUrl = false;

    public abstract void onCreateBeforeViewSet(Bundle savedInstanceState);

    public abstract void onCreateAfterViewSet(Bundle saveInstanceState);

    public abstract int getLayoutResourceId();

    public abstract int getMainFrameContentId();

    public abstract String getToolBarDefaultTitle();

    public abstract void restartActivity();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        initFragmentManager();
        onCreateBeforeViewSet(savedInstanceState);
        setContentView(getLayoutResourceId());
        onCreateAfterViewSet(savedInstanceState);
        //setToolbarTitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (setNavigationHomeAsBack()) {
            if (id == android.R.id.home) {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected abstract boolean setNavigationHomeAsBack();


    /* *********************************************************************************************
     *                                          main method
     */
    public void initFragmentManager() {
        mFragmentManager = getSupportFragmentManager();
    }

    public void initToolbar(Bundle saveInstanceState, int toolbarId) {
        vToolbar = (Toolbar) findViewById(toolbarId);
        setSupportActionBar(vToolbar);

        ActionBar ab = getSupportActionBar();
        setToolbarTitle(getToolBarDefaultTitle());
        ab.setDisplayHomeAsUpEnabled(true);
        if (setNavigationHomeAsBack()) {
            ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
        //ab.setTitle("Fitter");
    }

    public void initCollapsingToolbar(boolean isTitleAddedInToolbar,
                                      Bundle saveInstanceState,
                                      int collapsingToolbarId,
                                      int toolbarId) {
        mIsCollapsingToolbar = true;
        mIsTitleAddedInToolbar = isTitleAddedInToolbar;
        initToolbar(saveInstanceState, toolbarId);
        if (!mIsTitleAddedInToolbar) {
            vCollapsingToolbar = (CollapsingToolbarLayout) findViewById(collapsingToolbarId);
            //vCollapsingToolbar.setTitle(mTitle);
            getSupportActionBar().setTitle("");
        } else {
            //LOGV(TAG, isTitleAddedInToolbar+" ");
            getSupportActionBar().setTitle("");
            initToolbarTextView();
            //setToolbarTVTitle();
        }
        setToolbarTitle(getToolBarDefaultTitle());

    }

    private void initToolbarTextView() {
        vTVToolbar = new TextView(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            vTVToolbar.setTextAppearance(R.style.App_Base_TextAppearance_Toolbar_Light);
        } else {
            vTVToolbar.setTextAppearance(this, R.style.App_Base_TextAppearance_Toolbar_Light);
        }
        vToolbar.addView(vTVToolbar);
    }

    public void setToolbarTitle(String pTitle) {
        if (pTitle == null) {
            mTitle = getToolBarDefaultTitle();
        } else {
            mTitle = pTitle;
        }

        if (mIsCollapsingToolbar) {
            if (mIsTitleAddedInToolbar && vTVToolbar != null) {
                vTVToolbar.setText(mTitle);
            } else if (vCollapsingToolbar != null) {
                vCollapsingToolbar.setTitle(mTitle);
            }
        } else {
            getSupportActionBar().setTitle(mTitle);
        }
        /*if(mToolbar!=null){
            setToolbarTVTitle();
        }*/
    }

    /* *********************************************************************************************
     *                                  Bundle Section
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {


        super.onSaveInstanceState(outState);
        outState.putString(B_ARG_TOOLBAR_TITLE, mTitle);
        System.gc();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTitle = savedInstanceState.getString(B_ARG_TOOLBAR_TITLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }


    /* *********************************************************************************************
     *                        Fragment Dialog Section
     */

    public boolean startFragmentForResult(int fragmentKey, Bundle b) {
        return false;
    }

    public boolean startDialogForResult(int dialogKey, Bundle b) {
        return false;
    }

    public boolean startActivityForRequest(int requestKey, Bundle b){
        return false;
    }


    /* *********************************************************************************************
     *                                         Image Library
     */

    /*
    public void initImageLibrary() {
        AppImageLibrary appImageLibrary = ((ApplicationManager) getApplicationContext())
                .getAppImageLibrary();
        appImageLibrary.init(getContentResolver());
    }

    public ImageLibrary getImageLibrary() {
        AppImageLibrary appImageLibrary = ((ApplicationManager) getApplicationContext())
        .getAppImageLibrary();
        return appImageLibrary.getImageLibrary();
    }*/



    /* *********************************************************************************************
     *                                Permission Section
     */


    public boolean checkForPermission(int permissionKey) {
        if (permissionKey == KeyManifest.UsesPermission.GPS_1) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }else {
                return true;
            }
        }else if(permissionKey == KeyManifest.UsesPermission.ACCESS_COARSE_LOCATION) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //LOGW(TAG, "read phoe state false");
                return false;
            } else {
                //LOGW(TAG, "read phoe state true");
                return true;
            }
        }else if(permissionKey == KeyManifest.UsesPermission.PHONE){
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //LOGW(TAG, "read phoe state false");
                return false;
            }else {
                //LOGW(TAG, "read phoe state true");
                return true;
            }
        }else if(permissionKey == KeyManifest.UsesPermission.READ_CONTACT){
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                //LOGW(TAG, "read phoe state false");
                return false;
            }else {
                //LOGW(TAG, "read phoe state true");
                return true;
            }
        }else if(permissionKey == KeyManifest.UsesPermission.RECEIVE_SMS){
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                //LOGW(TAG, "read phoe state false");
                return false;
            }else {
                //LOGW(TAG, "read phoe state true");
                return true;
            }
        }
        return false;
    }

    public void setListenerPermissionChange(ListenerPermissionChange pListenerPermissionChange) {
        this.mListenerPermissionChange = pListenerPermissionChange;
    }

    public boolean requestForPermission(int permissionKey, ListenerPermissionChange pListenerPermissionChange) {

        if (permissionKey == KeyManifest.UsesPermission.GPS_1) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    permissionKey);
            setListenerPermissionChange(pListenerPermissionChange);
            return true;
        }else if (permissionKey == KeyManifest.UsesPermission.ACCESS_COARSE_LOCATION) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    permissionKey);
            setListenerPermissionChange(pListenerPermissionChange);
            return true;
        }else if(permissionKey == KeyManifest.UsesPermission.PHONE){
            //LOGW(TAG, "request read phone state");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    permissionKey);
            setListenerPermissionChange(pListenerPermissionChange);
            return true;
        }else if(permissionKey == KeyManifest.UsesPermission.READ_CONTACT){
            //LOGW(TAG, "request read phone state");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    permissionKey);
            setListenerPermissionChange(pListenerPermissionChange);
            return true;
        }else if(permissionKey == KeyManifest.UsesPermission.RECEIVE_SMS){
            //LOGW(TAG, "request read phone state");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECEIVE_SMS},
                    permissionKey);
            setListenerPermissionChange(pListenerPermissionChange);
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == KeyManifest.UsesPermission.GPS_1
                || requestCode == KeyManifest.UsesPermission.ACCESS_COARSE_LOCATION
                || requestCode == KeyManifest.UsesPermission.PHONE
                || requestCode == KeyManifest.UsesPermission.READ_CONTACT
                || requestCode == KeyManifest.UsesPermission.RECEIVE_SMS) {
            if (mListenerPermissionChange != null) {
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mListenerPermissionChange.onPermissionResult(requestCode, true);
                } else {
                    mListenerPermissionChange.onPermissionResult(requestCode, false);
                }
                mListenerPermissionChange = null;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*
    if (ActivityCompat.checkSelfPermission(getActivity(),
    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    && ActivityCompat.checkSelfPermission(getActivity(),
    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
    }*/





    public interface ListenerPermissionChange {
        public void onPermissionResult(int reqKey, boolean isAllowed);
    }

}
