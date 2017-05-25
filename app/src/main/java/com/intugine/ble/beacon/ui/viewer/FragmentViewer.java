package com.intugine.ble.beacon.ui.viewer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intugine.ble.beacon.R;
import com.intugine.ble.beacon.ui.ActivityApp;
import com.intugine.ble.beacon.ui.base.FragmentBase;
import com.intugine.ble.beacon.utils.UtilsResource;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.intugine.ble.beacon.util.LogUtils.LOGD;
import static com.intugine.ble.beacon.util.LogUtils.LOGI;
import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 20-05-2017.
 */

public class FragmentViewer extends FragmentBase {

    private static final String TAG = makeLogTag(FragmentViewer.class);
    TextView vTvMessage;
    TextView vTvIp;
    Map<String, WrapperPointer> mMapIndicators;
    private FrameLayout vFlNavigateIndicator;
    private int mWidth;
    private int mHeight;

    public static Fragment newInstance(int position, Bundle bundle) {
        FragmentViewer fragment = new FragmentViewer();
        Bundle args = new Bundle(bundle);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean setNavigationHomeAsBack() {
        return false;
    }

    @Override
    protected String getToolBarDefaultTitle() {
        return null;
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.viewer_fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapIndicators = new HashMap<String, WrapperPointer>(10);
        Activity activity = getActivity();
        if (activity instanceof ActivityApp) {
            ((ActivityApp) activity).initServer();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity activity = getActivity();
        if (activity instanceof ActivityApp) {
            String ip = ((ActivityApp) activity).getServerIp();
            vTvIp.setText(ip);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity activity = getActivity();
        if (activity instanceof ActivityApp) {
            ((ActivityApp) activity).startServer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Activity activity = getActivity();
        if (activity instanceof ActivityApp) {
            ((ActivityApp) activity).stopServer();
        }
    }

    private void initView(View view, Bundle savedInstanceState) {
        vTvIp = (TextView) view.findViewById(R.id.viewer_fragment_tv_ip);
        vTvMessage = (TextView) view.findViewById(R.id.viewer_fragment_tv_message);
        vFlNavigateIndicator = (FrameLayout) view.findViewById(R.id.navigate_fragment_indicator_container);

        ViewTreeObserver viewTreeObserver = vFlNavigateIndicator.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                vFlNavigateIndicator.getViewTreeObserver().removeOnPreDrawListener(this);
                updateArea(vFlNavigateIndicator.getMeasuredWidth(), vFlNavigateIndicator.getMeasuredHeight());
                LOGD(TAG, "height: " + mHeight);
                LOGD(TAG, "width: " + mWidth);
                // mNavigationHandler.initPointer();
                //mAnim2DropDown = new AnimDropDown(v2IVDropDown, v2LLContainer, mHeightSection2, false, mAnimDuration);
                return true;
            }
        });
    }

    private void updateArea(int measuredWidth, int measuredHeight) {
        int height = UtilsResource.getResourceDimenValue(getContext(), R.dimen.length_48);
        int width = UtilsResource.getResourceDimenValue(getContext(), R.dimen.length_24);

        mWidth = measuredWidth - width;
        mHeight = measuredHeight - height;
    }

    public void onServerMessageReceived(String pMessage) {
        String data = pMessage;


        long currentTime = Calendar.getInstance().getTimeInMillis();


        LOGI(TAG, data);
        String[] pack = data.split("\\|");
        if (pack != null && pack.length > 1) {

            String device = pack[0];
            LOGI(TAG, "device: " + device);

            if (device != null) {
                String[] position = pack[1].split(",");
                LOGI(TAG, "position: " + Arrays.toString(position));

                if (position != null && position.length > 1) {


                    WrapperPointer wrapperPointer = mMapIndicators.get(device);
                    if (wrapperPointer == null) {
                        wrapperPointer = new WrapperPointer(getContext(), vFlNavigateIndicator, device);
                        mMapIndicators.put(device, wrapperPointer);
                    }

                    if (position != null && position.length > 1) {
                        int x = Integer.valueOf(position[0]);
                        int y = Integer.valueOf(position[1]);
                        x= x>100?100:x;
                        y = y>100?100:y;
                        wrapperPointer.setPosition(mWidth * x / 100, mHeight * y / 100);
                    }
                }
            }


        }

        Iterator<Map.Entry<String, WrapperPointer>> it = mMapIndicators.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, WrapperPointer> pair = (Map.Entry) it.next();
            boolean isToRemove = pair.getValue().checkToRemove(currentTime);
            System.out.println(pair.getKey() + " = " + pair.getValue());
            if (isToRemove) {
                it.remove();
            }
            // avoids a ConcurrentModificationException
        }
        vTvMessage.setText(data);
    }


}
